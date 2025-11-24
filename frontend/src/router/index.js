import { createRouter, createWebHistory } from 'vue-router'
import { usePermissionStore } from '@/stores/permission'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/',
      component: () => import('@/layout/index.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue'),
          meta: { title: '首页', icon: 'HomeFilled' }
        },
        {
          path: 'user',
          name: 'UserManagement',
          component: () => import('@/views/user/index.vue'),
          meta: { title: '用户管理', icon: 'User' }
        },
        {
          path: 'role',
          name: 'RoleManagement',
          component: () => import('@/views/role/index.vue'),
          meta: { title: '角色管理', icon: 'Avatar' }
        },
        {
          path: 'permission',
          name: 'PermissionManagement',
          component: () => import('@/views/permission/index.vue'),
          meta: { title: '权限管理', icon: 'Lock' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.path === '/login') {
    next()
  } else {
    if (!token) {
      next('/login')
    } else {
      // 确保权限已加载
      const permissionStore = usePermissionStore()
      if (!permissionStore.loaded) {
        try {
          await permissionStore.loadPermissions()
        } catch (error) {
          console.error('加载权限失败:', error)
        }
      }
      next()
    }
  }
})

export default router
