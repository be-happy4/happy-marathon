import request from '@/config/axios'

// ===== Single Query =====

export interface PersonalScoreQueryReqVO {
  name: string
  idCard?: string
  phone?: string
}

export interface ResultItemVO {
  gameName?: string
  gameDate?: string
  bibNumber?: string
  gunTime?: string
  netTime?: string
  rank?: number
  ageGroup?: string
  matchStatus?: string
  matchedUser?: string
}

export interface MatchSummaryVO {
  totalFound?: number
  autoMatched?: number
  unmatched?: number
}

export interface PersonalScoreQueryRespVO {
  status?: string
  message?: string
  results?: ResultItemVO[]
  matchSummary?: MatchSummaryVO
}

// ===== Batch Query =====

export interface PersonItemVO {
  name: string
  idCard?: string
}

export interface PersonResultVO {
  name?: string
  idCardMasked?: string
  queryStatus?: string
  errorMsg?: string
  resultCount?: number
  results?: ResultItemVO[]
}

export interface BatchSummaryVO {
  totalPersons?: number
  successPersons?: number
  totalResults?: number
  matched?: number
  unmatched?: number
}

export interface PersonalScoreBatchRespVO {
  status?: string
  message?: string
  personResults?: PersonResultVO[]
  summary?: BatchSummaryVO
}

export const queryPersonalScore = (data: PersonalScoreQueryReqVO) => {
  return request.post({ url: '/crawler/personal-score/query', data })
}

export const batchQueryPersonalScore = (data: { persons: PersonItemVO[] }) => {
  return request.post({ url: '/crawler/personal-score/batch-query', data })
}
