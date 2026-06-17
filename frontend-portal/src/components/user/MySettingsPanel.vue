<template>
  <div class="my-settings-panel">
    <el-tabs v-model="activeTab" class="settings-tabsHorizontal">
      <!-- ============================================== -->
      <!-- Tab: 基本信息 -->
      <!-- ============================================== -->
      <el-tab-pane label="基本信息" name="info">
        <div class="tab-pane-content">
          <el-form
            ref="infoFormRef"
            :model="infoForm"
            :rules="infoRules"
            label-width="120px"
            class="settings-form"
            label-position="left"
          >
            <!-- 头像区域采用 el-upload -->
            <el-form-item label="用户头像" prop="avatar" class="avatar-form-item">
              <el-upload
                class="avatar-uploader-custom"
                action="#"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
                :http-request="handleAvatarUpload"
              >
                <div 
                  class="avatar-preview-box" 
                  v-loading="uploadAvatarLoading" 
                  element-loading-text="上传中..."
                >
                  <img v-if="infoForm.avatar" :src="infoForm.avatar" class="avatar-image" />
                  <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                  
                  <div class="avatar-hover-mask">
                    <el-icon><Camera /></el-icon>
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
          <el-form
            ref="emailFormRef"
            :model="emailForm"
            :rules="emailRules"
            label-width="120px"
            class="settings-form"
            label-position="left"
          >
            <!-- 友好提示 -->
            <el-alert
              title="修改邮箱后，旧邮箱将被取消注册关联，您之后需要使用新邮箱进行找回密码等操作。新邮箱不可与其他人重复。"
              type="warning"
              show-icon
              :closable="false"
              style="margin-bottom: 24px;"
            />

            <el-form-item label="新邮箱地址" prop="email">
              <el-input v-model="emailForm.email" placeholder="请输入你要更换的新邮箱" clearable />
            </el-form-item>

            <el-form-item label="验证码" prop="code">
              <div class="code-input-wrap">
                <el-input v-model="emailForm.code" placeholder="请输入收到的验证码" clearable />
                <el-button
                  type="primary"
                  plain
                  :disabled="emailCountdown > 0"
                  @click="handleSendEmailCode"
                  :loading="sendingEmailCode"
                >
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
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="120px"
            class="settings-form"
            label-position="left"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input 
                v-model="passwordForm.oldPassword" 
                type="password" 
                placeholder="请输入您正在使用的当前密码" 
                show-password 
                clearable 
              />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input 
                v-model="passwordForm.newPassword" 
                type="password" 
                placeholder="请输入新密码 (6-20位字符)" 
                show-password 
                clearable 
              />
            </el-form-item>

            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input 
                v-model="passwordForm.confirmPassword" 
                type="password" 
                placeholder="请再次确认新密码" 
                show-password 
                clearable 
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="passwordLoading" @click="handleUpdatePassword">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  getUserInfo, 
  updateUserInfo, 
  updatePassword, 
  sendUpdateEmailCode,
  updateEmail 
} from '@/api/user'
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
    const res = await getUserInfo()
    if (res.code === 200 && res.data) {
      const data = res.data
      infoForm.avatar = data.avatar || ''
      infoForm.nickname = data.nickname || ''
      infoForm.gender = data.gender !== undefined ? data.gender : 0
      
      // 记录只读字段
      originalUsername.value = data.username || ''
      originalEmail.value = data.email || ''
    }
  } catch (err) {
    // 错误处理由拦截器统一管理
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
    
    // 更新后主动刷新一次当前页 userStore 或者通知全局
    await userStore.fetchUserInfo()
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
    if (res.code === 200 && res.data) {
      infoForm.avatar = res.data // 接口直接返回文件 URL 链接的话取 data
      ElMessage.success('头像图片上传成功')
    } else {
      ElMessage.error(res.message || '上传头像失败，请稍后重试')
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
      userStore.logoutAndClear()
      router.replace('/login')
    } finally {
      passwordLoading.value = false
    }
  }).catch(() => {})
}


// 初始化
onMounted(() => {
  loadCurrentUserInfo()
})

</script>

<style scoped>
.my-settings-panel {
  padding: 10px 0;
  height: 100%;
}

/* 覆盖默认 el-tabs 的一些 padding，让内容区域充满 */
.settings-tabs {
  height: 100%;
}
:deep(.el-tabs__content) {
  min-height: 400px; /* 给一个最小展示空间 */
  padding-left: 32px;
}

.tab-pane-content {
  max-width: 600px; /* 让表单不用强行拉直占据全屏宽度 */
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

/* 头像项增强：横向 */
.avatar-uploader {
  display: flex;
  align-items: center;
  width: 100%;
}

.code-input-wrap {
  width: 100%;
  display: flex;
  gap: 12px;
}
.code-input-wrap .el-input {
  flex: 1;
}

/* ================= 针对新头像组件加的样式 ================= */
.avatar-form-item {
  display: flex;
  align-items: center;
}
:deep(.avatar-form-item .el-form-item__label) {
  /* 当设置为 left 的时候，重置行高防止上浮，或者直接 flex 属性覆盖 */
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
  border: 1px dashed var(--border-color);
  background-color: #fafafa;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  position: relative;
  cursor: pointer;
  transition: border-color var(--el-transition-duration-fast);
}
.avatar-preview-box:hover {
  border-color: var(--primary);
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

.settings-tabsHorizontal {
  padding: 10px 24px;
}
</style>
