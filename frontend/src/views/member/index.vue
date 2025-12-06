<template>
  <div class="member-management">
    <el-card class="member-card">
      <template #header>
        <div class="card-header">
          <span>会员管理</span>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form 
        :model="queryParams" 
        ref="queryForm" 
        :inline="true" 
        class="search-form"
        label-width="80px"
      >
        <el-form-item label="昵称">
          <el-input
            v-model="queryParams.nickname"
            placeholder="请输入昵称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        
        <el-form-item label="手机号">
          <el-input
            v-model="queryParams.phone"
            placeholder="请输入手机号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        
        <el-form-item label="会员等级">
          <el-select v-model="queryParams.memberLevel" placeholder="请选择会员等级" clearable>
            <el-option label="全部" :value="null" />
            <el-option label="普通会员" :value="1" />
            <el-option label="VIP" :value="2" />
            <el-option label="SVIP" :value="3" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="memberList"
        border
        class="member-table"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="nickname" label="昵称" min-width="120">
          <template #default="{ row }">
            <div class="nickname-cell">
              <el-avatar 
                v-if="row.avatar && row.avatar.startsWith('http')" 
                :size="32" 
                :src="row.avatar" 
                class="avatar"
              />
              <el-avatar 
                v-else 
                :size="32" 
                class="avatar"
              >
                {{ row.nickname?.substring(0, 1) || '未' }}
              </el-avatar>
              <span class="nickname">{{ row.nickname }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="openid" label="OpenID" min-width="180" show-overflow-tooltip />
        
        <el-table-column prop="phone" label="手机号" width="120" />
        
        <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />
        
        <el-table-column prop="memberLevel" label="会员等级" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.memberLevel === 3" type="danger">SVIP</el-tag>
            <el-tag v-else-if="row.memberLevel === 2" type="warning">VIP</el-tag>
            <el-tag v-else type="info">普通会员</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="points" label="积分" width="80" />
        
        <el-table-column prop="city" label="城市" width="100" show-overflow-tooltip />
        
        <el-table-column prop="lastLoginTime" label="最后登录" width="160">
          <template #default="{ row }">
            <span>{{ formatDate(row.lastLoginTime) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.gender === 1">男</el-tag>
            <el-tag v-else-if="row.gender === 2">女</el-tag>
            <span v-else>未知</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="注册时间" width="160">
          <template #default="{ row }">
            <span>{{ formatDate(row.createTime) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              link 
              size="small"
              @click="handleView(row)"
            >
              查看
            </el-button>
            <el-button 
              type="primary" 
              link 
              size="small"
              @click="handleOpenPalmtrees(row)"
            >
              命盘列表
            </el-button>
            <el-button 
              type="primary" 
              link 
              size="small"
              @click="handleStatusChange(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[20, 30, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 会员详情弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="会员详情"
      width="600px"
    >
      <el-form
        v-if="currentMember"
        :model="currentMember"
        label-width="100px"
      >
        <el-form-item label="昵称">
          <span>{{ currentMember.nickname }}</span>
        </el-form-item>
        
        <el-form-item label="OpenID">
          <span>{{ currentMember.openid }}</span>
        </el-form-item>
        
        <el-form-item label="手机号">
          <span>{{ currentMember.phone || '-' }}</span>
        </el-form-item>
        
        <el-form-item label="邮箱">
          <span>{{ currentMember.email || '-' }}</span>
        </el-form-item>
        
        <el-form-item label="真实姓名">
          <span>{{ currentMember.realName || '-' }}</span>
        </el-form-item>
        
        <el-form-item label="生日">
          <span>{{ currentMember.birthday || '-' }}</span>
        </el-form-item>
        
        <el-form-item label="地区">
          <span>{{ formatLocation(currentMember) }}</span>
        </el-form-item>
        
        <el-form-item label="会员等级">
          <el-tag v-if="currentMember.memberLevel === 3" type="danger">SVIP</el-tag>
          <el-tag v-else-if="currentMember.memberLevel === 2" type="warning">VIP</el-tag>
          <el-tag v-else type="info">普通会员</el-tag>
        </el-form-item>
        
        <el-form-item label="积分">
          <span>{{ currentMember.points || 0 }}</span>
        </el-form-item>
        
        <el-form-item label="会员过期">
          <span>{{ formatDate(currentMember.memberExpireTime) }}</span>
        </el-form-item>
        
        <el-form-item label="性别">
          <span>{{ formatGender(currentMember.gender) }}</span>
        </el-form-item>
        
        <el-form-item label="状态">
          <el-tag v-if="currentMember.status === 1" type="success">正常</el-tag>
          <el-tag v-else type="danger">禁用</el-tag>
        </el-form-item>
        
        <el-form-item label="最后登录">
          <span>{{ formatDate(currentMember.lastLoginTime) }}</span>
        </el-form-item>
        
        <el-form-item label="登录IP">
          <span>{{ currentMember.lastLoginIp || '-' }}</span>
        </el-form-item>
        
        <el-form-item label="注册时间">
          <span>{{ formatDate(currentMember.createTime) }}</span>
        </el-form-item>
        
        <el-form-item label="最后更新">
          <span>{{ formatDate(currentMember.updateTime) }}</span>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/request'

// 数据状态
const loading = ref(false)
const memberList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const currentMember = ref(null)
const router = useRouter()

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  nickname: '',
  phone: '',
  memberLevel: null
})

// 获取会员列表
const getMemberList = async () => {
  loading.value = true
  try {
    const res = await api({
      url: '/member/list',
      method: 'get',
      params: queryParams
    })
    
    if (res.code === 200) {
      memberList.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取会员列表失败')
    }
  } catch (error) {
    console.error('获取会员列表失败:', error)
    ElMessage.error('获取会员列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getMemberList()
}

// 重置查询
const resetQuery = () => {
  queryParams.nickname = ''
  queryParams.phone = ''
  queryParams.memberLevel = null
  queryParams.pageNum = 1
  getMemberList()
}

// 分页变化
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getMemberList()
}

const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getMemberList()
}

// 查看详情
const handleView = (row) => {
  currentMember.value = { ...row }
  dialogVisible.value = true
}

// 查看该会员的命盘列表
const handleOpenPalmtrees = (row) => {
  router.push(`/palmtree?memberId=${row.id}&nickname=${encodeURIComponent(row.nickname || '')}`)
}

// 状态变更
const handleStatusChange = (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  
  ElMessageBox.confirm(
    `确定要${action}会员"${row.nickname}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await api({
        url: `/member/${row.id}/status`,
        method: 'put',
        params: { status: newStatus }
      })
      
      if (res.code === 200) {
        ElMessage.success(`${action}成功`)
        row.status = newStatus
      } else {
        ElMessage.error(res.message || `${action}失败`)
      }
    } catch (error) {
      console.error(`${action}失败:`, error)
      ElMessage.error(`${action}失败: ` + (error.message || '网络错误'))
    }
  }).catch(() => {
    // 取消操作
  })
}

// 格式化性别
const formatGender = (gender) => {
  switch (gender) {
    case 1: return '男'
    case 2: return '女'
    default: return '未知'
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  try {
    const date = new Date(dateString)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    }).replace(/\//g, '-')
  } catch (e) {
    return dateString
  }
}

// 格式化地区
const formatLocation = (member) => {
  const parts = []
  if (member.country) parts.push(member.country)
  if (member.province) parts.push(member.province)
  if (member.city) parts.push(member.city)
  return parts.length > 0 ? parts.join(' - ') : '-'
}

// 初始化
onMounted(() => {
  getMemberList()
})
</script>

<style scoped lang="scss">
.member-management {
  padding: 10px;
  height: calc(100vh - 60px);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  
  .member-card {
    flex: 1;
    margin-bottom: 10px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    
    :deep(.el-card__header) {
      padding: 9px 10px;
      border-bottom: none;
    }
    
    :deep(.el-card__body) {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      padding: 10px 10px 0 !important;
    }
  }
  
  .search-form {
    margin-bottom: 10px;
  }
  
  .member-table {
    flex: 1;
    
    :deep(.el-table__cell) {
      padding: 8px 0;
    }
  }
  
  .nickname-cell {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .avatar {
      flex-shrink: 0;
    }
    
    .nickname {
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
  
  .pagination-container {
    margin-top: 5px !important;
    padding: 0 !important;
    border-top: none;
    justify-content: flex-end;
    display: flex;
    
    :deep(.el-pagination) {
      padding: 0 !important;
      margin: 0 !important;
    }
  }
}
</style>