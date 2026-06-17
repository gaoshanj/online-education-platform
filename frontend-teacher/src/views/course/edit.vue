<template>
  <div class="course-edit-container">
    <el-card class="admin-card">
      <el-steps :active="activeStep" finish-status="success" align-center class="steps">
        <el-step title="基本信息" icon="Edit" />
        <el-step title="章节管理" icon="Operation" />
      </el-steps>

      <!-- 第一步：基本信息 -->
      <div v-show="activeStep === 0" class="step-content">
        <el-form :model="courseForm" :rules="courseRules" ref="courseRef" label-width="100px" style="max-width: 800px; margin: 0 auto">
          <el-form-item label="课程名称" prop="courseName">
            <el-input v-model="courseForm.courseName" placeholder="如：Vue 3 从实战到项目落地" :disabled="isReadOnly" />
          </el-form-item>
          
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="课程分类" prop="categoryId">
                <el-tree-select 
                  v-model="courseForm.categoryId" 
                  :data="categoryTree" 
                  :props="{ label: 'categoryName', value: 'id' }" 
                  placeholder="选择课程分类" 
                  style="width: 100%" 
                  :disabled="isReadOnly"
                  node-key="id" 
                  check-strictly
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="封面图" prop="coverImage">
            <el-upload
              class="cover-uploader"
              action="#"
              :show-file-list="false"
              :http-request="handleCoverUpload"
              :disabled="isReadOnly"
              accept="image/*"
            >
              <img v-if="courseForm.coverImage" :src="courseForm.coverImage" class="cover-image" />
              <el-icon v-else class="uploader-icon"><Plus /></el-icon>
            </el-upload>
          </el-form-item>

          <el-form-item label="课程简介" prop="description">
            <el-input 
              v-model="courseForm.description" 
              type="textarea" 
              rows="5" 
              placeholder="请详细描述课程内容及受众" 
              :disabled="isReadOnly"
            />
          </el-form-item>

          <div class="footer-actions flex-center">
            <el-button @click="$router.push('/course/list')">返回列表</el-button>
            <el-button type="primary" :loading="submitLoading" @click="handleSave" :disabled="isReadOnly">保存并下一步</el-button>
            <el-button type="success" v-if="courseForm.id" @click="activeStep = 1">直接管理章节</el-button>
          </div>
        </el-form>
      </div>

      <!-- 第二步：章节管理 -->
      <div v-show="activeStep === 1" class="step-content">
        <ChapterManager :course-id="courseForm.id" :disabled="isReadOnly" v-if="courseForm.id" />
        
        <div class="footer-actions flex-center">
          <el-button @click="activeStep = 0">上一步（课程信息）</el-button>
          <el-button type="primary" @click="$router.push('/course/list')">完成并返回清单</el-button>
          <el-button 
            type="success" 
            v-if="[0, 3, 4].includes(courseForm.status)" 
            @click="handleSubmitAudit"
          >提交审核</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourseById, saveCourse, updateCourse, submitAudit } from '@/api/course'
import { getCategoryTree } from '@/api/category'
import { uploadImage } from '@/api/common'
import { useUserStore } from '@/store/user'
import ChapterManager from './components/ChapterManager.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const activeStep = ref(0)
const submitLoading = ref(false)
const courseRef = ref(null)
const categoryTree = ref([])

const courseForm = reactive({
  id: undefined,
  courseName: '',
  teacherId: userStore.userInfo?.id || undefined, 
  categoryId: undefined,
  coverImage: '',
  description: '',
  status: 0 // 默认为草稿
})

const courseRules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  coverImage: [{ required: true, message: '请上传封面图', trigger: 'change' }],
  description: [{ required: true, message: '请输入课程简介', trigger: 'blur' }]
}

// 权限判定：状态为 1(待审核) 或 2(已发布) 时只读
const isReadOnly = computed(() => [1, 2].includes(courseForm.status))

const loadCourse = async () => {
  const id = route.params.id
  if (id) {
    try {
      const res = await getCourseById(id)
      Object.assign(courseForm, res)
    } catch (e) {}
  }
}

const loadCategoryTree = async () => {
  try {
    const res = await getCategoryTree()
    categoryTree.value = res.data || res
  } catch (e) {}
}

const handleCoverUpload = async (options) => {
  try {
    const res = await uploadImage(options.file)
    courseForm.coverImage = res.data || res
    ElMessage.success('上传成功')
  } catch (e) {
    ElMessage.error('上传失败')
  }
}

const handleSave = async () => {
  if (!courseRef.value) return
  await courseRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        // 保证 teacherId 始终为当前用户
        if (!courseForm.teacherId) {
          courseForm.teacherId = userStore.userInfo?.id
        }
        
        if (courseForm.id) {
          await updateCourse(courseForm)
        } else {
          const res = await saveCourse(courseForm)
          // 新建模式，后端返回的是 long 类型的 id
          courseForm.id = typeof res === 'object' ? res.id || res.data : res
        }
        ElMessage.success('课程基本信息已保存')
        activeStep.value = 1
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleSubmitAudit = async () => {
  try {
    await ElMessageBox.confirm('确定提交审核吗？', '提示', { type: 'warning' })
    await submitAudit(courseForm.id)
    ElMessage.success('提交成功')
    router.push('/course/list')
  } catch (e) {}
}

onMounted(() => {
  loadCategoryTree()
  loadCourse()
})
</script>

<style scoped>
.steps {
  margin-bottom: 40px;
}
.step-content {
  padding-bottom: 20px;
}
.footer-actions {
  margin-top: 40px;
  gap: 20px;
}
.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: .2s;
}
.cover-uploader:hover {
  border-color: #409EFF;
}
.uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}
.cover-image {
  width: 178px;
  height: 100px;
  display: block;
  object-fit: cover;
}
</style>
