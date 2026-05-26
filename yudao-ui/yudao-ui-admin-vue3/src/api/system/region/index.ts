import request from '@/config/axios'

export interface RegionNodeVO {
  id: number
  name: string
  code?: string
  type?: number
  children?: RegionNodeVO[]
}

export const getRegionTree = (): Promise<RegionNodeVO[]> => {
  return request.get({ url: '/system/region/tree' })
}
