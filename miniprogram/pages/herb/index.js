import * as herbApi from '../../api/herb.js';

Page({
  data: {
    activeParent: '木',
    activeSub: '',
    childTabs: [],
    allHerbs: [],
    list: [],
    showSearch: false,
    searchKeyword: '',
    searchResults: [],
    loading: false,
    tabsConfig: {
      '木': ['木中木🌲', '木中火🔥', '木中土🪐', '木中金🟡', '木中水💧'],
      '火': ['火中木🌲', '火中火🔥', '火中土🪐', '火中金🟡', '火中水💧'],
      '土': ['土中木🌲', '土中火🔥', '土中土🪐', '土中金🟡', '土中水💧'],
      '金': ['金中木🌲', '金中火🔥', '金中土🪐', '金中金🟡', '金中水💧'],
      '水': ['水中木🌲', '水中火🔥', '水中土🪐', '水中金🟡', '水中水💧']
    }
  },
  onLoad() {
    // 检查登录状态，未登录则跳转到登录页
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      // 如果未登录或用户取消登录，停止页面加载
      return;
    }
    
    // 加载数据
    console.log('登录已完成，直接加载数据...');
    this.loadHerbs();
    
    // 设置子选项卡
    this.setData({ childTabs: this.data.tabsConfig['木'], activeSub: this.data.tabsConfig['木'][0] });
  },
  onShow() {
    // 每次页面显示时都检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      // 如果未登录或用户取消登录，停止页面加载
      return;
    }
  },
  loadHerbs() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    this.setData({ loading: true });
    console.log('🔍 开始加载药精数据...');
    herbApi.getHerbByElement('木').then(data => {
      console.log('✅ 成功获取远程药精数据:', data);
      // 修正数据提取逻辑，从data属性中获取实际数据
      const formatted = this.formatHerbData(data);
      this.setData({ allHerbs: formatted }, () => {
        this.filterList();
      });
    }).catch(err => {
      console.warn('⚠️ 网络加载失败，使用本地数据:', err);
      // 如果加载失败，使用本地数据
      this.loadLocalHerbs();
    }).finally(() => {
      this.setData({ loading: false });
    });
  },
  loadLocalHerbs() {
    const mockData = [
      { id: 1, number: '1-①', category: '木中木', categoryIcon: '🌲', name: '肉桂', alias: '广紫桂', properties: '温', taste: '辛,甘', natureClass: 'hot', effects: '补火助阳,引火归元', is_collected: false },
      { id: 2, number: '1-②', category: '木中火', categoryIcon: '🔥', name: '桂枝', alias: '柳桂', properties: '温', taste: '辛,甘', natureClass: 'warm', effects: '发汗解肌,温通经脉', is_collected: false },
      { id: 3, number: '1-③', category: '木中土', categoryIcon: '🪐', name: '白术', alias: '于术', properties: '温', taste: '苦,甘', natureClass: 'warm', effects: '健脾益气,燥湿利水', is_collected: false }
    ];
    this.setData({ allHerbs: mockData });
    this.filterList();
  },
  formatHerbData(herbs) {
    // 添加参数校验，防止传入 undefined 或 null
    // 修正数据提取逻辑，从data属性中获取实际数据
    const actualHerbs = herbs?.data || herbs;
    
    if (!actualHerbs || !Array.isArray(actualHerbs)) {
      console.warn('formatHerbData received invalid data:', herbs);
      return [];
    }
    
    return actualHerbs.map(herb => ({
      id: herb.id,
      number: herb.number,
      category: herb.category,
      categoryIcon: herb.categoryIcon,
      name: herb.name,
      alias: herb.alias || '',
      properties: herb.properties || '',
      taste: herb.taste ? herb.taste.split(',') : [],
      nature: herb.properties || '',
      natureClass: herb.natureClass || 'warm',
      effects: herb.effects ? herb.effects.split(',') : [],
      is_collected: false
    }));
  },
  switchParent(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const val = e.currentTarget.dataset.val;
    const childTabs = this.data.tabsConfig[val];
    this.setData({ activeParent: val, childTabs, activeSub: childTabs[0] });
    // 加载新的五行数据
    herbApi.getHerbByElement(val).then(data => {
      // 修正数据提取逻辑，从data属性中获取实际数据
      const formatted = this.formatHerbData(data);
      this.setData({ allHerbs: formatted }, () => {
        this.filterList();
      });
    }).catch(err => {
      console.error('加载五行数据失败:', err);
      this.filterList();
    });
  },
  switchSub(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const val = e.currentTarget.dataset.val;
    this.setData({ activeSub: val });
    this.filterList();
  },
  filterList() {
    const cat = this.data.activeSub.replace(/[🌲🔥🪐🟡💧]/g, '');
    const list = this.data.allHerbs.filter(h => h.category === cat);
    this.setData({ list });
  },
  onBack() {
    wx.switchTab({ url: '/pages/home/index' });
  },
  onSearch() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    this.setData({ showSearch: true });
  },
  onCloseSearch() {
    this.setData({ showSearch: false, searchKeyword: '', searchResults: [] });
  },
  onSearchInput(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const keyword = e.detail.value.trim();
    this.setData({ searchKeyword: keyword });
    if (keyword.length === 0) {
      this.setData({ searchResults: [] });
      return;
    }
    // 调用云业 API 搜索
    herbApi.searchHerb(keyword).then(results => {
      // 修正数据提取逻辑，从data属性中获取实际数据
      const formatted = this.formatHerbData(results);
      this.setData({ searchResults: formatted });
    }).catch(err => {
      console.error('搜索失败:', err);
      // 模糊搜索本地数据
      const results = this.data.allHerbs.filter(h => 
        h.name.includes(keyword) || 
        h.alias.includes(keyword) ||
        (h.effects && h.effects.some(ef => ef.includes(keyword))) ||
        (h.taste && h.taste.some(t => t.includes(keyword))) ||
        (h.properties && h.properties.includes(keyword))
      );
      this.setData({ searchResults: results });
    });
  },
  onSearchConfirm() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    if (this.data.searchResults.length > 0) {
      const first = this.data.searchResults[0];
      this.onCloseSearch();
      wx.navigateTo({ url: `/pages/herb/detail?id=${first.id}` });
    }
  },
  onSearchResultTap(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const id = e.currentTarget.dataset.id;
    this.onCloseSearch();
    wx.navigateTo({ url: `/pages/herb/detail?id=${id}` });
  },
  onCardTap(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/herb/detail?id=${id}` });
  },
  onToggleCollect(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const id = e.currentTarget.dataset.id;
    wx.vibrateShort();
    const allHerbs = this.data.allHerbs.map(h => {
      if (h.id === id) h.is_collected = !h.is_collected;
      return h;
    });
    this.setData({ allHerbs });
    this.filterList();
    const item = allHerbs.find(h => h.id === id);
    wx.showToast({ title: item.is_collected ? '已收藏' : '已取消收藏', icon: 'none' });
  },
  onReset() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    this.setData({ activeParent: '木', activeSub: this.data.tabsConfig['木'][0] });
    this.filterList();
  },
  onShowTheory() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    wx.showModal({
      title: '《辅行诀》原文',
      content: '以陰陽寒熱之氣為綱，以五藏虛實認識疾病，以氣味和合治療疾病。此乃阴阳五行配伍法则之纲领。',
      showCancel: false
    });
  }
});