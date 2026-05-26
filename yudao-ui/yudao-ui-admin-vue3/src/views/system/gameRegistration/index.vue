<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      class="-mb-15px"
      :model="queryParams"
      :inline="true"
      label-width="90px"
    >
      <el-form-item label="赛事" prop="gameIds">
        <el-select
          v-model="queryParams.gameIds"
          multiple
          clearable
          filterable
          collapse-tags
          collapse-tags-tooltip
          placeholder="请选择赛事"
          class="!w-240px"
        >
          <el-option v-for="g in gameList" :key="g.id" :label="g.name" :value="g.id" />
        </el-select>
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
      <el-form-item label="用户" prop="userId">
        <el-select
          v-model="queryParams.userId"
          clearable
          filterable
          placeholder="请选择用户"
          class="!w-240px"
        >
          <el-option v-for="u in userList" :key="u.id" :label="u.nickname" :value="u.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="报名状态" prop="registrationStatus">
        <el-select
          v-model="queryParams.registrationStatuses"
          multiple
          clearable
          collapse-tags
          collapse-tags-tooltip
          placeholder="请选择"
          class="!w-240px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.GAME_REGISTRATION_STATUS)"
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
          v-hasPermi="['system:game-registration:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon class="mr-5px" icon="ep:plus" /> 新增
        </el-button>
        <el-button
          v-hasPermi="['system:game-registration:delete']"
          plain
          type="danger"
          :disabled="checkedIds.length === 0"
          @click="handleDeleteBatch"
        >
          <Icon class="mr-5px" icon="ep:delete" /> 批量删除
        </el-button>
        <el-button
          v-hasPermi="['system:game-registration:export']"
          plain
          type="success"
          :loading="exportLoading"
          @click="handleExport"
        >
          <Icon class="mr-5px" icon="ep:download" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" @sort-change="handleSortChange" @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <el-table-column align="center" label="赛事" min-width="160">
        <template #default="scope">
          <router-link :to="{ path: '/system/game', query: { name: scope.row.gameName } }">
            {{ scope.row.gameName }}
          </router-link>
        </template>
      </el-table-column>
      <el-table-column align="center" label="比赛日期" prop="gameDate" sortable width="120" />
      <el-table-column align="center" label="用户" prop="userNickname" width="100" />
      <el-table-column align="center" label="报名状态" prop="registrationStatus" width="110">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.GAME_REGISTRATION_STATUS" :value="scope.row.registrationStatus" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="组别" width="120">
        <template #default="scope">
          <template v-if="scope.row.gameCategoryName === 'custom'">
            {{ fmtDistanceKm(scope.row.categoryDistanceM) }}km
          </template>
          <dict-tag
            v-else-if="scope.row.gameCategoryName"
            :type="DICT_TYPE.GAME_CATEGORY_TYPE"
            :value="scope.row.gameCategoryName"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="参赛号" prop="bibNumber" width="100" />
      <el-table-column align="center" label="净成绩" prop="netTimeMs" sortable width="110">
        <template #default="scope">
          {{ formatDurationMs(scope.row.netTimeMs) }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="枪声成绩" width="110">
        <template #default="scope">
          {{ formatDurationMs(scope.row.gunTimeMs) }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="配速" prop="pace" width="80">
        <template #default="scope">
          {{ scope.row.pace }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="时速" prop="speed" width="80">
        <template #default="scope">
          {{ scope.row.speed }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="性别排名" prop="genderPlace" sortable width="90" />
      <el-table-column align="center" label="总排名" prop="overallPlace" sortable width="80" />
      <el-table-column
        align="center"
        label="创建时间"
        prop="createTime"
        width="180"
        :formatter="dateFormatter"
      />
      <el-table-column align="center" fixed="right" label="操作" width="150">
        <template #default="scope">
          <el-button
            v-hasPermi="['system:game-registration:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['system:game-registration:delete']"
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

  <GameRegistrationForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import { buildSortingField } from '@/utils'
import { dateFormatter } from '@/utils/formatTime'
import { formatDurationMs } from '@/utils/marathonTime'
import download from '@/utils/download'
import * as GameApi from '@/api/system/game'
import * as GameRegistrationApi from '@/api/system/gameRegistration'
import * as UserApi from '@/api/system/user'
import GameRegistrationForm from './GameRegistrationForm.vue'

defineOptions({ name: 'SystemGameRegistration' })

const fmtDistanceKm = (meters: number) => parseFloat(((meters || 0) / 1000).toFixed(3))

const message = useMessage()
const { t } = useI18n()
const route = useRoute()

const loading = ref(true)
const total = ref(0)
const list = ref<GameRegistrationApi.GameRegistrationVO[]>([])
const gameList = ref<GameApi.GameSimpleVO[]>([])
const userList = ref<UserApi.UserVO[]>([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  gameIds: [] as number[],
  gameDate: [] as string[],
  userId: undefined,
  registrationStatuses: [] as string[],
  sortingFields: [] as { field: string; order: string }[]
})
const queryFormRef = ref()

const loadOptions = async () => {
  gameList.value = await GameApi.getGameSimpleList()
  userList.value = await UserApi.getSimpleUserList()
}

const getList = async () => {
  loading.value = true
  try {
    const data = await GameRegistrationApi.getGameRegistrationPage(queryParams)
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
  // Keep gameId from route if present
  if (route.query.gameId) {
    queryParams.gameIds = [Number(route.query.gameId)]
  }
  handleQuery()
}

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await GameRegistrationApi.deleteGameRegistration(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (rows: GameRegistrationApi.GameRegistrationVO[]) => {
  checkedIds.value = rows.map((row) => row.id!)
}

const handleDeleteBatch = async () => {
  try {
    await message.delConfirm()
    await GameRegistrationApi.deleteGameRegistrationList(checkedIds.value)
    checkedIds.value = []
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const exportLoading = ref(false)
const handleExport = async () => {
  try {
    exportLoading.value = true
    const data = await GameRegistrationApi.exportRegistration(queryParams)
    download.excel(data, '报名数据.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

onMounted(async () => {
  await loadOptions()
  // Pre-set game filter from route query
  if (route.query.gameId) {
    queryParams.gameIds = [Number(route.query.gameId)]
  }
  await getList()
})

// Watch route query changes (e.g., clicking 报名 button from game list)
watch(() => route.query.gameId, (newId) => {
  if (newId) {
    queryParams.gameIds = [Number(newId)]
  } else {
    queryParams.gameIds = []
  }
  handleQuery()
})
</script>
