<template>
  <section class="course-section">
    <div class="section-header">
      <div class="section-left">
        <h2 class="section-title">{{ title }}</h2>
        <span class="section-subtitle">{{ subtitle }}</span>
      </div>
      <router-link :to="moreLink" class="more-link">
        更多课程
        <el-icon><ArrowRight /></el-icon>
      </router-link>
    </div>

    <div v-if="loading" class="section-skeleton">
      <el-skeleton v-for="i in 4" :key="i" animated>
        <template #template>
          <el-skeleton-item variant="image" style="width:100%;height:140px;border-radius:8px" />
          <el-skeleton-item variant="h3" style="margin-top:10px;width:80%" />
          <el-skeleton-item variant="text" style="width:60%" />
        </template>
      </el-skeleton>
    </div>

    <div v-else-if="courses.length" class="section-grid">
      <CourseCard
        v-for="course in courses"
        :key="course.id"
        :course="course"
      />
    </div>

    <el-empty v-else description="暂无课程" :image-size="80" />
  </section>
</template>

<script setup>
import { ArrowRight } from '@element-plus/icons-vue'
import CourseCard from './CourseCard.vue'

defineProps({
  title: String,
  subtitle: String,
  courses: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  moreLink: { type: String, default: '/courses' }
})
</script>

<style scoped>
.course-section {
  background: #fff;
  border-radius: var(--radius);
  padding: 24px;
  box-shadow: var(--shadow);
}

.section-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 20px;
}

.section-left {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.section-subtitle {
  font-size: 13px;
  color: var(--text-secondary);
}

.more-link {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--primary);
  text-decoration: none;
  transition: color 0.15s;
}

.more-link:hover {
  color: var(--primary-dark);
}

.section-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.section-skeleton {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
</style>
