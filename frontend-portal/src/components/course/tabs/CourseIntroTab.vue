<template>
  <div class="course-intro-tab">
    <div v-if="description" class="intro-content">
      <!-- 假设后期可能会有富文本，现在先做段落渲染 -->
      <p class="intro-paragraph">{{ description }}</p>
    </div>
    <el-empty v-else description="暂无课程详情介绍" />
  </div>
</template>

<script setup>
defineProps({
  description: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.course-intro-tab {
  padding: 16px 0;
  min-height: 300px;
}

.intro-content {
  font-size: 15px;
  line-height: 1.8;
  color: var(--text-primary);
}

.intro-paragraph {
  white-space: pre-wrap;
}
</style>
