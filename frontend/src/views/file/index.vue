<template>
  <div class="file-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>文件管理</span>
          <el-upload
            :action="uploadAction"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :show-file-list="false"
            :data="{ category: queryForm.category }"
          >
            <el-button type="primary" v-permission="'system:file:upload'">
              <el-icon><Upload /></el-icon>
              上传文件
            </el-button>
          </el-upload>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="queryForm" :inline="true" style="margin-top: 10px; margin-bottom: 10px;">
        <el-form-item label="文件名称">
          <el-input v-model="queryForm.fileName" placeholder="请输入文件名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="文件分类">
          <el-select v-model="queryForm.category" placeholder="请选择分类" clearable style="width: 150px">
            <el-option label="默认" value="default" />
            <el-option label="图片" value="image" />
            <el-option label="文档" value="document" />
            <el-option label="视频" value="video" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 文件列表 -->
      <el-table :data="fileList" style="width: 100%; flex: 1;" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="fileName" label="文件名称" min-width="200" show-overflow-tooltip />
        <el-table-column label="预览" width="100" align="center">
          <template #default="{ row }">
            <el-image 
              v-if="isImage(row.fileType)"
              :src="row.fileUrl" 
              :preview-src-list="[row.fileUrl]"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px; cursor: pointer;"
            />
            <el-icon v-else style="font-size: 40px; color: #909399;"><Document /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="文件大小" width="120">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="fileType" label="文件类型" width="150" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="createTime" label="上传时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handlePreview(row)">
              <el-icon><View /></el-icon>
              预览
            </el-button>
            <el-button link type="primary" @click="handleDownload(row)">
              <el-icon><Download /></el-icon>
              下载
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-permission="'system:file:delete'">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryForm.current"
          v-model:page-size="queryForm.size"
          :page-sizes="[20, 30, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewVisible" title="文件预览" width="800px">
      <div style="text-align: center;">
        <el-image 
          v-if="isImage(previewFile.fileType)"
          :src="previewFile.fileUrl" 
          fit="contain"
          style="max-width: 100%; max-height: 600px;"
        />
        <div v-else>
          <p>该文件类型不支持在线预览</p>
          <el-button type="primary" @click="handleDownload(previewFile)">下载文件</el-button>
        </div>
      </div>
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload, Download, Delete, View, Document } from '@element-plus/icons-vue'
import { getFileList, deleteFile } from '@/api/file'

// 响应式数据
const loading = ref(false)
const fileList = ref([])
const total = ref(0)
const previewVisible = ref(false)
const previewFile = ref({})

const queryForm = reactive({
  current: 1,
  size: 20,
  fileName: '',
  category: ''
})

// 上传配置
const uploadAction = ref('http://localhost:8080/api/file/upload')
const uploadHeaders = ref({
  'Authorization': 'Bearer ' + localStorage.getItem('token')
})

// 加载文件列表
const loadFileList = async () => {
  loading.value = true
  try {
    const res = await getFileList(queryForm)
    fileList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('加载文件列表失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  queryForm.current = 1
  loadFileList()
}

// 重置
const handleReset = () => {
  queryForm.fileName = ''
  queryForm.category = ''
  handleQuery()
}

// 上传成功
const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('文件上传成功')
    loadFileList()
  } else {
    ElMessage.error(response.message || '文件上传失败')
  }
}

// 上传失败
const handleUploadError = (error) => {
  ElMessage.error('文件上传失败: ' + error.message)
}

// 上传前检查
const beforeUpload = (file) => {
  const maxSize = 100 * 1024 * 1024 // 100MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过 100MB')
    return false
  }
  return true
}

// 预览
const handlePreview = (row) => {
  previewFile.value = row
  previewVisible.value = true
}

// 下载
const handleDownload = (row) => {
  window.open(row.fileUrl, '_blank')
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该文件吗?', '提示', {
      type: 'warning'
    })
    await deleteFile(row.id)
    ElMessage.success('删除成功')
    loadFileList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 判断是否是图片
const isImage = (fileType) => {
  return fileType && fileType.startsWith('image/')
}

// 格式化文件大小
const formatFileSize = (size) => {
  if (!size) return '-'
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  if (size < 1024 * 1024 * 1024) return (size / 1024 / 1024).toFixed(2) + ' MB'
  return (size / 1024 / 1024 / 1024).toFixed(2) + ' GB'
}

// 格式化时间
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

onMounted(() => {
  loadFileList()
})
</script>

<style scoped>
.file-management {
  padding: 10px;
  height: calc(100vh - 60px);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.file-management .el-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-bottom: 10px;
}

.file-management .el-card :deep(.el-card__header) {
  padding: 9px 10px;
  border-bottom: none;
}

.file-management .el-card :deep(.el-card__body) {
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

.pagination-container {
  margin-top: 5px !important;
  padding: 0 !important;
  border-top: none !important;
  display: flex;
  justify-content: flex-end;
}
</style>
