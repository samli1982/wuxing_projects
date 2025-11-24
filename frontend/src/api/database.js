import request from '@/utils/request'

/**
 * 数据库查询 API
 */

// 获取所有表名
export function getAllTables() {
  return request({
    url: '/database/tables',
    method: 'get'
  })
}

// 获取表结构
export function getTableStructure(tableName) {
  return request({
    url: `/database/tables/${tableName}/structure`,
    method: 'get'
  })
}

// 获取表统计信息
export function getTableStats(tableName) {
  return request({
    url: `/database/tables/${tableName}/stats`,
    method: 'get'
  })
}

// 执行SQL查询
export function executeQuery(data) {
  return request({
    url: '/database/query',
    method: 'post',
    params: data
  })
}
