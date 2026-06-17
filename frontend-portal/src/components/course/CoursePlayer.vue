<template>
  <div class="course-player">
    <video
      v-if="section && section.videoUrl"
      :src="section.videoUrl"
      controls
      class="video-el"
      @pause="$emit('pause', $event)"
      @play="$emit('play', $event)"
      @timeupdate="onTimeUpdate"
      @loadedmetadata="onLoadedMetadata"
      ref="videoRef"
    ></video>
    <div v-else class="player-placeholder">
      <el-empty
        :description="section ? '当前小节暂无视频资源' : '请在目录中选择小节播放'"
        :image-size="80"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  section: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['pause', 'play', 'timeupdate'])
const videoRef = ref(null)
const localMaxPosition = ref(0)

function onTimeUpdate(e) {
  const current = Math.floor(e.target.currentTime)
  if (current > localMaxPosition.value) {
    localMaxPosition.value = current
  }
  emit('timeupdate', {
    currentTime: current,
    duration: Math.floor(e.target.duration || 0),
    maxPosition: localMaxPosition.value
  })
}

function onLoadedMetadata(e) {
  // 断点续播逻辑：如果有上一次的进度指针则自动跳到该指针
  if (props.section && props.section.lastPosition > 0) {
    e.target.currentTime = props.section.lastPosition
    ElMessage.success(`已为您自动跳转至上次观看位置`)
  }
}

// 切换小节时自动暂停新视频，等待用户手动点击播放（防止浏览器自动播放策略拦截）
watch(() => props.section, (newVal) => {
  if (videoRef.value) {
    videoRef.value.pause()
  }
  if (newVal) {
    localMaxPosition.value = newVal.maxPosition || 0
  }
}, { immediate: true })
</script>

<style scoped>
.course-player {
  width: 100%;
  aspect-ratio: 16 / 9;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.video-el {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.player-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #1c1f24;
}

:deep(.player-placeholder .el-empty__description) {
  color: #909399;
}
</style>
