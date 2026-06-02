<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      class="-mb-15px"
      :model="queryParams"
      :inline="true"
      label-width="90px"
    >
      <el-form-item label="赛事名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入赛事名称"
          clearable
          class="!w-240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="比赛日期" prop="gameDate">
        <el-date-picker
          v-model="queryParams.gameDate"
          value-format="YYYY-MM-DD"
          type="daterange"
          start-placeholder="开始"
          end-placeholder="结束"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="赛事类型" prop="gameType">
        <el-select
          v-model="queryParams.gameTypes"
          multiple
          clearable
          collapse-tags
          collapse-tags-tooltip
          placeholder="请选择"
          class="!w-240px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.GAME_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="赛事状态" prop="status">
        <el-select
          v-model="queryParams.statuses"
          multiple
          clearable
          collapse-tags
          collapse-tags-tooltip
          placeholder="请选择"
          class="!w-240px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.GAME_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="国际田联" prop="worldAthleticsLevel">
        <el-select
          v-model="queryParams.worldAthleticsLevels"
          multiple
          clearable
          collapse-tags
          collapse-tags-tooltip
          placeholder="请选择"
          class="!w-240px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.GAME_WORLD_ATHLETICS_LABEL_LEVEL)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="中国田联" prop="chinaRoadRunLevel">
        <el-select
          v-model="queryParams.chinaRoadRunLevels"
          multiple
          clearable
          collapse-tags
          collapse-tags-tooltip
          placeholder="请选择"
          class="!w-240px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.GAME_CHINA_ROAD_RUN_GAME_LEVEL)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" /> 重置</el-button>
        <el-button
          v-hasPermi="['system:game:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon class="mr-5px" icon="ep:plus" /> 新增
        </el-button>
        <el-button
          v-hasPermi="['system:game:delete']"
          plain
          type="danger"
          :disabled="checkedIds.length === 0"
          @click="handleDeleteBatch"
        >
          <Icon class="mr-5px" icon="ep:delete" /> 批量删除
        </el-button>
        <el-button
          v-hasPermi="['system:game:export']"
          plain
          type="success"
          :loading="exportLoading"
          @click="handleExport"
        >
          <Icon class="mr-5px" icon="ep:download" /> 导出
        </el-button>
        <el-button
          v-hasPermi="['system:game:import']"
          plain
          type="warning"
          @click="handleImport"
        >
          <Icon class="mr-5px" icon="ep:upload" /> 导入
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" @sort-change="handleSortChange" @selection-change="handleRowCheckboxChange" :default-sort="{prop: 'gameDate', order: 'descending'}">
      <el-table-column type="selection" width="55" />
      <el-table-column align="center" label="赛事名称" min-width="160" prop="name" show-overflow-tooltip />
      <el-table-column align="center" label="比赛日期" prop="gameDate" sortable width="120" />
      <el-table-column align="center" label="赛事类型" prop="gameType" width="120">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.GAME_TYPE" :value="scope.row.gameType" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="国际田联" prop="worldAthleticsLevel" width="100">
        <template #default="scope">
          <dict-tag
            :type="DICT_TYPE.GAME_WORLD_ATHLETICS_LABEL_LEVEL"
            :value="scope.row.worldAthleticsLevel"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="中国田联" prop="chinaRoadRunLevel" width="90">
        <template #default="scope">
          <dict-tag
            :type="DICT_TYPE.GAME_CHINA_ROAD_RUN_GAME_LEVEL"
            :value="scope.row.chinaRoadRunLevel"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" prop="status" width="110">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.GAME_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="地区" prop="regionName" min-width="120">
        <template #default="scope">
          <el-tooltip :content="scope.row.regionBreadcrumb" placement="top" :disabled="!scope.row.regionBreadcrumb">
            <span>{{ scope.row.regionName }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        label="创建时间"
        prop="createTime"
        width="180"
        :formatter="dateFormatter"
      />
      <el-table-column align="center" fixed="right" label="操作" width="200">
        <template #default="scope">
          <el-button
            v-hasPermi="['system:game:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <router-link :to="'/game/category/' + scope.row.id">
            <el-button link type="success">组别</el-button>
          </router-link>
          <router-link :to="{ path: '/system/game-registration', query: { gameId: scope.row.id } }">
            <el-button link type="warning">报名</el-button>
          </router-link>
          <el-button
            v-hasPermi="['system:game:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <GameForm ref="formRef" @success="getList" />
  <GameImportForm ref="importFormRef" @success="getList" />
</template>
<script lang="ts" setup>
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import { buildSortingField } from '@/utils'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import * as GameApi from '@/api/system/game'
import GameForm from './GameForm.vue'
import GameImportForm from './GameImportForm.vue'

defineOptions({ name: 'SystemGame' })

const message = useMessage()
const { t } = useI18n()
const route = useRoute()

const loading = ref(true)
const total = ref(0)
const list = ref<GameApi.GameVO[]>([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: '',
  gameDate: [],
  gameTypes: [] as string[],
  statuses: [] as string[],
  worldAthleticsLevels: [] as string[],
  chinaRoadRunLevels: [] as string[],
  sortingFields: [] as { field: string; order: string }[]
})
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await GameApi.getGamePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const handleSortChange = (params: any) => {
  queryParams.sortingFields = [buildSortingField(params)]
  handleQuery()
}

const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await GameApi.deleteGame(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (rows: GameApi.GameVO[]) => {
  checkedIds.value = rows.map((row) => row.id!)
}

const handleDeleteBatch = async () => {
  try {
    await message.delConfirm()
    await GameApi.deleteGameList(checkedIds.value)
    checkedIds.value = []
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const importFormRef = ref()
const handleImport = () => {
  importFormRef.value.open()
}

const exportLoading = ref(false)
const handleExport = async () => {
  try {
    exportLoading.value = true
    const data = await GameApi.exportGame(queryParams)
    download.excel(data, '赛事数据.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  const name = route.query.name as string
  if (name) {
    queryParams.name = name
  }
  getList()
})

// Watch route query changes (e.g., clicking game name link from registration page)
watch(() => route.query.name, (newName) => {
  queryParams.name = (newName as string) || ''
  handleQuery()
})
</script>
