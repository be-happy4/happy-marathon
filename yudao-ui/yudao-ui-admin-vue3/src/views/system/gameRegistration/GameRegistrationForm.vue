<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="800">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <el-form-item label="赛事" prop="gameId">
        <el-select
          v-model="formData.gameId"
          filterable
          placeholder="请选择赛事"
          class="!w-full"
          @change="onGameChange"
        >
          <el-option v-for="g in gameList" :key="g.id" :label="g.name" :value="g.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="用户" prop="userId">
        <el-select v-model="formData.userId" filterable placeholder="请选择用户" class="!w-full">
          <el-option v-for="u in userList" :key="u.id" :label="u.nickname" :value="u.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="报名状态" prop="registrationStatus">
        <el-select
          v-model="formData.registrationStatus"
          clearable
          placeholder="请选择"
          class="!w-full"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.GAME_REGISTRATION_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="参赛组别" prop="gameCategoryId">
        <el-select
          v-model="formData.gameCategoryId"
          clearable
          placeholder="请先选择赛事"
          class="!w-full"
        >
          <el-option
            v-for="c in categoryList"
            :key="c.id"
            :label="`${getDictLabel(DICT_TYPE.GAME_TYPE, c.gameType)}${c.distanceM ? ' (' + parseFloat((c.distanceM / 1000).toFixed(3)) + 'km)' : ''}`"
            :value="c.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="优先级" prop="priority">
        <el-input-number v-model="formData.priority" :min="0" :max="100" class="!w-full" />
      </el-form-item>
      <el-form-item label="个人标签" prop="tags">
        <el-input v-model="formData.tags" placeholder="多个标签以逗号分隔" />
      </el-form-item>
      <el-form-item label="参赛号码" prop="bibNumber">
        <el-input v-model="formData.bibNumber" placeholder="请输入参赛号码" />
      </el-form-item>
      <el-form-item label="枪声成绩" prop="gunTimeText">
        <el-input v-model="gunTimeText" placeholder="HH:mm:ss，如 02:58:32" />
      </el-form-item>
      <el-form-item label="净成绩" prop="netTimeText">
        <el-input v-model="netTimeText" placeholder="HH:mm:ss" />
      </el-form-item>
      <el-form-item label="性别排名" prop="genderPlace">
        <el-input-number v-model="formData.genderPlace" :min="1" class="!w-full" />
      </el-form-item>
      <el-form-item label="总排名" prop="overallPlace">
        <el-input-number v-model="formData.overallPlace" :min="1" class="!w-full" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { DICT_TYPE, getStrDictOptions, getDictLabel } from '@/utils/dict'
import { formatDurationMs, parseDurationMs } from '@/utils/marathonTime'
import * as GameApi from '@/api/system/game'
import * as GameRegistrationApi from '@/api/system/gameRegistration'
import * as UserApi from '@/api/system/user'

defineOptions({ name: 'GameRegistrationForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formData = ref<GameRegistrationApi.GameRegistrationVO>({})
const gunTimeText = ref('')
const netTimeText = ref('')
const gameList = ref<GameApi.GameSimpleVO[]>([])
const userList = ref<UserApi.UserVO[]>([])
const categoryList = ref<GameApi.GameCategoryVO[]>([])
const formRules = reactive({
  gameId: [{ required: true, message: '请选择赛事', trigger: 'change' }],
  userId: [{ required: true, message: '请选择用户', trigger: 'change' }]
})
const formRef = ref()

const loadOptions = async () => {
  gameList.value = await GameApi.getGameSimpleList()
  userList.value = await UserApi.getSimpleUserList()
}

const onGameChange = async (gameId: number) => {
  formData.value.gameCategoryId = undefined
  if (gameId) {
    categoryList.value = await GameApi.getGameCategories(gameId)
  } else {
    categoryList.value = []
  }
}

const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  await loadOptions()
  if (id) {
    formLoading.value = true
    try {
      formData.value = await GameRegistrationApi.getGameRegistration(id)
      gunTimeText.value = formatDurationMs(formData.value.gunTimeMs)
      netTimeText.value = formatDurationMs(formData.value.netTimeMs)
      if (formData.value.gameId) {
        categoryList.value = await GameApi.getGameCategories(formData.value.gameId)
      }
    } finally {
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
    const data = { ...formData.value } as GameRegistrationApi.GameRegistrationVO
    data.gunTimeMs = parseDurationMs(gunTimeText.value)
    data.netTimeMs = parseDurationMs(netTimeText.value)
    if (formType.value === 'create') {
      await GameRegistrationApi.createGameRegistration(data)
      message.success(t('common.createSuccess'))
    } else {
      await GameRegistrationApi.updateGameRegistration(data)
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
  gunTimeText.value = ''
  netTimeText.value = ''
  categoryList.value = []
  formRef.value?.resetFields()
}
</script>
