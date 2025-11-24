Page({
  data: {
    yearRange: [],
    monthRange: [],
    dayRange: [],
    shichenRange: [],
    yearIndex: 0,
    monthIndex: 0,
    dayIndex: 0,
    shichenIndex: 0,
    shichenLabel: '',
    form: {
      name: '',
      nickname: '',
      gender: 'male',
      birth_year: null,
      birth_month: null,
      birth_day: null,
      birth_hour_type: 'unknown',
      hour_index: null,
      location_city: '',
      location_lat: null,
      location_lng: null,
      calendar_type: 'gregorian',
      for_health_analysis: true
    }
  },
  onLoad() {
    const currentYear = new Date().getFullYear();
    const years = [];
    for (let y = 1900; y <= currentYear; y++) years.push(y);
    const months = Array.from({ length: 12 }, (_, i) => i + 1);
    const days = Array.from({ length: 31 }, (_, i) => i + 1);
    const shichen = ['不详','子(23-1)','丑(1-3)','寅(3-5)','卯(5-7)','辰(7-9)','巳(9-11)','午(11-13)','未(13-15)','申(15-17)','酉(17-19)','戌(19-21)','亥(21-23)'];
    this.setData({ yearRange: years, monthRange: months, dayRange: days, shichenRange: shichen });
  },
  onBack() { wx.navigateBack(); },
  onInputName(e) { this.setData({ 'form.name': e.detail.value }); },
  onInputNickname(e) { this.setData({ 'form.nickname': e.detail.value }); },
  onYearChange(e) {
    const idx = Number(e.detail.value);
    const yr = this.data.yearRange[idx];
    this.setData({ yearIndex: idx, 'form.birth_year': yr });
    this._updateDayRange();
  },
  onMonthChange(e) {
    const idx = Number(e.detail.value);
    const m = this.data.monthRange[idx];
    this.setData({ monthIndex: idx, 'form.birth_month': m });
    this._updateDayRange();
  },
  onDayChange(e) {
    const idx = Number(e.detail.value);
    const d = this.data.dayRange[idx];
    this.setData({ dayIndex: idx, 'form.birth_day': d });
  },
  _updateDayRange() {
    const m = this.data.form.birth_month || 1;
    const y = this.data.form.birth_year || new Date().getFullYear();
    const daysInMonth = new Date(y, m, 0).getDate();
    const range = Array.from({ length: daysInMonth }, (_, i) => i + 1);
    const safeIndex = Math.min(this.data.dayIndex, range.length - 1);
    this.setData({ dayRange: range, dayIndex: safeIndex, 'form.birth_day': range[safeIndex] });
  },
  onShichenChange(e) {
    const idx = Number(e.detail.value);
    const label = this.data.shichenRange[idx];
    if (label === '不详') {
      this.setData({ shichenIndex: idx, shichenLabel: label, 'form.birth_hour_type': 'unknown', 'form.hour_index': null });
      wx.showToast({ title: '不详：系统将以子时为准', icon: 'none' });
    } else {
      this.setData({ shichenIndex: idx, shichenLabel: label, 'form.birth_hour_type': 'precise', 'form.hour_index': idx - 1 });
    }
  },
  onCalendarSwitch(e) {
    const checked = e.detail.value;
    this.setData({ 'form.calendar_type': checked ? 'gregorian' : 'lunar' });
  },
  onInputCity(e) { this.setData({ 'form.location_city': e.detail.value }); },
  onLocate() {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        this.setData({ 'form.location_lat': res.latitude, 'form.location_lng': res.longitude, 'form.location_city': this.data.form.location_city || '当前位置' });
        wx.showToast({ title: '位置已更新', icon: 'none' });
      },
      fail: () => { wx.showToast({ title: '定位失败，请手动输入城市', icon: 'none' }); }
    });
  },
  onGenderChange(e) { this.setData({ 'form.gender': e.detail.value }); },
  onHealthSwitch(e) { this.setData({ 'form.for_health_analysis': !!e.detail.value }); },
  onSubmit() {
    const f = this.data.form;
    if (!f.nickname || !f.birth_year || !f.birth_month || !f.birth_day) {
      wx.showToast({ title: '请完善出生时间等信息', icon: 'none' });
      return;
    }
    const nameShow = f.name || f.nickname;
    const summary = `${nameShow}｜${f.birth_year}年生`;
    wx.setStorage({ key: 'latest_palmtree_summary', data: summary });
    wx.setStorage({ key: 'palmtree_latest', data: f });
    wx.navigateTo({ url: '/pages/palmtree/loading' });
  }
});
