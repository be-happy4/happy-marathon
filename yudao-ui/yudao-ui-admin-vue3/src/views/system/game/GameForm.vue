<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="800">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <el-form-item label="赛事名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入赛事名称" />
      </el-form-item>
      <el-form-item label="比赛日期" prop="gameDate">
        <el-date-picker
          v-model="formData.gameDate"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="选择比赛日期"
          class="!w-full"
        />
      </el-form-item>
      <el-form-item label="赛事类型" prop="gameType">
        <el-select v-model="formData.gameType" clearable placeholder="请选择赛事类型" class="!w-full">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.GAME_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="国际田联等级" prop="worldAthleticsLevel">
        <el-select
          v-model="formData.worldAthleticsLevel"
          clearable
          placeholder="请选择"
          class="!w-full"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.GAME_WORLD_ATHLETICS_LABEL_LEVEL)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="中国田联等级" prop="chinaRoadRunLevel">
        <el-select
          v-model="formData.chinaRoadRunLevel"
          clearable
          placeholder="请选择"
          class="!w-full"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.GAME_CHINA_ROAD_RUN_GAME_LEVEL)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="赛事状态" prop="status">
        <el-select v-model="formData.status" clearable placeholder="请选择赛事状态" class="!w-full">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.GAME_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="地区" prop="regionId">
        <el-cascader
          v-model="regionPath"
          :options="regionOptions"
          :props="{ value: 'id', label: 'name', checkStrictly: true, emitPath: false }"
          filterable
          clearable
          placeholder="请选择地区"
          class="!w-full"
        />
      </el-form-item>
      <el-form-item label="赛事标签" prop="tags">
        <el-input v-model="formData.tags" placeholder="多个标签以逗号分隔，如 wmm,cmm" />
      </el-form-item>
      <el-form-item label="官网链接" prop="url">
        <el-input v-model="formData.url" placeholder="https://" />
      </el-form-item>
      <el-form-item label="赛事主页" prop="remark">
        <el-tabs type="border-card" class="remark-tabs">
          <el-tab-pane label="编辑">
            <el-input
              v-model="formData.remark"
              type="textarea"
              :rows="10"
              placeholder="支持 Markdown 格式"
            />
          </el-tab-pane>
          <el-tab-pane label="预览">
            <div v-if="formData.remark" class="markdown-preview">
              <MarkdownView :content="formData.remark" />
            </div>
            <el-empty v-else description="暂无内容" />
          </el-tab-pane>
        </el-tabs>
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
import * as GameApi from '@/api/system/game'
import * as RegionApi from '@/api/system/region'

defineOptions({ name: 'GameForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formData = ref<GameApi.GameVO>({})
const regionPath = ref<number>()
const regionOptions = ref<RegionApi.RegionNodeVO[]>([])
const formRules = reactive({
  name: [{ required: true, message: '赛事名称不能为空', trigger: 'blur' }]
})
const formRef = ref()

const loadRegions = async () => {
  regionOptions.value = await RegionApi.getRegionTree()
}

const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  await loadRegions()
  if (id) {
    formLoading.value = true
    try {
      formData.value = await GameApi.getGame(id)
      regionPath.value = formData.value.regionId
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
    const data = { ...formData.value } as GameApi.GameVO
    data.regionId = regionPath.value
    if (formType.value === 'create') {
      await GameApi.createGame(data)
      message.success(t('common.createSuccess'))
    } else {
      await GameApi.updateGame(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const resetForm = () => {
  formData.value = {
    id: undefined,
    name: undefined,
    gameDate: undefined,
    gameType: undefined,
    worldAthleticsLevel: undefined,
    chinaRoadRunLevel: undefined,
    status: 'uncertain',
    tags: undefined,
    url: undefined,
    remark: undefined
  }
  regionPath.value = undefined
  formRef.value?.resetFields()
}
</script>
<style scoped>
.remark-tabs {
  width: 100%;
}
.markdown-preview {
  min-height: 200px;
  padding: 8px;
}
</style>
