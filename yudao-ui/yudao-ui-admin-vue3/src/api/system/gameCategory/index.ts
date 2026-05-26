import request from '@/config/axios'

export interface GameCategoryVO {
  id?: number
  gameId?: number
  gameName?: string
  gameDate?: string
  gameType?: string
  distanceM?: number
}

export const getGameCategoryPage = (params: PageParam) => {
  return request.get({ url: '/system/game-category/page', params })
}

export const getGameCategoryList = (gameId: number): Promise<GameCategoryVO[]> => {
  return request.get({ url: '/system/game-category/list', params: { gameId } })
}

export const createGameCategory = (data: GameCategoryVO) => {
  return request.post({ url: '/system/game-category/create', data })
}

export const updateGameCategory = (data: GameCategoryVO) => {
  return request.put({ url: '/system/game-category/update', data })
}

export const deleteGameCategory = (id: number) => {
  return request.delete({ url: '/system/game-category/delete?id=' + id })
}
