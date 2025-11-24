import request from '@/utils/request'

/**
 * 文件管理 API
 */

// 文件分页列表
export function getFileList(params) {
  return request({
    url: '/file/list',
    method: 'get',
    params
  })
}

// 文件详情
export function getFileById(id) {
  return request({
    url: `/file/${id}`,
    method: 'get'
  })
}

// 上传文件
export function uploadFile(data) {
  return request({
    url: '/file/upload',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data
  })
}

// 删除文件
export function deleteFile(id) {
  return request({
    url: `/file/${id}`,
    method: 'delete'
  })
}
