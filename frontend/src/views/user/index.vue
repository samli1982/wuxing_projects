<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd" v-permission="'system:user:add'">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
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
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" />
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
        <el-table-column label="操作" width="400" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)" v-permission="'system:user:query'">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="success" size="small" @click="handleAssignRole(row)" v-permission="'system:user:edit'">
              <el-icon><User /></el-icon>
              分配角色
            </el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)" v-permission="'system:user:edit'">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'system:user:delete'">
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
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
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
    <el-dialog v-model="viewDialogVisible" title="用户详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ viewData.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ viewData.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ viewData.nickname }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ viewData.email }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ viewData.phone }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewData.status === 1 ? 'success' : 'danger'">
            {{ viewData.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ viewData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ viewData.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
    
    <!-- 分配角色对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      :title="`分配角色 - ${currentUserName}`"
      width="500px"
      @close="handleRoleDialogClose"
    >
      <div class="role-dialog">
        <el-alert
          title="角色说明"
          type="info"
          :closable="false"
          style="margin-bottom: 20px"
        >
          <template #default>
            <div>选择该用户需要关联的角色，用户将拥有所选角色的所有权限。</div>
          </template>
        </el-alert>
        
        <div class="role-stats">
          <el-tag type="success">已选择: {{ selectedRoleCount }} 个角色</el-tag>
          <el-tag type="info" style="margin-left: 10px">总计: {{ allRoles.length }} 个角色</el-tag>
        </div>
        
        <el-divider />
        
        <div class="role-list">
          <el-checkbox-group v-model="selectedRoles" @change="handleRoleChange">
            <el-checkbox
              v-for="role in allRoles"
              :key="role.id"
              :label="role.id"
              class="role-checkbox"
            >
              <div class="role-item">
                <span class="role-name">{{ role.roleName }}</span>
                <el-tag size="small" style="margin-left: 10px">{{ role.roleCode }}</el-tag>
                <span class="role-desc" v-if="role.description">{{ role.description }}</span>
              </div>
            </el-checkbox>
          </el-checkbox-group>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitRole" :loading="submitLoading">
            <el-icon><Check /></el-icon>
            确定分配
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, register, updateUser, deleteUser, getUserRoles, assignRolesToUser } from '@/api/user'
import { getRoleList } from '@/api/role'
import { formatDateTime } from '@/utils/date'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const roleDialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = ref('新增用户')
const formRef = ref(null)
const currentUserId = ref(null)
const currentUserName = ref('')
const selectedRoles = ref([])
const allRoles = ref([])

const searchForm = reactive({
  username: ''
})

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

const tableData = ref([])

const form = reactive({
  id: null,
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  status: 1
})

const viewData = ref({})

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 计算已选择角色数量
const selectedRoleCount = computed(() => selectedRoles.value.length)

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      username: searchForm.username
    }
    const res = await getUserList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  getList()
}

// 重置
const handleReset = () => {
  searchForm.username = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增用户'
  dialogVisible.value = true
}

// 查看
const handleView = (row) => {
  viewData.value = { ...row }
  viewDialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.id)
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
          await updateUser(form)
          ElMessage.success('更新成功')
        } else {
          await register(form)
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
    username: '',
    password: '',
    nickname: '',
    email: '',
    phone: '',
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

// 分配角色
const handleAssignRole = async (row) => {
  currentUserId.value = row.id
  currentUserName.value = row.username
  roleDialogVisible.value = true
  
  try {
    // 获取所有角色
    const rolesRes = await getRoleList({ current: 1, size: 100 })
    allRoles.value = rolesRes.data.records || []
    
    // 获取用户已有角色
    const userRolesRes = await getUserRoles(row.id)
    selectedRoles.value = userRolesRes.data.map(r => r.id)
    
    console.log('所有角色:', allRoles.value)
    console.log('用户已有角色:', selectedRoles.value)
  } catch (error) {
    console.error('获取角色数据失败:', error)
    ElMessage.error('获取角色数据失败')
  }
}

// 角色选择变化
const handleRoleChange = (value) => {
  console.log('选中的角色:', value)
}

// 提交角色分配
const handleSubmitRole = async () => {
  submitLoading.value = true
  try {
    await assignRolesToUser(currentUserId.value, selectedRoles.value)
    ElMessage.success(`成功为用户「${currentUserName.value}」分配 ${selectedRoles.value.length} 个角色`)
    roleDialogVisible.value = false
  } catch (error) {
    console.error('角色分配失败:', error)
    ElMessage.error('角色分配失败，请稍后重试')
  } finally {
    submitLoading.value = false
  }
}

// 角色对话框关闭
const handleRoleDialogClose = () => {
  currentUserId.value = null
  currentUserName.value = ''
  selectedRoles.value = []
  allRoles.value = []
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 10px;
  
}

/* 角色分配对话框样式 */
.user-management {
  padding: 10px;
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.user-management .el-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-bottom: 10px;
}

.user-management .el-card :deep(.el-card__header) {
  padding: 9px 10px;
  border-bottom: none;
}

.user-management .el-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 10px 10px 0 !important;
}

.user-management .el-table {
  flex: 1;
}

.role-dialog {
  padding: 10px 0;
}

.role-stats {
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.role-list {
  max-height: 400px;
  overflow-y: auto;
}

.role-checkbox {
  display: block;
  margin-bottom: 15px;
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  transition: all 0.3s;
}

.role-checkbox:hover {
  background-color: #f5f7fa;
  border-color: #409eff;
}

.role-item {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.role-name {
  font-weight: 600;
  font-size: 14px;
  color: #303133;
}

.role-desc {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
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
