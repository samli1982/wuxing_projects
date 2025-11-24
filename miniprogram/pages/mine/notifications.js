Page({
  data: {
    solarTermNotification: true,
    yearlyWarning: true,
    learningReminder: false,
    therapyTips: true
  },

  onLoad() {
    this.loadSettings();
  },

  loadSettings() {
    wx.getStorage({
      key: 'notification_settings',
      success: (res) => {
        if (res.data) {
          this.setData(res.data);
        }
      }
    });
  },

  onToggleSolarTerm() {
    this.setData({ solarTermNotification: !this.data.solarTermNotification });
    this.saveSettings();
  },

  onToggleYearlyWarning() {
    this.setData({ yearlyWarning: !this.data.yearlyWarning });
    this.saveSettings();
  },

  onToggleLearningReminder() {
    this.setData({ learningReminder: !this.data.learningReminder });
    this.saveSettings();
  },

  onToggleTherapyTips() {
    this.setData({ therapyTips: !this.data.therapyTips });
    this.saveSettings();
  },

  saveSettings() {
    wx.setStorage({
      key: 'notification_settings',
      data: {
        solarTermNotification: this.data.solarTermNotification,
        yearlyWarning: this.data.yearlyWarning,
        learningReminder: this.data.learningReminder,
        therapyTips: this.data.therapyTips
      }
    });
  },

  onBack() {
    wx.navigateBack();
  }
});
