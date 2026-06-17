<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in stats" :key="item.title">
        <el-card shadow="hover" class="stat-card admin-card">
          <div class="stat-info">
            <div class="label">{{ item.title }}</div>
            <div class="value">{{ item.value }}</div>
          </div>
          <el-icon :class="['stat-icon', item.color]"><component :is="item.icon" /></el-icon>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="mt-20 admin-card">
      <template #header>最近课程动态</template>
      <el-empty description="开发中：此处将展示最近的审核动态和学生反馈" />
    </el-card>
  </div>
</template>

<script setup>
const stats = [
  { title: '课程总数', value: '12', icon: 'Reading', color: 'blue' },
  { title: '待审核', value: '2', icon: 'Clock', color: 'orange' },
  { title: '已发布', value: '8', icon: 'CircleCheck', color: 'green' },
  { title: '学生总数', value: '1,280', icon: 'User', color: 'purple' }
]
</script>

<style scoped>
.dashboard-container {
  padding: 10px;
}
.stat-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
}
.label {
  color: #909399;
  font-size: 14px;
}
.value {
  font-size: 24px;
  font-weight: bold;
  margin-top: 5px;
}
.stat-icon {
  font-size: 48px;
  opacity: 0.2;
}
.blue { color: #409EFF; }
.orange { color: #E6A23C; }
.green { color: #67C23A; }
.purple { color: #9B59B6; }
.mt-20 { margin-top: 20px; }
</style>
