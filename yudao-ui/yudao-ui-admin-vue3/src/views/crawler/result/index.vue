<template>
  <!-- 搜索栏 -->
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="90px">
      <el-form-item label="选手姓名" prop="name">
        <el-input v-model="queryParams.name" placeholder="搜索选手" clearable style="width:160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="赛事" prop="gameName">
        <el-input v-model="queryParams.gameName" placeholder="搜索赛事" clearable style="width:160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="匹配状态" prop="matchStatus">
        <el-select v-model="queryParams.matchStatus" placeholder="全部" clearable style="width:130px">
          <el-option label="未匹配" value="UNMATCHED" />
          <el-option label="已匹配" value="MATCHED" />
          <el-option label="冲突" value="CONFLICT" />
          <el-option label="重复" value="DUPLICATE" />
        </el-select>
      </el-form-item>
      <el-form-item label="导入状态" prop="importStatus">
        <el-select v-model="queryParams.importStatus" placeholder="全部" clearable style="width:130px">
          <el-option label="待导入" value="PENDING" />
          <el-option label="已确认" value="CONFIRMED" />
          <el-option label="已忽略" value="IGNORED" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 成绩列表 -->
  <ContentWrap title="爬取成绩列表">
    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="选手姓名" align="center" prop="name" width="120" />
      <el-table-column label="赛事" align="center" prop="gameName" min-width="180" show-overflow-tooltip />
      <el-table-column label="比赛日期" align="center" prop="gameDate" width="120" />
      <el-table-column label="参赛号" align="center" prop="bibNumber" width="100" />
      <el-table-column label="性别" align="center" prop="gender" width="60" />
      <el-table-column label="枪声成绩" align="center" width="100">
        <template #default="scope">
          {{ scope.row.gunTimeMs != null ? formatMs(scope.row.gunTimeMs) : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="净成绩" align="center" width="100">
        <template #default="scope">
          {{ scope.row.netTimeMs != null ? formatMs(scope.row.netTimeMs) : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="排名" align="center" prop="rank" width="70" />
      <el-table-column label="匹配状态" align="center" width="100">
        <template #default="scope">
          <el-tag :type="matchTagType(scope.row.matchStatus)" size="small">
            {{ scope.row.matchStatus ?? 'UNMATCHED' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="匹配置信度" align="center" prop="matchConfidence" width="100" />
      <el-table-column label="匹配策略" align="center" prop="matchStrategy" width="110" />
      <el-table-column label="导入状态" align="center" width="100">
        <template #default="scope">
          <el-tag :type="importTagType(scope.row.importStatus)" size="small">
            {{ scope.row.importStatus ?? 'PENDING' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="170" />
    </el-table>
    <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo"
      :total="total" @pagination="getList" />
  </ContentWrap>
</template>

<script lang="ts" setup>
import * as CrawlerResultApi from '@/api/crawler/result'
import type { CrawlerGameResultVO } from '@/api/crawler/result'

defineOptions({ name: 'CrawlerResult' })

const queryFormRef = ref()

const loading = ref(true)
const list = ref<CrawlerGameResultVO[]>([])
const total = ref(0)
const selectedRows = ref<CrawlerGameResultVO[]>([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined as string | undefined,
  gameName: undefined as string | undefined,
  matchStatus: undefined as string | undefined,
  importStatus: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await CrawlerResultApi.getResultPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryFormRef.value?.resetFields(); handleQuery() }

const handleSelectionChange = (rows: CrawlerGameResultVO[]) => {
  selectedRows.value = rows
}

const matchTagType = (s: string | undefined) => {
  if (s === 'MATCHED') return 'success'
  if (s === 'UNMATCHED') return 'warning'
  if (s === 'CONFLICT') return 'danger'
  if (s === 'DUPLICATE') return 'info'
  return ''
}

const importTagType = (s: string | undefined) => {
  if (s === 'CONFIRMED') return 'success'
  if (s === 'PENDING') return 'warning'
  if (s === 'IGNORED') return 'info'
  return ''
}

const formatMs = (ms: number): string => {
  if (!ms || ms <= 0) return '-'
  const h = Math.floor(ms / 3600000)
  const m = Math.floor((ms % 3600000) / 60000)
  const s = Math.floor((ms % 60000) / 1000)
  if (h > 0) return `${h}:${String(m).padStart(2,'0')}:${String(s).padStart(2,'0')}`
  return `${m}:${String(s).padStart(2,'0')}`
}

onMounted(() => getList())
</script>
