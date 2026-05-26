<template>
  <Dialog v-model="dialogVisible" title="导入赛事" width="500">
    <el-alert type="info" :closable="false" show-icon class="!mb-15px">
      <template #default>
        请先<el-button link type="primary" @click="handleTemplate">下载模板</el-button>，按格式填写后上传
      </template>
    </el-alert>
    <el-upload
      ref="uploadRef"
      drag
      :action="uploadUrl"
      :headers="headers"
      :limit="1"
      accept=".xls,.xlsx"
      :on-success="handleSuccess"
      :on-error="handleError"
    >
      <Icon icon="ep:upload" :size="32" />
      <div class="el-upload__text">拖拽文件或<em>点击上传</em></div>
      <template #tip>
        <div class="el-upload__tip">仅允许 Excel 文件</div>
      </template>
    </el-upload>
    <template #footer>
      <el-button @click="dialogVisible = false">关 闭</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { getAccessToken, getTenantId } from '@/utils/auth'
import * as GameApi from '@/api/system/game'
import download from '@/utils/download'

defineOptions({ name: 'GameImportForm' })

const message = useMessage()
const dialogVisible = ref(false)
const uploadUrl = import.meta.env.VITE_BASE_URL + import.meta.env.VITE_API_URL + '/system/game/import'
const headers = computed(() => ({
  Authorization: 'Bearer ' + getAccessToken(),
  'tenant-id': getTenantId()
}))

const emit = defineEmits(['success'])

const open = () => {
  dialogVisible.value = true
}
defineExpose({ open })

const handleTemplate = async () => {
  const res = await GameApi.getGameImportTemplate()
  download.excel(res, '赛事导入模板.xls')
}

const handleSuccess = () => {
  message.success('导入成功')
  dialogVisible.value = false
  emit('success')
}

const handleError = () => {
  message.error('导入失败，请检查文件格式')
}
</script>
