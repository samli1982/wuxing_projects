<template>
  <div class="menu-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜单管理</span>
          <el-button type="primary" @click="handleAdd(null)" v-permission="'system:permission:add'">
            <el-icon><Plus /></el-icon>
            新增菜单
          </el-button>
        </div>
      </template>

      <!-- 菜单树表格 -->
      <el-table 
        :data="menuTreeData" 
        style="width: 100%; flex: 1;" 
        border 
        stripe 
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        v-loading="loading"
      >
        <el-table-column prop="permissionName" label="菜单名称" min-width="200" />
        <el-table-column prop="permissionCode" label="权限编码" min-width="180" show-overflow-tooltip />
        <el-table-column prop="permissionType" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.permissionType === 1 ? 'primary' : 'success'" size="small">
              {{ row.permissionType === 1 ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="100" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.icon" :size="20">
              <component :is="row.icon" />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" min-width="150" show-overflow-tooltip />
        <el-table-column prop="component" label="组件路径" min-width="150" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleAdd(row)" v-permission="'system:permission:add'">
              <el-icon><Plus /></el-icon>
              新增
            </el-button>
            <el-button link type="primary" @click="handleEdit(row)" v-permission="'system:permission:edit'">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-permission="'system:permission:delete'">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="menuTreeOptions"
            :props="{ label: 'permissionName', value: 'id', children: 'children' }"
            check-strictly
            placeholder="请选择上级菜单"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="permissionType">
          <el-radio-group v-model="form.permissionType">
            <el-radio :label="1">菜单</el-radio>
            <el-radio :label="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="permissionName">
          <el-input v-model="form.permissionName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input v-model="form.permissionCode" placeholder="如: system:user:add" />
        </el-form-item>
        <el-form-item label="路由路径" v-if="form.permissionType === 1">
          <el-input v-model="form.path" placeholder="如: /system/user" />
        </el-form-item>
        <el-form-item label="组件路径" v-if="form.permissionType === 1">
          <el-input v-model="form.component" placeholder="如: views/user/index.vue" />
        </el-form-item>
        <el-form-item label="图标" v-if="form.permissionType === 1">
          <IconSelector v-model="form.icon" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getAllMenuTree, addPermission, updatePermission, deletePermission } from '@/api/menu'
import { usePermissionStore } from '@/stores/permission'
import IconSelector from '@/components/IconSelector.vue'

const permissionStore = usePermissionStore()

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const menuTreeData = ref([])
const menuTreeOptions = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const form = reactive({
  id: null,
  parentId: 0,
  permissionName: '',
  permissionCode: '',
  permissionType: 1,
  path: '',
  component: '',
  icon: '',
  sort: 0,
  status: 1
})

const rules = {
  permissionName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  permissionCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  permissionType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
}

// 加载菜单树
const loadMenuTree = async () => {
  loading.value = true
  try {
    const res = await getAllMenuTree()
    menuTreeData.value = res.data
    // 构建树形选择器选项（添加顶级选项）
    menuTreeOptions.value = [
      { id: 0, permissionName: '顶级菜单', children: res.data }
    ]
  } catch (error) {
    ElMessage.error('加载菜单失败')
  } finally {
    loading.value = false
  }
}

// 新增
const handleAdd = (row) => {
  resetForm()
  if (row) {
    form.parentId = row.id
  }
  dialogTitle.value = '新增菜单'
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  Object.assign(form, {
    id: row.id,
    parentId: row.parentId || 0,
    permissionName: row.permissionName,
    permissionCode: row.permissionCode,
    permissionType: row.permissionType,
    path: row.path || '',
    component: row.component || '',
    icon: row.icon || '',
    sort: row.sort || 0,
    status: row.status
  })
  dialogTitle.value = '编辑菜单'
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  // 检查是否有子菜单
  if (row.children && row.children.length > 0) {
    ElMessage.warning('请先删除子菜单')
    return
  }
  
  try {
    await ElMessageBox.confirm('确定要删除该菜单吗?', '提示', {
      type: 'warning'
    })
    await deletePermission(row.id)
    ElMessage.success('删除成功')
    loadMenuTree()
    
    // 刷新左侧菜单
    await permissionStore.refreshMenus()
    ElMessage.success('菜单已更新')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      const data = { ...form }
      if (form.id) {
        await updatePermission(form.id, data)
        ElMessage.success('更新成功')
      } else {
        await addPermission(data)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadMenuTree()
      
      // 刷新左侧菜单
      await permissionStore.refreshMenus()
      ElMessage.success('菜单已更新')
    } catch (error) {
      ElMessage.error(form.id ? '更新失败' : '新增失败')
    } finally {
      submitting.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
    parentId: 0,
    permissionName: '',
    permissionCode: '',
    permissionType: 1,
    path: '',
    component: '',
    icon: '',
    sort: 0,
    status: 1
  })
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

onMounted(() => {
  loadMenuTree()
})
</script>

<style scoped>
.menu-management {
  padding: 10px;
  height: calc(100vh - 60px);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.menu-management .el-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-bottom: 10px;
}

.menu-management .el-card :deep(.el-card__header) {
  padding: 9px 10px;
  border-bottom: none;
}

.menu-management .el-card :deep(.el-card__body) {
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
</style>
