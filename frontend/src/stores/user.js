import { defineStore } from 'pinia'
import { login } from '@/api/user'
import { usePermissionStore } from './permission'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token
  },

  actions: {
    // 登录
    async login(loginForm) {
      try {
        const res = await login(loginForm)
        this.token = res.data.token
        localStorage.setItem('token', res.data.token)
        
        // 登录成功后加载权限
        const permissionStore = usePermissionStore()
        await permissionStore.loadPermissions()
        
        return res
      } catch (error) {
        return Promise.reject(error)
      }
    },

    // 登出
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      
      // 清空权限
      const permissionStore = usePermissionStore()
      permissionStore.clearPermissions()
      
      router.push('/login')
    },

    // 设置用户信息
    setUserInfo(userInfo) {
      this.userInfo = userInfo
    }
  }
})
