Page({
  data: {
    activeTab: 'courses',
    favoritesCourses: [
      { id: 1, title: '五运六气详解', category: '基础理论' },
      { id: 2, title: '药精分类入门', category: '药精解读' }
    ],
    favoritesHerbs: [
      { id: 1, name: '人参', property: '温性补气' },
      { id: 2, name: '黄芪', property: '温性补气' }
    ],
    favoritePalmtrees: [
      { id: 1, nickname: '张三', year: 1990, constitution: '阴虚质' }
    ]
  },

  onTabChange(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab });
  },

  onBack() {
    wx.navigateBack();
  }
});
