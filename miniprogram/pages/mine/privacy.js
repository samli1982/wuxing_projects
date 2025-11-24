Page({
  data: {
    dataEncryption: true,
    anonymousAnalytics: false,
    cloudSync: true
  },

  onLoad() {
    this.loadSettings();
  },

  loadSettings() {
    wx.getStorage({
      key: 'privacy_settings',
      success: (res) => {
        if (res.data) {
          this.setData(res.data);
        }
      }
    });
  },

  onToggleEncryption() {
    this.setData({ dataEncryption: !this.data.dataEncryption });
    this.saveSettings();
  },

  onToggleAnalytics() {
    this.setData({ anonymousAnalytics: !this.data.anonymousAnalytics });
    this.saveSettings();
  },

  onToggleSync() {
    this.setData({ cloudSync: !this.data.cloudSync });
    this.saveSettings();
  },

  saveSettings() {
    wx.setStorage({
      key: 'privacy_settings',
      data: {
        dataEncryption: this.data.dataEncryption,
        anonymousAnalytics: this.data.anonymousAnalytics,
        cloudSync: this.data.cloudSync
      }
    });
  },

  onBack() {
    wx.navigateBack();
  }
});
