Page({
  data: {
    userInfo: {
      nickname: '子时居士',
      avatar_color: '#E63946'
    }
  },

  onLoad() {
    this.loadUserInfo();
  },

  loadUserInfo() {
    wx.getStorage({
      key: 'user_info',
      success: (res) => {
        if (res.data) {
          this.setData({ userInfo: res.data });
        }
      }
    });
  },

  onNicknameChange(e) {
    this.setData({ 'userInfo.nickname': e.detail.value });
  },

  onColorSelect(e) {
    const color = e.currentTarget.dataset.color;
    this.setData({ 'userInfo.avatar_color': color });
  },

  onSave() {
    wx.setStorage({
      key: 'user_info',
      data: this.data.userInfo,
      success: () => {
        wx.showToast({ title: '保存成功', icon: 'success' });
        setTimeout(() => wx.navigateBack(), 1500);
      }
    });
  },

  onBack() {
    wx.navigateBack();
  }
});
