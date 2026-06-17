<template>
  <div class="my-settings-panel">
    <el-card class="admin-card">
      <el-tabs v-model="activeTab" class="settings-tabsHorizontal">
        <!-- ============================================== -->
        <!-- Tab: 基本信息 -->
        <!-- ============================================== -->
        <el-tab-pane label="基本信息" name="info">
          <div class="tab-pane-content">
            <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="120px" class="settings-form"
              label-position="left">
              <!-- 头像区域采用 el-upload -->
              <el-form-item label="用户头像" prop="avatar" class="avatar-form-item">
                <el-upload class="avatar-uploader-custom" action="#" :show-file-list="false"
                  :before-upload="beforeAvatarUpload" :http-request="handleAvatarUpload">
                  <div class="avatar-preview-box" v-loading="uploadAvatarLoading" element-loading-text="上传中...">
                    <img v-if="infoForm.avatar" :src="infoForm.avatar" class="avatar-image" />
                    <el-icon v-else class="avatar-uploader-icon">
                      <Plus />
                    </el-icon>

                    <div class="avatar-hover-mask">
                      <el-icon>
                        <Camera />
                      </el-icon>
                      <span class="mask-text">修改头像</span>
                    </div>
                  </div>
                </el-upload>
              </el-form-item>

              <el-form-item label="用户名">
                <div class="input-with-hint">
                  <el-input v-model="originalUsername" disabled />
                  <span class="hint-text text-danger"></span>
                </div>
              </el-form-item>

              <el-form-item label="用户昵称" prop="nickname">
                <el-input v-model="infoForm.nickname" placeholder="请输入您的昵称" clearable />
              </el-form-item>

              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="infoForm.gender">
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="2">女</el-radio>
                  <el-radio :label="0">保密</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="当前绑定邮箱">
                <div class="input-with-hint">
                  <el-input v-model="originalEmail" disabled />
                  <span class="hint-text">安全限制：此处邮箱只能查看不可修改。需要修改请移步【邮箱更改】栏目。</span>
                </div>
              </el-form-item>

              <el-form-item>
                <el-button type="primary" :loading="infoLoading" @click="handleUpdateInfo">
                  保存基本信息
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- ============================================== -->
        <!-- Tab: 邮箱更改 -->
        <!-- ============================================== -->
        <el-tab-pane label="邮箱更改" name="email">
          <div class="tab-pane-content">
            <el-form ref="emailFormRef" :model="emailForm" :rules="emailRules" label-width="120px" class="settings-form"
              label-position="left">
              <!-- 友好提示 -->
              <el-alert title="修改邮箱后，旧邮箱将被取消注册关联，您之后需要使用新邮箱进行找回密码等操作。新邮箱不可与其他人重复。" type="warning" show-icon
                :closable="false" style="margin-bottom: 24px;" />

              <el-form-item label="新邮箱地址" prop="email">
                <el-input v-model="emailForm.email" placeholder="请输入你要更换的新邮箱" clearable />
              </el-form-item>

              <el-form-item label="验证码" prop="code">
                <div class="code-input-wrap">
                  <el-input v-model="emailForm.code" placeholder="请输入收到的验证码" clearable />
                  <el-button type="primary" plain :disabled="emailCountdown > 0" @click="handleSendEmailCode"
                    :loading="sendingEmailCode">
                    {{ emailCountdown > 0 ? `${emailCountdown}s 后重发` : '获取验证码' }}
                  </el-button>
                </div>
              </el-form-item>

              <el-form-item>
                <el-button type="primary" :loading="emailLoading" @click="handleUpdateEmail">
                  确认更换邮箱
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- ============================================== -->
        <!-- Tab: 密码更改 -->
        <!-- ============================================== -->
        <el-tab-pane label="密码更改" name="password">
          <div class="tab-pane-content">
            <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="120px"
              class="settings-form" label-position="left">
              <el-form-item label="原密码" prop="oldPassword">
                <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入您正在使用的当前密码" show-password
                  clearable />
              </el-form-item>

              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码 (6-20位字符)"
                  show-password clearable />
              </el-form-item>

              <el-form-item label="确认新密码" prop="confirmPassword">
                <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次确认新密码" show-password
                  clearable />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" :loading="passwordLoading" @click="handleUpdatePassword">
                  修改密码
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- ============================================== -->
        <!-- Tab: 认证信息 -->
        <!-- ============================================== -->
        <el-tab-pane label="认证资质" name="certification">
          <div class="tab-pane-content" style="max-width: 800px">
            <div style="margin-bottom: 24px">
              <el-alert v-if="certStatus === 0" title="资质审核中，请耐心等待管理员处理" type="info" show-icon :closable="false" />
              <el-alert v-else-if="certStatus === 1" title="审核通过，如需变更资料可以重新提交并在此期间继续行使讲师权限。" type="success" show-icon
                :closable="false" />
              <el-alert v-else-if="certStatus === 2" :title="'审核被拒绝。原因：' + certRejectReason" type="error" show-icon
                :closable="false" />
              <el-alert v-else-if="certStatus === 3" title="资料修改审核中，请耐心等待管理员处理。" type="warning" show-icon
                :closable="false" />
              <el-alert v-else-if="certStatus === 4" title="您的讲师资格已被取消！如有疑问请联系管理员。" type="error" show-icon
                :closable="false" />
              <el-alert v-else title="暂无任何申请记录" type="info" show-icon :closable="false" />
            </div>

            <el-form ref="certFormRef" :model="certForm" :rules="certRules" label-width="120px" class="settings-form"
              label-position="left" v-loading="loadingCert">
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="certForm.realName" placeholder="请输入真实姓名" :disabled="isCertLocked" clearable />
              </el-form-item>

              <el-form-item label="身份证号" prop="idCard">
                <el-input v-model="certForm.idCard" placeholder="请输入18位身份证号码" :disabled="isCertLocked" clearable />
              </el-form-item>

              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="certForm.phone" placeholder="请输入联系电话" :disabled="isCertLocked" clearable />
              </el-form-item>

              <el-form-item label="讲师邮箱" prop="email">
                <el-input v-model="certForm.email" placeholder="请输入作为讲师对外联系的邮箱" :disabled="isCertLocked" clearable />
              </el-form-item>

              <el-form-item label="最高学历" prop="education">
                <el-select v-model="certForm.education" placeholder="请选择学历" style="width: 100%"
                  :disabled="isCertLocked">
                  <el-option label="大专" value="大专" />
                  <el-option label="本科" value="本科" />
                  <el-option label="硕士" value="硕士" />
                  <el-option label="博士" value="博士" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>

              <el-form-item label="毕业专业" prop="major">
                <el-input v-model="certForm.major" placeholder="请输入毕业主修专业" :disabled="isCertLocked" clearable />
              </el-form-item>

              <el-form-item label="教学经验" prop="teachingExperience">
                <el-input type="textarea" v-model="certForm.teachingExperience" placeholder="请输入您的教学培训相关经历，字数在500字内为佳"
                  :rows="4" :disabled="isCertLocked" style="width: 100%" />
              </el-form-item>

              <el-form-item label="资质证书" prop="certificateUrls">
                <el-upload action="#" :http-request="handleCertUpload" list-type="picture-card"
                  :file-list="certFileList" :on-preview="handleCertPreview" :on-remove="handleCertRemove"
                  :disabled="isCertLocked" accept="image/jpeg,image/png,image/gif,image/webp">
                  <el-icon>
                    <Plus />
                  </el-icon>
                </el-upload>
                <div class="hint-text" style="width:100%; margin-top: 8px;">
                  请您拍照上传所有的资质证明（学位证、教资等，最多5张），支持 JPG/PNG 等主流格式。
                </div>
              </el-form-item>

              <el-form-item v-if="!isCertLocked">
                <el-button type="primary" :loading="submitCertLoading" @click="handleUpdateCert">
                  提交认证修改
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

      </el-tabs>
    </el-card>

    <!-- 证书预览图大图模式 -->
    <el-dialog v-model="certPreviewVisible" :title="'凭证照片预览'">
      <img w-full :src="certPreviewImage" alt="Preview Image" style="width: 100%; display: block;" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import {
  getUserInfo,
  updateUserInfo,
  updatePassword,
  sendUpdateEmailCode,
  updateEmail,
  getCertificationInfo,
  updateCertificationInfo
} from '@/api/profile'
import { uploadImage } from '@/api/common'
import { Plus, Camera } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()

// =======================
// 全局状态
// =======================
const activeTab = ref('info')

// 记录下接口里拉到的不能改的基本数据（用于比较和展示用）
const originalUsername = ref('')
const originalEmail = ref('')

// 取消倒计时引用的句柄
let emailTimer = null
onUnmounted(() => {
  if (emailTimer) clearInterval(emailTimer)
})

// =======================
// Tab1: 基本信息逻辑
// =======================
const infoFormRef = ref(null)
const infoLoading = ref(false)

const infoForm = reactive({
  avatar: '',
  nickname: '',
  gender: 0
})

const infoRules = {
  nickname: [
    { max: 20, message: '昵称最大不能超过 20 个字符', trigger: 'blur' }
  ]
}

const loadCurrentUserInfo = async () => {
  try {
    const data = await getUserInfo()
    if (data) {
      infoForm.avatar = data.avatar || ''
      infoForm.nickname = data.nickname || ''
      infoForm.gender = data.gender !== undefined ? data.gender : 0

      originalUsername.value = data.username || ''
      originalEmail.value = data.email || ''

      // 同时也可以更新 userStore
      if (typeof userStore.setUserInfo === 'function') {
        const currentUserInfo = userStore.userInfo || {}
        userStore.setUserInfo({ ...currentUserInfo, avatar: infoForm.avatar, nickname: infoForm.nickname })
      }
    }
  } catch (err) {
    console.error(err)
  }
}

const handleUpdateInfo = async () => {
  await infoFormRef.value.validate()
  infoLoading.value = true
  try {
    const payload = {
      avatar: infoForm.avatar,
      nickname: infoForm.nickname,
      gender: infoForm.gender
    }
    await updateUserInfo(payload)
    ElMessage.success('基本信息更新成功')

    // 再次刷新获取最新
    await loadCurrentUserInfo()
  } finally {
    infoLoading.value = false
  }
}

// ==== 头像上传相关逻辑 ====
const uploadAvatarLoading = ref(false)

const beforeAvatarUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif' || file.type === 'image/webp'
  const isLt2M = file.size / 1024 / 1024 < 5

  if (!isJpgOrPng) {
    ElMessage.error('上传头像图片只能是 JPG/PNG/GIF/WEBP 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 5MB!')
  }
  return isJpgOrPng && isLt2M
}

const handleAvatarUpload = async (options) => {
  try {
    uploadAvatarLoading.value = true
    const { file } = options
    const res = await uploadImage(file)
    const url = res.data || res

    if (typeof url === 'string') {
      infoForm.avatar = url
      ElMessage.success('头像图片上传成功')
    } else {
      ElMessage.error('上传头像失败，请稍后重试')
    }
  } catch (error) {
    ElMessage.error('上传头像出现异常')
  } finally {
    uploadAvatarLoading.value = false
  }
}


// =======================
// Tab2: 邮箱更改逻辑
// =======================
const emailFormRef = ref(null)
const emailLoading = ref(false)
const sendingEmailCode = ref(false)
const emailCountdown = ref(0) // 倒计时

const emailForm = reactive({
  email: '',
  code: ''
})

const emailRules = {
  email: [
    { required: true, message: '请输入要绑定的新邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入合法的邮箱格式', trigger: ['blur', 'change'] }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

const handleSendEmailCode = async () => {
  emailFormRef.value.validateField('email', async (valid) => {
    if (!valid) return
    if (emailForm.email === originalEmail.value) {
      ElMessage.warning('新邮箱不能与原邮箱一致，请重新输入')
      return;
    }
    try {
      sendingEmailCode.value = true
      await sendUpdateEmailCode(emailForm.email)
      ElMessage.success('验证码发送成功，请前往新邮箱查收')

      emailCountdown.value = 60
      emailTimer = setInterval(() => {
        emailCountdown.value--
        if (emailCountdown.value <= 0) {
          clearInterval(emailTimer)
        }
      }, 1000)
    } finally {
      sendingEmailCode.value = false
    }
  })
}

const handleUpdateEmail = async () => {
  await emailFormRef.value.validate()
  emailLoading.value = true
  try {
    await updateEmail({ email: emailForm.email, code: emailForm.code })
    ElMessage.success('邮箱更换成功！')

    // 清空表单，顺便把左边基本信息的邮箱显示也更新一下
    originalEmail.value = emailForm.email
    emailForm.email = ''
    emailForm.code = ''
    if (emailTimer) clearInterval(emailTimer)
    emailCountdown.value = 0
  } finally {
    emailLoading.value = false
  }
}


// =======================
// Tab3: 密码更改逻辑
// =======================
const passwordFormRef = ref(null)
const passwordLoading = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePass2ForPanel = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的新密码不一致!'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原来正在使用的密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2ForPanel, trigger: 'blur' }
  ]
}

const handleUpdatePassword = async () => {
  await passwordFormRef.value.validate()

  // 提供一道危险提示
  ElMessageBox.confirm('修改密码后，所有设备将立刻被迫下线，需要重新输入新密码进行登录。确定修改吗？', '重要提示', {
    confirmButtonText: '确定修改',
    cancelButtonText: '暂不修改',
    type: 'warning'
  }).then(async () => {
    passwordLoading.value = true
    try {
      await updatePassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      })

      ElMessage.success('密码修改成功，请重新登录！')
      userStore.logout()
      router.replace('/login')
    } finally {
      passwordLoading.value = false
    }
  }).catch(() => { })
}

// =======================
// Tab4: 认证信息逻辑
// =======================
const certFormRef = ref(null)
const loadingCert = ref(false)
const submitCertLoading = ref(false)

const certStatus = ref(-1) // -1无记录, 0待审核, 1已通过, 2已拒绝, 3修改待审核, 4已取消讲师资格
const certRejectReason = ref('')

// 受限：只有通过、失败、无记录可以起草/修改提交，如果处于审核状态(0/3/4)则锁定
const isCertLocked = computed(() => {
  return [0, 3, 4].includes(certStatus.value)
})

const certForm = reactive({
  realName: '',
  idCard: '',
  phone: '',
  email: '',
  education: '',
  major: '',
  teachingExperience: '',
  certificateUrls: ''
})

const certFileList = ref([]) // element ui 的呈现列表
const certPreviewVisible = ref(false)
const certPreviewImage = ref('')

const certRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式有误', trigger: ['blur', 'change'] }],
  certificateUrls: [{ required: true, message: '至少上传一张资质证明图片', trigger: 'change' }]
}

const loadCertificationInfo = async () => {
  loadingCert.value = true
  try {
    const data = await getCertificationInfo()

    if (data && Object.keys(data).length > 0) {
      certStatus.value = data.status !== undefined ? data.status : -1
      certRejectReason.value = data.rejectReason || ''

      certForm.realName = data.realName || ''
      certForm.idCard = data.idCard || ''
      certForm.phone = data.phone || ''
      certForm.email = data.email || ''
      certForm.education = data.education || ''
      certForm.major = data.major || ''
      certForm.teachingExperience = data.teachingExperience || ''
      certForm.certificateUrls = data.certificateUrls || ''

      // 解析逗号拼接的照片墙
      if (data.certificateUrls) {
        const urls = data.certificateUrls.split(',').filter(x => x)
        certFileList.value = urls.map(url => ({ name: url.substring(url.lastIndexOf('/') + 1), url: url }))
      } else {
        certFileList.value = []
      }
    }
  } catch (err) {
    certStatus.value = -1 // 若获取失败或者未记录，当作可添加
  } finally {
    loadingCert.value = false
  }
}

// ——多图上传区域——
const handleCertUpload = async (options) => {
  try {
    const { file } = options
    const res = await uploadImage(file)
    const url = res.data || res
    if (typeof url === 'string') {
      certFileList.value.push({ name: file.name, url: url })
      syncCertUrls()
      // 清理空对象验证
      if (certFormRef.value) certFormRef.value.clearValidate('certificateUrls')
    } else {
      ElMessage.error('上传图片失败，请稍后重试')
      options.onError(new Error('上传失败'))
    }
  } catch (err) {
    ElMessage.error('上传认证图片异常')
    options.onError(err)
  }
}

const handleCertRemove = (uploadFile, uploadFiles) => {
  certFileList.value = uploadFiles
  syncCertUrls()
}

const handleCertPreview = (uploadFile) => {
  certPreviewImage.value = uploadFile.url
  certPreviewVisible.value = true
}

const syncCertUrls = () => {
  certForm.certificateUrls = certFileList.value.map(item => item.url).join(',')
}

const handleUpdateCert = async () => {
  if (certFileList.value.length === 0) {
    ElMessage.warning('请至少上传一张资质证明或证书照片')
    return
  }
  await certFormRef.value.validate()

  submitCertLoading.value = true
  try {
    await updateCertificationInfo({
      realName: certForm.realName,
      idCard: certForm.idCard,
      phone: certForm.phone,
      email: certForm.email,
      education: certForm.education,
      major: certForm.major,
      teachingExperience: certForm.teachingExperience,
      certificateUrls: certForm.certificateUrls
    })

    ElMessage.success('认证信息已提交审核！')
    // 重新获取一下当前状态
    loadCertificationInfo()
  } finally {
    submitCertLoading.value = false
  }
}


// 初始化
onMounted(() => {
  loadCurrentUserInfo()
  loadCertificationInfo()
})

</script>

<style scoped>
.my-settings-panel {
  padding: 0;
  height: 100%;
}

.admin-card {
  min-height: calc(100vh - 120px);
}

/* 覆盖默认 el-tabs 的一些 padding，让内容区域充满 */
.settings-tabsHorizontal {
  padding: 0 10px;
}

:deep(.el-tabs__content) {
  min-height: 400px;
  padding: 20px 0;
}

.tab-pane-content {
  max-width: 600px;
  /* 让表单不用强行拉直占据全屏宽度 */
}

.settings-form {
  padding-top: 8px;
}

/* 通用输入与提示排版 */
.input-with-hint {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.hint-text {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.4;
}

.text-danger {
  color: #F56C6C;
}

.code-input-wrap {
  width: 100%;
  display: flex;
  gap: 12px;
}

.code-input-wrap .el-input {
  flex: 1;
}

/* 头像项增强：横向 */
.avatar-form-item {
  display: flex;
  align-items: center;
}

:deep(.avatar-form-item .el-form-item__label) {
  line-height: normal;
  align-self: center;
  padding-bottom: 0;
}

.avatar-uploader-custom {
  display: block;
}

.avatar-preview-box {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  border: 1px dashed var(--el-border-color);
  background-color: #fafafa;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  position: relative;
  cursor: pointer;
  transition: border-color 0.3s;
}

.avatar-preview-box:hover {
  border-color: var(--el-color-primary);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.avatar-uploader-icon {
  font-size: 24px;
  color: #8c939d;
}

.avatar-hover-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-preview-box:hover .avatar-hover-mask {
  opacity: 1;
}

.mask-text {
  font-size: 12px;
  margin-top: 4px;
}
</style>
