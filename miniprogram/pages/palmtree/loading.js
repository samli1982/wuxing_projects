Page({
  data: {
    startTime: 0
  },
  onLoad() {
    // 检查登录状态，未登录则跳转到登录页
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      // 如果未登录或用户取消登录，停止页面加载
      return;
    }
    
    const start = Date.now();
    this.setData({ startTime: start });
    // 调用后端 API 保存命盘
    this.savePalmtree();
  },
  onShow() {
    // 每次页面显示时都检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      // 如果未登录或用户取消登录，停止页面加载
      return;
    }
  },
  savePalmtree() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    // 从本地存储获取最新的命盘数据
    wx.getStorage({ 
      key: 'palmtree_latest', 
      success: (res) => {
        const formData = res.data;
        
        // 构建提交数据
        const submitData = {
          nickname: formData.nickname,
          name: formData.name,
          gender: formData.gender,
          birth_year: formData.birth_year,
          birth_month: formData.birth_month,
          birth_day: formData.birth_day,
          birth_hour_type: formData.birth_hour_type,
          hour_index: formData.hour_index,
          location_city: formData.location_city,
          location_lat: formData.location_lat,
          location_lng: formData.location_lng,
          calendar_type: formData.calendar_type,
          for_health_analysis: formData.for_health_analysis ? 1 : 0
        };
        
        // 调用后端 API 保存命盘
        const request = require('../../utils/request.js');
        request.post('/palmtree/save', submitData).then((res) => {
          const elapsed = Date.now() - this.data.startTime;
          if (elapsed < 1500) {
            // 很快返回，直接进入结果页，避免闪屏
            wx.redirectTo({ url: '/pages/palmtree/result' });
          } else if (elapsed >= 1500 && elapsed < 2500) {
            // 显示至少 2.5s
            const wait = 2500 - elapsed;
            setTimeout(() => {
              // 检查登录状态
              const { requireLogin } = require('../../utils/auth.js');
              if (!requireLogin()) {
                return;
              }
              wx.redirectTo({ url: '/pages/palmtree/result' });
            }, wait);
          } else {
            // 超过 2.5s，立即跳转
            wx.redirectTo({ url: '/pages/palmtree/result' });
          }
        }).catch((err) => {
          console.error('保存命盘失败:', err);
          wx.showToast({ title: '保存失败，请稍后重试', icon: 'none' });
          setTimeout(() => {
            wx.navigateBack();
          }, 2000);
        });
      },
      fail: () => {
        wx.showToast({ title: '无可保存的命盘数据', icon: 'none' });
        setTimeout(() => {
          wx.navigateBack();
        }, 2000);
      }
    });
  }
});