import * as wuyunApi from '../../api/wuyun.js';
import * as homeApi from '../../api/home.js';

Page({
  data: {
    statusBarHeight: 44,
    scrollDuration: 12,  // 滚动动画持续时间（秒）
    solarDateTime: '加载中...',
    sixtiesCycleInfo: {
      solarDate: '加载中...',
      solarTime: '00:00',
      year: '加载中...',
      month: '加载中...',
      day: '加载中...',
      hour: '加载中...',
      sixtyCycleDate: '加载中...'
    },
    emperorCalendarTitle: '干支纪年历',
    headerInfo: {
      sunriseTime: '加载中...',
      lunarDate: '加载中...',
      solarTermName: '加载中...',
      solarTermDayIndex: 0,
      beidouDirection: '加载中...'
    },
    wuyun: {
      year: 2025,
      ganzhi: '乙巳年',
      nayin: '覆灯火',
      wuyun: '金运不及',
      sitian: '厥阴风木',
      zaiquan: '少阳相火',
      climate: '春季风木主令，易肝阳旺盛；秋季燥金内收，宜滋阴润肺',
      healthTips: [
        '肝木受克，注意情绪波动',
        '肺金偏旺，防呼吸道不适',
        '宜养阴润燥，忌辛辣燥热',
        '午后适度运动，避免暴晒'
      ]
    },
    displayTips: [],
    showAllTips: false,
    hasPalmtree: false,
    latestPalmtreeSummary: '',
    healthTipVisible: true,
    healthTipText: '您四柱木弱，今值金运当令，建议佩戴绿植饰品调和。'
  },
  onLoad() {
    try {
      const sys = wx.getWindowInfo();
      if (sys && sys.statusBarHeight) {
        this.setData({ statusBarHeight: sys.statusBarHeight });
      }
    } catch (e) {}

    // 并行加载所有首页数据
    this.loadAllData();

    // 读取健康提示关闭缓存
    wx.getStorage({
      key: 'health_tip_closed',
      success: (res) => {
        if (res && res.data) {
          this.setData({ healthTipVisible: false });
        }
      }
    });

    // 读取最近命盘摘要
    wx.getStorage({
      key: 'latest_palmtree_summary',
      success: (res) => {
        if (res && res.data) {
          this.setData({ hasPalmtree: true, latestPalmtreeSummary: res.data });
        }
      },
      fail: () => {
        this.setData({ hasPalmtree: false, latestPalmtreeSummary: '' });
      }
    });
  },
  loadAllData() {
    console.log('📡 开始加载首页所有数据...');
    
    // 1. 加载公历时间
    this.loadSolarDateTime();
    
    // 2. 加载干支历信息
    this.loadSixtiesCycleInfo();
    
    // 3. 加载皇帝纪年历入口
    this.loadEmperorCalendarInfo();
    
    // 4. 加载五运六气数据
    const year = new Date().getFullYear();
    this.loadWuyun(year);
    
    // 5. 加载首页信息（日出日落、节气、农历、北斗等）
    this.loadHeaderInfo();
    
    // 6. 加载今日养生建议
    this.loadHealthAdvice();
  },
  loadSolarDateTime() {
    console.log('📡 加载公历时间...');
    // 创建定时器，每5秒更新一次
    const updateTime = () => {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      const hours = String(now.getHours()).padStart(2, '0');
      const minutes = String(now.getMinutes()).padStart(2, '0');
      const solarDateTime = `${year}-${month}-${day} ${hours}:${minutes}`;
      this.setData({ solarDateTime });
    };
    updateTime();
    this.solarTimeInterval = setInterval(updateTime, 5000);
  },
  loadSixtiesCycleInfo() {
    console.log('📡 加载干支历信息...');
    homeApi.getSixtiesCycleInfo().then(data => {
      console.log('✅ 干支历信息加载完成:', data);
      // 修正数据提取逻辑，从data属性中获取实际数据
      const formattedData = {
        solarDate: data.data.solarDate,
        solarTime: data.data.solarTime,
        year: data.data.year,
        month: data.data.month,
        day: data.data.day,
        hour: data.data.hour,
        sixtyCycleDate: data.data.sixtyCycleDate
      };
      this.setData({ sixtiesCycleInfo: formattedData });
    }).catch(err => {
      console.warn('⚠️ 加载干支历失败:', err);
      // 使用默认数据
    });
  },
  loadEmperorCalendarInfo() {
    console.log('📡 加载干支纪年历入口...');
    homeApi.getEmperorCalendarEntry().then(data => {
      console.log('✅ 干支纪年历入口加载完成:', data);
      // 修正数据提取逻辑，从data属性中获取实际数据
      this.setData({ emperorCalendarTitle: data?.data?.title || '干支纪年历' });
      this.emperorCalendarEntry = data.data;
    }).catch(err => {
      console.warn('⚠️ 加载干支纪年历入口失败:', err);
      // 使用默认数据
    });
  },
  loadHeaderInfo() {
    console.log('📡 加载首页头部信息...');
    
    // 同时加载日出日落、节气、农历、北斗信息
    Promise.all([
      homeApi.getSunriseSunset(),
      homeApi.getSolarTermInfo(),
      homeApi.getLunarInfo(),
      homeApi.getBeidouInfo()
    ]).then(([sunriseData, solarTermData, lunarData, beidouData]) => {
      console.log('✅ 首页信息加载完成');
      console.log('日出数据:', sunriseData);
      console.log('节气数据:', solarTermData);
      console.log('农历数据:', lunarData);
      console.log('北斗数据:', beidouData);
      
      // 修正数据提取逻辑，从data属性中获取实际数据
      const headerInfo = {
        sunriseTime: sunriseData?.data?.sunriseTime || '05:28',
        lunarDate: lunarData?.data?.lunarDate || '乙巳年四月初八',
        solarTermName: solarTermData?.data?.name || '立夏',
        solarTermDayIndex: solarTermData?.data?.dayIndex || 3,
        beidouDirection: beidouData?.data?.direction || '斗柄东南 · 天地火气渐升'
      };
      
      console.log('最终headerInfo:', headerInfo);
      console.log('🔄 调用 setData 前的 data.headerInfo:', this.data.headerInfo);
      this.setData({ headerInfo });
      console.log('✨ 调用 setData 后的 data.headerInfo:', this.data.headerInfo);
    }).catch(err => {
      console.warn('⚠️ 加载首页信息失败，使用默认数据:', err);
      console.error('详细错误信息:', err);
      // 失败时保持默认数据
    });
  },
  loadHealthAdvice() {
    console.log('📡 加载今日养生建议...');
    homeApi.getDailyHealthAdvice().then(data => {
      console.log('✅ 养生建议加载完成:', data);
      // 修正数据提取逻辑，从data属性中获取实际数据
      this.setData({ healthTipText: data?.data?.advice || this.data.healthTipText });
    }).catch(err => {
      console.warn('⚠️ 加载养生建议失败:', err);
      // 失败时保持默认建议
    });
  },
  loadWuyun(year) {
    console.log('📡 加载年份 ' + year + ' 的五运六气数据...');
    // 调用完整的五运六气接口
    wuyunApi.getWuyunByYear(year).then(data => {
      console.log('✅ 成功获取五运六气数据:', data);
      // 如果是新的complete接口返回数据
      const apiData = data.data || {};
      const wuyun = {
        year: apiData.year || year,
        ganzhi: apiData.ganzhi || '',
        nayin: apiData.nayin || '',
        wuyun: apiData.transportation || apiData.wuyun || '',
        sitian: apiData.sitian || '',
        zaiquan: apiData.zaiquan || '',
        climate: apiData.climate || '',
        healthTips: apiData.health_warnings || apiData.healthTips || []
      };
      console.log('✅ 五运六气数据已更新:', wuyun);
      // 同时更新健康提示显示列表
      const displayTips = wuyun.healthTips.slice(0, 3);
      this.setData({ wuyun, displayTips, showAllTips: false });
      console.log('📋 健康提示已计算:', displayTips);
    }).catch(err => {
      console.warn('⚠️ 加载五运六气失败，尝试加载基础数据:', err);
      // 如果complete接口失败，降级到原来的接口
      this.loadBasicWuyun(year);
    });
  },
  
  loadBasicWuyun(year) {
    // 降级方案：分别加载基础信息和健康建议
    const request = require('../../utils/request.js');
    Promise.all([
      request.get(`/api/wuyun/${year}`),
      request.get(`/api/wuyun/${year}/health-tips`)
    ]).then(([basicData, healthData]) => {
      const data = basicData.data || basicData;
      const health = healthData.data || healthData;
      const wuyun = {
        year: data.year || year,
        ganzhi: data.ganzhi || '',
        nayin: data.nayin || '',
        wuyun: data.wuyun || '',
        sitian: data.sitian || '',
        zaiquan: data.zaiquan || '',
        climate: '',
        healthTips: health.healthTips || []
      };
      // 同时更新健康提示显示列表
      const displayTips = wuyun.healthTips.slice(0, 3);
      this.setData({ wuyun, displayTips, showAllTips: false });
    }).catch(err => {
      console.warn('⚠️ 加载基础数据失败:', err);
      // 使用默认数据
    });
  },
  toggleTips() {
    const showAll = !this.data.showAllTips;
    this.setData({ showAllTips: showAll });
  },
  onShowSixtiesCycle() {
    const info = this.data.sixtiesCycleInfo;
    wx.showModal({
      title: '干支历详情',
      content: `公历：${info.solarDate} ${info.solarTime}\n干支：${info.sixtyCycleDate}\n年月日时依次为：${info.year} ${info.month} ${info.day} ${info.hour}`,
      showCancel: false
    });
  },
  onGoEmperorCalendar() {
    if (this.emperorCalendarEntry && this.emperorCalendarEntry.entry) {
      wx.navigateTo({ url: this.emperorCalendarEntry.entry });
    } else {
      wx.showToast({ title: '页面还未开放，敬请期待', icon: 'none' });
    }
  },
  onShowSolarTerm() {
    wx.showModal({
      title: '节气详情',
      content: `${this.data.headerInfo.solarTermName}·第${this.data.headerInfo.solarTermDayIndex}天\n天地火气渐升，宜养阴润燥。`,
      showCancel: false
    });
  },
  onUnload() {
    // 清除定时器
    if (this.solarTimeInterval) {
      clearInterval(this.solarTimeInterval);
    }
  },
  goWuYunDetail() {
    const wuyun = this.data.wuyun;
    const wuyunJSON = encodeURIComponent(JSON.stringify(wuyun));
    wx.navigateTo({
      url: `/pages/wuyun/detail?wuyun=${wuyunJSON}`
    });
  },
  onCreatePalmtree() {
    wx.navigateTo({ url: '/pages/palmtree/new' });
  },
  onViewPalmtree() {
    if (this.data.hasPalmtree) {
      wx.navigateTo({ url: '/pages/palmtree/list' });
    } else {
      wx.showToast({ title: '您尚未创建命盘，请先添加', icon: 'none' });
    }
  },
  onCloseHealthTip() {
    wx.setStorage({ key: 'health_tip_closed', data: Date.now() });
    this.setData({ healthTipVisible: false });
  },
  observers: {
    'wuyun.healthTips, showAllTips': function (tips, showAll) {
      const display = showAll ? tips : tips.slice(0, 3);
      this.setData({ displayTips: display });
    }
  }
});
