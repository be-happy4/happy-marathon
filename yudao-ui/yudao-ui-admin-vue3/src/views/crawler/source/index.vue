<template>
  <ContentWrap title="数据源管理">
    <el-table v-loading="loading" :data="list" @sort-change="handleSortChange">
      <el-table-column label="ID" align="center" prop="id" width="80" sortable="custom" />
      <el-table-column label="数据源名称" align="center" prop="name" min-width="140" sortable="custom" />
      <el-table-column label="标识" align="center" prop="sourceKey" width="130" sortable="custom" />
      <el-table-column label="爬取类型" align="center" prop="crawlType" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.crawlType === 'GAME' ? 'success' : 'warning'" size="small">
            {{ scope.row.crawlType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Cron 表达式" align="center" prop="cronExpression" width="160" show-overflow-tooltip>
        <template #default="scope">
          <span v-if="scope.row.cronExpression">{{ scope.row.cronExpression }}</span>
          <el-tag v-else type="info" size="small">未配置</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="启用" align="center" width="70">
        <template #default="scope">
          <el-tag :type="scope.row.enabled ? 'success' : 'info'" size="small">
            {{ scope.row.enabled ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最近执行" align="center" prop="lastRunTime" width="160" sortable="custom" />
      <el-table-column label="执行状态" align="center" width="100" prop="lastRunStatus" sortable="custom">
        <template #default="scope">
          <el-tag v-if="scope.row.lastRunStatus"
            :type="scope.row.lastRunStatus === 'SUCCESS' ? 'success' : 'danger'" size="small">
            {{ scope.row.lastRunStatus }}
          </el-tag>
          <span v-else class="color-gray">-</span>
        </template>
      </el-table-column>
      <el-table-column label="最近摘要" align="center" prop="lastRunSummary" min-width="200" show-overflow-tooltip />
      <el-table-column align="center" fixed="right" label="操作" width="240">
        <template #default="scope">
          <el-button link type="primary" @click="handleTrigger(scope.row.sourceKey!)" :loading="triggering === scope.row.sourceKey">
            <Icon icon="ep:video-play" class="mr-5px" /> 爬取
          </el-button>
          <el-button link type="primary" @click="openEdit(scope.row)">
            <Icon icon="ep:edit" class="mr-5px" /> 配置
          </el-button>
          <router-link :to="{ path: '/crawler/task-log', query: { sourceId: scope.row.id } }">
            <el-button link type="primary">
              <Icon icon="ep:document" class="mr-5px" /> 日志
            </el-button>
          </router-link>
        </template>
      </el-table-column>
    </el-table>
    <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo"
      :total="total" @pagination="getList" />
  </ContentWrap>

  <!-- 编辑对话框 -->
  <el-dialog v-model="editVisible" title="数据源配置" width="800px" :close-on-click-modal="false">
    <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
      <el-form-item label="数据源名称" prop="name">
        <el-input v-model="editForm.name" disabled />
      </el-form-item>
      <el-form-item label="定时调度" prop="cronExpression">
        <CronElementPlus v-model="editForm.cronExpression" locale="cn" style="width:100%" />
      </el-form-item>
      <el-form-item label="启用状态" prop="enabled">
        <el-switch v-model="editForm.enabled" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSaveEdit" :loading="saving">保存</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { CronElementPlus } from '@vue-js-cron/element-plus'
import '@vue-js-cron/element-plus/dist/element-plus.css'
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
const triggering = ref<string | false>(false)

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

const handleTrigger = async (sourceKey: string) => {
  triggering.value = sourceKey
  try {
    const data = await CrawlerSourceApi.triggerGameCrawl(sourceKey)
    message.success('爬取完成: ' + data)
    await getList()
  } catch {
    message.error('爬取失败')
  } finally {
    triggering.value = false
  }
}

// Edit dialog
const editVisible = ref(false)
const saving = ref(false)
const editFormRef = ref()
const editForm = reactive({
  id: 0,
  name: '',
  cronExpression: '',
  enabled: true
})
const editRules = {}

const openEdit = (row: CrawlerSourceVO) => {
  editForm.id = row.id!
  editForm.name = row.name!
  editForm.cronExpression = row.cronExpression || ''
  editForm.enabled = row.enabled ?? false
  editVisible.value = true
}

const handleSaveEdit = async () => {
  saving.value = true
  try {
    await CrawlerSourceApi.updateSource({
      id: editForm.id,
      cronExpression: editForm.cronExpression || undefined,
      enabled: editForm.enabled
    })
    message.success('保存成功')
    editVisible.value = false
    await getList()
  } catch {
    message.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => getList())
</script>
