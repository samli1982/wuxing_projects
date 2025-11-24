import request from '@/utils/request'

/**
 * 字典管理 API
 */

// 字典分页列表
export function getDictList(params) {
  return request({
    url: '/dictionary/list',
    method: 'get',
    params
  })
}

// 根据类型获取字典
export function getDictByType(dictType) {
  return request({
    url: `/dictionary/type/${dictType}`,
    method: 'get'
  })
}

// 获取所有字典类型
export function getDictTypes() {
  return request({
    url: '/dictionary/types',
    method: 'get'
  })
}

// 获取字典Map
export function getDictMap() {
  return request({
    url: '/dictionary/map',
    method: 'get'
  })
}

// 字典详情
export function getDictById(id) {
  return request({
    url: `/dictionary/${id}`,
    method: 'get'
  })
}

// 新增字典
export function addDict(data) {
  return request({
    url: '/dictionary',
    method: 'post',
    data
  })
}

// 更新字典
export function updateDict(id, data) {
  return request({
    url: `/dictionary/${id}`,
    method: 'put',
    data
  })
}

// 删除字典
export function deleteDict(id) {
  return request({
    url: `/dictionary/${id}`,
    method: 'delete'
  })
}

// 批量删除字典
export function deleteBatchDict(ids) {
  return request({
    url: '/dictionary/batch',
    method: 'delete',
    data: ids
  })
}
