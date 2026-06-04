<template>
  <div class="analytics-dashboard">
    <!-- 数据概览卡片 -->
    <el-row :gutter="16" class="mb-8px">
      <el-col v-for="card in overviewCards" :key="card.label" :xl="4" :lg="4" :md="8" :sm="12" :xs="24" class="mb-8px">
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

    <!-- 欢迎 + 快捷 -->
    <el-row :gutter="16" class="mb-8px">
      <el-col :xl="16" :lg="16" :md="24" :sm="24" :xs="24">
        <el-card shadow="hover" class="mb-8px">
          <el-row :gutter="16" justify="space-between">
            <el-col :xl="12" :lg="12" :md="12" :sm="24" :xs="24">
              <div class="flex items-center">
                <el-avatar :src="userAvatar" :size="56" class="mr-16px" />
                <div>
                  <div class="text-20px">{{ username }}，欢迎回来</div>
                  <div class="mt-8px text-14px text-gray-400">
                    数据覆盖 {{ years }} 个赛季 · {{ totalEvents }} 场赛事 · {{ totalResults }} 条成绩
                  </div>
                </div>
              </div>
            </el-col>
            <el-col :xl="12" :lg="12" :md="12" :sm="24" :xs="24">
              <div class="h-full flex items-center justify-end gap-12px lt-sm:mt-10px">
                <div class="px-8px text-right">
                  <div class="mb-8px text-13px text-gray-400">中国赛事</div>
                  <CountTo class="text-24px" :start-val="0" :end-val="chinaEvents" :duration="2000" />
                </div>
                <el-divider direction="vertical" />
                <div class="px-8px text-right">
                  <div class="mb-8px text-13px text-gray-400">中国选手成绩</div>
                  <CountTo class="text-24px" :start-val="0" :end-val="chinaResults" :duration="2000" />
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 趋势图表 -->
        <el-row :gutter="16">
          <el-col :xl="12" :lg="12" :md="24" :sm="24" :xs="24">
            <el-card shadow="hover" class="mb-8px">
              <template #header><span>赛事数量年度趋势</span></template>
              <Echart :options="gameTrendOption" height="260" />
            </el-card>
          </el-col>
          <el-col :xl="12" :lg="12" :md="24" :sm="24" :xs="24">
            <el-card shadow="hover" class="mb-8px">
              <template #header><span>成绩数量年度趋势</span></template>
              <Echart :options="resultTrendOption" height="260" />
            </el-card>
          </el-col>
        </el-row>
      </el-col>

      <el-col :xl="8" :lg="8" :md="24" :sm="24" :xs="24">
        <!-- 赛事类型 -->
        <el-card shadow="hover" class="mb-8px">
          <template #header><span>赛事类型分布</span></template>
          <Echart :options="gameTypeOption" height="240" />
        </el-card>
        <!-- 国际等级 -->
        <el-card shadow="hover" class="mb-8px">
          <template #header><span>国际田联等级</span></template>
          <Echart :options="levelOption" height="240" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 排行 -->
    <el-row :gutter="16">
      <el-col :xl="8" :lg="8" :md="12" :sm="24" :xs="24">
        <el-card shadow="hover" class="mb-8px">
          <template #header><span>中国省份赛事 TOP 10</span></template>
          <Echart :options="regionOption" height="300" />
        </el-card>
      </el-col>
      <el-col :xl="8" :lg="8" :md="12" :sm="24" :xs="24">
        <el-card shadow="hover" class="mb-8px">
          <template #header><span>国籍分布 TOP 10</span></template>
          <Echart :options="nationalityOption" height="300" />
        </el-card>
      </el-col>
      <el-col :xl="8" :lg="8" :md="24" :sm="24" :xs="24">
        <el-card shadow="hover" class="mb-8px">
          <template #header><span>性别比例</span></template>
          <Echart :options="genderOption" height="300" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup>
import { EChartsOption } from 'echarts'
import { useUserStore } from '@/store/modules/user'
import request from '@/config/axios'

defineOptions({ name: 'Index' })

const userStore = useUserStore()
const username = computed(() => userStore.getUser.nickname)
const userAvatar = computed(() => userStore.getUser.avatar)

const loading = ref(true)
const totalEvents = ref(0)
const totalResults = ref(0)
const chinaEvents = ref(0)
const chinaResults = ref(0)
const years = ref(0)

const overviewCards = ref<any[]>([])
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
const GENDER_LABELS: Record<string, string> = { M: '男', W: '女', X: '其他' }
const COUNTRY_LABELS: Record<string, string> = {
  CHN: '中国', ETH: '埃塞俄比亚', KEN: '肯尼亚', USA: '美国', JPN: '日本',
  GER: '德国', GBR: '英国', CAN: '加拿大', BRA: '巴西', RSA: '南非', UKR: '乌克兰'
}

function toLine(data: { y: string; c: number }[], color = '#5470c6'): EChartsOption {
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: data.map(d => d.y), boundaryGap: false },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ type: 'line', data: data.map(d => d.c), smooth: true, color, areaStyle: { opacity: 0.1 } }]
  }
}

function toBar(data: { n: string; c: number }[], labels?: Record<string, string>, color = '#409EFF'): EChartsOption {
  const d = [...data].reverse()
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '8%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: d.map(i => labels?.[i.n] ?? i.n), axisLabel: { width: 90, overflow: 'truncate' } },
    series: [{ type: 'bar', data: d.map(i => i.c), color, barMaxWidth: 24 }]
  }
}

function toPie(data: { n: string; c: number }[], labels?: Record<string, string>): EChartsOption {
  return {
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%', type: 'scroll', textStyle: { fontSize: 11 } },
    series: [{
      type: 'pie', radius: ['40%', '65%'], center: ['50%', '43%'],
      label: { formatter: '{b}\n{d}%', fontSize: 11 },
      data: data.map(d => ({ name: labels?.[d.n] ?? d.n, value: d.c })),
    }]
  }
}

async function load() {
  loading.value = true
  try {
    const resp = await request.get({ url: '/crawler/analytics' })
    const d = resp.data

    totalEvents.value = d.totalGames
    totalResults.value = d.totalResults
    years.value = d.gameTrend?.length ?? 0

    // Count china
    chinaEvents.value = d.regionDist?.reduce((s: number, i: any) => s + i.c, 0) ?? 0
    chinaResults.value = d.nationalityDist?.find((i: any) => i.n === 'CHN')?.c ?? 0

    overviewCards.value = [
      { label: '赛事总数', value: d.totalGames, icon: 'ep:medal', color: '#409EFF' },
      { label: '爬取赛事', value: d.totalCrawlerGames, icon: 'ep:collection', color: '#67C23A' },
      { label: '爬取成绩', value: d.totalResults, icon: 'ep:trophy', color: '#E6A23C' },
      { label: '已匹配', value: d.matchedResults, icon: 'ep:link', color: '#F56C6C' },
      { label: '已导入', value: d.importedResults, icon: 'ep:upload', color: '#909399' },
      { label: '注册用户', value: d.totalUsers, icon: 'ep:user', color: '#7c3aed' },
    ]

    gameTrendOption.value = toLine(d.gameTrend, '#409EFF')
    resultTrendOption.value = toLine(d.resultTrend, '#67C23A')
    gameTypeOption.value = toPie(d.gameTypeDist, TYPE_LABELS)
    levelOption.value = toPie(d.levelDist, LEVEL_LABELS)
    genderOption.value = toPie(d.genderDist, GENDER_LABELS)
    nationalityOption.value = toBar(d.nationalityDist.slice(0, 10), COUNTRY_LABELS, '#E6A23C')
    regionOption.value = toBar(d.regionDist.slice(0, 10).map((i: any) => ({ n: i.n?.replace(', CHN', '') ?? i.n, c: i.c })), {}, '#409EFF')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
