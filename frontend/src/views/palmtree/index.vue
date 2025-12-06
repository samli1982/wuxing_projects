<template>
  <div class="palmtree-management">
    <el-card class="palmtree-card">
      <template #header>
        <div class="card-header">
          <span>命盘管理</span>
        </div>
      </template>

      <!-- 快速按会员手机号/昵称查询入口（双入口） -->
      <div class="quick-search">
        <el-form :inline="true" class="search-form" label-width="120px">
          <el-form-item label="会员手机号/昵称">
            <el-input
              v-model="memberSearch.keyword"
              placeholder="输入手机号或昵称后回车"
              clearable
              @keyup.enter="handleSearchMemberForPalmtrees"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleSearchMemberForPalmtrees">查询命盘</el-button>
            <el-button icon="Refresh" @click="memberSearch.keyword = ''">清空</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="selectedMember" class="palmtree-list-section">
        <div class="section-header">
          <h3>
            <el-avatar :size="32" class="member-avatar">
              {{ selectedMember.nickname?.substring(0, 1) || '未' }}
            </el-avatar>
            <span>{{ selectedMember.nickname }} 的命盘列表</span>
          </h3>
          <div class="header-actions">
            <el-button size="small" @click="handleBackToMember">返回会员</el-button>
            <el-button type="primary" size="small" @click="handleExportPalmtrees">导出数据</el-button>
            <el-button type="warning" size="small" @click="handleRecalculatePalmtrees" :disabled="selectedPalmtrees.length === 0">🔄 批量重新计算 ({{ selectedPalmtrees.length }})</el-button>
          </div>
        </div>

        <!-- 命盘搜索表单 -->
        <el-form :model="palmtreeQuery" :inline="true" class="search-form" label-width="80px">
          <el-form-item label="命盘名称">
            <el-input v-model="palmtreeQuery.keyword" placeholder="请输入命盘昵称" clearable @keyup.enter="handleQueryPalmtrees" />
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="palmtreeQuery.gender" placeholder="请选择性别" clearable>
              <el-option label="全部" :value="null" />
              <el-option label="男" value="male" />
              <el-option label="女" value="female" />
              <el-option label="其他" value="other" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQueryPalmtrees">搜索</el-button>
            <el-button icon="Refresh" @click="resetPalmtreeQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 命盘表格 -->
        <el-table v-loading="palmtreeLoading" :data="palmtreeList" border class="palmtree-table" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" />
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="nickname" label="命盘名称" min-width="120" />
          <el-table-column prop="realName" label="真实姓名" width="100" />
          <el-table-column label="出生日期" width="130">
            <template #default="{ row }">
              <span>{{ formatBirthDate(row) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="birthCity" label="出生地" min-width="100" show-overflow-tooltip />
          <el-table-column prop="gender" label="性别" width="60">
            <template #default="{ row }">
              <span v-if="row.gender === 'male'">男</span>
              <span v-else-if="row.gender === 'female'">女</span>
              <span v-else>其他</span>
            </template>
          </el-table-column>
          <el-table-column prop="calendarType" label="历法" width="60">
            <template #default="{ row }">
              <el-tag v-if="row.calendarType === 'gregorian'" type="info">公历</el-tag>
              <el-tag v-else type="warning">农历</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="健康分析" width="80">
            <template #default="{ row }">
              <el-tag v-if="row.forHealthAnalysis === 1" type="success">是</el-tag>
              <el-tag v-else type="info">否</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160">
            <template #default="{ row }">
              <span>{{ formatDate(row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleViewPalmtree(row)">查看详情</el-button>
              <el-button type="danger" link size="small" @click="handleDeletePalmtree(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 命盘分页 -->
        <div class="pagination-container">
          <el-pagination v-model:current-page="palmtreeQuery.pageNum" v-model:page-size="palmtreeQuery.pageSize" :page-sizes="[20, 30, 50, 100]" :total="palmtreeTotal" layout="total, sizes, prev, pager, next, jumper" @size-change="handlePalmtreeSizeChange" @current-change="handlePalmtreeCurrentChange" />
        </div>
      </div>

      <!-- 无会员选择的提示 -->
      <el-empty v-else description="请从会员列表进入命盘管理" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as api from '@/api/palmtree'
import * as memberApi from '@/api/member'

const router = useRouter()
const route = useRoute()

// 命盘查询参数
const palmtreeQuery = reactive({
  keyword: '',
  gender: null,
  pageNum: 1,
  pageSize: 20
})

// 会员快速搜索
const memberSearch = reactive({ keyword: '' })

// 命盘列表与加载状态
const palmtreeList = ref([])
const palmtreeLoading = ref(false)
const palmtreeTotal = ref(0)

// 选中的会员和命盘
const selectedMember = ref(null)
const selectedPalmtrees = ref([])

// 初始化
onMounted(async () => {
  const memberId = route.query.memberId
  if (memberId) {
    try {
      const res = await memberApi.getMemberById(memberId)
      const data = res.data || res
      selectedMember.value = data?.data || data
      palmtreeQuery.pageNum = 1
      palmtreeQuery.pageSize = 20
      await handleQueryPalmtrees()
    } catch (e) {
      ElMessage.error('会员信息加载失败')
    }
  }
})

// ============ 命盘相关方法 ============
const handleQueryPalmtrees = async () => {
  if (!selectedMember.value) {
    ElMessage.warning('请先选择一个会员')
    return
  }

  palmtreeLoading.value = true
  try {
    const res = await api.getPalmtreesByMember(selectedMember.value.id, {
      keyword: palmtreeQuery.keyword,
      gender: palmtreeQuery.gender,
      pageNum: palmtreeQuery.pageNum,
      pageSize: palmtreeQuery.pageSize
    })
    const data = res.data || res
    palmtreeList.value = data.records || data.data?.records || []
    palmtreeTotal.value = data.total || data.data?.total || 0
  } catch (error) {
    ElMessage.error('加载命盘列表失败')
    console.error(error)
  } finally {
    palmtreeLoading.value = false
  }
}

const resetPalmtreeQuery = () => {
  palmtreeQuery.keyword = ''
  palmtreeQuery.gender = null
  palmtreeQuery.pageNum = 1
  palmtreeQuery.pageSize = 20
  handleQueryPalmtrees()
}

const handlePalmtreeSizeChange = () => {
  palmtreeQuery.pageNum = 1
  handleQueryPalmtrees()
}

const handlePalmtreeCurrentChange = () => {
  handleQueryPalmtrees()
}

const handleViewPalmtree = (row) => {
  router.push(`/palmtree/detail/${row.id}`)
}

const handleDeletePalmtree = (row) => {
  ElMessageBox.confirm(
    `确定删除命盘"${row.nickname}"吗？删除后将无法恢复。`,
    '删除确认',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await api.deletePalmtree(row.id)
      ElMessage.success('删除成功')
      handleQueryPalmtrees()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleSearchMemberForPalmtrees = async () => {
  const keyword = memberSearch.keyword?.trim()
  if (!keyword) {
    ElMessage.warning('请输入会员手机号或昵称')
    return
  }
  try {
    const res = await memberApi.getMemberList({
      keyword: keyword,
      pageNum: 1,
      pageSize: 2
    })
    const data = res.data || res
    const records = data.records || data.data?.records || []
    if (!records || records.length === 0) {
      ElMessage.info('未找到匹配的会员，请更精确输入')
      return
    }
    if (records.length > 1) {
      ElMessage.warning('找到多个会员，请输入更精确的手机号或昵称')
      return
    }
    selectedMember.value = records[0]
    palmtreeQuery.pageNum = 1
    palmtreeQuery.pageSize = 20
    palmtreeQuery.keyword = ''
    palmtreeQuery.gender = null
    await handleQueryPalmtrees()
  } catch (e) {
    ElMessage.error('查询会员失败')
  }
}

const handleExportPalmtrees = () => {
  ElMessage.info('导出功能开发中...')
}

const handleBackToMember = () => {
  router.push('/member')
}

const handleSelectionChange = (selection) => {
  selectedPalmtrees.value = selection
}

const handleRecalculatePalmtrees = async () => {
  if (selectedPalmtrees.value.length === 0) {
    ElMessage.warning('请选择至少一个命盘')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定重新计算选中的 ${selectedPalmtrees.value.length} 个命盘吗？此操作将重新计算所有数据。`,
      '批量重新计算',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )

    palmtreeLoading.value = true
    const ids = selectedPalmtrees.value.map(p => Number(p.id))
    const res = await api.recalculatePalmtrees(ids)
    const data = res.data || res
    const result = data.data || data

    ElMessage.success(
      `重新计算完成！成功 ${result.successCount} 个，失败 ${result.failureCount} 个`
    )

    if (result.errors && result.errors.length > 0) {
      console.warn('错误信息:', result.errors)
    }

    selectedPalmtrees.value = []
    await handleQueryPalmtrees()
  } catch (error) {
    if (!error.toString().includes('cancel')) {
      ElMessage.error('重新计算失败')
      console.error(error)
    }
  } finally {
    palmtreeLoading.value = false
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-')
}

const formatBirthDate = (palmtree) => {
  if (!palmtree.birthYear) return '-'
  const month = String(palmtree.birthMonth).padStart(2, '0')
  const day = String(palmtree.birthDay).padStart(2, '0')
  const hour = String(palmtree.birthHour || 0).padStart(2, '0')
  const minute = String(palmtree.birthMinute || 0).padStart(2, '0')
  return `${palmtree.birthYear}-${month}-${day} ${hour}:${minute}`
}
</script>

<style scoped lang="scss">
.palmtree-management {
  padding: 10px;
  height: calc(100vh - 60px);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;

  .palmtree-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    margin-bottom: 10px;

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

  .quick-search {
    margin-bottom: 10px;
  }

  .palmtree-list-section {
    .section-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 10px;

      h3 {
        margin: 0;
        font-size: 14px;
        color: #333;
        display: flex;
        align-items: center;
        gap: 8px;

        .member-avatar {
          flex-shrink: 0;
        }
      }

      .header-actions {
        display: flex;
        gap: 8px;
        align-items: center;
      }
    }

    .search-form {
      margin-bottom: 10px;
    }

    .palmtree-table {
      flex: 1;
    }
  }

  .pagination-container {
    margin-top: 5px !important;
    padding: 0 !important;
    border-top: none !important;
    display: flex;
    justify-content: flex-end;

    :deep(.el-pagination) {
      padding: 0 !important;
      margin: 0 !important;
    }
  }
}
</style>
