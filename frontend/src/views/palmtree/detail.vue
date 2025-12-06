<template>
  <div class="palmtree-detail">
    <el-card class="detail-card">
      <template #header>
        <div class="card-header">
          <el-button icon="Back" @click="handleGoBack">返回</el-button>
          <span>命盘详情</span>
          <div class="right-actions">
            <el-button type="primary" size="small" @click="handleEdit" v-if="!isEditing">编辑</el-button>
            <el-button type="success" size="small" @click="handleSave" v-if="isEditing">保存</el-button>
            <el-button size="small" @click="handleCancel" v-if="isEditing">取消</el-button>
            <el-button type="danger" size="small" @click="handleDelete">删除</el-button>
          </div>
        </div>
      </template>

      <el-skeleton :loading="loading" animated>
        <div v-if="palmtree">
          <el-tabs tab-position="top" class="detail-tabs">
            <!-- 基本信息标签 -->
            <el-tab-pane label="基本信息">
              <el-form :model="palmtree" label-width="120px" class="detail-form">
                <el-row :gutter="40">
                  <el-col :span="12">
                    <el-form-item label="命盘名称">
                      <el-input v-if="isEditing" v-model="palmtree.nickname" />
                      <span v-else>{{ palmtree.nickname }}</span>
                    </el-form-item>
                    <el-form-item label="真实姓名">
                      <el-input v-if="isEditing" v-model="palmtree.realName" />
                      <span v-else>{{ palmtree.realName || '-' }}</span>
                    </el-form-item>
                    <el-form-item label="性别">
                      <el-select v-if="isEditing" v-model="palmtree.gender">
                        <el-option label="男" value="male" />
                        <el-option label="女" value="female" />
                        <el-option label="其他" value="other" />
                      </el-select>
                      <span v-else>
                        <span v-if="palmtree.gender === 'male'">男</span>
                        <span v-else-if="palmtree.gender === 'female'">女</span>
                        <span v-else>其他</span>
                      </span>
                    </el-form-item>
                    <el-form-item label="历法">
                      <el-select v-if="isEditing" v-model="palmtree.calendarType">
                        <el-option label="公历" value="gregorian" />
                        <el-option label="农历" value="lunar" />
                      </el-select>
                      <span v-else>{{ palmtree.calendarType === 'gregorian' ? '公历' : '农历' }}</span>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="出生年月日">
                      <el-date-picker
                        v-if="isEditing"
                        v-model="birthDate"
                        type="date"
                        value-format="YYYY-MM-DD"
                        @change="onBirthDateChange"
                      />
                      <span v-else>{{ formatBirthDate(palmtree) }}</span>
                    </el-form-item>
                    <el-form-item label="出生时辰">
                      <el-select v-if="isEditing" v-model="palmtree.birthHourIndex">
                        <el-option label="不详" :value="0" />
                        <el-option label="子时" :value="1" />
                        <el-option label="丑时" :value="2" />
                        <el-option label="寅时" :value="3" />
                        <el-option label="卯时" :value="4" />
                        <el-option label="辰时" :value="5" />
                        <el-option label="巳时" :value="6" />
                        <el-option label="午时" :value="7" />
                        <el-option label="未时" :value="8" />
                        <el-option label="申时" :value="9" />
                        <el-option label="酉时" :value="10" />
                        <el-option label="戌时" :value="11" />
                        <el-option label="亥时" :value="12" />
                      </el-select>
                      <span v-else>{{ getHourLabel(palmtree.birthHourIndex) }}</span>
                    </el-form-item>
                    <el-form-item label="出生地">
                      <el-input v-if="isEditing" v-model="palmtree.birthCity" />
                      <span v-else>{{ palmtree.birthCity || '-' }}</span>
                    </el-form-item>
                    <el-form-item label="健康分析">
                      <el-switch
                        v-if="isEditing"
                        v-model="palmtree.forHealthAnalysis"
                        :active-value="1"
                        :inactive-value="0"
                      />
                      <el-tag v-else :type="palmtree.forHealthAnalysis === 1 ? 'success' : 'info'">
                        {{ palmtree.forHealthAnalysis === 1 ? '是' : '否' }}
                      </el-tag>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </el-tab-pane>

            <!-- 八字信息标签 -->
            <el-tab-pane label="八字信息" v-if="detail">
              <el-form :model="detail" label-width="120px" class="detail-form">
                <el-row :gutter="40">
                  <el-col :span="12">
                    <el-form-item label="年柱">
                      <span class="eight-char">{{ detail.yearHeavenlyStem }}{{ detail.yearEarthlyBranch }}</span>
                    </el-form-item>
                    <el-form-item label="月柱">
                      <span class="eight-char">{{ detail.monthHeavenlyStem }}{{ detail.monthEarthlyBranch }}</span>
                    </el-form-item>
                    <el-form-item label="日柱">
                      <span class="eight-char">{{ detail.dayHeavenlyStem }}{{ detail.dayEarthlyBranch }}</span>
                    </el-form-item>
                    <el-form-item label="时柱">
                      <span class="eight-char">{{ detail.hourHeavenlyStem }}{{ detail.hourEarthlyBranch }}</span>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="纳音">
                      <span>{{ detail.nayin || '-' }}</span>
                    </el-form-item>
                    <el-form-item label="空亡">
                      <span>{{ detail.kongwang || '-' }}</span>
                    </el-form-item>
                    <el-form-item label="胎元">
                      <span>{{ detail.taiyuan || '-' }}</span>
                    </el-form-item>
                    <el-form-item label="喜用神">
                      <span v-if="detail.usefulGods">{{ parseUsefulGods(detail.usefulGods).join(', ') || '-' }}</span>
                      <span v-else>-</span>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </el-tab-pane>

            <!-- 五行信息标签 -->
            <el-tab-pane label="五行信息" v-if="detail && detail.wuxingData">
              <el-form label-width="120px" class="detail-form">
                <el-row :gutter="40">
                  <el-col :span="12">
                    <el-form-item label="五行分布">
                      <div class="wuxing-distribution">
                        <div v-for="(count, element) in parseWuxingData(detail.wuxingData)" :key="element" class="wuxing-item">
                          <span class="wuxing-name" :class="'wuxing-' + element">{{ element }}</span>
                          <span class="wuxing-count">{{ count }}</span>
                        </div>
                      </div>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="强弱等级">
                      <div v-if="detail.wuxingStrength">
                        {{ detail.wuxingStrength }}
                      </div>
                      <span v-else>-</span>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </el-tab-pane>

            <!-- 体质信息标签 -->
            <el-tab-pane label="体质信息" v-if="detail">
              <el-form label-width="120px" class="detail-form">
                <el-row :gutter="40">
                  <el-col :span="12">
                    <el-form-item label="体质类型">
                      <el-tag>{{ detail.constitutionType || '-' }}</el-tag>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="体质分类">
                      <span>{{ detail.constitutionCategory || '-' }}</span>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="40">
                  <el-col :span="24">
                    <el-form-item label="体质描述">
                      <el-input
                        v-model="detail.constitutionAnalysis"
                        type="textarea"
                        :rows="4"
                        readonly
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </el-tab-pane>

            <!-- 五运六气标签 -->
            <el-tab-pane label="五运六气" v-if="detail && detail.wuyunliuqiData">
              <el-form label-width="120px" class="detail-form">
                <el-row :gutter="40">
                  <el-col :span="24">
                    <el-form-item label="五运六气">
                      <el-input
                        v-model="detail.wuyunliuqiData"
                        type="textarea"
                        :rows="6"
                        readonly
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </el-tab-pane>

            <!-- 其他信息标签 -->
            <el-tab-pane label="其他信息">
              <el-form :model="palmtree" label-width="120px" class="detail-form">
                <el-form-item label="创建时间">
                  <span>{{ formatDate(palmtree.createTime) }}</span>
                </el-form-item>
                <el-form-item label="更新时间">
                  <span>{{ formatDate(palmtree.updateTime) }}</span>
                </el-form-item>
                <el-form-item label="状态">
                  <el-tag :type="palmtree.status === 1 ? 'success' : 'danger'">
                    {{ palmtree.status === 1 ? '正常' : '禁用' }}
                  </el-tag>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-skeleton>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as api from '@/api/palmtree'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const isEditing = ref(false)
const palmtree = ref(null)
const detail = ref(null)
const birthDate = ref(null)
const originalPalmtree = ref(null)

// 初始化
onMounted(() => {
  loadPalmtreeDetail()
})

// 加载命盘详情
const loadPalmtreeDetail = async () => {
  loading.value = true
  try {
    const res = await api.getPalmtreeDetail(route.params.id)
    const data = res.data || res
    palmtree.value = data.palmtree || {}
    detail.value = data.detail || null
    originalPalmtree.value = JSON.parse(JSON.stringify(palmtree.value))
    
    // 初始化出生日期
    if (palmtree.value.birthYear && palmtree.value.birthMonth && palmtree.value.birthDay) {
      const month = String(palmtree.value.birthMonth).padStart(2, '0')
      const day = String(palmtree.value.birthDay).padStart(2, '0')
      birthDate.value = `${palmtree.value.birthYear}-${month}-${day}`
    }
  } catch (error) {
    ElMessage.error('加载命盘详情失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 出生日期变更处理
const onBirthDateChange = (val) => {
  if (val) {
    const date = new Date(val)
    palmtree.value.birthYear = date.getFullYear()
    palmtree.value.birthMonth = date.getMonth() + 1
    palmtree.value.birthDay = date.getDate()
  }
}

// 编辑
const handleEdit = () => {
  isEditing.value = true
}

// 保存
const handleSave = async () => {
  try {
    await api.updatePalmtree(palmtree.value.id, palmtree.value)
    ElMessage.success('保存成功')
    isEditing.value = false
    originalPalmtree.value = JSON.parse(JSON.stringify(palmtree.value))
  } catch (error) {
    ElMessage.error('保存失败')
    console.error(error)
  }
}

// 取消编辑
const handleCancel = () => {
  palmtree.value = JSON.parse(JSON.stringify(originalPalmtree.value))
  isEditing.value = false
}

// 删除
const handleDelete = () => {
  ElMessageBox.confirm(
    `确定删除命盘"${palmtree.value.nickname}"吗？删除后将无法恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await api.deletePalmtree(palmtree.value.id)
      ElMessage.success('删除成功')
      router.push('/palmtree')
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 用户取消
  })
}

// 返回
const handleGoBack = () => {
  router.push('/palmtree')
}

// 格式化日期
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

// 格式化出生日期
const formatBirthDate = (palm) => {
  if (!palm.birthYear) return '-'
  const month = String(palm.birthMonth).padStart(2, '0')
  const day = String(palm.birthDay).padStart(2, '0')
  const hour = String(palm.birthHour || 0).padStart(2, '0')
  const minute = String(palm.birthMinute || 0).padStart(2, '0')
  return `${palm.birthYear}-${month}-${day} ${hour}:${minute}`
}

// 获取时辰标签
const getHourLabel = (index) => {
  const hours = ['不详', '子', '丑', '寅', '卯', '辰', '巳', '午', '未', '申', '酉', '戌', '亥']
  return hours[index] || '不详'
}

// 解析六行数据
const parseWuxingData = (wuxingDataStr) => {
  try {
    if (typeof wuxingDataStr === 'string') {
      const data = JSON.parse(wuxingDataStr)
      return data
    }
    return wuxingDataStr
  } catch (e) {
    return {}
  }
}

// 解析喜用神
const parseUsefulGods = (usefulGodsStr) => {
  try {
    if (typeof usefulGodsStr === 'string') {
      const data = JSON.parse(usefulGodsStr)
      return data.names || []
    }
    return usefulGodsStr.names || []
  } catch (e) {
    return []
  }
}
</script>

<style scoped lang="scss">
.palmtree-detail {
  padding: 10px;
  height: calc(100vh - 60px);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: auto;

  .detail-card {
    flex: 1;
    overflow: hidden;
    display: flex;
    flex-direction: column;

    ::v-deep(.el-card__header) {
      padding: 9px 10px;
      border-bottom: 1px solid #f0f0f0;

      .card-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 20px;

        span {
          flex: 1;
          font-size: 14px;
          font-weight: 500;
        }

        .right-actions {
          display: flex;
          gap: 5px;
        }
      }
    }

    ::v-deep(.el-card__body) {
      padding: 10px 10px 0 !important;
      flex: 1;
      overflow: auto;
    }
  }

  .detail-tabs {
    height: 100%;

    ::v-deep(.el-tabs__content) {
      height: 100%;
      overflow: auto;
    }

    ::v-deep(.el-tab-pane) {
      height: 100%;
    }
  }

  .detail-form {
    padding: 10px 0;

    ::v-deep(.el-form-item) {
      margin-bottom: 10px;
    }

    .eight-char {
      display: inline-block;
      padding: 4px 12px;
      border: 2px solid #07c160;
      border-radius: 4px;
      font-weight: 600;
      color: #333;
      background: #f9f9f9;
    }

    .wuxing-distribution {
      display: grid;
      grid-template-columns: repeat(5, 1fr);
      gap: 10px;

      .wuxing-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 5px;
        padding: 8px;
        border-radius: 4px;
        background: #f5f5f5;

        .wuxing-name {
          font-weight: 600;
          font-size: 14px;

          &.wuxing-木 {
            color: #3a9e8f;
          }

          &.wuxing-火 {
            color: #e63946;
          }

          &.wuxing-土 {
            color: #f4d06f;
          }

          &.wuxing-金 {
            color: #666;
          }

          &.wuxing-水 {
            color: #457b9d;
          }
        }

        .wuxing-count {
          font-size: 18px;
          font-weight: bold;
          color: #333;
        }
      }
    }
  }
}
</style>
