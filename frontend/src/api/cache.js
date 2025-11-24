import request from '@/utils/request'

/**
 * 缓存管理 API
 */

// 获取缓存键列表
export function getCacheKeys(pattern) {
  return request({
    url: '/cache/keys',
    method: 'get',
    params: { pattern }
  })
}

// 获取缓存详情
export function getCacheInfo(key) {
  return request({
    url: `/cache/${encodeURIComponent(key)}`,
    method: 'get'
  })
}

// 删除缓存
export function deleteCache(key) {
  return request({
    url: `/cache/${encodeURIComponent(key)}`,
    method: 'delete'
  })
}

// 批量删除缓存
export function deleteCaches(keys) {
  return request({
    url: '/cache/batch',
    method: 'delete',
    data: keys
  })
}

// 清空所有缓存
export function clearAllCache() {
  return request({
    url: '/cache/all',
    method: 'delete'
  })
}

// 获取Redis信息
export function getRedisInfo() {
  return request({
    url: '/cache/info',
    method: 'get'
  })
}
