<template>
  <div class="learning-course-card">
    <!-- 左侧封面 -->
    <div class="card-cover">
      <el-image
        :src="course.coverImage"
        fit="cover"
        lazy
        class="cover-image"
      >
        <template #error>
          <div class="image-placeholder">
            <el-icon><Picture /></el-icon>
            <span>暂无图片</span>
          </div>
        </template>
      </el-image>
    </div>

    <!-- 中间信息 -->
    <div class="card-info">
      <h3 class="course-title">{{ course.courseName }}</h3>
      
      <div class="enroll-time">
        <span>报名时间：{{ formatTime(course.enrollTime) }}</span>
      </div>

      <div class="progress-info">
        <div class="progress-row">
          已学习：{{ course.completedChapters || 0 }} / {{ course.totalChapters || 0 }} 节
        </div>
        <div class="progress-row recent-chapter">
          最近学习：{{ course.recentChapterName ? course.recentChapterName : '-' }}
        </div>
      </div>
    </div>

    <!-- 右侧操作 -->
    <div class="card-actions">
      <el-button 
        type="primary" 
        @click="handleContinue"
      >
        {{ hasRecent ? '继续学习' : '开始学习' }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Picture } from '@element-plus/icons-vue'

const props = defineProps({
  course: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['continue'])

const hasRecent = computed(() => {
  return props.course.recentChapterId != null
})

function formatTime(timeStr) {
  if (!timeStr) return '-'
  try {
    const d = new Date(timeStr)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  } catch (e) {
    return timeStr
  }
}

function handleContinue() {
  emit('continue', props.course.courseId)
}
</script>

<style scoped>
.learning-course-card {
  display: flex;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  padding: 0;
  gap: 20px;
  align-items: center;
}

.learning-course-card:hover {
  background: #fdfdfd;
}

/* 左侧封面 */
.card-cover {
  width: 160px;
  height: 90px;
  flex-shrink: 0;
  border-radius: 6px;
  overflow: hidden;
  background: #f5f7fa;
}

.cover-image {
  width: 100%;
  height: 100%;
  transition: transform 0.3s;
}

.learning-course-card:hover .cover-image {
  transform: scale(1.05); /* 悬浮微动效 */
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-placeholder);
  font-size: 12px;
  gap: 4px;
}
.image-placeholder .el-icon {
  font-size: 20px;
}

/* 中侧信息 */
.card-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0; /* 允许截断 */
}

.course-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.enroll-time {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.progress-info {
  font-size: 13px;
  color: var(--text-regular);
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.recent-chapter {
  color: var(--primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 右侧按钮 */
.card-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 120px;
}
</style>
