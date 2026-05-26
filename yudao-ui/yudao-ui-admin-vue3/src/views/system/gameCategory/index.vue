<template>
  <ContentWrap>
    <el-form class="-mb-15px" :inline="true">
      <el-form-item>
        <el-button plain type="primary" @click="openForm('create')">
          <Icon class="mr-5px" icon="ep:plus" /> 新增组别
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" @sort-change="handleSortChange">
      <el-table-column align="center" label="所属赛事" prop="gameName" min-width="180" show-overflow-tooltip />
      <el-table-column align="center" label="组别类型" width="140">
        <template #default="scope">
          <template v-if="dictValues.includes(scope.row.gameType)">
            <dict-tag :type="DICT_TYPE.GAME_CATEGORY_TYPE" :value="scope.row.gameType" />
          </template>
          <span v-else>{{ scope.row.distanceKm }}km</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="距离(km)" width="120">
        <template #default="scope">
          {{ parseFloat(((scope.row.distanceM / 1000)).toFixed(3)) }}
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="150">
        <template #default="scope">
          <el-button
            v-hasPermi="['system:game-category:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['system:game-category:delete']"
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

  <GameCategoryForm ref="formRef" :game-id="gameId" @success="getList" />
</template>
<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { buildSortingField } from '@/utils'
import * as GameCategoryApi from '@/api/system/gameCategory'
import GameCategoryForm from './GameCategoryForm.vue'

defineOptions({ name: 'SystemGameCategory' })

const dictValues = ['marathon', 'half_marathon', '10km', 'custom']

const route = useRoute()
const message = useMessage()
const { t } = useI18n()

const gameId = computed(() => Number(route.params.gameId))

const loading = ref(true)
const total = ref(0)
const list = ref<GameCategoryApi.GameCategoryVO[]>([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 100,
  gameId: gameId.value,
  sortingFields: [] as { field: string; order: string }[]
})

const handleSortChange = (params: any) => {
  queryParams.sortingFields = [buildSortingField(params)]
  getList()
}

const getList = async () => {
  loading.value = true
  try {
    queryParams.gameId = gameId.value
    const data = await GameCategoryApi.getGameCategoryPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await GameCategoryApi.deleteGameCategory(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

onMounted(() => {
  getList()
})
</script>
