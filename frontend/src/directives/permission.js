import { usePermissionStore } from '@/stores/permission'

/**
 * 权限指令
 * 用法：
 * v-permission="'system:user:add'" - 单个权限
 * v-permission="['system:user:add', 'system:user:edit']" - 多个权限（拥有任一即可）
 */
export default {
  mounted(el, binding) {
    const { value } = binding
    const permissionStore = usePermissionStore()
    
    if (!value) return
    
    let hasPermission = false
    
    if (typeof value === 'string') {
      // 单个权限
      hasPermission = permissionStore.hasPermission(value)
    } else if (Array.isArray(value)) {
      // 多个权限，拥有任一即可
      hasPermission = permissionStore.hasAnyPermission(value)
    }
    
    if (!hasPermission) {
      // 没有权限，移除元素
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}

/**
 * 权限指令（需要全部权限）
 * 用法：
 * v-permission-all="['system:user:add', 'system:user:edit']"
 */
export const permissionAll = {
  mounted(el, binding) {
    const { value } = binding
    const permissionStore = usePermissionStore()
    
    if (!value) return
    
    const hasPermission = Array.isArray(value)
      ? permissionStore.hasAllPermissions(value)
      : permissionStore.hasPermission(value)
    
    if (!hasPermission) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}
