import request from '@/utils/request'

/**
 * 操作日志 API
 */

// 操作日志分页列表
export function getLogList(params) {
  return request({
    url: '/log/operation/list',
    method: 'get',
    params
  })
}

// 操作日志详情
export function getLogById(id) {
  return request({
    url: `/log/operation/${id}`,
    method: 'get'
  })
}

// 删除操作日志
export function deleteLog(id) {
  return request({
    url: `/log/operation/${id}`,
    method: 'delete'
  })
}
