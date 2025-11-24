<template>
  <div class="icon-selector">
    <el-input
      v-model="selectedIcon"
      placeholder="请选择图标"
      readonly
      @click="dialogVisible = true"
    >
      <template #prefix>
        <el-icon v-if="selectedIcon">
          <component :is="selectedIcon" />
        </el-icon>
      </template>
      <template #suffix>
        <el-icon style="cursor: pointer;">
          <ArrowDown />
        </el-icon>
      </template>
    </el-input>

    <el-dialog v-model="dialogVisible" title="选择图标" width="800px">
      <el-input
        v-model="searchText"
        placeholder="搜索图标"
        clearable
        style="margin-bottom: 20px;"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <div class="icon-list">
        <div
          v-for="icon in filteredIcons"
          :key="icon"
          class="icon-item"
          :class="{ active: selectedIcon === icon }"
          @click="selectIcon(icon)"
        >
          <el-icon :size="24">
            <component :is="icon" />
          </el-icon>
          <div class="icon-name">{{ icon }}</div>
        </div>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSelect">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ArrowDown, Search } from '@element-plus/icons-vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const dialogVisible = ref(false)
const searchText = ref('')
const selectedIcon = ref(props.modelValue)

// Element Plus 图标列表
const iconList = ref([
  'User', 'Lock', 'Setting', 'Menu', 'HomeFilled', 'Avatar', 'Collection',
  'Document', 'Folder', 'Files', 'Plus', 'Edit', 'Delete', 'View', 'Download',
  'Upload', 'Search', 'Refresh', 'Close', 'Check', 'Warning', 'InfoFilled',
  'SuccessFilled', 'CircleClose', 'CircleCheck', 'ArrowRight', 'ArrowLeft',
  'ArrowUp', 'ArrowDown', 'More', 'MoreFilled', 'Star', 'StarFilled',
  'Calendar', 'Clock', 'Location', 'Phone', 'Message', 'ChatDotRound',
  'Bell', 'BellFilled', 'ShoppingCart', 'Goods', 'Money', 'Ticket',
  'Tools', 'Compass', 'DataLine', 'DataBoard', 'PieChart', 'Histogram',
  'TrendCharts', 'Grid', 'List', 'Operation', 'Promotion', 'Key',
  'Link', 'Paperclip', 'Share', 'Printer', 'Monitor', 'Camera',
  'Picture', 'PictureFilled', 'VideoCamera', 'Headset', 'Medal',
  'Trophy', 'Flag', 'Notification', 'Stamp', 'Reading', 'Notebook',
  'ScaleToOriginal', 'FullScreen', 'CirclePlus', 'RemoveFilled', 'House'
])

// 过滤图标
const filteredIcons = computed(() => {
  if (!searchText.value) return iconList.value
  return iconList.value.filter(icon => 
    icon.toLowerCase().includes(searchText.value.toLowerCase())
  )
})

// 选择图标
const selectIcon = (icon) => {
  selectedIcon.value = icon
}

// 确认选择
const confirmSelect = () => {
  emit('update:modelValue', selectedIcon.value)
  dialogVisible.value = false
}

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  selectedIcon.value = newVal
})
</script>

<script>
import { watch } from 'vue'
export default {
  name: 'IconSelector'
}
</script>

<style scoped>
.icon-selector {
  width: 100%;
}

.icon-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 10px;
  max-height: 400px;
  overflow-y: auto;
  padding: 10px 0;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.icon-item:hover {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.icon-item.active {
  border-color: #409eff;
  background-color: #409eff;
  color: #fff;
}

.icon-name {
  margin-top: 8px;
  font-size: 12px;
  text-align: center;
  word-break: break-all;
}
</style>
