<template>
  <div class="permission-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" @click="handleAdd" v-permission="'system:permission:add'">
            <el-icon><Plus /></el-icon>
            新增权限
          </el-button>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="权限名称">
          <el-input v-model="searchForm.permissionName" placeholder="请输入权限名称" clearable />
        </el-form-item>
        <el-form-item label="权限类型">
          <el-select v-model="searchForm.permissionType" placeholder="请选择" clearable>
            <el-option label="菜单" :value="1" />
            <el-option label="按钮" :value="2" />
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
        style="width: 100%"
        v-loading="loading"
        row-key="id"
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="permissionName" label="权限名称" width="120" />
        <el-table-column prop="permissionCode" label="权限编码" width="180" />
        <el-table-column label="权限类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.permissionType === 1 ? 'success' : 'info'">
              {{ row.permissionType === 1 ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由地址" />
        <el-table-column prop="icon" label="图标" width="150" />
        <el-table-column prop="sort" label="排序" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="200">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)" v-permission="'system:permission:query'">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)" v-permission="'system:permission:edit'">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'system:permission:delete'">
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
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="form.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input v-model="form.permissionCode" :disabled="isEdit" placeholder="如：system:user:add" />
        </el-form-item>
        <el-form-item label="权限类型" prop="permissionType">
          <el-radio-group v-model="form.permissionType">
            <el-radio :label="1">菜单</el-radio>
            <el-radio :label="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="父级权限" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="treeData"
            :props="{ value: 'id', label: 'permissionName', children: 'children' }"
            check-strictly
            :render-after-expand="false"
            placeholder="请选择父级权限（可选）"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="路由地址" prop="path" v-if="form.permissionType === 1">
          <el-input v-model="form.path" placeholder="如：/system/user" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="form.permissionType === 1">
          <el-input v-model="form.icon" placeholder="如：user" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
    <el-dialog v-model="viewDialogVisible" title="权限详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="权限ID">{{ viewData.id }}</el-descriptions-item>
        <el-descriptions-item label="权限名称">{{ viewData.permissionName }}</el-descriptions-item>
        <el-descriptions-item label="权限编码" :span="2">{{ viewData.permissionCode }}</el-descriptions-item>
        <el-descriptions-item label="权限类型">
          <el-tag :type="viewData.permissionType === 1 ? 'success' : 'info'">
            {{ viewData.permissionType === 1 ? '菜单' : '按钮' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewData.status === 1 ? 'success' : 'danger'">
            {{ viewData.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="父级ID">{{ viewData.parentId || '无' }}</el-descriptions-item>
        <el-descriptions-item label="排序">{{ viewData.sort }}</el-descriptions-item>
        <el-descriptions-item label="路由地址" :span="2">{{ viewData.path || '-' }}</el-descriptions-item>
        <el-descriptions-item label="图标">{{ viewData.icon || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ viewData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ viewData.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getPermissionList, 
  getPermissionTree,
  createPermission, 
  updatePermission, 
  deletePermission 
} from '@/api/permission'
import { formatDateTime } from '@/utils/date'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = ref('新增权限')
const formRef = ref(null)

const searchForm = reactive({
  permissionName: '',
  permissionType: null
})

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

const tableData = ref([])
const treeData = ref([])

const form = reactive({
  id: null,
  permissionName: '',
  permissionCode: '',
  permissionType: 1,
  parentId: 0,
  path: '',
  icon: '',
  sort: 0,
  status: 1
})

const viewData = ref({})

const formRules = {
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' }
  ],
  permissionCode: [
    { required: true, message: '请输入权限编码', trigger: 'blur' }
  ],
  permissionType: [
    { required: true, message: '请选择权限类型', trigger: 'change' }
  ]
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size
    }
    const res = await getPermissionList(params)
    // 如果是树形结构，直接使用
    if (res.data.records && Array.isArray(res.data.records)) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取权限列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取树形数据
const getTreeData = async () => {
  try {
    const res = await getPermissionTree()
    treeData.value = [
      { id: 0, permissionName: '顶级权限', children: res.data || [] }
    ]
  } catch (error) {
    console.error('获取权限树失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  getList()
}

// 重置
const handleReset = () => {
  searchForm.permissionName = ''
  searchForm.permissionType = null
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增权限'
  dialogVisible.value = true
  getTreeData()
}

// 查看
const handleView = (row) => {
  viewData.value = { ...row }
  viewDialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑权限'
  Object.assign(form, row)
  dialogVisible.value = true
  getTreeData()
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该权限吗？删除后不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deletePermission(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updatePermission(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await createPermission(form)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    permissionName: '',
    permissionCode: '',
    permissionType: 1,
    parentId: 0,
    path: '',
    icon: '',
    sort: 0,
    status: 1
  })
}

// 分页
const handleSizeChange = (val) => {
  pagination.size = val
  getList()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.permission-management {
  padding: 10px;
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.permission-management .el-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-bottom: 10px;
}

.permission-management .el-card :deep(.el-card__header) {
  padding: 9px 10px;
  border-bottom: none;
}

.permission-management .el-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 10px 10px 0 !important;
}

.permission-management .el-table {
  flex: 1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 10px;
  
}

/* 分页容器 - 固定在底部 */
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
