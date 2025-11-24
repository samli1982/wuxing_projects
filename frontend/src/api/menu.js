import request from '@/utils/request'

/**
 * 菜单管理 API
 */

// 获取当前用户菜单树
export function getMenuTree() {
  return request({
    url: '/menu/tree',
    method: 'get'
  })
}

// 获取所有菜单树（管理用）
export function getAllMenuTree() {
  return request({
    url: '/menu/tree/all',
    method: 'get'
  })
}

// 获取权限列表（使用Permission接口）
export function getPermissionList(params) {
  return request({
    url: '/permission/list',
    method: 'get',
    params
  })
}

// 新增权限/菜单
export function addPermission(data) {
  return request({
    url: '/permission',
    method: 'post',
    data
  })
}

// 更新权限/菜单
export function updatePermission(id, data) {
  return request({
    url: `/permission/${id}`,
    method: 'put',
    data
  })
}

// 删除权限/菜单
export function deletePermission(id) {
  return request({
    url: `/permission/${id}`,
    method: 'delete'
  })
}

// 获取权限详情
export function getPermissionById(id) {
  return request({
    url: `/permission/${id}`,
    method: 'get'
  })
}
