import request from '@/utils/request'

/**
 * 命盘管理API
 */

// 获取用户的命盘列表（分页）
export function getPalmtreesByMember(memberId, params) {
  return request({
    url: `/palmtree/member/${memberId}/page`,
    method: 'get',
    params: {
      current: params.pageNum,
      size: params.pageSize,
      keyword: params.keyword,
      gender: params.gender
    }
  })
}

// 获取命盘详情
export function getPalmtreeDetail(palmtreeId) {
  return request({
    url: `/palmtree/${palmtreeId}`,
    method: 'get'
  })
}

// 删除命盘
export function deletePalmtree(palmtreeId) {
  return request({
    url: `/palmtree/${palmtreeId}`,
    method: 'delete'
  })
}

// 批量删除命盘
export function deletePalmtreesBatch(ids) {
  return request({
    url: '/palmtree/batch',
    method: 'delete',
    data: { ids }
  })
}

// 更新命盘信息
export function updatePalmtree(id, data) {
  return request({
    url: `/palmtree/${id}`,
    method: 'put',
    data
  })
}

// 批量重新计算命盘
export function recalculatePalmtrees(ids) {
  return request({
    url: '/palmtree/recalculate',
    method: 'post',
    data: { ids }
  })
}
