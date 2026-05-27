import request from '@/config/axios'

export interface GameVO {
  id?: number
  name?: string
  gameDate?: string
  gameType?: string
  worldAthleticsLevel?: string
  chinaRoadRunLevel?: string
  status?: string
  tags?: string
  url?: string
  regionId?: number
  regionName?: string
  regionBreadcrumb?: string
  remark?: string
  createTime?: Date
}

export interface GameSimpleVO {
  id: number
  name: string
  gameDate?: string
}

export interface GameCategoryVO {
  id: number
  gameId: number
  gameType: string
  distanceM?: number
}

export const getGamePage = (params: PageParam) => {
  return request.get({ url: '/system/game/page', params })
}

export const getGame = (id: number): Promise<GameVO> => {
  return request.get({ url: '/system/game/get?id=' + id })
}

export const createGame = (data: GameVO) => {
  return request.post({ url: '/system/game/create', data })
}

export const updateGame = (data: GameVO) => {
  return request.put({ url: '/system/game/update', data })
}

export const deleteGame = (id: number) => {
  return request.delete({ url: '/system/game/delete?id=' + id })
}

export const deleteGameList = (ids: number[]) => {
  return request.delete({ url: '/system/game/delete-list', params: { ids: ids.join(',') } })
}

export const getGameSimpleList = (): Promise<GameSimpleVO[]> => {
  return request.get({ url: '/system/game/simple-list' })
}

export const getGameCategories = (gameId: number): Promise<GameCategoryVO[]> => {
  return request.get({ url: '/system/game/categories', params: { gameId } })
}

export const exportGame = (params: any) => {
  return request.download({ url: '/system/game/export-excel', params })
}

export const getGameImportTemplate = () => {
  return request.download({ url: '/system/game/get-import-template' })
}

export const importGame = (data: FormData) => {
  return request.upload({ url: '/system/game/import', data })
}
