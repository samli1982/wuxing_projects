Page({
  data: {
    palmtrees: [],
    selectedId: '',
    fabX: 20,
    fabY: 0,
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
    // 初始化测试数据（首次运行时创建样例命盘）
    this.initTestDataIfNeeded();
    this.loadPalmtrees();
    this.loadCurrent();
  },
  onShow() {
    this.loadPalmtrees();
  },
  // 初始化测试数据（仅当palmtrees为空时创建）
  initTestDataIfNeeded() {
    wx.getStorage({
      key: 'palmtrees',
      fail: () => {
        // 本地存储为空，创建3个测试命盘
        const testPalmtrees = [
          {
            id: 'pt_test_001',
            nickname: '张三',
            year: 1990,
            constitution_type: '阳盛阴虚质',
            created_at: new Date().toISOString()
          },
          {
            id: 'pt_test_002',
            nickname: '李四',
            year: 1985,
            constitution_type: '阴虚质',
            created_at: new Date().toISOString()
          },
          {
            id: 'pt_test_003',
            nickname: '王五',
            year: 1995,
            constitution_type: '湿热质',
            created_at: new Date().toISOString()
          }
        ];
        
        wx.setStorage({ key: 'palmtrees', data: testPalmtrees });
        console.log('✅ 已创建测试命盘数据');
      }
    });
  },
  loadPalmtrees() {
    wx.getStorage({ 
      key: 'palmtrees', 
      success: (res) => {
        const list = res.data || [];
        console.log('👀 加载命盘列表：', list.length, '条', list);
        if (list.length > 0) {
          const lastId = list[list.length - 1].id;
          this.setData({ 
            palmtrees: list, 
            selectedId: lastId
          }, () => {
            console.log('✅ 页面数据已更新， palmtrees:', this.data.palmtrees);
          });
          // 自动加载最后一个命盘的详细数据
          this.loadPalmtreeDetail(lastId);
        } else {
          console.log('⚠️ 命盘列表为空');
          this.setData({ palmtrees: [], selectedId: '' });
        }
      }, 
      fail: () => {
        console.log('❌ 未找到命盘数据，需要初始化');
        this.setData({ palmtrees: [], selectedId: '' });
      }
    });
  },
  loadPalmtreeDetail(id) {
    const selected = this.data.palmtrees.find(p => p.id === id);
    if (!selected) return;
    
    wx.getStorage({
      key: `palmtree_${id}`,
      success: (res) => {
        const data = res.data || {};
        const nick = data.nickname || selected.nickname || '未命名';
        const hourNames = ['子','丑','寅','卯','辰','巳','午','未','申','酉','戌','亥'];
        const hourLabel = (typeof data.hour_index === 'number' && data.hour_index >= 0) ? hourNames[data.hour_index] : '子';
        const city = data.location_city || '未知城市';
        const birth = `${data.birth_year || selected.year}年${data.birth_month || 1}月${data.birth_day || 1}日 ${hourLabel}时（${city}）`;
        
        this.setData({
          nickname: nick,
          birthInfo: birth,
          eight: data.eight || this.data.eight,
          wuxing: data.wuxing || this.data.wuxing,
          constitution: data.constitution || this.data.constitution,
          analysis: data.analysis || this.data.analysis,
          wuyunliuqi: data.wuyunliuqi || this.data.wuyunliuqi
        });
        
        this._calcBarWidth();
      },
      fail: () => {
        // 若无详细数据，显示基本信息
        this.setData({
          nickname: selected.nickname || '未命名',
          birthInfo: `${selected.year}年出生`
        });
      }
    });
  },
  loadCurrent() {
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
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab });
    this._updateTips();
  },
  onBarTap(e) {
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
  switchPalmtree(e) {
    const id = e.currentTarget.dataset.id;
    if (!id) {
      console.error('❌ 命盘ID为空，无法切换');
      wx.showToast({ title: '命盘ID错误', icon: 'none' });
      return;
    }
    
    console.log('🔄 切换命盘，目标ID:', id);
    this.setData({ selectedId: id });
    this.loadPalmtreeDetail(id);
    wx.showToast({ title: '命盘已切换', icon: 'none', duration: 1500 });
  },
  onCreate() {
    wx.navigateTo({ url: '/pages/palmtree/new' });
  },
  onEdit() {
    wx.showToast({ title: '编辑功能待实现', icon: 'none' });
  },
  onLongPress(e) {
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
    wx.showModal({ title: '喜用神原理', content: '喜用神是补益命局五行、调节阴阳平衡的关键。日主癸水弱，火旺克金，宜补水助金以平衡全局。', showCancel: false });
  },
  onSave() {
    const currentYear = new Date().getFullYear();
    
    const item = {
      id: `pt_${Date.now()}`,
      nickname: this.data.nickname || '未命名',
      year: this.data.wuyunliuqi?.year || currentYear,
      constitution_type: this.data.constitution || '未知',
      created_at: new Date().toISOString()
    };
    
    // 保存命盘列表
    wx.getStorage({ 
      key: 'palmtrees', 
      success: (res) => {
        const list = res.data || [];
        // 避免重复保存
        if (!list.find(p => p.nickname === item.nickname && p.year === item.year)) {
          list.push(item);
          wx.setStorage({ key: 'palmtrees', data: list });
          
          // 保存详细数据
          const detailData = {
            nickname: this.data.nickname,
            birth_year: this.data.wuyunliuqi?.year || currentYear,
            birth_month: 1,
            birth_day: 1,
            hour_index: 0,
            location_city: '未知城市',
            eight: this.data.eight,
            wuxing: this.data.wuxing,
            constitution: this.data.constitution,
            analysis: this.data.analysis,
            wuyunliuqi: this.data.wuyunliuqi
          };
          wx.setStorage({ key: `palmtree_${item.id}`, data: detailData });
          
          this.setData({ palmtrees: list, selectedId: item.id });
          wx.showToast({ title: '已保存', icon: 'success' });
        } else {
          wx.showToast({ title: '命盘已存在', icon: 'none' });
        }
      }, 
      fail: () => {
        const list = [item];
        wx.setStorage({ key: 'palmtrees', data: list });
        
        // 保存详细数据
        const detailData = {
          nickname: this.data.nickname,
          birth_year: this.data.wuyunliuqi?.year || currentYear,
          birth_month: 1,
          birth_day: 1,
          hour_index: 0,
          location_city: '未知城市',
          eight: this.data.eight,
          wuxing: this.data.wuxing,
          constitution: this.data.constitution,
          analysis: this.data.analysis,
          wuyunliuqi: this.data.wuyunliuqi
        };
        wx.setStorage({ key: `palmtree_${item.id}`, data: detailData });
        
        this.setData({ palmtrees: list, selectedId: item.id });
        wx.showToast({ title: '已保存', icon: 'success' });
      }
    });
  },
  onDetail() {
    wx.navigateTo({ url: '/pages/learn/index' });
  },
  onShareAppMessage() {
    return {
      title: `${this.data.nickname}的命盘报告`,
      path: '/pages/palmtree/index'
    };
  }
});
