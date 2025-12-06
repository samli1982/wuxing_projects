/**
 * 封装微信请求函数
 */

// 服务器地址 - 使用实际IP地址而不是localhost
const BASE_URL = 'http://localhost:8080'  // 强够徒彿情况下使用 localhost

/**
 * 请求函数
 * @param {Object} options 请求参数
 */
function request(options) {
  // 获取token
  const token = wx.getStorageSync('token')
  
  console.log('🔧 请求调试信息:');
  console.log('  URL:', options.url);
  console.log('  Method:', options.method);
  console.log('  Data:', options.data);
  console.log('  Token:', token ? '存在' : '不存在');
  
  // 设置默认参数
  const defaultOptions = {
    url: '',
    method: 'GET',
    data: {},
    header: {
      'Content-Type': 'application/json'
    },
    success: () => {},
    fail: () => {},
    complete: () => {}
  }
  
  // 合并参数
  const mergedOptions = Object.assign({}, defaultOptions, options)
  
  // 添加token到请求头
  if (token) {
    mergedOptions.header['Authorization'] = `Bearer ${token}`
  }
  
  // 完整URL
  if (!mergedOptions.url.startsWith('http')) {
    // 自动添加/api前缀（如果还没有）
    if (!mergedOptions.url.startsWith('/api')) {
      mergedOptions.url = '/api' + mergedOptions.url
    }
    mergedOptions.url = BASE_URL + mergedOptions.url
  }
  
  console.log('🔍 发起请求:', mergedOptions.url, mergedOptions.method, mergedOptions.data);
  
  // 发起请求
  return new Promise((resolve, reject) => {
    wx.request({
      ...mergedOptions,
      success: (res) => {
        console.log('✅ 请求成功:', res.statusCode, res.data);
        // 处理响应
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data)
          } else if (res.data.code === 401) {
            // Token过期或无效，清除本地存储并跳转到登录页
            wx.removeStorageSync('token')
            wx.removeStorageSync('memberInfo')
            wx.showToast({
              title: '登录已过期，请重新登录',
              icon: 'none'
            })
            // 跳转到登录页
            wx.redirectTo({
              url: '/pages/login/index'
            })
            reject(res.data)
          } else {
            // 业务错误
            wx.showToast({
              title: res.data.message || '请求失败',
              icon: 'none'
            })
            reject(res.data)
          }
        } else if (res.statusCode === 401) {
          // HTTP 401错误，Token过期或无效
          wx.removeStorageSync('token')
          wx.removeStorageSync('memberInfo')
          wx.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none'
          })
          // 跳转到登录页
          wx.redirectTo({
            url: '/pages/login/index'
          })
          reject(res)
        } else {
          // HTTP错误
          console.error('❌ HTTP错误:', res.statusCode, res);
          wx.showToast({
            title: '网络请求失败(' + res.statusCode + ')',
            icon: 'none'
          })
          reject(res)
        }
      },
      fail: (err) => {
        // 网络错误
        console.error('❌ 网络请求失败:', err);
        wx.showToast({
          title: '网络连接失败，请检查网络设置',
          icon: 'none'
        })
        reject(err)
      },
      complete: mergedOptions.complete
    })
  })
}

module.exports = {
  request,
  post: (url, data) => request({ url, method: 'POST', data }),
  get: (url, data) => request({ url, method: 'GET', data }),
  put: (url, data) => request({ url, method: 'PUT', data }),
  delete: (url, data) => request({ url, method: 'DELETE', data }),
  BASE_URL
}