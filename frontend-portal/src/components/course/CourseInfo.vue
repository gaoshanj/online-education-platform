<template>
  <div class="course-info">
    <h1 class="info-title">{{ course.courseName }}</h1>
    <p class="info-desc">{{ course.description || '暂无课程简介' }}</p>

    <div class="info-stats">
      <div class="stat-item">
        <el-icon><UserFilled /></el-icon>
        <span>讲师: {{ course.teacherName || '平台讲师' }}</span>
      </div>
      <div class="stat-item">
        <el-icon><TrendCharts /></el-icon>
        <span>{{ course.studentCount || 0 }} 人已报名</span>
      </div>
      <div class="stat-item">
        <el-icon><View /></el-icon>
        <span>{{ course.viewCount || 0 }} 次浏览</span>
      </div>
    </div>

    <div class="info-actions">
      <!-- 报名/学习状态 -->
      <template v-if="!isLoggedIn">
        <el-button type="primary" size="large" class="main-btn" @click="$emit('enroll')">
          报名学习
        </el-button>
      </template>
      <template v-else-if="!course.isEnrolled">
        <el-button
          type="primary"
          size="large"
          class="main-btn"
          :loading="enrollLoading"
          @click="$emit('enroll')"
        >
          报名学习
        </el-button>
      </template>
      <template v-else>
        <el-button type="success" size="large" class="main-btn" disabled plain>
          ✅ 已报名 (进度 {{ course.learningProgress || 0 }}%)
        </el-button>
      </template>

      <!-- 互动按钮 -->
      <div class="action-icons">
        <el-button
          size="large"
          circle
          :type="course.isLiked ? 'danger' : 'default'"
          :plain="course.isLiked"
          @click="$emit('toggle-like')"
          :title="course.isLiked ? '取消点赞' : '点赞课程'"
        >
          <template #icon>
            <el-icon style="font-size: 18px;">
              <Icon v-if="course.isLiked" icon="mdi:heart" color="#FF2442" />
              <Icon v-else icon="mdi:heart-outline" />
            </el-icon>
          </template>
        </el-button>
        <span class="action-count">{{ course.likeCount || 0 }}</span>

        <el-button
          size="large"
          circle
          :type="course.isFavorited ? 'warning' : 'default'"
          :plain="course.isFavorited"
          @click="$emit('toggle-favorite')"
          :title="course.isFavorited ? '取消收藏' : '收藏课程'"
          style="margin-left: 16px;"
        >
          <template #icon>
            <el-icon style="font-size: 18px;"><Star /></el-icon>
          </template>
        </el-button>
        <span class="action-count">{{ course.favoriteCount || 0 }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { UserFilled, TrendCharts, View, Star } from '@element-plus/icons-vue'
import { Icon } from '@iconify/vue'

defineProps({
  course: {
    type: Object,
    required: true
  },
  isLoggedIn: {
    type: Boolean,
    default: false
  },
  enrollLoading: {
    type: Boolean,
    default: false
  }
})

defineEmits(['enroll', 'toggle-like', 'toggle-favorite'])
</script>

<style scoped>
.course-info {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.info-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.3;
  margin-bottom: 12px;
}

.info-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 24px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
}

.info-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  margin-bottom: auto; /* Push actions to the bottom */
  padding-bottom: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
  background: var(--bg-page);
  padding: 6px 12px;
  border-radius: 4px;
}

.stat-item .el-icon {
  font-size: 16px;
  color: var(--primary);
}

.info-actions {
  display: flex;
  align-items: center;
  gap: 24px;
  padding-top: 24px;
  border-top: 1px solid var(--border-color);
}

.main-btn {
  width: 200px;
  font-weight: 600;
}

.action-icons {
  display: flex;
  align-items: center;
  gap: 6px;
}

.action-count {
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: 500;
  min-width: 24px;
}
</style>
