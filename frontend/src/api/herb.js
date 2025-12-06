import request from '@/utils/request'

// 获取药精列表
export function getHerbList(params) {
  return request({
    url: '/herb/list',
    method: 'get',
    params
  })
}

// 获取药精详情
export function getHerbDetail(id) {
  return request({
    url: `/herb/${id}`,
    method: 'get'
  })
}

// 新增药精
export function addHerb(data) {
  return request({
    url: '/herb',
    method: 'post',
    data
  })
}

// 编辑药精
export function editHerb(id, data) {
  return request({
    url: `/herb/${id}`,
    method: 'put',
    data
  })
}

// 删除药精
export function deleteHerb(id) {
  return request({
    url: `/herb/${id}`,
    method: 'delete'
  })
}

// 批量删除药精
export function batchDeleteHerb(ids) {
  return request({
    url: '/herb/batch',
    method: 'delete',
    data: ids
  })
}
