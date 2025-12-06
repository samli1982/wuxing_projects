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
    path: '/herb',
    name: 'HerbManagement',
    meta: { 
      title: '药精管理', 
      icon: 'Leaf',
      permission: 'herb:manage'
    }
  },
  {
    path: '/palmtree',
    name: 'PalmtreeManagement',
    meta: { 
      title: '命盘管理', 
      icon: 'CircleCheckFilled',
      permission: 'palmtree:manage'
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
      },
      {
        path: '/dictionary',
        name: 'DictionaryManagement',
        meta: { 
          title: '字典管理', 
          icon: 'Collection',
          permission: 'system:dict'
        }
      },
      {
        path: '/log/operation',
        name: 'OperationLog',
        meta: { 
          title: '操作日志', 
          icon: 'Document',
          permission: 'system:log'
        }
      },
      {
        path: '/file',
        name: 'FileManagement',
        meta: { 
          title: '文件管理', 
          icon: 'Folder',
          permission: 'system:file'
        }
      },
      {
        path: '/menu',
        name: 'MenuManagement',
        meta: { 
          title: '菜单管理', 
          icon: 'Menu',
          permission: 'system:menu'
        }
      },
      {
        path: '/database',
        name: 'DatabaseQuery',
        meta: { 
          title: '数据库查询', 
          icon: 'DataLine',
          permission: 'system:database'
        }
      },
      {
        path: '/cache',
        name: 'CacheManagement',
        meta: { 
          title: '缓存管理', 
          icon: 'Coin',
          permission: 'system:cache'
        }
      },
      {
        path: '/member',
        name: 'MemberManagement',
        meta: { 
          title: '会员管理', 
          icon: 'UserFilled',
          permission: 'system:member'
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
