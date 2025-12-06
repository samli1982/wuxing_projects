App({
  globalData: {
    memberInfo: null,
    loginDone: false
  },

  onLaunch() {
    // 应用启动时检查登录状态
    this.checkLoginStatus();
  },

  checkLoginStatus() {
    // 检查是否已有会员信息
    const memberInfo = wx.getStorageSync('memberInfo');
    if (memberInfo) {
      console.log('✅ 已有会员信息，已登录');
      this.globalData.memberInfo = memberInfo;
      this.globalData.loginDone = true;
    } else {
      console.log('ℹ️ 未登录或游客模式');
      this.globalData.memberInfo = null;
      this.globalData.loginDone = true;
    }
  },

  // 检查是否需要登录
  requireLogin() {
    // 每次都检查存储中的会员信息，而不是使用全局变量
    const memberInfo = wx.getStorageSync('memberInfo');
    
    // 如果已经有会员信息，直接返回true
    if (memberInfo) {
      return true;
    }
    
    // 如果没有会员信息，显示模态框提示用户登录
    wx.showModal({
      title: '提示',
      content: '该功能需要登录后使用，是否前往登录？',
      success(res) {
        if (res.confirm) {
          wx.navigateTo({
            url: '/pages/login/index'
          })
        } else {
          // 用户点击取消，跳转到首页
          wx.switchTab({
            url: '/pages/home/index'
          })
        }
      }
    })
    
    // 无论用户选择什么，都返回false表示当前未登录
    return false;
  },

  // 退出登录
  logout() {
    // 清除会员信息
    wx.removeStorageSync('memberInfo');
    this.globalData.memberInfo = null;
    
    // 跳转到登录页
    wx.redirectTo({
      url: '/pages/login/index'
    })
  }
})