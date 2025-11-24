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
    tabsConfig: {
      '木': ['木中木🌲', '木中火🔥', '木中土🪐', '木中金🟡', '木中水💧'],
      '火': ['火中木🌲', '火中火🔥', '火中土🪐', '火中金🟡', '火中水💧'],
      '土': ['土中木🌲', '土中火🔥', '土中土🪐', '土中金🟡', '土中水💧'],
      '金': ['金中木🌲', '金中火🔥', '金中土🪐', '金中金🟡', '金中水💧'],
      '水': ['水中木🌲', '水中火🔥', '水中土🪐', '水中金🟡', '水中水💧']
    }
  },
  onLoad() {
    this.loadHerbs();
    this.setData({ childTabs: this.data.tabsConfig['木'], activeSub: this.data.tabsConfig['木'][0] });
    this.filterList();
  },
  loadHerbs() {
    const mockData = [
      { id: 'mu-mu-1', number: '1-①', category: '木中木', categoryIcon: '🌲', name: '肉桂', alias: ['广紫桂'], properties: { taste: ['辛', '甘'], nature: '大热', natureClass: 'hot' }, effects: ['补火助阳', '引火归元'], is_collected: false },
      { id: 'mu-huo-1', number: '1-②', category: '木中火', categoryIcon: '🔥', name: '桂枝', alias: ['柳桂'], properties: { taste: ['辛', '甘'], nature: '温', natureClass: 'warm' }, effects: ['发汗解肌', '温通经脉'], is_collected: false },
      { id: 'mu-tu-1', number: '1-③', category: '木中土', categoryIcon: '🪐', name: '白术', alias: ['于术'], properties: { taste: ['苦', '甘'], nature: '温', natureClass: 'warm' }, effects: ['健脾益气', '燥湿利水'], is_collected: false }
    ];
    this.setData({ allHerbs: mockData });
  },
  switchParent(e) {
    const val = e.currentTarget.dataset.val;
    const childTabs = this.data.tabsConfig[val];
    this.setData({ activeParent: val, childTabs, activeSub: childTabs[0] });
    this.filterList();
  },
  switchSub(e) {
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
    this.setData({ showSearch: true });
  },
  onCloseSearch() {
    this.setData({ showSearch: false, searchKeyword: '', searchResults: [] });
  },
  onSearchInput(e) {
    const keyword = e.detail.value.trim();
    this.setData({ searchKeyword: keyword });
    if (keyword.length === 0) {
      this.setData({ searchResults: [] });
      return;
    }
    const results = this.data.allHerbs.filter(h => 
      h.name.includes(keyword) || 
      h.effects.some(ef => ef.includes(keyword)) ||
      h.properties.taste.some(t => t.includes(keyword)) ||
      h.properties.nature.includes(keyword)
    );
    this.setData({ searchResults: results });
  },
  onSearchConfirm() {
    if (this.data.searchResults.length > 0) {
      const first = this.data.searchResults[0];
      this.onCloseSearch();
      wx.navigateTo({ url: `/pages/herb/detail?id=${first.id}` });
    }
  },
  onSearchResultTap(e) {
    const id = e.currentTarget.dataset.id;
    this.onCloseSearch();
    wx.navigateTo({ url: `/pages/herb/detail?id=${id}` });
  },
  onCardTap(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/herb/detail?id=${id}` });
  },
  onToggleCollect(e) {
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
    this.setData({ activeParent: '木', activeSub: this.data.tabsConfig['木'][0] });
    this.filterList();
  },
  onShowTheory() {
    wx.showModal({
      title: '《辅行诀》原文',
      content: '以陰陽寒熱之氣為綱，以五藏虛實認識疾病，以氣味和合治療疾病。此乃阴阳五行配伍法则之纲领。',
      showCancel: false
    });
  }
});
