import request from '@/config/axios'

export interface CrawlerGameVO {
  id?: number
  sourceId?: number
  sourceGameId?: string
  name?: string
  nameEn?: string
  gameDate?: string
  regionName?: string
  regionId?: number
  worldAthleticsLevel?: string
  chinaRoadRunLevel?: string
  status?: string
  tags?: string
  importStatus?: string
  importTime?: string
  importGameId?: number
  remark?: string
  createTime?: string
  categories?: CrawlerGameCategoryVO[]
}

export interface CrawlerGameCategoryVO {
  id?: number
  gameType?: string
  distanceKm?: number
}

export const getGamePage = (params: PageParam) => {
  return request.get({ url: '/crawler/game/page', params })
}
