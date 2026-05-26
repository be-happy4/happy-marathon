<template>
  <!-- 搜索栏 -->
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="80px">
      <el-form-item label="数据源" prop="sourceId">
        <el-select v-model="queryParams.sourceId" placeholder="全部" clearable style="width:180px">
          <el-option v-for="s in sources" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
          <el-option label="成功" value="SUCCESS" />
          <el-option label="失败" value="FAILED" />
          <el-option label="运行中" value="RUNNING" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 任务日志列表 -->
  <ContentWrap title="任务执行日志">
    <el-table v-loading="loading" :data="list" @row-click="showDetail" highlight-current-row @sort-change="handleSortChange" :default-sort="{prop: 'createTime', order: 'descending'}">
      <el-table-column label="ID" align="center" prop="id" width="80" sortable="custom" />
      <el-table-column label="数据源" align="center" prop="sourceId" width="100" sortable="custom" />
      <el-table-column label="爬取类型" align="center" prop="crawlType" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.crawlType === 'GAME' ? 'success' : 'warning'" size="small">
            {{ scope.row.crawlType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="模式" align="center" prop="crawlMode" width="100" />
      <el-table-column label="状态" align="center" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'SUCCESS' ? 'success' : scope.row.status === 'FAILED' ? 'danger' : 'warning'" size="small">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="爬取总数" align="center" prop="totalFetched" width="90" sortable="custom" />
      <el-table-column label="新增" align="center" prop="totalNew" width="70" sortable="custom" />
      <el-table-column label="更新" align="center" prop="totalUpdated" width="70" />
      <el-table-column label="跳过" align="center" prop="totalIgnored" width="70" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="170" sortable="custom" />
      <el-table-column label="错误信息" align="center" min-width="200" show-overflow-tooltip>
        <template #default="scope">
          <span v-if="scope.row.errorMsg" class="color-danger">{{ scope.row.errorMsg }}</span>
          <span v-else class="color-gray">-</span>
        </template>
      </el-table-column>
    </el-table>
    <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo"
      :total="total" @pagination="getList" />
  </ContentWrap>

  <!-- 日志详情弹窗 -->
  <Dialog v-model="detailVisible" title="任务日志详情" width="700">
    <el-descriptions v-if="detail" :column="2" border>
      <el-descriptions-item label="日志 ID">{{ detail.id }}</el-descriptions-item>
      <el-descriptions-item label="数据源 ID">{{ detail.sourceId }}</el-descriptions-item>
      <el-descriptions-item label="爬取类型">{{ detail.crawlType }}</el-descriptions-item>
      <el-descriptions-item label="模式">{{ detail.crawlMode }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="detail.status === 'SUCCESS' ? 'success' : 'danger'" size="small">{{ detail.status }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
      <el-descriptions-item label="爬取总数">{{ detail.totalFetched ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="新增">{{ detail.totalNew ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="更新">{{ detail.totalUpdated ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="跳过">{{ detail.totalIgnored ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="错误信息" :span="2">
        <pre v-if="detail.errorMsg" style="color:red;max-height:300px;overflow:auto">{{ detail.errorMsg }}</pre>
        <span v-else class="color-gray">无</span>
      </el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script lang="ts" setup>
import { buildSortingField } from '@/utils'
import * as CrawlerTaskLogApi from '@/api/crawler/taskLog'
import type { CrawlerTaskLogVO } from '@/api/crawler/taskLog'
import * as CrawlerSourceApi from '@/api/crawler/source'

defineOptions({ name: 'CrawlerTaskLog' })

const message = useMessage()
const queryFormRef = ref()

const loading = ref(true)
const list = ref<CrawlerTaskLogVO[]>([])
const total = ref(0)
const sources = ref<{ id?: number; name?: string }[]>([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  sourceId: undefined as number | undefined,
  status: undefined as string | undefined,
  sortingFields: [] as { field: string; order: string }[]
})

const handleSortChange = (params: any) => {
  queryParams.sortingFields = [buildSortingField(params)]
  getList()
}

const getList = async () => {
  loading.value = true
  try {
    const data = await CrawlerTaskLogApi.getTaskLogPage(queryParams)
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
const resetQuery = () => { queryFormRef.value?.resetFields(); handleQuery() }

// 详情弹窗
const detailVisible = ref(false)
const detail = ref<CrawlerTaskLogVO | null>(null)
const showDetail = (row: CrawlerTaskLogVO) => {
  detail.value = row
  detailVisible.value = true
}

onMounted(() => {
  getSources()
  getList()
})
</script>
