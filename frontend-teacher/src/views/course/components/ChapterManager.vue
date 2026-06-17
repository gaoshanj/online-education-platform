<template>
  <div class="chapter-manager">
    <div class="header">
      <span class="tip">温馨提示：先通过基本信息保存生成课程，即可在此管理章节和小节。</span>
      <el-button type="primary" size="small" icon="Plus" @click="handleAddChapter" :disabled="disabled">添加章节</el-button>
    </div>

    <el-empty v-if="!chapters.length" description="暂无章节内容，请点击右上角添加" />

    <div class="chapter-tree-box" v-else>
      <el-tree
        :data="chapters"
        :props="{ label: 'chapterName', children: 'children' }"
        default-expand-all
      >
        <template #default="{ node, data }">
          <div class="custom-tree-node">
            <template v-if="data.parentId === 0">
              <div class="info">
                <el-icon class="folder-icon"><FolderOpened /></el-icon>
                <span class="name">{{ node.label }}</span>
              </div>
              <div class="actions" @click.stop>
                <el-button link type="primary" icon="Plus" size="small" @click="handleAddSection(data)" :disabled="disabled">添加小节</el-button>
                <el-button link type="primary" icon="Edit" size="small" @click="handleEditChapter(data)" :disabled="disabled">编辑</el-button>
                <el-button link type="danger" icon="Delete" size="small" @click="handleDelete(data)" :disabled="disabled">删除</el-button>
              </div>
            </template>
            <template v-else>
              <div class="info">
                <el-icon class="video-icon"><VideoPlay /></el-icon>
                <span class="name">{{ node.label }}</span>
              </div>
              <div class="actions" @click.stop>
                <span class="video-duration" v-if="data.videoDuration" style="margin-right: 15px; color: #909399; font-size: 13px;">时长: {{ formatDuration(data.videoDuration) }}</span>
                <el-button v-if="data.videoUrl" type="primary" link size="small" @click="handlePreviewSection(data)" style="margin-right: 15px;">
                  <el-icon><VideoPlay /></el-icon>预览播放
                </el-button>
                <el-button link type="primary" icon="Edit" size="small" @click="handleEditSection(data, node.parent.data)" :disabled="disabled">编辑</el-button>
                <el-button link type="danger" icon="Delete" size="small" @click="handleDelete(data)" :disabled="disabled">删除</el-button>
              </div>
            </template>
          </div>
        </template>
      </el-tree>
    </div>

    <!-- 章节/小节 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      destroy-on-close
    >
      <el-form :model="form" label-width="80px" ref="formRef" :rules="rules">
        <el-form-item label="名称" prop="chapterName">
          <el-input v-model="form.chapterName" placeholder="如：第一章：快速入门" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        
        <!-- 小节特有字段 -->
        <template v-if="form.parentId !== 0">
          <el-form-item label="小节视频" prop="videoUrl">
            <el-upload
              class="video-uploader"
              action="#"
              :show-file-list="false"
              :auto-upload="true"
              :http-request="handleVideoUpload"
              :before-upload="beforeVideoUpload"
              :disabled="disabled || uploadLoading"
              accept="video/*"
            >
              <el-button :loading="uploadLoading">
                {{ uploadLoading ? '上传中...' : (form.videoUrl ? '重新上传视频' : '选择并上传视频') }}
              </el-button>
            </el-upload>
            <div v-if="form.videoUrl" class="video-url-preview">
              {{ form.videoUrl }}
            </div>
          </el-form-item>
          <el-form-item label="视频时长" prop="videoDuration">
            <el-input-number v-model="form.videoDuration" :min="0" disabled />
            <span class="unit">（秒，自动读取）</span>
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="previewVisible"
      :title="'预览 - ' + previewTitle"
      width="800px"
      destroy-on-close
      @close="handlePreviewClose"
    >
      <video v-if="previewUrl" :src="previewUrl" controls autoplay class="preview-video"></video>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getChapterTree, saveOrUpdateChapter, deleteChapter } from '@/api/chapter'
import { uploadVideo } from '@/api/common'

const props = defineProps({
  courseId: [String, Number],
  disabled: Boolean
})

const chapters = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const uploadLoading = ref(false)
const formRef = ref(null)

const previewVisible = ref(false)
const previewTitle = ref('')
const previewUrl = ref('')

const form = reactive({
  id: undefined,
  courseId: undefined,
  chapterName: '',
  parentId: 0,
  sortOrder: 0,
  videoUrl: '',
  videoDuration: 0
})

const rules = {
  chapterName: [{ required: true, message: '请输入名称', trigger: 'blur' }]
}

const loadChapters = async () => {
  if (!props.courseId) return
  try {
    chapters.value = await getChapterTree(props.courseId)
  } catch (error) {}
}

const resetForm = (parentId = 0) => {
  Object.assign(form, {
    id: undefined,
    courseId: props.courseId,
    chapterName: '',
    parentId: parentId,
    sortOrder: 0,
    videoUrl: '',
    videoDuration: 0
  })
}

const handleAddChapter = () => {
  dialogTitle.value = '添加章节'
  resetForm(0)
  dialogVisible.value = true
}

const handleAddSection = (chapter) => {
  dialogTitle.value = `添加小节 - ${chapter.chapterName}`
  resetForm(chapter.id)
  dialogVisible.value = true
}

const handleEditChapter = (chapter) => {
  dialogTitle.value = '编辑章节'
  Object.assign(form, chapter)
  dialogVisible.value = true
}

const handleEditSection = (section, chapter) => {
  dialogTitle.value = '编辑小节'
  Object.assign(form, section)
  dialogVisible.value = true
}

// 视频上传前的钩子：用于解析时长
const beforeVideoUpload = (file) => {
  return new Promise((resolve) => {
    const videoUrl = URL.createObjectURL(file)
    const audioElement = new Audio(videoUrl)
    audioElement.addEventListener('loadedmetadata', () => {
      // 获取秒数，向下取整
      form.videoDuration = Math.floor(audioElement.duration || 0)
      URL.revokeObjectURL(videoUrl)
      resolve(true)
    })
    audioElement.addEventListener('error', () => {
      URL.revokeObjectURL(videoUrl)
      // 解析失败不影响上传本身
      resolve(true)
    })
  })
}

// 视频上传的自定义请求
const handleVideoUpload = async (options) => {
  try {
    uploadLoading.value = true
    const res = await uploadVideo(options.file)
    form.videoUrl = res.data || res
    ElMessage.success('视频上传成功')
  } catch (e) {
    ElMessage.error('视频上传失败，请重试')
  } finally {
    uploadLoading.value = false
  }
}

const handlePreviewSection = (section) => {
  previewTitle.value = section.chapterName
  previewUrl.value = section.videoUrl
  previewVisible.value = true
}

const handlePreviewClose = () => {
  previewUrl.value = ''
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await saveOrUpdateChapter(form)
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadChapters()
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDelete = async (item) => {
  try {
    await ElMessageBox.confirm(`确定删除"${item.chapterName}"吗？`, '警告', { type: 'error' })
    await deleteChapter(item.id)
    ElMessage.success('删除成功')
    loadChapters()
  } catch (e) {}
}

const formatDuration = (seconds) => {
  if (!seconds) return '0秒'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  
  let res = ''
  if (h > 0) res += `${h}小时`
  if (m > 0) res += `${m}分`
  if (s > 0 || res === '') res += `${s}秒`
  return res
}

onMounted(() => {
  loadChapters()
})
</script>

<style scoped>
.chapter-manager {
  padding: 10px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: #fdf6ec;
  padding: 10px 15px;
  border-radius: 4px;
}
.tip {
  color: #e6a23c;
  font-size: 13px;
}
.chapter-tree-box {
  background: #f8fbff;
  padding: 16px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
  min-height: 100px;
}
:deep(.el-tree-node__content) {
  height: 40px;
}
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 15px;
  padding-right: 8px;
}
.custom-tree-node .info {
  display: flex;
  align-items: center;
}
.custom-tree-node .name {
  font-weight: bold;
}
.custom-tree-node .actions {
  display: flex;
  align-items: center;
}
.folder-icon {
  margin-right: 8px;
  color: #409EFF;
}
.video-icon {
  margin-right: 8px;
  color: #67c23a;
}
.free-tag {
  margin-left: 10px;
}
.unit {
  margin-left: 10px;
  color: #909399;
}
.video-url-preview {
  margin-top: 5px;
  font-size: 12px;
  color: #409EFF;
  word-break: break-all;
  line-height: 1.2;
}
.preview-video {
  width: 100%;
  max-height: 500px;
  background: #000;
}
</style>
