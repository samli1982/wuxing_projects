<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '240px'" class="sidebar">
      <div class="logo">
        <span v-if="!isCollapse">天人合一助手</span>
        <span v-else>五行</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        router
      >
        <template v-for="menu in menus" :key="menu.path">
          <!-- 一级菜单（无子菜单） -->
          <el-menu-item
            v-if="!menu.children || menu.children.length === 0"
            :index="menu.path"
          >
            <el-icon>
              <component :is="menu.meta.icon" />
            </el-icon>
            <template #title>{{ menu.meta.title }}</template>
          </el-menu-item>
          
          <!-- 一级菜单（有子菜单） -->
          <el-sub-menu v-else :index="menu.path">
            <template #title>
              <el-icon>
                <component :is="menu.meta.icon" />
              </el-icon>
              <span>{{ menu.meta.title }}</span>
            </template>
            
            <!-- 二级菜单 -->
            <el-menu-item
              v-for="subMenu in menu.children"
              :key="subMenu.path"
              :index="subMenu.path"
            >
              <el-icon>
                <component :is="subMenu.meta.icon" />
              </el-icon>
              <template #title>{{ subMenu.meta.title }}</template>
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>
    
    <!-- 主内容区 -->
    <el-container class="main-container" :style="{ marginLeft: isCollapse ? '64px' : '240px' }">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-icon" @click="toggleCollapse">
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><UserFilled /></el-icon>
              <span>{{ username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()
const permissionStore = usePermissionStore()

const isCollapse = ref(false)
const username = ref('Admin')

const activeMenu = computed(() => route.path)

// 获取动态菜单
const menus = computed(() => permissionStore.menus)

// 组件加载时加载权限（如果还没加载）
onMounted(async () => {
  if (!permissionStore.loaded) {
    await permissionStore.loadPermissions()
  }
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
    })
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex;
}

/* 侧边栏样式 - 匹配原型UI */
.sidebar {
  background-color: #192a56;
  transition: width 0.3s;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  overflow-x: hidden;
  overflow-y: auto;
  z-index: 1000;
}

/* 主内容区容器 */
.main-container {
  flex: 1;
  transition: margin-left 0.3s;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  font-weight: 600;
  border-bottom: 1px solid #485b8c;
  background-color: #192a56;
}

.el-menu {
  border-right: none;
  background-color: #192a56;
  padding: 20px 0;
}

:deep(.el-menu-item) {
  color: #bfcbd9;
  font-size: 14px;
  padding: 12px 20px;
  margin: 0;
  border-radius: 0;
  transition: all 0.3s;
}

:deep(.el-menu-item:hover),
:deep(.el-menu-item.is-active) {
  background-color: #273c75 !important;
  color: #fff !important;
}

:deep(.el-menu-item .el-icon) {
  margin-right: 10px;
  width: 20px;
  text-align: center;
}

/* 子菜单样式 */
:deep(.el-sub-menu__title) {
  color: #bfcbd9;
  font-size: 14px;
  padding: 12px 20px;
  transition: all 0.3s;
}

:deep(.el-sub-menu__title:hover) {
  background-color: #273c75 !important;
  color: #fff !important;
}

:deep(.el-sub-menu .el-menu-item) {
  background-color: #1f2d3d !important;
  min-width: 0;
  padding-left: 50px;
}

:deep(.el-sub-menu .el-menu-item:hover) {
  background-color: #273c75 !important;
}

/* 顶部导航栏 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  transition: color 0.3s;
  color: #2c3e50;
}

.collapse-icon:hover {
  color: #192a56;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 6px;
  transition: all 0.3s;
  color: #2c3e50;
  font-size: 14px;
}

.user-info:hover {
  background-color: #f5f7fa;
  color: #192a56;
}

.user-info span {
  margin-left: 8px;
}

/* 主内容区 */
.main-content {
  background-color: #f5f7fa;
  padding: 10px;
  min-height: calc(100vh - 60px);
  box-sizing: border-box;
  overflow: hidden;
}
</style>
