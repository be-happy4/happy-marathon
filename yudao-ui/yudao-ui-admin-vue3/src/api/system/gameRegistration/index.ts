import request from '@/config/axios'

export interface GameRegistrationVO {
  id?: number
  gameId?: number
  gameName?: string
  gameDate?: string
  userId?: number
  userNickname?: string
  registrationStatus?: string
  gameCategoryId?: number
  gameCategoryName?: string
  categoryDistanceM?: number
  pace?: string
  speed?: number
  priority?: number
  tags?: string
  bibNumber?: string
  gunTimeMs?: number
  netTimeMs?: number
  genderPlace?: number
  overallPlace?: number
  remark?: string
  createTime?: Date
}

export const getGameRegistrationPage = (params: PageParam) => {
  return request.get({ url: '/system/game-registration/page', params })
}

export const getGameRegistration = (id: number) => {
  return request.get({ url: '/system/game-registration/get?id=' + id })
}

export const createGameRegistration = (data: GameRegistrationVO) => {
  return request.post({ url: '/system/game-registration/create', data })
}

export const updateGameRegistration = (data: GameRegistrationVO) => {
  return request.put({ url: '/system/game-registration/update', data })
}

export const deleteGameRegistration = (id: number) => {
  return request.delete({ url: '/system/game-registration/delete?id=' + id })
}

export const deleteGameRegistrationList = (ids: number[]) => {
  return request.delete({
    url: '/system/game-registration/delete-list',
    params: { ids: ids.join(',') }
  })
}

export const exportRegistration = (params: any) => {
  return request.download({ url: '/system/game-registration/export-excel', params })
}
