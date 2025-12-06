const { request } = require('../utils/request.js');

/**
 * 五运六气API
 */

/**
 * 计算指定年份的五运六气
 */
export const getWuyunByYear = (year) => {
  return request({
    url: `/api/wuyun/${year}/complete`,
    method: 'GET'
  });
};

/**
 * 批量计算多个年份的五运六气
 */
export const getWuyunRange = (startYear, endYear) => {
  return request({
    url: '/api/wuyun/range',
    method: 'GET',
    data: { startYear, endYear }
  });
};

/**
 * 获取年份的天干地支
 */
export const getGanzhi = (year) => {
  return request({
    url: `/api/wuyun/${year}/ganzhi`,
    method: 'GET'
  });
};

/**
 * 获取年份的五运
 */
export const getWuyun = (year) => {
  return request({
    url: `/api/wuyun/${year}/wuyun`,
    method: 'GET'
  });
};

/**
 * 获取年份的司天和在泉
 */
export const getSiTianZaiQuan = (year) => {
  return request({
    url: `/api/wuyun/${year}/sitian-zaiquan`,
    method: 'GET'
  });
};

/**
 * 获取年份的健康建议
 */
export const getHealthTips = (year) => {
  return request({
    url: `/api/wuyun/${year}/health-tips`,
    method: 'GET'
  });
};

// 五运六气API接口
export const wuyunApi = {
  // 获取指定年份的五运六气数据
  getWuyunData(year) {
    return request({
      url: `/api/wuyun/${year}`,
      method: 'GET'
    });
  },

  // 获取当年的五运六气数据
  getCurrentWuyunData() {
    return request({
      url: '/api/wuyun/current',
      method: 'GET'
    });
  }
};