<template>
  <div class="app-container">
    <!-- Row 1: 总览统计卡片 -->
    <el-row :gutter="15" class="panel-group">
      <el-col :span="4" v-for="card in overviewCards" :key="card.key">
        <el-card shadow="hover" :body-style="{ padding: '10px' }" class="box-card">
          <div class="card-panel">
            <div class="card-panel-icon-wrapper" :style="{ color: card.color }">
              <el-icon class="card-panel-icon">
                <component :is="card.icon" />
              </el-icon>
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">{{ card.title }}</div>
              <!-- 显示数值，如为评分则保留一位小数 -->
              <div class="card-panel-num">
                {{ card.key === 'avgScore' ? (overviewData[card.key] || 0)?.toFixed(1) : (overviewData[card.key] || 0)
                }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Row 2: 直线趋势图（多指标同时显示版） -->
    <el-row>
      <el-col :span="24">
        <el-card shadow="never" class="chart-container">
          <div class="chart-header">
            <span class="chart-title">全平台趋势分析</span>
            <el-radio-group v-model="trendDays" size="small" @change="fetchAllTrendData">
              <el-radio-button :label="7">最近7天</el-radio-button>
              <el-radio-button :label="30">最近30天</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="lineChartRef" class="chart" style="height: 280px;" v-loading="loadingTrend"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Row 2.5: 累计趋势图（自平台启动日起） -->
    <el-row>
      <el-col :span="24">
        <el-card shadow="never" class="chart-container">
          <div class="chart-header">
            <span class="chart-title">累计数据统计（自平台启动日起）</span>
            <el-tag size="small" type="info">累计用户 & 累计学习</el-tag>
          </div>
          <div ref="cumulativeChartRef" class="chart" style="height: 280px;" v-loading="loadingCumulative"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Row 3: 饼图和条形图 -->
    <el-row :gutter="15">
      <!-- 课程评分分布 -->
      <el-col :span="12">
        <el-card shadow="never" class="chart-container">
          <div class="chart-header">
            <span class="chart-title">评分分布统计</span>
          </div>
          <div ref="pieChartRef" class="chart" style="height: 250px;" v-loading="loadingPie"></div>
        </el-card>
      </el-col>

      <!-- 优秀内容排行（支持属性切换） -->
      <el-col :span="12">
        <el-card shadow="never" class="chart-container">
          <div class="chart-header">
            <span class="chart-title">课程排行榜 Top10</span>
            <el-select v-model="topRankBy" size="small" style="width: 120px;" @change="fetchTopCoursesData">
              <el-option label="按课程热度" value="hot" />
              <el-option label="按学习人数" value="studentCount" />
            </el-select>
          </div>
          <div ref="barChartRef" class="chart" style="height: 250px;" v-loading="loadingTop"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, shallowRef } from 'vue'
import * as echarts from 'echarts'
import {
  getDashboardOverview,
  getRatingDistribution,
  getTopCourses,
  getTrendData,
  getCumulativeData
} from '@/api/dashboard'

// ----- 第1部分：基础总览数据模块 -----
const overviewData = ref({
  userCount: 0,
  teacherCount: 0,
  courseCount: 0,
  studentCount: 0,
  reviewCount: 0,
  avgScore: 0
})

// 参照讲师端的配色设计与尺寸
const overviewCards = [
  { key: 'userCount', title: '用户总数', icon: 'User', color: '#40c9c6' },
  { key: 'teacherCount', title: '讲师总数', icon: 'Avatar', color: '#36a3f7' },
  { key: 'courseCount', title: '课程总数', icon: 'Reading', color: '#f4516c' },
  { key: 'studentCount', title: '学习人数', icon: 'Headset', color: '#e6a23c' },
  { key: 'reviewCount', title: '评价总数', icon: 'Comment', color: '#34bfa3' },
  { key: 'avgScore', title: '综合评分', icon: 'Star', color: '#f56c6c' }
]

const fetchOverviewData = async () => {
  try {
    const res = await getDashboardOverview()
    if (res && res.data) {
      overviewData.value = res.data
    }
  } catch (error) {
    console.error('获取监控总览数据失败', error)
  }
}

// ----- 共有图表 Ref 初始化区域 -----
const lineChartRef = ref(null)
const pieChartRef = ref(null)
const barChartRef = ref(null)
const cumulativeChartRef = ref(null)

const lineChart = shallowRef(null)
const pieChart = shallowRef(null)
const barChart = shallowRef(null)
const cumulativeChart = shallowRef(null)

// Resize 响应图表大小
const handleResize = () => {
  if (lineChart.value) lineChart.value.resize()
  if (pieChart.value) pieChart.value.resize()
  if (barChart.value) barChart.value.resize()
  if (cumulativeChart.value) cumulativeChart.value.resize()
}

// ----- 第2部分：趋势折线图 (Trend) -----
const trendDays = ref(7)
// 固定三大指标
const trendMetrics = ['newUser', 'activeLearn', 'newCourse']
const loadingTrend = ref(false)

const METRIC_MAP = {
  newUser: { name: '每日新增用户', color: '#409EFF' },
  activeLearn: { name: '每日活跃人数', color: '#67C23A' },
  newCourse: { name: '每日新增课程', color: '#E6A23C' }
}

const fetchAllTrendData = async () => {
  loadingTrend.value = true
  if (trendMetrics.length === 0) {
    if (lineChart.value) {
      lineChart.value.clear()
    }
    loadingTrend.value = false
    return
  }

  try {
    // 接口文档为单指标获取（/api/admin/dashboard/trend），这里通过 Promise.all 并发获取多指标再合并组装图表
    const requests = trendMetrics.map(metric => getTrendData({ days: trendDays.value, metric }))
    const results = await Promise.all(requests)

    if (results.length > 0) {
      // 获取共有 X 轴（日期相同）
      const dates = results[0]?.data?.dates || []

      const seriesList = results.map((res, index) => {
        const metricKey = trendMetrics[index]
        const config = METRIC_MAP[metricKey]
        const values = res?.data?.values || []
        return {
          name: config.name,
          type: 'line',
          smooth: true,
          data: values,
          itemStyle: { color: config.color }
          // 根据需要可以不再每个都显示渐变填充，以免重叠干扰
        }
      })

      const option = {
        tooltip: { trigger: 'axis' },
        legend: {
          data: trendMetrics.map(k => METRIC_MAP[k].name),
          top: 0
        },
        grid: { top: 35, left: '2%', right: '3%', bottom: '2%', containLabel: true },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates
        },
        yAxis: { type: 'value' },
        series: seriesList
      }

      if (!lineChart.value) {
        lineChart.value = echarts.init(lineChartRef.value)
      }
      lineChart.value.setOption(option, true)
    }
  } catch (error) {
    console.error('获取多指标趋势数据失败', error)
  } finally {
    loadingTrend.value = false
  }
}

// ----- 第2.5部分：累计趋势图（自平台启动日起） -----
const loadingCumulative = ref(false)

const fetchCumulativeData = async () => {
  loadingCumulative.value = true
  try {
    const res = await getCumulativeData()
    if (res && res.data) {
      const { dates, cumulativeUsers, cumulativeLearns } = res.data

      // 当数据点过多时，进行采样以避免图表卡顿（最多显示60个数据点）
      const MAX_POINTS = 60
      let sampledDates = dates
      let sampledUsers = cumulativeUsers
      let sampledLearns = cumulativeLearns

      if (dates.length > MAX_POINTS) {
        const step = Math.ceil(dates.length / MAX_POINTS)
        sampledDates = []
        sampledUsers = []
        sampledLearns = []
        for (let i = 0; i < dates.length; i += step) {
          sampledDates.push(dates[i])
          // 累计值取采样点的实际值（不是最后一个）
          sampledUsers.push(cumulativeUsers[Math.min(i + step - 1, dates.length - 1)])
          sampledLearns.push(cumulativeLearns[Math.min(i + step - 1, dates.length - 1)])
        }
        // 确保包含最后一个点
        if (sampledDates[sampledDates.length - 1] !== dates[dates.length - 1]) {
          sampledDates.push(dates[dates.length - 1])
          sampledUsers.push(cumulativeUsers[cumulativeUsers.length - 1])
          sampledLearns.push(cumulativeLearns[cumulativeLearns.length - 1])
        }
      }

      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: (params) => {
            let html = `<div style="font-weight:bold">${params[0].axisValue}</div>`
            params.forEach(p => {
              html += `<br/>${p.marker} ${p.seriesName}: <b>${p.value}</b>`
            })
            return html
          }
        },
        legend: {
          data: ['累计用户数', '累计学习次数'],
          top: 0
        },
        grid: { top: 35, left: '3%', right: '3%', bottom: '2%', containLabel: true },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: sampledDates,
          axisLabel: { fontSize: 11, rotate: sampledDates.length > 30 ? 30 : 0 }
        },
        yAxis: [
          {
            type: 'value',
            name: '用户数',
            position: 'left',
            splitLine: { lineStyle: { type: 'dashed' } }
          },
          {
            type: 'value',
            name: '学习次数',
            position: 'right',
            splitLine: { show: false }
          }
        ],
        series: [
          {
            name: '累计用户数',
            type: 'line',
            smooth: true,
            data: sampledUsers,
            itemStyle: { color: '#409EFF' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64,158,255,0.25)' },
                { offset: 1, color: 'rgba(64,158,255,0.02)' }
              ])
            }
          },
          {
            name: '累计学习次数',
            type: 'line',
            smooth: true,
            yAxisIndex: 1,
            data: sampledLearns,
            itemStyle: { color: '#67C23A' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(103,194,58,0.25)' },
                { offset: 1, color: 'rgba(103,194,58,0.02)' }
              ])
            }
          }
        ]
      }

      if (!cumulativeChart.value) {
        cumulativeChart.value = echarts.init(cumulativeChartRef.value)
      }
      cumulativeChart.value.setOption(option, true)
    }
  } catch (error) {
    console.error('获取累计趋势数据失败', error)
  } finally {
    loadingCumulative.value = false
  }
}

// ----- 第3部分：评分分布饼图 (Pie) -----
const loadingPie = ref(false)

const fetchPieData = async () => {
  loadingPie.value = true
  try {
    const res = await getRatingDistribution()
    if (res && res.data) {
      const dbData = res.data
      const pieData = dbData.ratings.map((star, idx) => ({
        value: dbData.counts[idx],
        name: `${star} 星 (${dbData.percentages[idx]}%)` // 参考讲师端，简洁的图例名称
      }))

      const option = {
        tooltip: { trigger: 'item' },
        legend: { orient: 'vertical', left: 'left' },
        series: [
          {
            name: '评分分布',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: { show: false, position: 'center' },
            emphasis: {
              label: { show: true, fontSize: 20, fontWeight: 'bold' }
            },
            labelLine: { show: false },
            data: pieData
          }
        ]
      }
      if (!pieChart.value) {
        pieChart.value = echarts.init(pieChartRef.value)
      }
      pieChart.value.setOption(option)
    }
  } catch (error) {
    console.error('获取评分饼图失败', error)
  } finally {
    loadingPie.value = false
  }
}

// ----- 第4部分：课程条形分布排行榜图 (Bar) -----
const topRankBy = ref('hot') // hot / studentCount
const loadingTop = ref(false)

const fetchTopCoursesData = async () => {
  loadingTop.value = true
  try {
    const res = await getTopCourses({ rankBy: topRankBy.value })
    if (res && res.data && res.data.items) {
      const rawItems = res.data.items
      // 从下往上展示
      const chartItems = [...rawItems].reverse()

      const yDataNames = chartItems.map(item => item.courseName)
      const seriesValues = chartItems.map(item => item.value)

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'value' },
        yAxis: {
          type: 'category',
          data: yDataNames,
          axisLabel: {
            interval: 0,
            width: 120,
            overflow: 'truncate',
            formatter: (val) => {
              return val.length > 12 ? val.substr(0, 11) + '..' : val
            }
          }
        },
        series: [
          {
            name: topRankBy.value === 'hot' ? '推荐热度指标' : '学习总人数',
            type: 'bar',
            data: seriesValues,
            itemStyle: {
              color: topRankBy.value === 'hot' ? '#f56c6c' : '#409EFF',
            },
            label: {
              show: true,
              position: 'right'
            }
          }
        ]
      }

      if (!barChart.value) {
        barChart.value = echarts.init(barChartRef.value)
      }
      barChart.value.setOption(option, true)
    }
  } catch (error) {
    console.error('获取排行榜数据失败', error)
  } finally {
    loadingTop.value = false
  }
}

// ----- 生命周期绑定 -----
onMounted(() => {
  fetchOverviewData()
  fetchAllTrendData()
  fetchCumulativeData()
  fetchPieData()
  fetchTopCoursesData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (lineChart.value) lineChart.value.dispose()
  if (pieChart.value) pieChart.value.dispose()
  if (barChart.value) barChart.value.dispose()
  if (cumulativeChart.value) cumulativeChart.value.dispose()
})
</script>

<style scoped>
/* ----- 直接复用讲师端 stat.vue 页面核心布局体系样式 ----- */
.app-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 50px);
  overflow-x: hidden;
}

.panel-group {
  margin-bottom: 15px;
}

.box-card {
  border: none;
}

.card-panel {
  height: 70px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-panel-icon-wrapper {
  padding: 10px;
  border-radius: 6px;
  font-size: 38px;
}

.card-panel-icon {
  display: block;
}

.card-panel-description {
  font-weight: bold;
  text-align: right;
}

.card-panel-text {
  line-height: 18px;
  color: #8c8c8c;
  font-size: 13px;
  margin-bottom: 8px;
}

.card-panel-num {
  font-size: 18px;
  color: #666;
}

.chart-container {
  margin-bottom: 15px;
  background: #fff;
  border-radius: 4px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.chart-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}
</style>
