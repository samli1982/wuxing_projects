/**
 * 认证工具类
 */

/**
 * 检查是否需要登录
 * @returns {boolean} 是否已登录
 */
function requireLogin() {
  // 获取会员信息
  const memberInfo = wx.getStorageSync('memberInfo');
  
  console.log('🔐 登录状态检查:');
  console.log('  memberInfo:', memberInfo);
  
  // 如果已经有会员信息，直接返回true
  if (memberInfo) {
    console.log('  ✅ 已登录');
    return true;
  }
  
  console.log('  ❌ 未登录');
  
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
}

module.exports = {
  requireLogin
}