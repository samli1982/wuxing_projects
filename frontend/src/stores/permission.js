import { defineStore } from 'pinia'
import { getCurrentUserPermissions } from '@/api/permission'
import { getMenuTree } from '@/api/menu'
import { filterMenuByPermission } from '@/config/menu'

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    permissions: [],
    loaded: false,
    menus: [] // 添加菜单列表
  }),

  getters: {
    // 获取所有权限编码
    permissionCodes: (state) => {
      return state.permissions.map(p => p.permissionCode)
    },
    
    // 获取菜单权限
    menuPermissions: (state) => {
      return state.permissions.filter(p => p.permissionType === 1)
    },
    
    // 获取按钮权限
    buttonPermissions: (state) => {
      return state.permissions.filter(p => p.permissionType === 2)
    }
  },

  actions: {
    // 加载当前用户的权限
    async loadPermissions() {
      if (this.loaded) return
      
      try {
        // 加载权限列表
        const permRes = await getCurrentUserPermissions()
        this.permissions = permRes.data || []
        
        // 直接从后端加载菜单树（已经根据权限过滤）
        const menuRes = await getMenuTree()
        this.menus = menuRes.data || []
        
        this.loaded = true
        
        console.log('加载权限成功:', this.permissions.length, '个')
        console.log('加载菜单成功:', this.menus.length, '个')
      } catch (error) {
        console.error('加载权限失败:', error)
        this.permissions = []
        this.menus = []
      }
    },
    
    // 检查是否拥有某个权限
    hasPermission(permissionCode) {
      return this.permissionCodes.includes(permissionCode)
    },
    
    // 检查是否拥有任一权限
    hasAnyPermission(permissionCodes) {
      if (!permissionCodes || permissionCodes.length === 0) return true
      return permissionCodes.some(code => this.hasPermission(code))
    },
    
    // 检查是否拥有所有权限
    hasAllPermissions(permissionCodes) {
      if (!permissionCodes || permissionCodes.length === 0) return true
      return permissionCodes.every(code => this.hasPermission(code))
    },
    
    // 清空权限
    clearPermissions() {
      this.permissions = []
      this.loaded = false
      this.menus = []
    },
    
    // 刷新菜单（强制重新加载）
    async refreshMenus() {
      this.loaded = false
      await this.loadPermissions()
    }
  }
})
