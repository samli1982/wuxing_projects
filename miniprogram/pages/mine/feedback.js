Page({
  data: {
    feedbackTypes: ['功能建议', '问题报告', '内容优化', '其他'],
    selectedTypeIndex: 0,
    feedbackContent: '',
    contactInfo: ''
  },

  onTypeChange(e) {
    this.setData({ selectedTypeIndex: e.detail.value });
  },

  onContentChange(e) {
    this.setData({ feedbackContent: e.detail.value });
  },

  onContactChange(e) {
    this.setData({ contactInfo: e.detail.value });
  },

  onSubmit() {
    if (this.data.feedbackContent.length < 10) {
      wx.showToast({ title: '请输入至少 10 个字符', icon: 'none' });
      return;
    }

    wx.showToast({ title: '感谢你的反馈！', icon: 'success' });
    setTimeout(() => {
      this.setData({ feedbackContent: '', contactInfo: '' });
      wx.navigateBack();
    }, 1500);
  },

  onBack() {
    wx.navigateBack();
  }
});
