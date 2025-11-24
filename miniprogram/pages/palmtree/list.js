Page({
  data: {
    list: []
  },
  onLoad() {
    this.loadList();
  },
  onPullDownRefresh() {
    this.loadList();
    setTimeout(() => wx.stopPullDownRefresh(), 800);
  },
  loadList() {
    wx.getStorage({
      key: 'palmtrees',
      success: (res) => {
        const list = res.data || [];
        this.setData({ list });
      },
      fail: () => {
        this.setData({ list: [] });
      }
    });
  },
  onBack() {
    wx.switchTab({ url: '/pages/home/index' });
  },
  onCardTap(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/palmtree/result?id=${id}` });
  },
  onCreate() {
    wx.navigateTo({ url: '/pages/palmtree/new' });
  },
  onDelete(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认删除',
      content: '删除后将无法恢复，是否继续？',
      success: (res) => {
        if (res.confirm) {
          let list = this.data.list.filter(item => item.id !== id);
          this.setData({ list });
          wx.setStorage({ key: 'palmtrees', data: list });
          wx.showToast({ title: '已删除', icon: 'success' });
        }
      }
    });
  },
  onShare(e) {
    const id = e.currentTarget.dataset.id;
    const item = this.data.list.find(i => i.id === id);
    if (!item) return;
    wx.showActionSheet({
      itemList: ['生成海报分享', '复制链接'],
      success: (res) => {
        if (res.tapIndex === 0) {
          wx.showToast({ title: '海报生成中…', icon: 'loading' });
        } else if (res.tapIndex === 1) {
          wx.setClipboardData({ data: `/pages/palmtree/result?id=${id}` });
          wx.showToast({ title: '链接已复制', icon: 'success' });
        }
      }
    });
  }
});