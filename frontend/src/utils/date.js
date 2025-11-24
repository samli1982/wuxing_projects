/**
 * 日期时间格式化工具
 */

/**
 * 格式化日期时间
 * @param {string|Date} dateTime - 日期时间字符串或Date对象
 * @param {string} format - 格式化模板，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的日期时间字符串
 */
export function formatDateTime(dateTime, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!dateTime) return '-'
  
  const date = new Date(dateTime)
  
  // 检查日期是否有效
  if (isNaN(date.getTime())) return '-'
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化日期（不含时间）
 * @param {string|Date} dateTime - 日期时间字符串或Date对象
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(dateTime) {
  return formatDateTime(dateTime, 'YYYY-MM-DD')
}

/**
 * 格式化时间（不含日期）
 * @param {string|Date} dateTime - 日期时间字符串或Date对象
 * @returns {string} 格式化后的时间字符串
 */
export function formatTime(dateTime) {
  return formatDateTime(dateTime, 'HH:mm:ss')
}

/**
 * 格式化为友好的时间显示
 * @param {string|Date} dateTime - 日期时间字符串或Date对象
 * @returns {string} 友好的时间显示
 */
export function formatRelativeTime(dateTime) {
  if (!dateTime) return '-'
  
  const date = new Date(dateTime)
  if (isNaN(date.getTime())) return '-'
  
  const now = new Date()
  const diff = now - date
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  
  if (seconds < 60) {
    return '刚刚'
  } else if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return formatDateTime(dateTime, 'YYYY-MM-DD HH:mm')
  }
}
