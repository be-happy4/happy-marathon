<template>
  <!-- 搜索栏 -->
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="100px">
      <el-form-item label="赛事名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="搜索赛事名称" clearable style="width:180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="数据源" prop="sourceId">
        <el-select v-model="queryParams.sourceId" placeholder="全部" clearable style="width:180px">
          <el-option v-for="s in sources" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="世界田联等级" prop="worldAthleticsLevels">
        <el-select v-model="queryParams.worldAthleticsLevels" placeholder="全部" clearable multiple collapse-tags style="width:160px">
          <el-option v-for="dict in getStrDictOptions(DICT_TYPE.GAME_WORLD_ATHLETICS_LABEL_LEVEL)"
            :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="中国田协等级" prop="chinaRoadRunLevels">
        <el-select v-model="queryParams.chinaRoadRunLevels" placeholder="全部" clearable multiple collapse-tags style="width:140px">
          <el-option v-for="dict in getStrDictOptions(DICT_TYPE.GAME_CHINA_ROAD_RUN_GAME_LEVEL)"
            :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="赛事状态" prop="statuses">
        <el-select v-model="queryParams.statuses" placeholder="全部" clearable multiple collapse-tags style="width:140px">
          <el-option v-for="dict in getStrDictOptions(DICT_TYPE.GAME_STATUS)"
            :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="导入状态" prop="importStatuses">
        <el-select v-model="queryParams.importStatuses" placeholder="全部" clearable multiple collapse-tags style="width:150px">
          <el-option label="待导入" value="PENDING" />
          <el-option label="已导入" value="IMPORTED" />
          <el-option label="已忽略" value="IGNORED" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 赛事列表 -->
  <ContentWrap title="爬取赛事列表">
    <el-table v-loading="loading" :data="list" @sort-change="handleSortChange" :default-sort="{prop: 'gameDate', order: 'descending'}">
      <el-table-column label="ID" align="center" prop="id" width="80" sortable="custom" />
      <el-table-column label="数据源" align="center" width="130">
        <template #default="scope">
          <el-tag v-if="scope.row.sourceId === 1" size="small" effect="plain">WA</el-tag>
          <el-tag v-else-if="scope.row.sourceId === 2" size="small" type="success">runchina</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="赛事名称" align="center" prop="name" min-width="200" show-overflow-tooltip sortable="custom" />
      <el-table-column label="比赛日期" align="center" prop="gameDate" width="120" sortable="custom" />
      <el-table-column label="地区" align="center" prop="regionName" width="120" show-overflow-tooltip sortable="custom" />
      <el-table-column label="世界田联等级" align="center" width="100" prop="worldAthleticsLevel" sortable="custom">
        <template #default="scope">
          <dict-tag v-if="scope.row.worldAthleticsLevel" :type="DICT_TYPE.GAME_WORLD_ATHLETICS_LABEL_LEVEL" :value="scope.row.worldAthleticsLevel" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="中国田协等级" align="center" width="100" prop="chinaRoadRunLevel" sortable="custom">
        <template #default="scope">
          <dict-tag v-if="scope.row.chinaRoadRunLevel" :type="DICT_TYPE.GAME_CHINA_ROAD_RUN_GAME_LEVEL" :value="scope.row.chinaRoadRunLevel" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="90" prop="status" sortable="custom">
        <template #default="scope">
          <dict-tag v-if="scope.row.status" :type="DICT_TYPE.GAME_STATUS" :value="scope.row.status" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="导入状态" align="center" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.importStatus === 'PENDING' ? 'warning' : scope.row.importStatus === 'IMPORTED' ? 'success' : 'info'" size="small">
            {{ scope.row.importStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="组别" align="center" width="150">
        <template #default="scope">
          <template v-if="scope.row.categories && scope.row.categories.length">
            <dict-tag v-for="c in scope.row.categories" :key="c.gameType" :type="DICT_TYPE.GAME_TYPE" :value="c.gameType" size="small" />
          </template>
          <span v-else>-</span>
        </template>
      </el-table-column>
    </el-table>
    <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo"
      :total="total" @pagination="getList" />
  </ContentWrap>
</template>

<script lang="ts" setup>
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import { buildSortingField } from '@/utils'
import * as CrawlerGameApi from '@/api/crawler/game'
import type { CrawlerGameVO } from '@/api/crawler/game'
import * as CrawlerSourceApi from '@/api/crawler/source'

defineOptions({ name: 'CrawlerGame' })

const queryFormRef = ref()

const loading = ref(true)
const list = ref<CrawlerGameVO[]>([])
const total = ref(0)
const sources = ref<{ id?: number; name?: string }[]>([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined as string | undefined,
  sourceId: undefined as number | undefined,
  worldAthleticsLevels: [] as string[],
  chinaRoadRunLevels: [] as string[],
  statuses: [] as string[],
  importStatuses: [] as string[],
  sortingFields: [{ field: 'gameDate', order: 'descending' }] as { field: string; order: string }[]
})

const handleSortChange = (params: any) => {
  queryParams.sortingFields = [buildSortingField(params)]
  getList()
}

const getList = async () => {
  loading.value = true
  try {
    const data = await CrawlerGameApi.getGamePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const getSources = async () => {
  try {
    const data = await CrawlerSourceApi.getSourcePage({ pageNo: 1, pageSize: 50 })
    sources.value = data.list
  } catch { /* ignore */ }
}

const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryFormRef.value?.resetFields(); queryParams.sortingFields = [{ field: 'gameDate', order: 'descending' }]; handleQuery() }

onMounted(() => {
  getSources()
  getList()
})
</script>
