import request from '@/config/axios'

export interface CrawlerGameResultVO {
  id?: number
  sourceId?: number
  crawlerGameId?: number
  crawlerCategoryId?: number
  gameName?: string
  gameDate?: string
  bibNumber?: string
  name?: string
  nameEn?: string
  nationality?: string
  gender?: string
  ageGroup?: string
  idCard?: string
  passport?: string
  gunTimeMs?: number
  netTimeMs?: number
  rank?: number
  genderRank?: number
  categoryRank?: number
  matchStatus?: string
  matchUserId?: number
  matchConfidence?: string
  matchStrategy?: string
  importStatus?: string
  importTime?: string
  createTime?: string
}

export const getResultPage = (params: PageParam) => {
  return request.get({ url: '/crawler/result/page', params })
}

export const confirmResults = (ids: number[], userId: number) => {
  return request.post({
    url: '/crawler/result/confirm',
    params: { ids: ids.join(','), userId }
  })
}

export const ignoreResults = (ids: number[]) => {
  return request.post({
    url: '/crawler/result/ignore',
    params: { ids: ids.join(',') }
  })
}

export const manualMatch = (resultId: number, userId: number) => {
  return request.post({
    url: '/crawler/result/manual-match',
    params: { resultId, userId }
  })
}
