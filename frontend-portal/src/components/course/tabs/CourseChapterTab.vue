<template>
  <div class="course-chapter-tab">
    <div v-if="loading" class="loading-wrap">
      <el-skeleton :rows="4" animated />
    </div>
    <template v-else-if="chapters.length">
      <div v-for="chapter in chapters" :key="chapter.id" class="chapter-group">
        <div class="chapter-parent">
          <el-icon class="folder-icon"><FolderOpened /></el-icon>
          <span class="parent-title">{{ chapter.chapterName }}</span>
        </div>
        
        <div
          v-for="child in chapter.children"
          :key="child.id"
          class="chapter-child"
          :class="{ 
            active: currentSectionId === child.id,
            locked: child.isLocked
          }"
          @click="handleSelect(child)"
        >
          <el-icon class="play-icon">
            <VideoPlay v-if="!child.isLocked" />
            <Lock v-else />
          </el-icon>
          
          <span class="child-title">{{ child.chapterName }}</span>
          
          <el-tag
            v-if="!child.isLocked && child.globalIndex < 3 && !isEnrolled"
            size="small"
            type="warning"
            effect="light"
            class="tag-try"
          >支持试看</el-tag>

          <span class="child-duration">{{ formatDuration(child.videoDuration) }}</span>
          
          <el-tag
            v-if="child.isCompleted === 1"
            size="small"
            type="success"
            effect="light"
          >已完结</el-tag>
          <span 
            v-else-if="child.progressPercent > 0" 
            class="child-progress"
          >
            已学习 {{ child.progressPercent }}%
          </span>
        </div>
      </div>
    </template>
    <el-empty v-else description="暂无章节内容" />
  </div>
</template>

<script setup>
import { FolderOpened, VideoPlay, Lock } from '@element-plus/icons-vue'

const props = defineProps({
  chapters: {
    type: Array,
    required: true
  },
  currentSectionId: {
    type: [Number, String],
    default: null
  },
  isEnrolled: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['select-section'])

function handleSelect(child) {
  if (child.isLocked) {
    // 抛弃 locked 事件由父组件给提示
    emit('select-section', { ...child, $locked: true })
    return
  }
  emit('select-section', child)
}

function formatDuration(seconds) {
  if (!seconds) return '--'
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}
</script>

<style scoped>
.course-chapter-tab {
  padding: 8px 0;
  min-height: 300px;
}

.chapter-group {
  margin-bottom: 12px;
}

.chapter-parent {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: var(--bg-page);
  border-radius: 6px;
  font-weight: 700;
  font-size: 15px;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.folder-icon {
  font-size: 18px;
  color: var(--text-secondary);
}

.chapter-child {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px 12px 42px;
  font-size: 14px;
  color: var(--text-regular);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 2px;
}

.chapter-child:hover:not(.locked) {
  background: #f5f7fa;
  color: var(--primary);
}

.chapter-child.active {
  background: var(--primary-light);
  color: var(--primary);
  font-weight: 600;
}

.chapter-child.locked {
  color: var(--text-placeholder);
  cursor: not-allowed;
  background: #fafafa;
}

.play-icon {
  font-size: 18px;
}

.child-title {
  flex: 1;
}

.child-duration {
  color: var(--text-secondary);
  font-size: 13px;
  margin-left: 12px;
  margin-right: 12px;
  font-family: monospace;
}

.child-progress {
  color: #e6a23c;
  font-size: 12px;
  margin-left: 8px;
}

.tag-try {
  margin-left: 8px;
}
</style>
