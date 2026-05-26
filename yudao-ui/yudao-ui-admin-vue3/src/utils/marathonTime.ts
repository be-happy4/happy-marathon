/** 毫秒格式化为 HH:mm:ss（保留到秒） */
export const formatDurationMs = (ms?: number | null): string => {
  if (ms == null || ms < 0) {
    return ''
  }
  const totalSeconds = Math.floor(ms / 1000)
  const s = totalSeconds % 60
  const totalMinutes = Math.floor(totalSeconds / 60)
  const m = totalMinutes % 60
  const h = Math.floor(totalMinutes / 60)
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

/** HH:mm:ss 解析为毫秒 */
export const parseDurationMs = (text?: string): number | undefined => {
  if (!text) {
    return undefined
  }
  const trimmed = text.trim()
  const match = trimmed.match(/^(\d+):(\d{1,2}):(\d{1,2})$/)
  if (!match) {
    return undefined
  }
  const h = Number(match[1])
  const m = Number(match[2])
  const s = Number(match[3])
  return ((h * 60 + m) * 60 + s) * 1000
}
