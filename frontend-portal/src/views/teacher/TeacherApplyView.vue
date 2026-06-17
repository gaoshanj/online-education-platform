<template>
  <div class="teacher-apply-page">
    <div class="page-container">

      <!-- 页面加载中 -->
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="10" animated />
      </div>

      <template v-else>
        <!-- 页面标题面包屑 -->
        <section class="page-header" style="margin-bottom: 20px;">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/teacher-apply' }">讲师入驻</el-breadcrumb-item>
            <el-breadcrumb-item>认证信息</el-breadcrumb-item>
          </el-breadcrumb>
        </section>

        <!-- 第二部分：状态提示卡片区域 (根据 status 渲染不同类型卡片) -->
        <section class="status-alert-section" v-if="applyStatus !== null">
          <!-- 待审核 (0) -->
          <el-alert v-if="applyStatus === 0" title="申请审核中" type="info" show-icon :closable="false">
            <p class="alert-desc">您的讲师申请已经提交，正在等待管理员审核，请耐心等待。</p>
          </el-alert>

          <!-- 已通过 (1) -->
          <el-alert v-if="applyStatus === 1" title="恭喜您已成为讲师" type="success" show-icon :closable="false">
            <p class="alert-desc">您现在可以进入讲师后台创建课程分享您的知识。</p>
            <el-button type="success" size="small" class="alert-action-btn" @click="goToTeacherAdmin">
              进入讲师后台
            </el-button>
          </el-alert>

          <!-- 已拒绝 (2) -->
          <el-alert v-if="applyStatus === 2" title="讲师申请未通过" type="error" show-icon :closable="false">
            <p class="alert-desc">您的讲师申请未通过审核，请根据拒绝原因修改后重新提交。</p>
            <p class="reject-reason" v-if="formData.rejectReason">
              <strong>拒绝原因：</strong>{{ formData.rejectReason }}
            </p>
          </el-alert>

          <!-- 修改待审 (3) -->
          <el-alert v-if="applyStatus === 3" title="认证信息审核中" type="warning" show-icon :closable="false">
            <p class="alert-desc">您提交的认证信息修改正在审核，审核期间不影响您的讲师权限。</p>
            <el-button type="warning" size="small" class="alert-action-btn" @click="goToTeacherAdmin">
              进入讲师后台
            </el-button>
          </el-alert>

          <!-- 已取消资格 (4) -->
          <el-alert v-if="applyStatus === 4" title="讲师资格已取消" type="warning" show-icon :closable="false">
            <p class="alert-desc">您的讲师资格已被管理员取消，如需继续成为讲师，请重新提交申请。</p>
          </el-alert>
        </section>

        <!-- 第三部分：申请信息表单区域 -->
        <section class="form-section">
          <div class="form-header">
            <h2>{{ isReadOnly ? '认证信息' : '填写申请资料' }}</h2>
          </div>

          <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" :disabled="isReadOnly"
            class="apply-form">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="formData.realName" placeholder="请输入您的真实姓名" />
            </el-form-item>

            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="formData.idCard" placeholder="请输入18位有效身份证号" maxlength="18" />
            </el-form-item>

            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入手机号码" maxlength="11" />
            </el-form-item>

            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入您的邮箱 (可选)" />
            </el-form-item>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="最高学历" prop="education">
                  <el-select v-model="formData.education" placeholder="请选择学历" style="width: 100%">
                    <el-option label="大专" value="大专" />
                    <el-option label="本科" value="本科" />
                    <el-option label="硕士" value="硕士" />
                    <el-option label="博士及以上" value="博士及以上" />
                    <el-option label="其他" value="其他" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="专业方向" prop="major">
                  <el-input v-model="formData.major" placeholder="例如：计算机科学与技术" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="教学经验" prop="teachingExperience">
              <el-input v-model="formData.teachingExperience" type="textarea" :rows="4"
                placeholder="请简要描述您的授课经历及擅长领域 (可选)" maxlength="500" show-word-limit />
            </el-form-item>

            <!-- 资质证书上传 -->
            <el-form-item label="资质证书" prop="certificateUrls">
              <div class="upload-wrapper">
                <el-upload action="/api/common/file/upload/image" :headers="uploadHeaders" list-type="picture-card"
                  :file-list="certificateFileList" :on-success="handleUploadSuccess" :on-error="handleUploadError"
                  :on-remove="handleRemoveFile" :on-preview="handlePreview" :before-upload="beforeUpload" :limit="5"
                  :on-exceed="handleExceed" :disabled="isReadOnly" accept="image/jpeg,image/png,image/jpg">
                  <el-icon>
                    <Plus />
                  </el-icon>
                  <template #tip v-if="!isReadOnly">
                    <div class="el-upload__tip">
                      最多上传5张图片（单张不超过5MB），支持 jpg/png 格式，可上传教师资格证、学历证等。
                    </div>
                  </template>
                </el-upload>
              </div>
            </el-form-item>

            <!-- 底部操作按钮区域 -->
            <div class="form-actions" v-if="!isReadOnly">
              <!-- 未申请情况 -->
              <el-button v-if="applyStatus === null" type="primary" size="large" @click="handleSubmitForm"
                :loading="submitLoading" class="submit-btn">
                提交申请
              </el-button>

              <!-- 已拒绝 或 已取消资格 的重新提交情况 -->
              <el-button v-if="applyStatus === 2" type="primary" size="large" @click="handleUpdateForm"
                :loading="submitLoading" class="submit-btn">
                重新提交申请
              </el-button>

              <el-button v-if="applyStatus === 4" type="primary" size="large" @click="handleUpdateForm"
                :loading="submitLoading" class="submit-btn">
                重新申请
              </el-button>
            </div>
          </el-form>
        </section>

      </template>
    </div>

    <!-- 图片预览弹窗 -->
    <el-dialog v-model="previewDialogVisible" title="图片预览" width="50%">
      <img w-full :src="previewImageUrl" alt="Preview Image" style="width: 100%; border-radius: 4px;" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMyApplyStatus, submitApply, updateApply } from '@/api/teacher-apply'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const submitLoading = ref(false)
const applyStatus = ref(null) // null: 未申请, 0: 待审, 1: 通过, 2: 拒绝, 3: 修改待审, 4: 取消资格
const isReadOnly = computed(() => [0, 1, 3].includes(applyStatus.value))

const formRef = ref(null)

// 申请表单数据
const formData = reactive({
  id: null,
  realName: '',
  idCard: '',
  phone: '',
  email: '',
  education: '',
  major: '',
  teachingExperience: '',
  certificateUrls: '',
  rejectReason: ''
})

// 表单校验规则
const rules = {
  realName: [
    { required: true, message: '请输入您的姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入有效的身份证号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ],
  education: [
    { required: true, message: '请选择最高学历', trigger: 'change' }
  ]
}

// ---------------- 上传区域逻辑 ----------------
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

// 与上传组件绑定的文件列表
const certificateFileList = ref([])

// 初始化展示服务端返回的 certificateUrls
const initFileList = (urlsStr) => {
  if (!urlsStr) {
    certificateFileList.value = []
    return
  }
  const urls = urlsStr.split(',').filter(u => u)
  certificateFileList.value = urls.map((url, index) => ({
    name: `cert-${index}`,
    url: url
  }))
}

const beforeUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg'
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) ElMessage.error('上传凭证图片只能是 JPG/PNG 格式!')
  if (!isLt5M) ElMessage.error('上传凭证图片大小不能超过 5MB!')

  return isImage && isLt5M
}

const handleUploadSuccess = (res, uploadFile, uploadFiles) => {
  if (res.code === 200 || res.code === 0) { // 根据实际全局响应 code 判别
    ElMessage.success('上传成功')
    // 重新构建表单字段的 urls 字符串
    syncCertificateUrls(uploadFiles)
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const handleUploadError = (err) => {
  console.error(err)
  ElMessage.error('网络或服务器错误，上传失败')
}

const handleRemoveFile = (uploadFile, uploadFiles) => {
  syncCertificateUrls(uploadFiles)
}

const syncCertificateUrls = (uploadFiles) => {
  // 提取所有上传成功（带有response.data或者本来就有url）的链接并拼装
  const urls = uploadFiles.map(f => {
    if (f.response && (f.response.code === 200 || f.response.code === 0)) {
      return f.response.data
    }
    return f.url
  }).filter(u => u)

  formData.certificateUrls = urls.join(',')
  certificateFileList.value = uploadFiles
}

const handleExceed = () => {
  ElMessage.warning('最多只能上传 5 张图片！')
}

// 图片预览
const previewDialogVisible = ref(false)
const previewImageUrl = ref('')
const handlePreview = (uploadFile) => {
  previewImageUrl.value = uploadFile.url || uploadFile.response?.data
  previewDialogVisible.value = true
}

// ---------------- 数据加载与提交逻辑 ----------------

const fetchData = async () => {
  loading.value = true
  try {
    const { data } = await getMyApplyStatus()
    if (data) {
      applyStatus.value = data.status
      // 回填表单数据
      formData.id = data.id
      formData.realName = data.realName || ''
      formData.idCard = data.idCard || ''
      formData.phone = data.phone || ''
      formData.email = data.email || ''
      formData.education = data.education || ''
      formData.major = data.major || ''
      formData.teachingExperience = data.teachingExperience || ''
      formData.rejectReason = data.rejectReason || ''
      formData.certificateUrls = data.certificateUrls || ''

      initFileList(formData.certificateUrls)
    } else {
      // 没有任何申请记录
      applyStatus.value = null
    }
  } catch (error) {
    console.error('获取讲师申请状态失败:', error)
  } finally {
    loading.value = false
  }
}

const emitSubmitForm = async (apiMethod) => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请将必填信息填写完整正确')
      return
    }

    try {
      await ElMessageBox.confirm('确定提交讲师入驻申请吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      submitLoading.value = true
      // 组装最终 DTO
      const payload = {
        realName: formData.realName,
        idCard: formData.idCard,
        phone: formData.phone,
        email: formData.email,
        education: formData.education,
        major: formData.major,
        teachingExperience: formData.teachingExperience,
        certificateUrls: formData.certificateUrls
      }

      await apiMethod(payload)
      ElMessage.success('申请提交成功！请等待管理员审核。')

      // 刷新数据展示新状态
      fetchData()

    } catch (err) {
      if (err !== 'cancel') {
        console.error('提交失败:', err)
      }
    } finally {
      submitLoading.value = false
    }
  })
}

// 首次提交
const handleSubmitForm = () => {
  emitSubmitForm(submitApply)
}

// 重新修改提交
const handleUpdateForm = () => {
  emitSubmitForm(updateApply)
}

const goToTeacherAdmin = () => {
  // 根据需求跳转到 localhost:3000/login (教师后台可能是独立的系统)
  window.location.href = 'http://localhost:3000/login'
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.teacher-apply-page {
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
  padding: 40px 0;
}

.page-container {
  max-width: 900px;
  margin: 0 auto;
}

/* 状态提示卡区域 */
.status-alert-section {
  margin-bottom: 24px;
}

.status-alert-section :deep(.el-alert) {
  padding: 16px;
  border-radius: 8px;
}

.status-alert-section :deep(.el-alert__title) {
  font-size: 16px;
  font-weight: bold;
}

.alert-desc {
  margin-top: 8px;
  font-size: 14px;
  line-height: 1.5;
}

.reject-reason {
  margin-top: 12px;
  color: #f56c6c;
  background-color: #fef0f0;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 13px;
}

.alert-action-btn {
  margin-top: 16px;
}

/* 表单区域样式 */
.form-section {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 32px 40px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.form-header {
  margin-bottom: 30px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 16px;
}

.form-header h2 {
  font-size: 20px;
  color: #303133;
}

.apply-form {
  max-width: 700px;
  margin: 0 auto;
}

.upload-wrapper {
  width: 100%;
}

.form-actions {
  margin-top: 40px;
  text-align: center;
}

.submit-btn {
  width: 200px;
  font-size: 16px;
}
</style>
