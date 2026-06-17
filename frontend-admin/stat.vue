<template>
  <div class="app-container">
    <!-- Row 1: 总览统计卡片 -->
    <el-row :gutter="15" class="panel-group">
      <el-col :span="4" v-for="card in overviewCards" :key="card.key">
        <el-card shadow="hover" :body-style="{ padding: '10px' }" class="box-card">
          <div class="card-panel">
            <div class="card-panel-icon-wrapper" :style="{ color: card.color }">
              <el-icon class="card-panel-icon"><component :is="card.icon" /></el-icon>
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">{{ card.title }}</div>
              <div class="card-panel-num">{{ overviewData[card.key] || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Row 2: 直线趋势图 -->
    <el-row>
      <el-col :span="24">
        <el-card shadow="never" class="chart-container">
          <div class="chart-header">
            <span class="chart-title">学习人数趋势</span>
            <el-radio-group v-model="trendDays" size="small" @change="fetchTrendData">
              <el-radio-button :label="7">最近7天</el-radio-button>
              <el-radio-button :label="30">最近30天</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="lineChartRef" class="chart" style="height: 240px;" v-loading="loadingTrend"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Row 3: 饼图和条形图 -->
    <el-row :gutter="15">
      <el-col :span="12">
        <el-card shadow="never" class="chart-container">
          <div class="chart-header">
            <span class="chart-title">评分分布</span>
          </div>
          <div ref="pieChartRef" class="chart" style="height: 250px;" v-loading="loadingPie"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never" class="chart-container">
          <div class="chart-header">
            <span class="chart-title">课程排行榜 Top10</span>
            <el-select v-model="topType" size="small" style="width: 120px" @change="renderBarChart">
              <el-option label="按学习人数" value="studentRank" />
              <el-option label="按评分" value="scoreRank" />
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
import { getDashboardOverview, getRatingDistribution, getTopCourses, getTrendData } from '@/api/dashboard'

// ----- 数据卡片 -----
const overviewData = ref({
  courseCount: 0,
  studentCount: 0,
  likeCount: 0,
  favoriteCount: 0,
  reviewCount: 0,
  avgScore: 0
})

const overviewCards = [
  { key: 'courseCount', title: '课程总数', icon: 'Reading', color: '#40c9c6' },
  { key: 'studentCount', title: '学习人数', icon: 'UserFilled', color: '#36a3f7' },
  { key: 'likeCount', title: '点赞总数', icon: 'Avatar', color: '#f4516c' },
  { key: 'favoriteCount', title: '收藏总数', icon: 'StarFilled', color: '#e6a23c' },
  { key: 'reviewCount', title: '评价总数', icon: 'Comment', color: '#34bfa3' },
  { key: 'avgScore', title: '平均评分', icon: 'Medal', color: '#f56c6c' }
]

// ----- 图表 Ref 与实例 -----
const lineChartRef = ref(null)
const pieChartRef = ref(null)
const barChartRef = ref(null)

const lineChart = shallowRef(null)
const pieChart = shallowRef(null)
const barChart = shallowRef(null)

// 状态控制
const trendDays = ref(7)
const topType = ref('studentRank')
const loadingTrend = ref(false)
const loadingPie = ref(false)
const loadingTop = ref(false)

// 排行榜原始数据
const topCoursesData = ref({
  scoreRank: [],
  studentRank: []
})

// ----- Resize 事件 -----
const handleResize = () => {
  if (lineChart.value) lineChart.value.resize()
  if (pieChart.value) pieChart.value.resize()
  if (barChart.value) barChart.value.resize()
}

// ----- 获取总览数据 -----
const fetchOverviewData = async () => {
  try {
    const res = await getDashboardOverview()
    if (res) {
      overviewData.value = res
    }
  } catch (error) {
    console.error('获取总览数据失败', error)
  }
}

// ----- 获取趋势数据并渲染 -----
const fetchTrendData = async () => {
  loadingTrend.value = true
  try {
    const res = await getTrendData({ days: trendDays.value })
    if (res) {
      const option = {
        tooltip: { trigger: 'axis' },
        legend: { 
          data: ['每日活跃人数', '每日新增学员'],
          top: 0
        },
        grid: { top: 35, left: '3%', right: '4%', bottom: '2%', containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: res.dates },
        yAxis: { type: 'value' },
        series: [
          {
            name: '每日活跃人数',
            type: 'line',
            smooth: true,
            data: res.activeStudents,
            itemStyle: { color: '#409EFF' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64,158,255,0.3)' },
                { offset: 1, color: 'rgba(64,158,255,0)' }
              ])
            }
          },
          {
            name: '每日新增学员',
            type: 'line',
            smooth: true,
            data: res.newStudents,
            itemStyle: { color: '#67C23A' }
          }
        ]
      }
      if (!lineChart.value) {
        lineChart.value = echarts.init(lineChartRef.value)
      }
      lineChart.value.setOption(option)
    }
  } catch (error) {
    console.error('获取趋势数据失败', error)
  } finally {
    loadingTrend.value = false
  }
}

// ----- 获取评分分布并渲染 -----
const fetchPieData = async () => {
  loadingPie.value = true
  try {
    const res = await getRatingDistribution()
    if (res) {
      const pieData = res.ratings.map((rating, index) => ({
        value: res.counts[index],
        name: `${rating} 星 (${res.percentages[index]}%)`
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
    console.error('获取评分分布失败', error)
  } finally {
    loadingPie.value = false
  }
}

// ----- 获取排行榜数据并渲染 -----
const fetchTopCoursesData = async () => {
  loadingTop.value = true
  try {
    const res = await getTopCourses()
    if (res) {
      topCoursesData.value = res
      renderBarChart()
    }
  } catch (error) {
    console.error('获取排行数据失败', error)
  } finally {
    loadingTop.value = false
  }
}

const renderBarChart = () => {
  const dataList = topCoursesData.value[topType.value] || []
  // 从下往上展示，由于 echarts y轴类目默认从下往上
  const reverseData = [...dataList].reverse()
  const yData = reverseData.map(item => item.courseName)
  const seriesData = reverseData.map(item => item.value)

  const option = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value' },
    yAxis: {
      type: 'category',
      data: yData,
      axisLabel: {
        interval: 0,
        width: 120,
        overflow: 'truncate'
      }
    },
    series: [
      {
        name: topType.value === 'studentRank' ? '学习人数' : '评分',
        type: 'bar',
        data: seriesData,
        itemStyle: { color: topType.value === 'studentRank' ? '#409EFF' : '#ff9900' },
        label: { show: true, position: 'right' }
      }
    ]
  }
  
  if (!barChart.value) {
    barChart.value = echarts.init(barChartRef.value)
  }
  barChart.value.setOption(option)
}

onMounted(() => {
  fetchOverviewData()
  fetchTrendData()
  fetchPieData()
  fetchTopCoursesData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (lineChart.value) lineChart.value.dispose()
  if (pieChart.value) pieChart.value.dispose()
  if (barChart.value) barChart.value.dispose()
})
</script>

<style scoped>
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
