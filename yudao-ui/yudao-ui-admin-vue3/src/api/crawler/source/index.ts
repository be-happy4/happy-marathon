import request from '@/config/axios'

export interface CrawlerSourceVO {
  id?: number
  name?: string
  sourceKey?: string
  crawlType?: string
  sourceType?: string
  handlerType?: string
  handlerClass?: string
  baseUrl?: string
  authConfig?: string
  requestConfig?: string
  responseMapping?: string
  cronExpression?: string
  enabled?: boolean
  sortOrder?: number
  lastRunTime?: string
  lastRunStatus?: string
  lastRunSummary?: string
  createTime?: string
}

export const getSourcePage = (params: PageParam) => {
  return request.get({ url: '/crawler/source/page', params })
}

export const getSource = (id: number): Promise<CrawlerSourceVO> => {
  return request.get({ url: '/crawler/source/get?id=' + id })
}

export const createSource = (data: CrawlerSourceVO) => {
  return request.post({ url: '/crawler/source/create', data })
}

export const updateSource = (data: CrawlerSourceVO) => {
  return request.put({ url: '/crawler/source/update', data })
}

export const deleteSource = (id: number) => {
  return request.delete({ url: '/crawler/source/delete?id=' + id })
}

export const triggerGameCrawl = (sourceKey?: string) => {
  return request.post({ url: '/crawler/source/trigger-game-crawl', params: sourceKey ? { sourceKey } : {} })
}
