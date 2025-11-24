<template>
  <div class="dictionary-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>字典管理</span>
          <el-button type="primary" @click="handleAdd" v-permission="'system:dict:add'">
            <el-icon><Plus /></el-icon>
            新增字典
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="字典类型">
          <el-select v-model="searchForm.dictType" placeholder="请选择字典类型" clearable style="width: 200px">
            <el-option
              v-for="type in dictTypes"
              :key="type.dictType"
              :label="type.dictTypeName"
              :value="type.dictType"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="字典键">
          <el-input
            v-model="searchForm.dictKey"
            placeholder="请输入字典键"
            clearable
            style="width: 200px"
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
        <el-table-column prop="dictType" label="字典类型" width="200" />
        <el-table-column prop="dictTypeName" label="类型名称" width="200" />
        <el-table-column prop="dictKey" label="字典键" width="150" />
        <el-table-column prop="dictValue" label="字典值" width="150" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="200">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="270" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleEdit(row)"
              v-permission="'system:dict:edit'"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              link
              @click="handleDelete(row.id)"
              v-permission="'system:dict:delete'"
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="form.dictType" placeholder="请输入字典类型（英文）" />
        </el-form-item>
        <el-form-item label="类型名称" prop="dictTypeName">
          <el-input v-model="form.dictTypeName" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="字典键" prop="dictKey">
          <el-input v-model="form.dictKey" placeholder="请输入字典键" />
        </el-form-item>
        <el-form-item label="字典值" prop="dictValue">
          <el-input v-model="form.dictValue" placeholder="请输入字典值" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import {
  getDictList,
  getDictTypes,
  getDictById,
  addDict,
  updateDict,
  deleteDict
} from '@/api/dictionary'

// 搜索表单
const searchForm = reactive({
  dictType: '',
  dictKey: ''
})

// 字典类型列表
const dictTypes = ref([])

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增字典')
const formRef = ref(null)
const form = reactive({
  id: null,
  dictType: '',
  dictTypeName: '',
  dictKey: '',
  dictValue: '',
  sort: 0,
  status: 1,
  remark: ''
})

// 表单验证规则
const rules = {
  dictType: [{ required: true, message: '请输入字典类型', trigger: 'blur' }],
  dictTypeName: [{ required: true, message: '请输入类型名称', trigger: 'blur' }],
  dictKey: [{ required: true, message: '请输入字典键', trigger: 'blur' }],
  dictValue: [{ required: true, message: '请输入字典值', trigger: 'blur' }]
}

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

// 获取字典类型列表
const loadDictTypes = async () => {
  try {
    const res = await getDictTypes()
    if (res.code === 200) {
      dictTypes.value = res.data || []
    }
  } catch (error) {
    console.error('获取字典类型失败:', error)
  }
}

// 加载列表数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    const res = await getDictList(params)
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
  searchForm.dictType = ''
  searchForm.dictKey = ''
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

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增字典'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row) => {
  dialogTitle.value = '编辑字典'
  try {
    const res = await getDictById(row.id)
    if (res.code === 200) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取字典详情失败')
  }
}

// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条字典吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteDict(id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交表单
const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (form.id) {
          res = await updateDict(form.id, form)
        } else {
          res = await addDict(form)
        }
        if (res.code === 200) {
          ElMessage.success(form.id ? '更新成功' : '新增成功')
          dialogVisible.value = false
          loadData()
          loadDictTypes() // 重新加载字典类型
        }
      } catch (error) {
        ElMessage.error(form.id ? '更新失败' : '新增失败')
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  form.id = null
  form.dictType = ''
  form.dictTypeName = ''
  form.dictKey = ''
  form.dictValue = ''
  form.sort = 0
  form.status = 1
  form.remark = ''
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

// 初始化
onMounted(() => {
  loadDictTypes()
  loadData()
})
</script>

<style scoped>
/* 页面容器 */
.dictionary-management {
  padding: 10px;
  height: calc(100vh - 60px);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

/* 卡片 */
.dictionary-management .el-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-bottom: 10px;
}

/* 卡片头部 */
.dictionary-management .el-card :deep(.el-card__header) {
  padding: 9px 10px;
  border-bottom: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 卡片内容 */
.dictionary-management .el-card :deep(.el-card__body) {
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
.dictionary-management .el-table {
  flex: 1;
}

/* 分页容器 */
.pagination-container {
  margin-top: 5px !important;
  padding: 10px !important;
  border-top: none !important;
  display: flex;
  justify-content: flex-end;
}

.pagination-container :deep(.el-pagination) {
  padding: 0 !important;
  margin: 0 !important;
}
</style>
