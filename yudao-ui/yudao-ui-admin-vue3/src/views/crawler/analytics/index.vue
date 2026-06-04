<template>
  <div>
    <!-- 数据概览卡片 -->
    <el-row :gutter="16" class="mb-16px">
      <el-col v-for="card in overviewCards" :key="card.label" :xl="4" :lg="6" :md="8" :sm="12" :xs="24">
        <el-card shadow="hover">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-14px text-gray-400">{{ card.label }}</div>
              <div class="text-28px font-bold mt-4px">{{ card.value }}</div>
            </div>
            <Icon :icon="card.icon" :size="36" :style="{ color: card.color }" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 年度趋势 -->
    <el-row :gutter="16" class="mb-16px">
      <el-col :xl="12" :lg="12" :md="24" :sm="24" :xs="24">
        <el-card shadow="hover" class="mb-16px">
          <template #header><span>赛事数量年度趋势</span></template>
          <Echart :options="gameTrendOption" height="300" />
        </el-card>
      </el-col>
      <el-col :xl="12" :lg="12" :md="24" :sm="24" :xs="24">
        <el-card shadow="hover" class="mb-16px">
          <template #header><span>成绩数量年度趋势</span></template>
          <Echart :options="resultTrendOption" height="300" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 分布图表 -->
    <el-row :gutter="16" class="mb-16px">
      <el-col :xl="8" :lg="8" :md="12" :sm="24" :xs="24">
        <el-card shadow="hover" class="mb-16px">
          <template #header><span>赛事类型分布</span></template>
          <Echart :options="gameTypeOption" height="300" />
        </el-card>
      </el-col>
      <el-col :xl="8" :lg="8" :md="12" :sm="24" :xs="24">
        <el-card shadow="hover" class="mb-16px">
          <template #header><span>等级分布</span></template>
          <Echart :options="levelOption" height="300" />
        </el-card>
      </el-col>
      <el-col :xl="8" :lg="8" :md="12" :sm="24" :xs="24">
        <el-card shadow="hover" class="mb-16px">
          <template #header><span>性别比例</span></template>
          <Echart :options="genderOption" height="300" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 国籍 + 地区 -->
    <el-row :gutter="16">
      <el-col :xl="12" :lg="12" :md="24" :sm="24" :xs="24">
        <el-card shadow="hover" class="mb-16px">
          <template #header><span>国籍分布 TOP 15</span></template>
          <Echart :options="nationalityOption" height="400" />
        </el-card>
      </el-col>
      <el-col :xl="12" :lg="12" :md="24" :sm="24" :xs="24">
        <el-card shadow="hover" class="mb-16px">
          <template #header><span>中国省份赛事分布 TOP 15</span></template>
          <Echart :options="regionOption" height="400" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup>
import { EChartsOption } from 'echarts'
import request from '@/config/axios'

defineOptions({ name: 'CrawlerAnalytics' })

const loading = ref(true)

// Overview
const overviewCards = ref<any[]>([])

// Chart options
const gameTrendOption = ref<EChartsOption>({})
const resultTrendOption = ref<EChartsOption>({})
const gameTypeOption = ref<EChartsOption>({})
const levelOption = ref<EChartsOption>({})
const genderOption = ref<EChartsOption>({})
const nationalityOption = ref<EChartsOption>({})
const regionOption = ref<EChartsOption>({})

const TYPE_LABELS: Record<string, string> = {
  marathon: '全马', half_marathon: '半马', road_run: '10K/路跑', trail_run: '越野'
}
const LEVEL_LABELS: Record<string, string> = {
  platinum: '白金标', gold: '金标', elite: '精英标', label: '标牌'
}
const GENDER_LABELS: Record<string, string> = { M: '男', W: '女', X: '混合' }

function toBarOption(data: { n: string; c: number }[], labels?: Record<string, string>, color: string = '#5470c6'): EChartsOption {
  const names = data.map(d => labels?.[d.n] ?? d.n).reverse()
  const vals = data.map(d => d.c).reverse()
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '8%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: names, axisLabel: { width: 100, overflow: 'truncate' } },
    series: [{ type: 'bar', data: vals, color, barMaxWidth: 30 }]
  }
}

function toLineOption(data: { y: string; c: number }[], color: string = '#5470c6'): EChartsOption {
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: data.map(d => d.y), boundaryGap: false },
    yAxis: { type: 'value' },
    series: [{ type: 'line', data: data.map(d => d.c), smooth: true, color, areaStyle: { opacity: 0.15 } }]
  }
}

function toPieOption(data: { n: string; c: number }[], labels?: Record<string, string>): EChartsOption {
  return {
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%', type: 'scroll' },
    series: [{
      type: 'pie', radius: ['35%', '65%'], center: ['50%', '45%'],
      label: { formatter: '{b} {d}%' },
      data: data.map(d => ({ name: labels?.[d.n] ?? d.n, value: d.c })),
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' } }
    }]
  }
}

async function loadData() {
  loading.value = true
  try {
    const resp = await request.get({ url: '/crawler/analytics' })
    const d = resp.data

    // Overview cards
    overviewCards.value = [
      { label: '生产赛事', value: d.totalGames, icon: 'ep:medal', color: '#409EFF' },
      { label: '爬取赛事', value: d.totalCrawlerGames, icon: 'ep:collection', color: '#67C23A' },
      { label: '爬取成绩', value: d.totalResults, icon: 'ep:trophy', color: '#E6A23C' },
      { label: '已匹配', value: d.matchedResults, icon: 'ep:link', color: '#F56C6C' },
      { label: '已导入', value: d.importedResults, icon: 'ep:upload', color: '#909399' },
      { label: '用户', value: d.totalUsers, icon: 'ep:user', color: '#5470c6' }
    ]

    gameTrendOption.value = toLineOption(d.gameTrend, '#409EFF')
    resultTrendOption.value = toLineOption(d.resultTrend, '#67C23A')
    gameTypeOption.value = toPieOption(d.gameTypeDist, TYPE_LABELS)
    levelOption.value = toPieOption(d.levelDist, LEVEL_LABELS)
    genderOption.value = toPieOption(d.genderDist, GENDER_LABELS)
    nationalityOption.value = toBarOption(d.nationalityDist, {}, '#E6A23C')
    regionOption.value = toBarOption(d.regionDist, {}, '#5470c6')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
