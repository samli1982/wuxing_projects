<template>
  <div class="log-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="操作模块">
          <el-input
            v-model="searchForm.module"
            placeholder="请输入操作模块"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="操作人">
          <el-input
            v-model="searchForm.operator"
            placeholder="请输入操作人"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 380px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table
        :data="tableData"
        stripe
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="module" label="操作模块" width="150" />
        <el-table-column prop="operationType" label="操作类型" width="150" />
        <el-table-column prop="description" label="操作描述" width="200" show-overflow-tooltip />
        <el-table-column prop="operator" label="操作人" width="150" />
        <el-table-column prop="ip" label="操作IP" width="200" />
        <el-table-column prop="duration" label="执行时长" width="120">
          <template #default="{ row }">
            {{ row.duration }}ms
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="200">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleView(row)"
            >
              查看详情
            </el-button>
            <el-button
              type="danger"
              size="small"
              link
              @click="handleDelete(row.id)"
              v-permission="'system:log:delete'"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[20, 30, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="操作日志详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="操作模块">{{ detail.module }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ detail.operationType }}</el-descriptions-item>
        <el-descriptions-item label="操作描述" :span="2">{{ detail.description }}</el-descriptions-item>
        <el-descriptions-item label="请求方法" :span="2">{{ detail.method }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detail.operator }}</el-descriptions-item>
        <el-descriptions-item label="操作IP">{{ detail.ip }}</el-descriptions-item>
        <el-descriptions-item label="执行时长">{{ detail.duration }}ms</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detail.status === 1 ? 'success' : 'danger'">
            {{ detail.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作时间" :span="2">
          {{ formatDateTime(detail.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <el-input
            v-model="detail.params"
            type="textarea"
            :rows="5"
            readonly
          />
        </el-descriptions-item>
        <el-descriptions-item label="返回结果" :span="2">
          <el-input
            v-model="detail.result"
            type="textarea"
            :rows="5"
            readonly
          />
        </el-descriptions-item>
        <el-descriptions-item v-if="detail.errorMsg" label="错误信息" :span="2">
          <el-text type="danger">{{ detail.errorMsg }}</el-text>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getLogList, getLogById, deleteLog } from '@/api/log'

// 搜索表单
const searchForm = reactive({
  module: '',
  operator: ''
})

// 时间范围
const dateRange = ref([])

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 详情对话框
const dialogVisible = ref(false)
const detail = ref({})

// 时间格式化
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 加载列表数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      module: searchForm.module,
      operator: searchForm.operator,
      startTime: dateRange.value?.[0] || '',
      endTime: dateRange.value?.[1] || ''
    }
    const res = await getLogList(params)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.module = ''
  searchForm.operator = ''
  dateRange.value = []
  pagination.current = 1
  loadData()
}

// 分页大小改变
const handleSizeChange = () => {
  loadData()
}

// 页码改变
const handleCurrentChange = () => {
  loadData()
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getLogById(row.id)
    if (res.code === 200) {
      detail.value = res.data
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条日志吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteLog(id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* 页面容器 */
.log-management {
  padding: 10px;
  height: calc(100vh - 60px);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

/* 卡片 */
.log-management .el-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-bottom: 10px;
}

/* 卡片头部 */
.log-management .el-card :deep(.el-card__header) {
  padding: 9px 10px;
  border-bottom: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 卡片内容 */
.log-management .el-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 10px 10px 0 !important;
}

/* 搜索表单 */
.search-form {
  margin-bottom: 10px;
}

/* 表格 */
.log-management .el-table {
  flex: 1;
}

/* 分页容器 */
.pagination-container {
  margin-top: 5px !important;
  padding: 0 !important;
  border-top: none !important;
  display: flex;
  justify-content: flex-end;
}

.pagination-container :deep(.el-pagination) {
  padding: 0 !important;
  margin: 0 !important;
}
</style>
