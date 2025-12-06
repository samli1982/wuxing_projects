Page({
  data: {
    yearRange: [],
    gregorianMonthRange: [], // 公历月份：1-12
    lunarMonthRange: [], // 农历月份：正月-腊月
    currentMonthRange: [], // 当前显示的月份范围
    gregorianDayRange: [], // 公历日期：1-31
    lunarDayRange: [], // 农历日期：初一-三十
    currentDayRange: [], // 当前显示的日期范围
    lunarMonthData: [], // 农历月份完整数据（包含index、monthValue、isLeap等）
    lunarDayData: [], // 农历日期完整数据（包含day、name）
    birthTime: '', // 出生时间（HH:mm格式）
    yearIndex: 0,
    monthIndex: 0,
    dayIndex: 0,
    displayMonth: '', // 显示的月份文本
    displayDay: '', // 显示的日期文本
    form: {
      name: '',
      nickname: '',
      gender: 'male',
      birth_year: null,
      birth_month: null,
      birth_day: null,
      birth_hour: null,  // 小时（0-23）
      birth_minute: null, // 分钟（0-59）
      location_city: '',
      location_lat: null,
      location_lng: null,
      calendar_type: 'gregorian',
      for_health_analysis: true
    }
  },
  onLoad() {
    // 检查登录状态，未登录则跳转到登录页
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      // 如果未登录或用户取消登录，停止页面加载
      return;
    }
    
    // 获取状态栏高度
    const systemInfo = wx.getSystemInfoSync();
    this.setData({ statusBarHeight: systemInfo.statusBarHeight });
    
    const currentYear = new Date().getFullYear();
    const years = [];
    // 倒序生成年份数组，从当前年份到1900年
    for (let y = currentYear; y >= 1900; y--) years.push(y);
    
    // 公历月份：1-12
    const gregorianMonths = Array.from({ length: 12 }, (_, i) => i + 1);
    
    // 公历日期：1-31
    const gregorianDays = Array.from({ length: 31 }, (_, i) => i + 1);
    
    // 初始化为公历
    this.setData({ 
      yearRange: years,
      gregorianMonthRange: gregorianMonths,
      lunarMonthRange: [], // 农历月份将动态加载
      currentMonthRange: gregorianMonths,
      gregorianDayRange: gregorianDays,
      lunarDayRange: [], // 农历日期将动态加载
      currentDayRange: gregorianDays
    });
  },
  onShow() {
    // 每次页面显示时都检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      // 如果未登录或用户取消登录，停止页面加载
      return;
    }
  },
  onBack() { 
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    wx.navigateBack(); 
  },
  onInputName(e) { 
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    this.setData({ 'form.name': e.detail.value }); 
  },
  onInputNickname(e) { 
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    this.setData({ 'form.nickname': e.detail.value }); 
  },
  onYearChange(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const idx = Number(e.detail.value);
    const yr = this.data.yearRange[idx];
    this.setData({ yearIndex: idx, 'form.birth_year': yr });
    
    // 如果是农历，重新加载该年的月份
    if (this.data.form.calendar_type === 'lunar') {
      this._loadLunarMonths(yr);
    } else {
      this._updateDayRange();
    }
  },
  onMonthChange(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const idx = Number(e.detail.value);
    const isLunar = this.data.form.calendar_type === 'lunar';
    
    if (isLunar) {
      // 农历：使用后端返回的月份数据
      const monthData = this.data.lunarMonthData[idx];
      const displayText = monthData.name;
      
      this.setData({ 
        monthIndex: idx, 
        'form.birth_month': monthData.monthValue,
        'form.is_leap_month': monthData.isLeap,
        displayMonth: displayText
      });
      
      // 加载该月的日期
      this._loadLunarDays(this.data.form.birth_year, monthData.monthValue, monthData.isLeap);
    } else {
      // 公历
      const monthValue = idx + 1;
      this.setData({ 
        monthIndex: idx, 
        'form.birth_month': monthValue,
        'form.is_leap_month': false,
        displayMonth: monthValue
      });
      this._updateDayRange();
    }
  },
  onDayChange(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const idx = Number(e.detail.value);
    const isLunar = this.data.form.calendar_type === 'lunar';
    
    if (isLunar) {
      // 农历：使用后端返回的日期数据
      const dayData = this.data.lunarDayData[idx];
      this.setData({ 
        dayIndex: idx, 
        'form.birth_day': dayData.day,
        displayDay: dayData.name
      });
    } else {
      // 公历
      const dayValue = idx + 1;
      this.setData({ 
        dayIndex: idx, 
        'form.birth_day': dayValue,
        displayDay: dayValue
      });
    }
  },
  _updateDayRange() {
    const m = this.data.form.birth_month || 1;
    const y = this.data.form.birth_year || new Date().getFullYear();
    
    // 公历：根据年月计算天数
    const daysInMonth = new Date(y, m, 0).getDate();
    const range = Array.from({ length: daysInMonth }, (_, i) => i + 1);
    const safeIndex = Math.min(this.data.dayIndex, range.length - 1);
    this.setData({ 
      currentDayRange: range, 
      dayIndex: safeIndex, 
      'form.birth_day': range[safeIndex],
      displayDay: range[safeIndex]
    });
  },
  onTimeChange(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const time = e.detail.value; // HH:mm 格式
    const [hour, minute] = time.split(':').map(Number);
    
    this.setData({ 
      birthTime: time,
      'form.birth_hour': hour,
      'form.birth_minute': minute
    });
  },
  onCalendarTypeChange(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const calendarType = e.detail.value; // 'gregorian' 或 'lunar'
    const isLunar = calendarType === 'lunar';
    
    this.setData({ 'form.calendar_type': calendarType });
    
    if (isLunar) {
      // 切换到农历：加载农历月份
      const year = this.data.form.birth_year || new Date().getFullYear();
      this._loadLunarMonths(year);
    } else {
      // 切换到公历：恢复公历数据
      const monthIdx = Math.min(this.data.monthIndex, 11);
      const monthValue = monthIdx + 1;
      
      this.setData({ 
        currentMonthRange: this.data.gregorianMonthRange,
        monthIndex: monthIdx,
        'form.birth_month': monthValue,
        'form.is_leap_month': false,
        displayMonth: monthValue
      });
      this._updateDayRange();
    }
  },
  
  // 加载农历月份
  _loadLunarMonths(year) {
    wx.showLoading({ title: '加载中...', mask: true });
    
    const request = require('../../utils/request.js');
    request.get(`/lunar-calendar/months/${year}`).then((res) => {
      wx.hideLoading();
      
      const months = res.data;
      const monthNames = months.map(m => m.name);
      
      // 重置月份索引
      const monthIdx = 0;
      const firstMonth = months[monthIdx];
      
      this.setData({ 
        lunarMonthData: months, // 保存完整数据
        lunarMonthRange: monthNames,
        currentMonthRange: monthNames,
        monthIndex: monthIdx,
        'form.birth_month': firstMonth.monthValue,
        'form.is_leap_month': firstMonth.isLeap,
        displayMonth: firstMonth.name
      });
      
      // 加载第一个月的日期
      this._loadLunarDays(year, firstMonth.monthValue, firstMonth.isLeap);
    }).catch((err) => {
      wx.hideLoading();
      console.error('加载农历月份失败:', err);
      wx.showToast({ title: '加载农历月份失败', icon: 'none' });
    });
  },
  
  // 加载农历日期
  _loadLunarDays(year, monthValue, isLeap) {
    wx.showLoading({ title: '加载中...', mask: true });
    
    const request = require('../../utils/request.js');
    request.get(`/lunar-calendar/days/${year}/${monthValue}/${isLeap}`).then((res) => {
      wx.hideLoading();
      
      const days = res.data;
      const dayNames = days.map(d => d.name);
      
      // 重置日期索引
      const dayIdx = 0;
      const firstDay = days[dayIdx];
      
      this.setData({ 
        lunarDayData: days, // 保存完整数据
        lunarDayRange: dayNames,
        currentDayRange: dayNames,
        dayIndex: dayIdx,
        'form.birth_day': firstDay.day,
        displayDay: firstDay.name
      });
    }).catch((err) => {
      wx.hideLoading();
      console.error('加载农历日期失败:', err);
      wx.showToast({ title: '加载农历日期失败', icon: 'none' });
    });
  },
  onInputCity(e) { 
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    this.setData({ 'form.location_city': e.detail.value }); 
  },
  onLocate() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        this.setData({ 'form.location_lat': res.latitude, 'form.location_lng': res.longitude, 'form.location_city': this.data.form.location_city || '当前位置' });
        wx.showToast({ title: '位置已更新', icon: 'none' });
      },
      fail: () => { wx.showToast({ title: '定位失败，请手动输入城市', icon: 'none' }); }
    });
  },
  onGenderChange(e) { 
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    this.setData({ 'form.gender': e.detail.value }); 
  },
  onHealthSwitch(e) { 
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    this.setData({ 'form.for_health_analysis': !!e.detail.value }); 
  },
  onSubmit() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const f = this.data.form;
    
    // 验证必填字段
    if (!f.nickname) {
      wx.showToast({ title: '请输入昵称', icon: 'none' });
      return;
    }
    if (!f.birth_year || !f.birth_month || !f.birth_day) {
      wx.showToast({ title: '请选择完整的出生日期', icon: 'none' });
      return;
    }
    if (f.birth_hour === null || f.birth_hour === undefined) {
      wx.showToast({ title: '请选择出生时间', icon: 'none' });
      return;
    }
    
    // 构建提交数据
    const submitData = {
      nickname: f.nickname,
      name: f.name,
      gender: f.gender,
      birth_year: f.birth_year,
      birth_month: f.birth_month,
      birth_day: f.birth_day,
      birth_hour: f.birth_hour,
      birth_minute: f.birth_minute || 0,
      location_city: f.location_city,
      location_lat: f.location_lat,
      location_lng: f.location_lng,
      calendar_type: f.calendar_type,
      for_health_analysis: f.for_health_analysis ? 1 : 0
    };
    
    console.log('提交数据:', submitData);
    console.log('birth_hour:', f.birth_hour, 'birth_minute:', f.birth_minute);
    
    // 显示加载提示
    wx.showLoading({ title: '正在保存...', mask: true });
    
    // 调用后端 API 保存命盘
    const request = require('../../utils/request.js');
    request.post('/palmtree/save', submitData).then((res) => {
      wx.hideLoading();
      wx.showToast({ title: '保存成功', icon: 'success' });
      // 延迟跳转到命盘页面（使用switchTab因为index是TabBar页面）
      setTimeout(() => {
        wx.switchTab({ url: '/pages/palmtree/index' });
      }, 1500);
    }).catch((err) => {
      wx.hideLoading();
      console.error('保存命盘失败:', err);
      wx.showToast({ title: '保存失败，请重试', icon: 'none' });
    });
  }
});