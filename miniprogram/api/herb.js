const { request } = require('../utils/request.js');

/**
 * 获取请求头（包含token）
 */
function getHeaders() {
  const headers = {};
  try {
    const token = wx.getStorageSync('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
  } catch (e) {
    console.error('获取token失败:', e);
  }
  return headers;
}

/**
 * 药精API
 */

/**
 * 分页查询药精
 */
export const getHerbList = (params) => {
  return request({
    url: '/api/herb/list',
    method: 'GET',
    data: params
  });
};

/**
 * 根据五行分类查询
 */
export const getHerbByElement = (element) => {
  return request({
    url: `/api/herb/element/${element}`,
    method: 'GET'
  });
};

/**
 * 根据子分类查询
 */
export const getHerbByCategory = (category) => {
  return request({
    url: `/api/herb/category/${category}`,
    method: 'GET'
  });
};

/**
 * 搜索药精
 */
export const searchHerb = (keyword) => {
  return request({
    url: '/api/herb/search',
    method: 'GET',
    data: { keyword }
  });
};

/**
 * 获取药精详情
 */
export const getHerbDetail = (id) => {
  return request({
    url: `/api/herb/${id}`,
    method: 'GET'
  });
};