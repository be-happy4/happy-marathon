<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="500">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <el-form-item label="组别类型" prop="gameType">
        <el-select
          v-model="formData.gameType"
          placeholder="请选择"
          class="!w-full"
          @change="onTypeChange"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.GAME_CATEGORY_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="距离(km)" prop="distanceM">
        <el-input-number
          :model-value="formData.distanceM ? formData.distanceM / 1000 : undefined"
          @update:model-value="(v: number) => formData.distanceM = v ? v * 1000 : undefined"
          :precision="3"
          :min="0"
          :disabled="formData.gameType !== 'custom'"
          class="!w-full"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import * as GameCategoryApi from '@/api/system/gameCategory'

defineOptions({ name: 'GameCategoryForm' })

const props = defineProps<{ gameId: number }>()

const { t } = useI18n()
const message = useMessage()

const presets: Record<string, number> = {
  marathon: 42.195,
  half_marathon: 21.0975,
  '10km': 10
}

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formData = ref<GameCategoryApi.GameCategoryVO>({})
const formRules = reactive({
  gameType: [{ required: true, message: '请选择组别类型', trigger: 'change' }]
})
const formRef = ref()

const onTypeChange = (val: string) => {
  if (val && val !== 'custom') {
    formData.value.distanceM = presets[val] * 1000
  }
}

const open = (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      GameCategoryApi.getGameCategoryPage({ pageNo: 1, pageSize: 100, gameId: props.gameId }).then(
        (data) => {
          const found = data.list.find((c) => c.id === id)
          if (found) formData.value = { ...found }
          formLoading.value = false
        }
      )
    } catch {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

const emit = defineEmits(['success'])
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    const data = { ...formData.value, gameId: props.gameId }
    if (formType.value === 'create') {
      await GameCategoryApi.createGameCategory(data)
      message.success(t('common.createSuccess'))
    } else {
      await GameCategoryApi.updateGameCategory(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const resetForm = () => {
  formData.value = {}
  formRef.value?.resetFields()
}
</script>
