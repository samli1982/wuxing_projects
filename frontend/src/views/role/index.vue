<template>
  <div class="role-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd" v-permission="'system:role:add'">
            <el-icon><Plus /></el-icon>
            新增角色
          </el-button>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
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
        :max-height="tableHeight"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="roleCode" label="角色编码" />
        <el-table-column prop="description" label="角色描述" />
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
            <el-button type="primary" size="small" @click="handleView(row)" v-permission="'system:role:query'">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)" v-permission="'system:role:edit'">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'system:role:delete'">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
            <el-button type="success" size="small" @click="handleAssignPermission(row)" v-permission="'system:role:assign'">
              <el-icon><Key /></el-icon>
              分配权限
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
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" :disabled="isEdit" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
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
    
    <!-- 分配权限对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      :title="`分配权限 - ${currentRoleName}`"
      width="700px"
      @close="handlePermissionDialogClose"
    >
      <div class="permission-dialog">
        <el-alert
          title="权限说明"
          type="info"
          :closable="false"
          style="margin-bottom: 20px"
        >
          <template #default>
            <div>勾选子权限后，父权限会自动包含。可以展开树状结构查看详细权限。</div>
          </template>
        </el-alert>
        
        <div class="permission-stats">
          <el-tag type="success">已选择: {{ selectedCount }} 个权限</el-tag>
          <el-tag type="info" style="margin-left: 10px">总计: {{ totalPermissionCount }} 个权限</el-tag>
        </div>
        
        <div class="permission-actions">
          <el-button size="small" @click="handleExpandAll">
            <el-icon><FolderOpened /></el-icon>
            展开全部
          </el-button>
          <el-button size="small" @click="handleCollapseAll">
            <el-icon><Folder /></el-icon>
            折叠全部
          </el-button>
          <el-button size="small" @click="handleCheckAll">
            <el-icon><Select /></el-icon>
            全选
          </el-button>
          <el-button size="small" @click="handleUncheckAll">
            <el-icon><CloseBold /></el-icon>
            取消全选
          </el-button>
        </div>
        
        <el-divider />
        
        <div class="permission-tree-container">
          <el-tree
            ref="permissionTreeRef"
            :data="permissionTreeData"
            show-checkbox
            node-key="id"
            :props="{ label: 'permissionName', children: 'children' }"
            :default-expand-all="false"
            :expand-on-click-node="false"
            @check="handleCheckChange"
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <el-icon v-if="data.permissionType === 1" style="color: #409eff">
                  <Menu />
                </el-icon>
                <el-icon v-else style="color: #67c23a">
                  <Key />
                </el-icon>
                <span style="margin-left: 8px">{{ data.permissionName }}</span>
                <el-tag
                  v-if="data.permissionType === 1"
                  size="small"
                  type="primary"
                  style="margin-left: 10px"
                >
                  菜单
                </el-tag>
                <el-tag
                  v-else
                  size="small"
                  type="success"
                  style="margin-left: 10px"
                >
                  按钮
                </el-tag>
                <el-tag
                  size="small"
                  style="margin-left: 5px"
                >
                  {{ data.permissionCode }}
                </el-tag>
              </span>
            </template>
          </el-tree>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitPermission" :loading="submitLoading">
            <el-icon><Check /></el-icon>
            确定分配
          </el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 查看对话框 -->
    <el-dialog v-model="viewDialogVisible" title="角色详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="角色ID">{{ viewData.id }}</el-descriptions-item>
        <el-descriptions-item label="角色名称">{{ viewData.roleName }}</el-descriptions-item>
        <el-descriptions-item label="角色编码">{{ viewData.roleCode }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewData.status === 1 ? 'success' : 'danger'">
            {{ viewData.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="角色描述" :span="2">{{ viewData.description }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ viewData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ viewData.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, createRole, updateRole, deleteRole, getRoleDetail } from '@/api/role'
import { getPermissionTree, getRolePermissions, assignPermissionsToRole } from '@/api/permission'
import { formatDateTime } from '@/utils/date'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = ref('新增角色')
const formRef = ref(null)
const permissionTreeRef = ref(null)
const currentRoleId = ref(null)
const currentRoleName = ref('')
const selectedCount = ref(0)
const totalPermissionCount = ref(0)
const tableHeight = ref(600)

const searchForm = reactive({
  roleName: ''
})

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

const tableData = ref([])
const permissionTreeData = ref([])

const form = reactive({
  id: null,
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

const viewData = ref({})

const formRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '角色编码只能包含大写字母和下划线', trigger: 'blur' }
  ]
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      roleName: searchForm.roleName
    }
    const res = await getRoleList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取角色列表失败:', error)
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
  searchForm.roleName = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增角色'
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
  dialogTitle.value = '编辑角色'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteRole(row.id)
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
          await updateRole(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await createRole(form)
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
    roleName: '',
    roleCode: '',
    description: '',
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

// 分配权限
const handleAssignPermission = async (row) => {
  currentRoleId.value = row.id
  currentRoleName.value = row.roleName
  permissionDialogVisible.value = true
  
  try {
    // 获取权限树（后端已经返回树形结构，直接使用）
    const treeRes = await getPermissionTree()
    permissionTreeData.value = treeRes.data || []
    
    console.log('权限树数据:', permissionTreeData.value)
    console.log('第一个节点children:', permissionTreeData.value[0]?.children)
    
    // 计算总权限数
    totalPermissionCount.value = countPermissions(permissionTreeData.value)
    
    // 获取角色已有权限
    const rolePermRes = await getRolePermissions(row.id)
    const permissionIds = rolePermRes.data.map(p => p.id)
    
    // 设置选中的权限（只选中叶子节点）
    await nextTick()
    if (permissionTreeRef.value) {
      const leafNodeIds = getLeafNodeIds(permissionTreeData.value, permissionIds)
      permissionTreeRef.value.setCheckedKeys(leafNodeIds, false)
      selectedCount.value = permissionTreeRef.value.getCheckedKeys().length
    }
  } catch (error) {
    console.error('获取权限数据失败:', error)
    ElMessage.error('获取权限数据失败')
  }
}

// 获取叶子节点ID（避免选中父节点后子节点被强制选中）
const getLeafNodeIds = (tree, checkedIds) => {
  const leafIds = []
  
  const traverse = (nodes) => {
    nodes.forEach(node => {
      if (checkedIds.includes(node.id)) {
        if (!node.children || node.children.length === 0) {
          // 叶子节点
          leafIds.push(node.id)
        } else {
          // 非叶子节点，继续遍历
          traverse(node.children)
        }
      }
    })
  }
  
  traverse(tree)
  return leafIds
}

// 计算权限总数
const countPermissions = (tree) => {
  let count = 0
  const traverse = (nodes) => {
    nodes.forEach(node => {
      count++
      if (node.children && node.children.length > 0) {
        traverse(node.children)
      }
    })
  }
  traverse(tree)
  return count
}

// 权限选择变化
const handleCheckChange = () => {
  if (permissionTreeRef.value) {
    selectedCount.value = permissionTreeRef.value.getCheckedKeys().length
  }
}

// 展开全部
const handleExpandAll = () => {
  if (!permissionTreeRef.value) return
  const allNodes = permissionTreeRef.value.store.nodesMap
  Object.values(allNodes).forEach(node => {
    node.expanded = true
  })
}

// 折叠全部
const handleCollapseAll = () => {
  if (!permissionTreeRef.value) return
  const allNodes = permissionTreeRef.value.store.nodesMap
  Object.values(allNodes).forEach(node => {
    node.expanded = false
  })
}

// 全选
const handleCheckAll = () => {
  if (!permissionTreeRef.value) return
  const allIds = []
  const traverse = (nodes) => {
    nodes.forEach(node => {
      allIds.push(node.id)
      if (node.children && node.children.length > 0) {
        traverse(node.children)
      }
    })
  }
  traverse(permissionTreeData.value)
  permissionTreeRef.value.setCheckedKeys(allIds)
  selectedCount.value = allIds.length
}

// 取消全选
const handleUncheckAll = () => {
  if (!permissionTreeRef.value) return
  permissionTreeRef.value.setCheckedKeys([])
  selectedCount.value = 0
}

// 提交权限分配
const handleSubmitPermission = async () => {
  if (!permissionTreeRef.value) return
  
  // 获取选中的权限ID（包括半选中的父节点）
  const checkedKeys = permissionTreeRef.value.getCheckedKeys()
  const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
  const allKeys = [...checkedKeys, ...halfCheckedKeys]
  
  if (allKeys.length === 0) {
    ElMessageBox.confirm(
      '您没有选择任何权限，该角色将无法访问任何功能。确定继续吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(async () => {
      await submitPermissions(allKeys)
    })
    return
  }
  
  await submitPermissions(allKeys)
}

const submitPermissions = async (permissionIds) => {
  submitLoading.value = true
  try {
    await assignPermissionsToRole(currentRoleId.value, permissionIds)
    ElMessage.success(`成功为角色「${currentRoleName.value}」分配 ${permissionIds.length} 个权限`)
    permissionDialogVisible.value = false
  } catch (error) {
    console.error('权限分配失败:', error)
    ElMessage.error('权限分配失败，请稍后重试')
  } finally {
    submitLoading.value = false
  }
}

// 权限对话框关闭
const handlePermissionDialogClose = () => {
  if (permissionTreeRef.value) {
    permissionTreeRef.value.setCheckedKeys([], false)
  }
  currentRoleId.value = null
  currentRoleName.value = ''
  selectedCount.value = 0
  totalPermissionCount.value = 0
  permissionTreeData.value = []
}

// 计算表格高度
function calculateTableHeight() {
  nextTick(() => {
    const cardBody = document.querySelector('.role-management .el-card__body')
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

onMounted(() => {
  getList()
  calculateTableHeight()
  window.addEventListener('resize', calculateTableHeight)
})
</script>

<style scoped>
.role-management {
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

.role-management {
  padding: 10px;
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.role-management .el-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-bottom: 10px;
}

.role-management .el-card :deep(.el-card__header) {
  padding: 9px 10px;
  border-bottom: none;
}

.role-management .el-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 10px 10px 0 !important;
}

.role-management .el-table {
  flex: 1;
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

/* 权限分配对话框样式 */
.permission-dialog {
  padding: 10px 0;
}

.permission-stats {
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.permission-actions {
  margin-bottom: 15px;
}

.permission-tree-container {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 15px;
}

.tree-node {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
