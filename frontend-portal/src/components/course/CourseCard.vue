<template>
  <div class="course-card" @click="goDetail">
    <!-- 封面图 -->
    <div class="card-cover">
      <img :src="course.coverImage || defaultCover" :alt="course.courseName" />
      
      <!-- 右上角悬浮管理操作按钮 (红心/黄星) -->
      <div 
        v-if="isManageMode" 
        class="card-action-btn"
        @click.stop="handleCancelAction"
        :title="manageType === 'favorite' ? '取消收藏' : '取消点赞'"
      >
        <el-icon v-if="manageType === 'favorite'" class="icon-star"><StarFilled /></el-icon>
        <span v-else class="icon-heart">
          <Icon icon="mdi:heart" width="16" height="16" color="#FF2442" />
        </span>
      </div>
    </div>

    <!-- 卡片内容 -->
    <div class="card-body">
      <h3 class="card-title">{{ course.courseName }}</h3>
      <p class="card-desc text-ellipsis-2">{{ course.description || '暂无简介' }}</p>

      <div class="card-meta">
        <span class="meta-teacher">
          <el-icon><UserFilled /></el-icon>
          {{ course.teacherName || '未知讲师' }}
        </span>
        <span class="meta-enroll">
          <el-icon><TrendCharts /></el-icon>
          {{ formatCount(course.studentCount) }}人已报名
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { UserFilled, TrendCharts, StarFilled } from '@element-plus/icons-vue'
import { Icon } from '@iconify/vue'

const props = defineProps({
  course: {
    type: Object,
    required: true
  },
  // 是否开启管理模式 (右上角出现可取消按钮)
  isManageMode: {
    type: Boolean,
    default: false
  },
  // 管理类型：'like' | 'favorite'
  manageType: {
    type: String,
    default: 'favorite'
  }
})

const emit = defineEmits(['cancel-action'])

const router = useRouter()
const defaultCover = 'https://via.placeholder.com/320x180/409eff/ffffff?text=课程封面'

function goDetail() {
  router.push(`/course/${props.course.id}`)
}

function handleCancelAction() {
  emit('cancel-action', props.course)
}

function formatCount(num) {
  if (!num) return '0'
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return String(num)
}
</script>

<style scoped>
.course-card {
  background: #fff;
  border-radius: var(--radius);
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: var(--shadow);
  display: flex;
  flex-direction: column;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.2);
}

.card-cover {
  width: 100%;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  background: var(--primary-light);
  position: relative;
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.course-card:hover .card-cover img {
  transform: scale(1.04);
}

.card-action-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
  cursor: pointer;
  transition: all 0.2s;
  z-index: 2;
}

.card-action-btn:hover {
  transform: scale(1.15);
}

.icon-star {
  color: #FFC300; /* 明亮的金黄色 */
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-heart {
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-body {
  padding: 12px 14px 14px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.card-desc {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.5;
  flex: 1;
}

.card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
  padding-top: 8px;
  border-top: 1px solid var(--border-color);
}

.meta-teacher,
.meta-enroll {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-enroll {
  color: var(--primary);
  font-weight: 500;
}
</style>
