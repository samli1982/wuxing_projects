import request from '@/utils/request'

/**
 * 会员管理API
 */

// 获取会员列表
export function getMemberList(params) {
  return request({
    url: '/member/list',
    method: 'get',
    params
  })
}

// 更新会员信息
export function updateMember(data) {
  return request({
    url: '/member/update',
    method: 'put',
    data
  })
}

// 获取会员详情
export function getMemberById(id) {
  return request({
    url: `/member/${id}`,
    method: 'get'
  })
}