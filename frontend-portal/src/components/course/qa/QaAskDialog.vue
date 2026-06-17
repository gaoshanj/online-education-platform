<template>
  <el-dialog
    v-model="visible"
    :title="`向课程《${courseName}》提问`"
    width="600px"
    destroy-on-close
    @closed="handleClosed"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="80px"
      label-position="top"
    >
      <el-form-item label="问题标题" prop="title">
        <el-input
          v-model="formData.title"
          placeholder="请输入一句话说明您的问题（最多50字）"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>
      
      <el-form-item label="问题描述" prop="content">
        <el-input
          v-model="formData.content"
          type="textarea"
          :rows="6"
          placeholder="请输入详细的问题描述，帮助老师或同学更好地解答..."
          maxlength="1000"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          提交问题
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { askQuestion } from '@/api/question'
import { ElMessage } from 'element-plus'

const props = defineProps({
  courseId: {
    type: Number,
    required: true
  },
  courseName: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['success'])

const visible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const formData = reactive({
  title: '',
  content: ''
})

const rules = {
  title: [
    { required: true, message: '请输入问题标题', trigger: 'blur' },
    { min: 5, max: 50, message: '长度在 5 到 50 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入详细问题描述', trigger: 'blur' },
    { min: 10, max: 1000, message: '长度在 10 到 1000 个字符', trigger: 'blur' }
  ]
}

// 暴露给父组件调用
function open() {
  visible.value = true
}

// 弹窗完全关闭后清理表单
function handleClosed() {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await askQuestion({
          courseId: props.courseId,
          title: formData.title,
          content: formData.content
        })
        ElMessage.success('问题提交成功！')
        visible.value = false
        emit('success')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

defineExpose({
  open
})
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
