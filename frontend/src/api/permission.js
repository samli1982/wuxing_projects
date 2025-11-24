import request from '@/utils/request'

// 权限列表
export function getPermissionList(params) {
  return request({
    url: '/permission/list',
    method: 'get',
    params
  })
}

// 权限树
export function getPermissionTree() {
  return request({
    url: '/permission/tree',
    method: 'get'
  })
}

// 权限详情
export function getPermissionDetail(id) {
  return request({
    url: `/permission/${id}`,
    method: 'get'
  })
}

// 新增权限
export function createPermission(data) {
  return request({
    url: '/permission',
    method: 'post',
    data
  })
}

// 更新权限
export function updatePermission(id, data) {
  return request({
    url: `/permission/${id}`,
    method: 'put',
    data
  })
}

// 删除权限
export function deletePermission(id) {
  return request({
    url: `/permission/${id}`,
    method: 'delete'
  })
}

// 获取角色的权限列表
export function getRolePermissions(roleId) {
  return request({
    url: `/permission/role/${roleId}`,
    method: 'get'
  })
}

// 为角色分配权限
export function assignPermissionsToRole(roleId, permissionIds) {
  return request({
    url: `/permission/role/${roleId}`,
    method: 'post',
    data: permissionIds
  })
}

// 获取当前用户的权限
export function getCurrentUserPermissions() {
  return request({
    url: '/permission/current',
    method: 'get'
  })
}
