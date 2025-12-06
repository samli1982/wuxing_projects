Page({
  data: {
    palmtrees: [],
    selectedId: '',
    nickname: '',
    birthInfo: '',
    eight: {
      year: { heavenly_stem: '庚', earthly_branch: '午' },
      month: { heavenly_stem: '辛', earthly_branch: '巳' },
      day: { heavenly_stem: '癸', earthly_branch: '酉' },
      hour: { heavenly_stem: '戊', earthly_branch: '午' }
    },
    nayin: '路旁土',
    kongwang: '戌亥',
    taiyuan: '壬申',
    wuxing: { 木: 18, 火: 32, 土: 20, 金: 25, 水: 5 },
    barWidth: { 木: '0%', 火: '0%', 土: '0%', 金: '0%', 水: '0%' },
    barWidthWood: '0%',
    barWidthFire: '0%',
    barWidthEarth: '0%',
    barWidthMetal: '0%',
    barWidthWater: '0%',
    wuxingWood: 0,
    wuxingFire: 0,
    wuxingEarth: 0,
    wuxingMetal: 0,
    wuxingWater: 0,
    usefulGods: ['水', '金'],
    wuyunliuqi: {
      year: 1990,
      transportation: '金运太过',
      tiansi: '少阴君火司天',
      zaiquan: '阳明燥金在泉',
      climate: '燥热偏盛，易伤肺津',
      health_warnings: ['肺金过盛，防呼吸系统不适', '心火受制，注意情绪低落']
    },
    constitution: '阳盛阴虚质',
    analysis: {
      cause: '火旺克金，水气不足，肝郁化火',
      symptom: '口干舌燥、失眠多梦、情绪急躁',
      tune: '滋阴降火，疏肝理气'
    },
    activeTab: 'diet',
    allTips: {
      diet: [
        { type: 'diet', text: '宜食黑色食物补肾（黑豆、海带），忌辛辣烧烤' }
      ],
      lifestyle: [
        { type: 'lifestyle', text: '建议晚10点前入睡，晨练面向东方吐纳' }
      ],
      emotion: [
        { type: 'emotion', text: '保持心境平和，可听羽调音乐安神' }
      ],
      herb: [
        { type: 'herb', text: '推荐麦冬泡水代茶饮，辅以酸枣仁安神' }
      ]
    },
    displayTips: []
  },
  onLoad() {
    // 检查登录状态，未登录则跳转到登录页
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      // 如果未登录或用户取消登录，停止页面加载
      return;
    }
    
    this.loadPalmtrees();
    this.loadCurrent();
  },
  onShow() {
    // 每次页面显示时都检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      // 如果未登录或用户取消登录，停止页面加载
      return;
    }
  },
  loadPalmtrees() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    // 从后端加载当前用户的命盘列表
    const request = require('../../utils/request.js');
    request.get('/palmtree/list').then((res) => {
      const data = res.data || res;
      const list = Array.isArray(data) ? data : (data.records || []);
      this.setData({ palmtrees: list, selectedId: list.length > 0 ? list[list.length - 1].id : '' });
    }).catch((err) => {
      console.error('加载命盘列表失败:', err);
      // 故際佔杨：仅从本地存储加载
      wx.getStorage({ key: 'palmtrees', success: (res) => {
        const list = res.data || [];
        this.setData({ palmtrees: list, selectedId: list.length > 0 ? list[list.length - 1].id : '' });
      }, fail: () => this.setData({ palmtrees: [], selectedId: '' }) });
    });
  },
  loadCurrent() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    wx.getStorage({ key: 'palmtree_latest', success: (res) => {
      const f = res.data || {};
      const nick = f.nickname || f.name || '未命名';
      const hourNames = ['子','丑','寅','卯','辰','巳','午','未','申','酉','戌','亥'];
      const hourLabel = (typeof f.hour_index === 'number' && f.hour_index >= 0) ? hourNames[f.hour_index] : '子';
      const city = f.location_city || '未知城市';
      const birth = `${f.year || f.birth_year}年${f.month || f.birth_month}月${f.day || f.birth_day}日 ${hourLabel}时（${city}）`;
      this.setData({ nickname: nick, birthInfo: birth });
      this._calcBarWidth();
      this._updateTips();
    }});
  },
  _calcBarWidth() {
    const v = this.data.wuxing;
    const max = Math.max(v.木, v.火, v.土, v.金, v.水) || 1;
    const pct = (n) => `${Math.round((n / max) * 100)}%`;
    this.setData({
      barWidth: { 木: pct(v.木), 火: pct(v.火), 土: pct(v.土), 金: pct(v.金), 水: pct(v.水) },
      barWidthWood: pct(v.木),
      barWidthFire: pct(v.火),
      barWidthEarth: pct(v.土),
      barWidthMetal: pct(v.金),
      barWidthWater: pct(v.水),
      wuxingWood: v.木,
      wuxingFire: v.火,
      wuxingEarth: v.土,
      wuxingMetal: v.金,
      wuxingWater: v.水
    });
  },
  _updateTips() {
    this.setData({ displayTips: this.data.allTips[this.data.activeTab] });
  },
  switchTab(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab });
    this._updateTips();
  },
  onBarTap(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const name = e.currentTarget.dataset.name;
    const explain = {
      '木': '木弱：肝胆气不足，易情绪波动，可补木青色食物',
      '火': '火旺：易上火烦躁，宜滋阴降火',
      '土': '土偏：脾胃需调理，宜健脾祛湿',
      '金': '金偏：肺气盛或不足，注意呼吸道',
      '水': '水弱：肾虚畏寒，宜温补与保暖'
    }[name] || '五行说明';
    wx.showModal({ title: `${name}行释义`, content: explain, showCancel: false });
  },
  onBack() {
    wx.redirectTo({ url: '/pages/palmtree/new' });
  },
  switchPalmtree(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const id = e.currentTarget.dataset.id;
    this.setData({ selectedId: id });
    wx.showToast({ title: '命盘已切换', icon: 'none' });
  },
  onCreate() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    wx.navigateTo({ url: '/pages/palmtree/new' });
  },
  onEdit() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    wx.showToast({ title: '编辑功能待实现', icon: 'none' });
  },
  onLongPress(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const id = e.currentTarget.dataset.id;
    wx.showActionSheet({
      itemList: ['重命名', '删除'],
      success: (res) => {
        if (res.tapIndex === 0) {
          wx.showToast({ title: '重命名功能待实现', icon: 'none' });
        } else if (res.tapIndex === 1) {
          wx.showModal({ title: '确认删除', content: '删除后将无法恢复，是否继续？', success: (r) => {
            if (r.confirm) {
              let list = this.data.palmtrees.filter(p => p.id !== id);
              this.setData({ palmtrees: list });
              wx.setStorage({ key: 'palmtrees', data: list });
              wx.showToast({ title: '已删除', icon: 'success' });
            }
          }});
        }
      }
    });
  },
  onShowGodsTheory() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    wx.showModal({ title: '喜用神原理', content: '喜用神是补益命局五行、调节阴阳平衡的关键。日主癸水弱，火旺克金，宜补水助金以平衡全局。', showCancel: false });
  },
  onSave() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    // 从本地存储获取最新的命盘数据
    wx.getStorage({ 
      key: 'palmtree_latest', 
      success: (res) => {
        const formData = res.data;
        
        // 构建提交数据
        const submitData = {
          nickname: formData.nickname,
          name: formData.name,
          gender: formData.gender,
          birth_year: formData.birth_year,
          birth_month: formData.birth_month,
          birth_day: formData.birth_day,
          birth_hour_type: formData.birth_hour_type,
          hour_index: formData.hour_index,
          location_city: formData.location_city,
          location_lat: formData.location_lat,
          location_lng: formData.location_lng,
          calendar_type: formData.calendar_type,
          for_health_analysis: formData.for_health_analysis ? 1 : 0
        };
        
        // 调用后端 API 保存命盘
        const request = require('../../utils/request.js');
        request.post('/palmtree/save', submitData).then((res) => {
          wx.showToast({ title: '命盘已保存', icon: 'success' });
        }).catch((err) => {
          console.error('保存命盘失败:', err);
          wx.showToast({ title: '保存失败，请稍后重试', icon: 'none' });
        });
      },
      fail: () => {
        wx.showToast({ title: '无可保存的命盘数据', icon: 'none' });
      }
    });
  },
  onDetail() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    wx.navigateTo({ url: '/pages/learn/index' });
  },
  onShareAppMessage() {
    return {
      title: `${this.data.nickname}的命盘报告`,
      path: '/pages/palmtree/result'
    };
  }
});