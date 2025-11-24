Page({
  data: {
    cloudBackupEnabled: false
  },

  onLoad() {
    this.loadSettings();
  },

  loadSettings() {
    wx.getStorage({
      key: 'backup_settings',
      success: (res) => {
        if (res.data) {
          this.setData({ cloudBackupEnabled: res.data.cloudBackupEnabled });
        }
      }
    });
  },

  onExportJSON() {
    wx.showToast({ title: 'JSON 导出功能待实现', icon: 'none' });
  },

  onExportCSV() {
    wx.showToast({ title: 'CSV 导出功能待实现', icon: 'none' });
  },

  onCloudBackup() {
    this.setData({ cloudBackupEnabled: !this.data.cloudBackupEnabled });
    wx.setStorage({
      key: 'backup_settings',
      data: { cloudBackupEnabled: this.data.cloudBackupEnabled }
    });
    wx.showToast({ 
      title: this.data.cloudBackupEnabled ? '云端备份已启用' : '云端备份已禁用', 
      icon: 'none' 
    });
  },

  onBack() {
    wx.navigateBack();
  }
});
