import request from '@/utils/request'

// 角色列表
export function getRoleList(params) {
  return request({
    url: '/role/list',
    method: 'get',
    params
  })
}

// 角色详情
export function getRoleDetail(id) {
  return request({
    url: `/role/${id}`,
    method: 'get'
  })
}

// 新增角色
export function createRole(data) {
  return request({
    url: '/role',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(id, data) {
  return request({
    url: `/role/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  })
}

// 批量删除角色
export function deleteBatchRole(ids) {
  return request({
    url: '/role/batch',
    method: 'delete',
    data: ids
  })
}
