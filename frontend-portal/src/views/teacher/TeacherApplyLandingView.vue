<template>
  <div class="teacher-landing-page">
    <div class="page-container">
      <section class="intro-section">
        <h1 class="page-title">讲师入驻申请</h1>
        <p class="page-subtitle">成为讲师后可以发布课程、查看学员学习情况，与更多学习者分享您的知识与经验。</p>
        
        <el-row :gutter="24" class="advantage-cards">
          <el-col :span="8">
            <el-card shadow="hover" class="adv-card">
              <el-icon class="adv-icon text-primary"><Notebook /></el-icon>
              <h3>发布自己的课程</h3>
              <p>打造专属在线课堂，沉淀您的知识体系</p>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="adv-card">
              <el-icon class="adv-icon text-success"><Share /></el-icon>
              <h3>分享专业知识</h3>
              <p>发挥专长，与千万学习者进行思维碰撞</p>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="adv-card">
              <el-icon class="adv-icon text-warning"><TrendCharts /></el-icon>
              <h3>帮助更多学习者成长</h3>
              <p>答疑解惑，在教育路上实现您的价值</p>
            </el-card>
          </el-col>
        </el-row>

        <div class="action-section">
          <el-button type="primary" size="large" class="apply-btn" @click="goToForm">
            去填写申请资料
          </el-button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Plus, Share, Notebook, TrendCharts } from '@element-plus/icons-vue'

const router = useRouter()

const goToForm = () => {
  router.push({ path: '/teacher-apply/form' })
}
</script>

<style scoped>
.teacher-landing-page {
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
  padding: 60px 0;
}

.page-container {
  max-width: 900px;
  margin: 0 auto;
}

.intro-section {
  text-align: center;
}

.page-title {
  font-size: 36px;
  color: #303133;
  margin-bottom: 16px;
}

.page-subtitle {
  font-size: 18px;
  color: #606266;
  margin-bottom: 40px;
}

.advantage-cards {
  margin-top: 40px;
  margin-bottom: 60px;
}

.adv-card {
  height: 100%;
  text-align: center;
  padding: 30px 10px;
  border-radius: 8px;
}

.adv-icon {
  font-size: 56px;
  margin-bottom: 20px;
}

.text-primary { color: #409eff; }
.text-success { color: #67c23a; }
.text-warning { color: #e6a23c; }

.adv-card h3 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 12px;
}

.adv-card p {
  font-size: 14px;
  color: #909399;
  line-height: 1.6;
}

.action-section {
  margin-top: 40px;
}

.apply-btn {
  width: 240px;
  font-size: 18px;
  height: 50px;
  border-radius: 25px;
}
</style>
