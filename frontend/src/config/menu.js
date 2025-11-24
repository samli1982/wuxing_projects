// 菜单配置 - 基于权限动态生成（支持二级菜单）
export const menuConfig = [
  {
    path: '/',
    name: 'Dashboard',
    meta: { 
      title: '首页', 
      icon: 'HomeFilled'
    }
  },
  {
    path: '/system',
    name: 'System',
    meta: { 
      title: '系统管理', 
      icon: 'Setting'
    },
    children: [
      {
        path: '/user',
        name: 'UserManagement',
        meta: { 
          title: '用户管理', 
          icon: 'User',
          permission: 'system:user'
        }
      },
      {
        path: '/role',
        name: 'RoleManagement',
        meta: { 
          title: '角色管理', 
          icon: 'Avatar',
          permission: 'system:role'
        }
      },
      {
        path: '/permission',
        name: 'PermissionManagement',
        meta: { 
          title: '权限管理', 
          icon: 'Lock',
          permission: 'system:permission'
        }
      }
    ]
  }
]

// 根据权限过滤菜单（支持递归过滤子菜单）
export function filterMenuByPermission(permissions) {
  const permissionCodes = permissions.map(p => p.permissionCode)
  
  function filterMenu(menus) {
    return menus.filter(menu => {
      // 首页不需要权限
      if (menu.path === '/') return true
      
      // 处理有子菜单的情况
      if (menu.children && menu.children.length > 0) {
        // 递归过滤子菜单
        const filteredChildren = filterMenu(menu.children)
        
        // 如果有任何子菜单有权限，则显示父菜单
        if (filteredChildren.length > 0) {
          menu.children = filteredChildren
          return true
        }
        return false
      }
      
      // 检查是否有对应的权限
      if (menu.meta.permission) {
        return permissionCodes.includes(menu.meta.permission)
      }
      
      return true
    })
  }
  
  return filterMenu(JSON.parse(JSON.stringify(menuConfig)))
}
