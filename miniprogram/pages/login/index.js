Page({
  data: {
    needPhoneAuth: true  // 是否需要手机号授权
  },

  onLoad() {
    console.log('登录页面加载完成');
    this.checkIfNeedPhoneAuth();
  },

  // 检查是否需要手机号授权
  checkIfNeedPhoneAuth() {
    const { request } = require('../../utils/request.js');
    
    // 先获取登录凭证
    wx.login({
      success: (res) => {
        if (res.code) {
          // 调用后端检查接口，查询该openid对应的会员是否已有手机号
          request({
            url: '/api/app/auth/check-phone',
            method: 'POST',
            data: { code: res.code }
          }).then(checkRes => {
            // 如果已有手机号，则不需要再次授权
            this.setData({
              needPhoneAuth: !checkRes.data.hasPhone
            });
            console.log('是否需要手机授权:', !checkRes.data.hasPhone);
          }).catch(err => {
            console.log('检查手机号失败，默认需要授权');
            this.setData({ needPhoneAuth: true });
          });
        }
      }
    });
  },

  // 快速登录（无需手机号授权）
  onQuickLogin() {
    console.log('快速登录');
    
    wx.login({
      success: (res) => {
        if (res.code) {
          const { request } = require('../../utils/request.js');
          
          request({
            url: '/api/app/auth/login',
            method: 'POST',
            data: { code: res.code }
          }).then(loginRes => {
            console.log('快速登录成功:', loginRes);
            
            wx.setStorageSync('token', loginRes.data.token);
            wx.setStorageSync('memberInfo', loginRes.data.memberInfo);
            
            wx.showToast({
              title: '登录成功',
              icon: 'success'
            });
            
            setTimeout(() => {
              wx.switchTab({ url: '/pages/home/index' });
            }, 1500);
          }).catch(err => {
            console.error('快速登录失败', err);
            wx.showToast({
              title: err.message || '登录失败',
              icon: 'none'
            });
          });
        }
      }
    });
  },

  // 手机授权登录（首次登录）
  onPhoneAuthLogin(e) {
    console.log('手机授权回调:', e);
    
    // 检查是否授权成功
    if (e.detail.errMsg !== 'getPhoneNumber:ok') {
      console.log('用户拒绝授权手机号');
      wx.showToast({
        title: '需要授权手机号才能登录',
        icon: 'none'
      });
      return;
    }
    
    // 获取微信登录凭证
    wx.login({
      success: (res) => {
        if (res.code) {
          console.log('获取微信登录凭证成功:', res.code);
          console.log('手机号加密数据:', e.detail.encryptedData);
          console.log('手机号iv:', e.detail.iv);
          
          // 引入请求工具
          const { request } = require('../../utils/request.js');
          
          // 发送登录请求到后端
          request({
            url: '/api/app/auth/login',
            method: 'POST',
            data: {
              code: res.code,
              encryptedData: e.detail.encryptedData,
              iv: e.detail.iv
            }
          }).then(loginRes => {
            console.log('登录请求成功:', loginRes);
            
            // 保存token和会员信息
            wx.setStorageSync('token', loginRes.data.token);
            wx.setStorageSync('memberInfo', loginRes.data.memberInfo);
            
            // 登录成功提示
            wx.showToast({
              title: '登录成功',
              icon: 'success'
            });
            
            // 延迟跳转到首页
            setTimeout(() => {
              wx.switchTab({
                url: '/pages/home/index'
              });
            }, 1500);
          }).catch(err => {
            console.error('登录请求失败', err);
            wx.showToast({
              title: err.message || '登录失败',
              icon: 'none'
            });
          });
        } else {
          console.log('登录失败！' + res.errMsg);
          wx.showToast({
            title: '登录失败，请重试',
            icon: 'none'
          });
        }
      },
      fail: (err) => {
        console.error('微信登录失败', err);
        wx.showToast({
          title: '登录失败，请重试',
          icon: 'none'
        });
      }
    });
  },

  // 游客模式
  onGuestMode() {
    // 清除可能存在的token和用户信息
    wx.removeStorageSync('token');
    wx.removeStorageSync('memberInfo');
    
    // 跳转到首页
    wx.switchTab({
      url: '/pages/home/index'
    });
  }
});