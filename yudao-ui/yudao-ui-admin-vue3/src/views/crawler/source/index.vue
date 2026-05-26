<template>
  <ContentWrap>
    <el-form class="-mb-15px" :inline="true">
      <el-form-item>
        <el-button type="primary" @click="handleTriggerCrawl" :loading="triggering">
          <Icon icon="ep:video-play" class="mr-5px" /> 手动触发赛事爬取
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-tag v-if="triggerResult" type="success" effect="plain">{{ triggerResult }}</el-tag>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 数据源列表 -->
  <ContentWrap title="数据源列表">
    <el-table v-loading="loading" :data="list" @sort-change="handleSortChange">
      <el-table-column label="ID" align="center" prop="id" width="80" sortable="custom" />
      <el-table-column label="数据源名称" align="center" prop="name" min-width="180" sortable="custom" />
      <el-table-column label="标识" align="center" prop="sourceKey" width="130" sortable="custom" />
      <el-table-column label="爬取类型" align="center" prop="crawlType" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.crawlType === 'GAME' ? 'success' : 'warning'" size="small">
            {{ scope.row.crawlType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="处理方式" align="center" prop="handlerType" width="100" />
      <el-table-column label="实现类" align="center" prop="handlerClass" min-width="240" show-overflow-tooltip />
      <el-table-column label="启用" align="center" width="70">
        <template #default="scope">
          <el-tag :type="scope.row.enabled ? 'success' : 'info'" size="small">
            {{ scope.row.enabled ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最近执行时间" align="center" prop="lastRunTime" width="170" sortable="custom" />
      <el-table-column label="最近执行状态" align="center" width="130" prop="lastRunStatus" sortable="custom">
        <template #default="scope">
          <el-tag v-if="scope.row.lastRunStatus"
            :type="scope.row.lastRunStatus === 'SUCCESS' ? 'success' : 'danger'" size="small">
            {{ scope.row.lastRunStatus }}
          </el-tag>
          <span v-else class="color-gray">-</span>
        </template>
      </el-table-column>
      <el-table-column label="最近摘要" align="center" prop="lastRunSummary" min-width="200" show-overflow-tooltip />
    </el-table>
    <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo"
      :total="total" @pagination="getList" />
  </ContentWrap>
</template>

<script lang="ts" setup>
import { buildSortingField } from '@/utils'
import * as CrawlerSourceApi from '@/api/crawler/source'
import type { CrawlerSourceVO } from '@/api/crawler/source'

defineOptions({ name: 'CrawlerSource' })

const message = useMessage()

const loading = ref(true)
const list = ref<CrawlerSourceVO[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  sortingFields: [] as { field: string; order: string }[]
})
const triggering = ref(false)
const triggerResult = ref('')

const handleSortChange = (params: any) => {
  queryParams.sortingFields = [buildSortingField(params)]
  getList()
}

const getList = async () => {
  loading.value = true
  try {
    const data = await CrawlerSourceApi.getSourcePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleTriggerCrawl = async () => {
  triggering.value = true
  triggerResult.value = ''
  try {
    const data = await CrawlerSourceApi.triggerGameCrawl()
    triggerResult.value = data || '触发成功'
    message.success('爬取任务已触发')
    setTimeout(() => getList(), 2000)
  } catch {
    triggerResult.value = '触发失败'
  } finally {
    triggering.value = false
  }
}

onMounted(() => getList())
</script>
