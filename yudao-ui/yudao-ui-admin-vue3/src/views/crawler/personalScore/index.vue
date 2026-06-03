<template>
  <!-- 模式切换 -->
  <ContentWrap>
    <el-radio-group v-model="mode" @change="resetAll">
      <el-radio-button value="single">单次查询</el-radio-button>
      <el-radio-button value="batch">批量查询</el-radio-button>
    </el-radio-group>
    <span class="ml-15px text-gray-400 text-12px">
      {{ mode === 'single' ? '每次独立打开浏览器' : '手动过一次验证码，自动查询多人' }}
    </span>
  </ContentWrap>

  <!-- ========== 单次查询 ========== -->
  <template v-if="mode === 'single'">
    <ContentWrap>
      <el-alert type="info" :closable="false" show-icon class="!mb-15px">
        <template #title>
          通过中国马拉松官网公开查询页，按姓名和身份证号查询个人完赛记录。
        </template>
      </el-alert>
      <el-form ref="singleFormRef" :model="singleForm" :inline="true" :rules="singleRules" label-width="100px">
        <el-form-item label="选手姓名" prop="name">
          <el-input v-model="singleForm.name" placeholder="请输入姓名" clearable style="width:180px" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="singleForm.idCard" placeholder="选填" clearable style="width:260px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="querying" @click="handleSingleQuery">
            <Icon icon="ep:search" class="mr-5px" /> 查询
          </el-button>
          <el-button @click="resetSingle">
            <Icon icon="ep:refresh" class="mr-5px" /> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </ContentWrap>

    <!-- 单次结果 -->
    <QueryResult
      :status="resultStatus"
      :message="resultMessage"
      :results="results"
      :match-summary="matchSummary"
    />
  </template>

  <!-- ========== 批量查询 ========== -->
  <template v-else>
    <ContentWrap>
      <el-alert type="warning" :closable="false" show-icon class="!mb-15px">
        <template #title>批量查询流程</template>
        <template #default>
          <ol class="m-0 pl-20px">
            <li>点击「开始批量查询」→ 桌面打开 Chrome 浏览器</li>
            <li>手动完成滑块验证码（最多等待 120 秒）</li>
            <li>验证码通过后自动逐人查询</li>
          </ol>
        </template>
      </el-alert>

      <div class="mb-10px">
        <el-button type="primary" :loading="batchQuerying" @click="handleBatchQuery">
          <Icon icon="ep:video-play" class="mr-5px" /> 开始批量查询
        </el-button>
        <el-button @click="addPersonRow">+ 添加行</el-button>
        <el-button @click="batchPersons = [{ name: '', idCard: '' }]">清空列表</el-button>
        <el-button @click="pasteFromClipboard">从剪贴板粘贴</el-button>
        <span class="ml-10px text-gray-400">{{ batchPersons.length }} 人</span>
      </div>

      <el-table :data="batchPersons" border stripe max-height="400">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column label="姓名" prop="name" min-width="150">
          <template #default="scope">
            <el-input v-model="scope.row.name" placeholder="必填" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="身份证号" prop="idCard" min-width="220">
          <template #default="scope">
            <el-input v-model="scope.row.idCard" placeholder="选填，提高匹配度" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template #default="scope">
            <el-button link type="danger" size="small" @click="batchPersons.splice(scope.$index, 1)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </ContentWrap>

    <!-- 批量进度 -->
    <ContentWrap v-if="batchQuerying || batchStatus">
      <el-alert
        v-if="batchStatus === 'CAPTCHA_REQUIRED'"
        type="warning" :closable="false" show-icon :title="batchMessage"
      />
      <el-alert
        v-else-if="batchStatus === 'ERROR'"
        type="error" :closable="false" show-icon :title="batchMessage"
      />

      <!-- 汇总 -->
      <el-row v-if="batchSummary.totalResults" :gutter="20" class="mt-15px mb-15px">
        <el-col :span="6"><el-statistic title="查询人数" :value="batchSummary.totalPersons" /></el-col>
        <el-col :span="6"><el-statistic title="成功" :value="batchSummary.successPersons">
          <template #suffix><el-tag size="small" type="success">OK</el-tag></template>
        </el-statistic></el-col>
        <el-col :span="6"><el-statistic title="总成绩" :value="batchSummary.totalResults" /></el-col>
        <el-col :span="6"><el-statistic title="已匹配" :value="batchSummary.matched">
          <template #suffix><el-tag size="small" type="success">MATCHED</el-tag></template>
        </el-statistic></el-col>
      </el-row>

      <!-- 每人结果 -->
      <el-collapse v-if="batchPersonResults.length" class="mt-15px">
        <el-collapse-item
          v-for="pr in batchPersonResults" :key="pr.name"
          :title="`${pr.name} (${pr.idCardMasked || '无证件号'}) — ${pr.queryStatus === 'OK' ? pr.resultCount + ' 条成绩' : pr.queryStatus}`"
          :name="pr.name"
        >
          <template v-if="pr.queryStatus === 'OK' && pr.results?.length">
            <el-table :data="pr.results" border stripe size="small">
              <el-table-column label="赛事名称" prop="gameName" min-width="160" show-overflow-tooltip />
              <el-table-column label="日期" prop="gameDate" width="110" />
              <el-table-column label="参赛号" prop="bibNumber" width="90" />
              <el-table-column label="枪声成绩" prop="gunTime" width="100" />
              <el-table-column label="净成绩" prop="netTime" width="100" />
              <el-table-column label="排名" prop="rank" width="70" />
              <el-table-column label="年龄组" prop="ageGroup" width="90" />
              <el-table-column label="匹配" width="80">
                <template #default="s">
                  <el-tag v-if="s.row.matchStatus === 'MATCHED'" size="small" type="success">已匹配</el-tag>
                  <el-tag v-else size="small" type="warning">待匹配</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </template>
          <template v-else>
            <el-empty :description="pr.errorMsg || '无数据'" :image-size="60" />
          </template>
        </el-collapse-item>
      </el-collapse>
    </ContentWrap>
  </template>
</template>

<script lang="ts" setup>
import * as PersonalScoreApi from '@/api/crawler/personalScore'
import QueryResult from './QueryResult.vue'

defineOptions({ name: 'CrawlerPersonalScore' })

const message = useMessage()

// ===== Mode =====
const mode = ref<'single' | 'batch'>('single')

// ===== Single =====
const singleFormRef = ref()
const querying = ref(false)
const resultStatus = ref('')
const resultMessage = ref('')
const results = ref<PersonalScoreApi.ResultItemVO[]>([])
const matchSummary = ref<PersonalScoreApi.MatchSummaryVO>({ totalFound: 0, autoMatched: 0, unmatched: 0 })
const singleForm = reactive({ name: '', idCard: '' })
const singleRules = { name: [{ required: true, message: '请输入姓名', trigger: 'blur' }] }

const handleSingleQuery = async () => {
  await singleFormRef.value?.validate()
  querying.value = true
  resultStatus.value = ''
  try {
    const resp = await PersonalScoreApi.queryPersonalScore({ name: singleForm.name, idCard: singleForm.idCard || undefined })
    resultStatus.value = resp.status || 'ERROR'
    resultMessage.value = resp.message || ''
    if (resp.status === 'OK') {
      results.value = resp.results || []
      matchSummary.value = resp.matchSummary || { totalFound: 0, autoMatched: 0, unmatched: 0 }
      message.success(resp.message || '查询成功')
    } else if (resp.status === 'NO_RESULT') {
      message.info(resp.message || '未查询到记录')
    } else if (resp.status === 'CAPTCHA_REQUIRED') {
      message.warning('需要手动完成滑块验证码')
    }
  } catch {
    resultStatus.value = 'ERROR'
    resultMessage.value = '网络请求失败'
  } finally {
    querying.value = false
  }
}

const resetSingle = () => { singleFormRef.value?.resetFields(); resultStatus.value = ''; results.value = [] }

// ===== Batch =====
const batchQuerying = ref(false)
const batchStatus = ref('')
const batchMessage = ref('')
const batchSummary = ref<PersonalScoreApi.BatchSummaryVO>({ totalPersons: 0, successPersons: 0, totalResults: 0, matched: 0, unmatched: 0 })
const batchPersonResults = ref<PersonalScoreApi.PersonResultVO[]>([])
const batchPersons = ref<{ name: string; idCard: string }[]>([{ name: '', idCard: '' }])

const addPersonRow = () => batchPersons.value.push({ name: '', idCard: '' })

const pasteFromClipboard = async () => {
  try {
    const text = await navigator.clipboard.readText()
    const lines = text.trim().split(/[\n\r]+/)
    const parsed: { name: string; idCard: string }[] = []
    for (const line of lines) {
      const parts = line.trim().split(/[\t,，\s]+/)
      if (parts.length >= 1 && parts[0]) {
        parsed.push({ name: parts[0], idCard: parts[1] || '' })
      }
    }
    if (parsed.length) {
      batchPersons.value = parsed
      message.success(`已粘贴 ${parsed.length} 人`)
    }
  } catch {
    message.warning('无法读取剪贴板，请手动输入或点击「添加行」')
  }
}

const handleBatchQuery = async () => {
  const valid = batchPersons.value.filter(p => p.name.trim())
  if (!valid.length) { message.warning('请至少填写一个姓名'); return }
  if (valid.length > 50) { message.warning('单次最多 50 人'); return }

  batchQuerying.value = true
  batchStatus.value = ''
  batchMessage.value = ''
  batchPersonResults.value = []
  batchSummary.value = { totalPersons: 0, successPersons: 0, totalResults: 0, matched: 0, unmatched: 0 }

  try {
    const persons = valid.map(p => ({ name: p.name.trim(), idCard: p.idCard?.trim() || undefined }))
    const resp = await PersonalScoreApi.batchQueryPersonalScore({ persons })

    batchStatus.value = resp.status || 'ERROR'
    batchMessage.value = resp.message || ''
    batchPersonResults.value = resp.personResults || []
    batchSummary.value = resp.summary || { totalPersons: 0, successPersons: 0, totalResults: 0, matched: 0, unmatched: 0 }

    if (resp.status === 'OK') {
      message.success(resp.message || '批量查询完成')
    } else if (resp.status === 'CAPTCHA_REQUIRED') {
      message.warning('需要手动完成滑块验证码，请重试')
    }
  } catch {
    batchStatus.value = 'ERROR'
    batchMessage.value = '网络请求失败或超时（批量查询可能耗时较长）'
  } finally {
    batchQuerying.value = false
  }
}

const resetAll = () => {
  resultStatus.value = ''
  batchStatus.value = ''
  batchPersonResults.value = []
}

// ===== Remove unused =====
// singleForm, singleRules, matchSummary, results, resultMessage are used in template
void singleForm; void singleRules; void matchSummary; void results; void resultMessage
</script>
