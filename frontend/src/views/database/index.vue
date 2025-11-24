<template>
  <div class="database-management">
    <el-row :gutter="10">
      <!-- 左侧：表列表 -->
      <el-col :span="6">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>数据库表</span>
              <el-button size="small" @click="loadTables" :loading="loadingTables">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <el-input
            v-model="tableSearch"
            placeholder="搜索表名"
            clearable
            style="margin-bottom: 10px;"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <div class="table-list">
            <div
              v-for="table in filteredTables"
              :key="table"
              class="table-item"
              :class="{ active: selectedTable === table }"
              @click="selectTable(table)"
            >
              <el-icon><Document /></el-icon>
              <span>{{ table }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：表数据展示 -->
      <el-col :span="18">
        <el-card v-if="selectedTable">
          <template #header>
            <div class="card-header">
              <span>{{ selectedTable }}</span>
              <div>
                <el-tag type="info" v-if="tableData.total">共 {{ tableData.total }} 条</el-tag>
                <el-button size="small" @click="loadTableData(selectedTable, currentPage)" :loading="loadingData">
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
              </div>
            </div>
          </template>

          <el-tabs v-model="activeTab">
            <!-- 表数据 -->
            <el-tab-pane label="表数据" name="data">
              <el-table
                :data="tableData.rows"
                border
                stripe
                style="width: 100%"
                max-height="500"
                v-loading="loadingData"
              >
                <el-table-column
                  v-for="column in tableData.columns"
                  :key="column"
                  :prop="column"
                  :label="column"
                  min-width="120"
                  show-overflow-tooltip
                />
              </el-table>
              <div class="pagination-container" v-if="tableData.total > 0">
                <el-pagination
                  v-model:current-page="currentPage"
                  v-model:page-size="pageSize"
                  :page-sizes="[20, 50, 100, 200]"
                  :total="tableData.total"
                  layout="total, sizes, prev, pager, next, jumper"
                  @size-change="handleSizeChange"
                  @current-change="handlePageChange"
                />
              </div>
            </el-tab-pane>

            <!-- 表结构 -->
            <el-tab-pane label="表结构" name="structure">
              <el-table
                :data="tableStructure"
                border
                stripe
                v-loading="loadingStructure"
              >
                <el-table-column prop="Field" label="字段名" width="200" />
                <el-table-column prop="Type" label="类型" width="150" />
                <el-table-column prop="Null" label="允许空" width="100" align="center" />
                <el-table-column prop="Key" label="键" width="100" align="center" />
                <el-table-column prop="Default" label="默认值" width="120" />
                <el-table-column prop="Extra" label="额外" min-width="150" />
              </el-table>
            </el-tab-pane>

            <!-- 表统计 -->
            <el-tab-pane label="表统计" name="stats">
              <el-descriptions :column="2" border v-loading="loadingStats">
                <el-descriptions-item label="行数">{{ tableStats.rowCount }}</el-descriptions-item>
                <el-descriptions-item label="存储引擎">{{ tableStats.engine }}</el-descriptions-item>
                <el-descriptions-item label="行格式">{{ tableStats.rowFormat }}</el-descriptions-item>
                <el-descriptions-item label="数据大小">{{ formatBytes(tableStats.dataLength) }}</el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ tableStats.createTime }}</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{ tableStats.updateTime }}</el-descriptions-item>
                <el-descriptions-item label="字符集">{{ tableStats.collation }}</el-descriptions-item>
                <el-descriptions-item label="备注">{{ tableStats.comment || '-' }}</el-descriptions-item>
              </el-descriptions>
            </el-tab-pane>
          </el-tabs>
        </el-card>

        <!-- 未选择表时的提示 -->
        <el-empty v-else description="请从左侧选择要查看的数据表" :image-size="200" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Document } from '@element-plus/icons-vue'
import { getAllTables, getTableStructure, getTableStats, executeQuery } from '@/api/database'

const loadingTables = ref(false)
const loadingStructure = ref(false)
const loadingStats = ref(false)
const loadingData = ref(false)

const tables = ref([])
const tableSearch = ref('')
const selectedTable = ref('')
const activeTab = ref('data')
const currentPage = ref(1)
const pageSize = ref(20)

const tableData = ref({
  columns: [],
  rows: [],
  total: 0
})
const tableStructure = ref([])
const tableStats = ref({})

// 过滤表列表
const filteredTables = computed(() => {
  if (!tableSearch.value) return tables.value
  return tables.value.filter(table => 
    table.toLowerCase().includes(tableSearch.value.toLowerCase())
  )
})

// 加载所有表
const loadTables = async () => {
  loadingTables.value = true
  try {
    const res = await getAllTables()
    tables.value = res.data
  } catch (error) {
    ElMessage.error('加载表列表失败')
  } finally {
    loadingTables.value = false
  }
}

// 选择表
const selectTable = async (table) => {
  selectedTable.value = table
  currentPage.value = 1
  activeTab.value = 'data'
  
  // 加载表数据
  loadTableData(table, 1)
  
  // 加载表结构
  loadTableStructure(table)
  
  // 加载表统计
  loadTableStats(table)
}

// 加载表数据
const loadTableData = async (table, page) => {
  loadingData.value = true
  try {
    const sql = `SELECT * FROM ${table}`
    const res = await executeQuery({ 
      sql, 
      pageNum: page, 
      pageSize: pageSize.value 
    })
    tableData.value = res.data
  } catch (error) {
    ElMessage.error('加载表数据失败')
  } finally {
    loadingData.value = false
  }
}

// 加载表结构
const loadTableStructure = async (table) => {
  loadingStructure.value = true
  try {
    const res = await getTableStructure(table)
    tableStructure.value = res.data
  } catch (error) {
    ElMessage.error('加载表结构失败')
  } finally {
    loadingStructure.value = false
  }
}

// 加载表统计
const loadTableStats = async (table) => {
  loadingStats.value = true
  try {
    const res = await getTableStats(table)
    tableStats.value = res.data
  } catch (error) {
    ElMessage.error('加载表统计失败')
  } finally {
    loadingStats.value = false
  }
}

// 分页大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadTableData(selectedTable.value, 1)
}

// 页码改变
const handlePageChange = (page) => {
  currentPage.value = page
  loadTableData(selectedTable.value, page)
}

// 格式化字节
const formatBytes = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

onMounted(() => {
  loadTables()
})
</script>

<style scoped>
.database-management {
  padding: 10px;
  height: calc(100vh - 60px);
  box-sizing: border-box;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-card :deep(.el-card__header) {
  padding: 9px 10px;
  border-bottom: none;
}

.el-card :deep(.el-card__body) {
  padding: 10px;
}

.table-list {
  max-height: calc(100vh - 240px);
  overflow-y: auto;
}

.table-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 5px;
  transition: all 0.3s;
}

.table-item:hover {
  background-color: #f5f7fa;
}

.table-item.active {
  background-color: #409eff;
  color: white;
}

.pagination-container {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}
</style>
