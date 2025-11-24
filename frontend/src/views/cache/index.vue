<template>
  <div class="cache-management">
    <!-- Redis信息卡片 -->
    <el-row :gutter="10" style="margin-bottom: 10px;">
      <el-col :span="12">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon :size="32"><Coin /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">缓存键总数</div>
              <div class="stat-value">{{ redisInfo.keyCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon :size="32"><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">数据类型</div>
              <div class="stat-value" style="font-size: 14px;">
                {{ formatTypeCount(redisInfo.typeCount) }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>缓存列表</span>
          <div>
            <el-button type="primary" @click="loadCacheKeys" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
            <el-button type="danger" @click="handleClearAll" v-permission="'system:cache:clear'">
              <el-icon><Delete /></el-icon>
              清空所有
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="缓存键">
          <el-input
            v-model="searchPattern"
            placeholder="支持通配符，如: user:*"
            clearable
            style="width: 300px;"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 缓存键列表 -->
      <el-table
        :data="cacheKeys"
        border
        stripe
        style="width: 100%; flex: 1;"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="key" label="缓存键" min-width="300" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link type="primary" @click="viewCacheDetail(row)">{{ row }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewCacheDetail(row)" v-permission="'system:cache:query'">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-permission="'system:cache:delete'">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 批量操作 -->
      <div style="margin-top: 10px;" v-if="selectedKeys.length > 0">
        <el-button type="danger" @click="handleBatchDelete" v-permission="'system:cache:delete'">
          <el-icon><Delete /></el-icon>
          批量删除（{{ selectedKeys.length }}）
        </el-button>
      </div>
    </el-card>

    <!-- 缓存详情对话框 -->
    <el-dialog v-model="detailVisible" title="缓存详情" width="700px">
      <el-descriptions :column="1" border v-loading="loadingDetail">
        <el-descriptions-item label="缓存键">{{ cacheDetail.key }}</el-descriptions-item>
        <el-descriptions-item label="数据类型">{{ cacheDetail.type }}</el-descriptions-item>
        <el-descriptions-item label="过期时间">
          <el-tag v-if="cacheDetail.ttl === -1" type="success">永不过期</el-tag>
          <el-tag v-else-if="cacheDetail.ttl === -2" type="danger">已过期</el-tag>
          <el-tag v-else type="info">{{ cacheDetail.ttl }} 秒</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="缓存值">
          <el-input
            v-model="cacheValueDisplay"
            type="textarea"
            :rows="10"
            readonly
            style="font-family: 'Courier New', monospace;"
          />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete, View, Coin, DataLine } from '@element-plus/icons-vue'
import { getCacheKeys, getCacheInfo, deleteCache, deleteCaches, clearAllCache, getRedisInfo } from '@/api/cache'

const loading = ref(false)
const loadingDetail = ref(false)
const searchPattern = ref('')
const cacheKeys = ref([])
const selectedKeys = ref([])
const detailVisible = ref(false)
const cacheDetail = ref({})
const redisInfo = ref({})

const cacheValueDisplay = computed(() => {
  if (!cacheDetail.value.value) return ''
  
  try {
    // 如果是对象或数组，格式化JSON显示
    if (typeof cacheDetail.value.value === 'object') {
      return JSON.stringify(cacheDetail.value.value, null, 2)
    }
    return String(cacheDetail.value.value)
  } catch (e) {
    return String(cacheDetail.value.value)
  }
})

// 加载Redis信息
const loadRedisInfo = async () => {
  try {
    const res = await getRedisInfo()
    redisInfo.value = res.data
  } catch (error) {
    console.error('加载Redis信息失败:', error)
  }
}

// 加载缓存键列表
const loadCacheKeys = async () => {
  loading.value = true
  try {
    const res = await getCacheKeys(searchPattern.value || '*')
    cacheKeys.value = res.data
    await loadRedisInfo()
  } catch (error) {
    ElMessage.error('加载缓存键列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  loadCacheKeys()
}

// 重置
const handleReset = () => {
  searchPattern.value = ''
  loadCacheKeys()
}

// 查看缓存详情
const viewCacheDetail = async (key) => {
  detailVisible.value = true
  loadingDetail.value = true
  try {
    const res = await getCacheInfo(key)
    cacheDetail.value = res.data
  } catch (error) {
    ElMessage.error('获取缓存详情失败')
  } finally {
    loadingDetail.value = false
  }
}

// 删除缓存
const handleDelete = async (key) => {
  try {
    await ElMessageBox.confirm('确定要删除该缓存吗?', '提示', {
      type: 'warning'
    })
    await deleteCache(key)
    ElMessage.success('删除成功')
    loadCacheKeys()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedKeys.value.length} 个缓存吗?`, '提示', {
      type: 'warning'
    })
    await deleteCaches(selectedKeys.value)
    ElMessage.success('批量删除成功')
    selectedKeys.value = []
    loadCacheKeys()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 清空所有缓存
const handleClearAll = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有缓存吗？此操作不可恢复！', '警告', {
      type: 'warning',
      confirmButtonText: '确定清空',
      cancelButtonText: '取消'
    })
    await clearAllCache()
    ElMessage.success('清空成功')
    loadCacheKeys()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  }
}

// 选择改变
const handleSelectionChange = (selection) => {
  selectedKeys.value = selection
}

// 格式化类型统计
const formatTypeCount = (typeCount) => {
  if (!typeCount || Object.keys(typeCount).length === 0) {
    return '-'
  }
  return Object.entries(typeCount)
    .map(([type, count]) => `${type}: ${count}`)
    .join(', ')
}

onMounted(() => {
  loadCacheKeys()
})
</script>

<style scoped>
.cache-management {
  padding: 10px;
  height: calc(100vh - 60px);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.cache-management .el-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-bottom: 10px;
}

.cache-management .el-card :deep(.el-card__header) {
  padding: 9px 10px;
  border-bottom: none;
}

.cache-management .el-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 10px 10px 0 !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 10px;
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
</style>
