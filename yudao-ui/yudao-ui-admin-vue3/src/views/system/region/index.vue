<template>
  <ContentWrap>
    <el-table
      v-loading="loading"
      :data="treeData"
      row-key="id"
      default-expand-all
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column align="center" label="编号" prop="id" width="80" />
      <el-table-column label="地区名称" prop="name" min-width="200">
        <template #default="scope">
          <el-tooltip :content="scope.row.breadcrumb" placement="top" :disabled="!scope.row.breadcrumb">
            <span>{{ scope.row.name }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column align="center" label="ISO 代码" prop="code" width="120" />
      <el-table-column align="center" label="类型" prop="type" width="100">
        <template #default="scope">
          {{ typeMap[scope.row.type] }}
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as RegionApi from '@/api/system/region'

defineOptions({ name: 'SystemRegion' })

const loading = ref(true)
const treeData = ref<RegionApi.RegionNodeVO[]>([])

const typeMap: Record<number, string> = { 1: '洲', 2: '国家', 3: '省份/州', 4: '城市' }

const getList = async () => {
  loading.value = true
  try {
    treeData.value = await RegionApi.getRegionTree()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
