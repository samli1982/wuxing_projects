<template>
  <div class="herb-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>药精管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增药精
          </el-button>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="名称">
          <el-input v-model="searchForm.keyword" placeholder="输入药精名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="五行">
          <el-select v-model="searchForm.element" placeholder="选择五行" clearable style="width: 120px">
            <el-option label="木" value="木" />
            <el-option label="火" value="火" />
            <el-option label="土" value="土" />
            <el-option label="金" value="金" />
            <el-option label="水" value="水" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
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
        style="width: 100%; flex: 1;"
        v-loading="loading"
        height="500"
      >
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="number" label="编号" width="120" />
        <el-table-column prop="name" label="名称" width="200" />
        <el-table-column prop="alias" label="别名" width="200" show-overflow-tooltip />
        <el-table-column prop="element" label="五行" width="120" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="properties" label="性质" width="120" />
        <el-table-column prop="taste" label="五味" width="150" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="350" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
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
    
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="编号" prop="number">
          <el-input v-model="form.number" placeholder="如：1-①" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入药精名称" />
        </el-form-item>
        <el-form-item label="别名" prop="alias">
          <el-input v-model="form.alias" placeholder="多个别名用逗号分隔" />
        </el-form-item>
        <el-form-item label="五行" prop="element">
          <el-select v-model="form.element" placeholder="选择五行">
            <el-option label="木" value="木" />
            <el-option label="火" value="火" />
            <el-option label="土" value="土" />
            <el-option label="金" value="金" />
            <el-option label="水" value="水" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" placeholder="如：木中木" />
        </el-form-item>
        <el-form-item label="分类图标" prop="categoryIcon">
          <el-input v-model="form.categoryIcon" placeholder="如：🌲" />
        </el-form-item>
        <el-form-item label="性质" prop="properties">
          <el-input v-model="form.properties" placeholder="如：温、热、凉等" />
        </el-form-item>
        <el-form-item label="五味" prop="taste">
          <el-input v-model="form.taste" placeholder="多个五味用逗号分隔" />
        </el-form-item>
        <el-form-item label="性质分类" prop="natureClass">
          <el-select v-model="form.natureClass" placeholder="选择性质分类">
            <el-option label="热" value="hot" />
            <el-option label="温" value="warm" />
            <el-option label="平" value="neutral" />
            <el-option label="凉" value="cool" />
            <el-option label="寒" value="cold" />
          </el-select>
        </el-form-item>
        <el-form-item label="功效" prop="effects">
          <el-input 
            v-model="form.effects" 
            type="textarea" 
            placeholder="多个功效用逗号分隔"
            rows="2"
          />
        </el-form-item>
        <el-form-item label="详细描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            placeholder="请输入详细描述"
            rows="3"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 查看对话框 -->
    <el-dialog v-model="viewDialogVisible" title="药精详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="编号">{{ viewData.number }}</el-descriptions-item>
        <el-descriptions-item label="ID">{{ viewData.id }}</el-descriptions-item>
        <el-descriptions-item label="名称">{{ viewData.name }}</el-descriptions-item>
        <el-descriptions-item label="别名">{{ viewData.alias }}</el-descriptions-item>
        <el-descriptions-item label="五行">{{ viewData.element }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ viewData.category }}</el-descriptions-item>
        <el-descriptions-item label="性质">{{ viewData.properties }}</el-descriptions-item>
        <el-descriptions-item label="五味">{{ viewData.taste }}</el-descriptions-item>
        <el-descriptions-item label="性质分类">{{ viewData.natureClass }}</el-descriptions-item>
        <el-descriptions-item label="排序">{{ viewData.sort }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewData.status === 1 ? 'success' : 'danger'">
            {{ viewData.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(viewData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="功效" :span="2">{{ viewData.effects }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ viewData.description }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, View, Edit, Delete } from '@element-plus/icons-vue'
import * as herbApi from '@/api/herb'

// 状态
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const isEdit = ref(false)
const tableHeight = ref(600)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  element: '',
  status: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 表单
const form = reactive({
  number: '',
  name: '',
  alias: '',
  element: '',
  category: '',
  categoryIcon: '',
  properties: '',
  taste: '',
  natureClass: '',
  effects: '',
  description: '',
  sort: 0,
  status: 1,
  remark: ''
})

const formRef = ref()
const viewData = ref({})

// 表单验证规则
const formRules = {
  number: [{ required: true, message: '请输入编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入药精名称', trigger: 'blur' }],
  element: [{ required: true, message: '请选择五行', trigger: 'change' }],
  category: [{ required: true, message: '请输入分类', trigger: 'blur' }],
  properties: [{ required: true, message: '请输入性质', trigger: 'blur' }],
  taste: [{ required: true, message: '请输入五味', trigger: 'blur' }],
  effects: [{ required: true, message: '请输入功效', trigger: 'blur' }]
}

const dialogTitle = ref('')

// 获取列表
async function fetchList() {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      element: searchForm.element || undefined,
      category: undefined,
      status: searchForm.status !== '' ? searchForm.status : undefined
    }
    const res = await herbApi.getHerbList(params)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载药精列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  pagination.current = 1
  fetchList()
}

// 重置
function handleReset() {
  searchForm.keyword = ''
  searchForm.element = ''
  searchForm.status = ''
  pagination.current = 1
  fetchList()
}

// 新增
function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增药精'
  Object.assign(form, {
    number: '',
    name: '',
    alias: '',
    element: '',
    category: '',
    categoryIcon: '',
    properties: '',
    taste: '',
    natureClass: 'warm',
    effects: '',
    description: '',
    sort: 0,
    status: 1,
    remark: ''
  })
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

// 编辑
async function handleEdit(row) {
  isEdit.value = true
  dialogTitle.value = '编辑药精'
  try {
    const res = await herbApi.getHerbDetail(row.id)
    if (res.code === 200) {
      Object.assign(form, res.data)
      formRef.value?.clearValidate()
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载药精详情失败')
  }
}

// 查看
async function handleView(row) {
  try {
    const res = await herbApi.getHerbDetail(row.id)
    if (res.code === 200) {
      viewData.value = res.data
      viewDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载药精详情失败')
  }
}

// 删除
function handleDelete(row) {
  ElMessageBox.confirm(
    `确定删除药精 "${row.name}" 吗?`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await herbApi.deleteHerb(row.id)
      ElMessage.success('删除成功')
      fetchList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消操作
  })
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      if (isEdit.value) {
        await herbApi.editHerb(form.id, form)
        ElMessage.success('编辑成功')
      } else {
        await herbApi.addHerb(form)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      fetchList()
    } catch (error) {
      ElMessage.error(isEdit.value ? '编辑失败' : '添加失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 关闭对话框
function handleDialogClose() {
  formRef.value?.clearValidate()
}

// 分页变化
function handleSizeChange() {
  pagination.current = 1
  fetchList()
}

function handleCurrentChange() {
  fetchList()
}

// 格式化时间
function formatDateTime(dateTime) {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 计算表格高度
function calculateTableHeight() {
  nextTick(() => {
    const cardBody = document.querySelector('.herb-management .el-card__body')
    if (cardBody) {
      const searchForm = document.querySelector('.search-form')
      const pagination = document.querySelector('.pagination-container')
      const bodyHeight = cardBody.clientHeight
      const searchHeight = searchForm ? searchForm.clientHeight + 10 : 0
      const paginationHeight = pagination ? pagination.clientHeight + 5 : 0
      const availableHeight = bodyHeight - searchHeight - paginationHeight - 10
      tableHeight.value = Math.max(availableHeight, 400)
    }
  })
}

// 初始化
onMounted(() => {
  fetchList()
  calculateTableHeight()
  window.addEventListener('resize', calculateTableHeight)
})
</script>

<style scoped>
.herb-management {
  padding: 10px;
  height: calc(100vh - 60px);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.herb-management .el-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-bottom: 10px;
}

.herb-management .el-card :deep(.el-card__header) {
  padding: 9px 10px;
  border-bottom: none;
}

.herb-management .el-card :deep(.el-card__body) {
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

.herb-management .el-table {
  overflow-y: auto;
  border: 1px solid #ebeef5;
}

.pagination-container {
  margin-top: 5px !important;
  padding: 10px  !important;
  border-top: none !important;
  display: flex;
  justify-content: flex-end;
}

.pagination-container :deep(.el-pagination) {
  padding: 0 !important;
  margin: 0 !important;
}

:deep(.el-form-item) {
  margin-bottom: 18px;
}
</style>
