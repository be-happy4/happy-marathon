<template>
  <!-- 状态提示 -->
  <ContentWrap v-if="status">
    <el-alert v-if="status === 'CAPTCHA_REQUIRED'" type="warning" :closable="false" show-icon>
      <template #title><span class="text-16px font-bold">需要人工验证</span></template>
      <template #default>
        <p>中国马拉松官网有滑块验证码保护。请查看桌面上的 <b>Chrome 浏览器窗口</b>，手动完成验证后点击查询。</p>
      </template>
    </el-alert>

    <el-result v-else-if="status === 'NO_RESULT'" icon="info" title="未查询到成绩记录"
      sub-title="该选手在中国马拉松官网上没有公开的成绩数据" />

    <el-result v-else-if="status === 'ERROR'" icon="error" title="查询失败" :sub-title="message || '未知错误'" />
  </ContentWrap>

  <!-- 结果展示 -->
  <ContentWrap v-if="status === 'OK'">
    <el-row :gutter="20" class="mb-15px">
      <el-col :span="8">
        <el-statistic title="查询到记录" :value="matchSummary?.totalFound || 0" />
      </el-col>
      <el-col :span="8">
        <el-statistic title="自动匹配" :value="matchSummary?.autoMatched || 0">
          <template #suffix><el-tag size="small" type="success">MATCHED</el-tag></template>
        </el-statistic>
      </el-col>
      <el-col :span="8">
        <el-statistic title="待处理" :value="matchSummary?.unmatched || 0">
          <template #suffix><el-tag size="small" type="warning">UNMATCHED</el-tag></template>
        </el-statistic>
      </el-col>
    </el-row>

    <el-table :data="results" border stripe>
      <el-table-column label="赛事名称" prop="gameName" min-width="180" show-overflow-tooltip />
      <el-table-column label="比赛日期" prop="gameDate" width="120" />
      <el-table-column label="参赛号" prop="bibNumber" width="100" />
      <el-table-column label="枪声成绩" prop="gunTime" width="110" />
      <el-table-column label="净成绩" prop="netTime" width="110" />
      <el-table-column label="总排名" prop="rank" width="80" align="center" />
      <el-table-column label="年龄组" prop="ageGroup" width="100" />
      <el-table-column label="匹配状态" width="110" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.matchStatus === 'MATCHED'" type="success">已匹配</el-tag>
          <el-tag v-else-if="scope.row.matchStatus === 'UNMATCHED'" type="warning">待匹配</el-tag>
          <el-tag v-else type="info">{{ scope.row.matchStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="匹配用户" prop="matchedUser" min-width="120" />
    </el-table>
  </ContentWrap>
</template>

<script lang="ts" setup>
import type { ResultItemVO, MatchSummaryVO } from '@/api/crawler/personalScore'

defineOptions({ name: 'QueryResult' })
defineProps<{
  status: string
  message?: string
  results: ResultItemVO[]
  matchSummary: MatchSummaryVO
}>()
</script>
