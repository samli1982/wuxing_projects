<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">用户总数</div>
              <div class="stat-value">{{ stats.userCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon :size="32"><Stamp /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">角色总数</div>
              <div class="stat-value">{{ stats.roleCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #e6a23c;">
              <el-icon :size="32"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">操作日志</div>
              <div class="stat-value">{{ stats.logCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #f56c6c;">
              <el-icon :size="32"><Files /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">文件总数</div>
              <div class="stat-value">{{ stats.fileCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/user')" v-permission="'system:user'">
              <el-icon><User /></el-icon>
              用户管理
            </el-button>
            <el-button type="success" @click="$router.push('/role')" v-permission="'system:role'">
              <el-icon><Stamp /></el-icon>
              角色管理
            </el-button>
            <el-button type="warning" @click="$router.push('/permission')" v-permission="'system:permission'">
              <el-icon><Key /></el-icon>
              权限管理
            </el-button>
            <el-button type="info" @click="$router.push('/menu')" v-permission="'system:menu'">
              <el-icon><Menu /></el-icon>
              菜单管理
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>系统信息</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="系统名称">天人合一助手</el-descriptions-item>
            <el-descriptions-item label="系统版本">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="后端框架">Spring Boot 3.2</el-descriptions-item>
            <el-descriptions-item label="前端框架">Vue 3 + Element Plus</el-descriptions-item>
            <el-descriptions-item label="数据库">MySQL 8.0</el-descriptions-item>
            <el-descriptions-item label="缓存">Redis</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Stamp, Document, Files, Key, Menu } from '@element-plus/icons-vue'

const stats = ref({
  userCount: 0,
  roleCount: 0,
  logCount: 0,
  fileCount: 0
})

onMounted(() => {
  // 这里可以调用API获取实际统计数据
  // 目前使用模拟数据
  stats.value = {
    userCount: 10,
    roleCount: 3,
    logCount: 156,
    fileCount: 42
  }
})
</script>

<style scoped>
.dashboard {
  padding: 10px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.quick-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.el-card {
  margin-bottom: 10px;
}

.el-card :deep(.el-card__header) {
  padding: 12px 15px;
}

.el-card :deep(.el-card__body) {
  padding: 15px;
}
</style>
