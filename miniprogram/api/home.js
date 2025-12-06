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
 * 首页API接口
 */
export const homeApi = {
  // 获取首页信息
  getHomeInfo() {
    return request({
      url: '/api/home/info',
      method: 'GET'
    });
  },

  // 获取命盘列表
  getPalmtreeList() {
    return request({
      url: '/api/home/palmtrees',
      method: 'GET'
    });
  },

  // 获取今日养生建议
  getTodayAdvice() {
    return request({
      url: '/api/home/advice/today',
      method: 'GET'
    });
  },

  // 获取干支历信息
  getGanzhiCalendar() {
    return request({
      url: '/api/home/ganzhi',
      method: 'GET'
    });
  }
};

/**
 * 获取当前日期的节气信息
 */
export const getSolarTermInfo = (date) => {
  const params = date ? { date } : {};
  return request({
    url: '/api/home/solar-term',
    method: 'GET',
    data: params
  });
};

/**
 * 获取农历信息
 */
export const getLunarInfo = (date) => {
  const params = date ? { date } : {};
  return request({
    url: '/api/home/lunar',
    method: 'GET',
    data: params
  });
};

/**
 * 获取日出日落时间
 */
export const getSunriseSunset = (date, latitude, longitude) => {
  const data = {
    date: date || new Date().toISOString().split('T')[0],
    latitude: latitude || 0,
    longitude: longitude || 0
  };
  return request({
    url: '/api/home/sunrise-sunset',
    method: 'GET',
    data: data
  });
};

/**
 * 获取北斗信息（斗柄方向等）
 */
export const getBeidouInfo = (date) => {
  const params = date ? { date } : {};
  return request({
    url: '/api/home/beidou',
    method: 'GET',
    data: params
  });
};

/**
 * 获取今日养生建议
 */
export const getDailyHealthAdvice = (date) => {
  const params = date ? { date } : {};
  return request({
    url: '/api/home/health-advice',
    method: 'GET',
    data: params
  });
};

/**
 * 获取干支历信息
 */
export const getSixtiesCycleInfo = (dateTime, retryCount = 0) => {
  // 优先使用 date 参数，如果是完整的 dateTime 也支持
  const params = dateTime ? { date: dateTime } : {};
  return request({
    url: '/api/home/sixties-cycle',
    method: 'GET',
    data: params
  });
};

/**
 * 获取皇帝纪年历入口
 */
export const getEmperorCalendarEntry = () => {
  return request({
    url: '/api/home/emperor-calendar',
    method: 'GET'
  });
};

/**
 * 获取干支纪年历的宜忌信息
 */
export const getEmperorCalendarYiJi = (date, retryCount = 0) => {
  const params = date ? { date } : {};
  return request({
    url: '/api/home/emperor-calendar/yi-ji',
    method: 'GET',
    data: params
  });
};

/**
 * 获取干支纪年历的详细信息
 */
export const getEmperorCalendarDetail = (date, retryCount = 0) => {
  const params = date ? { date } : {};
  return request({
    url: '/api/home/emperor-calendar/detail',
    method: 'GET',
    data: params
  });
};

/**
 * 获取时辰吉凶信息
 */
export const getShiChenInfo = (date, retryCount = 0) => {
  const params = date ? { date } : {};
  return request({
    url: '/api/home/emperor-calendar/shichen',
    method: 'GET',
    data: params
  });
};

/**
 * 获取干支日期详细信息（包含建除十二值神、十二神、星宿）
 */
export const getSixtyCycleDayInfo = (date, retryCount = 0) => {
  const params = date ? { date } : {};
  return request({
    url: '/api/home/sixties-cycle/day-info',
    method: 'GET',
    data: params
  });
};