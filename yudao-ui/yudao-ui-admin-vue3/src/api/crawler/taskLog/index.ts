import request from '@/config/axios'

export interface CrawlerTaskLogVO {
  id?: number
  sourceId?: number
  sourceName?: string
  crawlType?: string
  crawlMode?: string
  startTime?: string
  endTime?: string
  status?: string
  totalFetched?: number
  totalNew?: number
  totalUpdated?: number
  totalIgnored?: number
  errorMsg?: string
  createTime?: string
}

export const getTaskLogPage = (params: PageParam) => {
  return request.get({ url: '/crawler/task-log/page', params })
}

export const getTaskLog = (id: number): Promise<CrawlerTaskLogVO> => {
  return request.get({ url: '/crawler/task-log/get?id=' + id })
}
