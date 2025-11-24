import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo(userId) {
  return request({
    url: '/user/info',
    method: 'get',
    params: { userId }
  })
}

// 更新用户信息
export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

// 获取用户列表（这个接口需要后端添加）
export function getUserList(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

// 获取用户的角色列表
export function getUserRoles(userId) {
  return request({
    url: `/user/role/${userId}`,
    method: 'get'
  })
}

// 为用户分配角色
export function assignRolesToUser(userId, roleIds) {
  return request({
    url: `/user/role/${userId}`,
    method: 'post',
    data: roleIds
  })
}
