/**
 * 通用格式化工具库
 */

/**
 * 格式化学习时长（将秒数转换为可读的 时/分/秒）
 * @param {Number} seconds 总秒数
 * @returns {String} 易读的时长文本
 */
export function formatDuration(seconds) {
    if (seconds === null || seconds === undefined || seconds <= 0) return '0分'

    const h = Math.floor(seconds / 3600)
    const m = Math.floor((seconds % 3600) / 60)
    const s = seconds % 60

    let result = ''
    if (h > 0) result += `${h}小时`
    if (m > 0) result += `${m}分钟`
    if (s > 0 && h === 0 && m === 0) {
        // 只有秒的情况
        result += `${s}秒`
    }

    // 例如整除的情况
    if (!result) {
        if (s > 0) return `${s}秒`
        return '0分'
    }

    return result
}
