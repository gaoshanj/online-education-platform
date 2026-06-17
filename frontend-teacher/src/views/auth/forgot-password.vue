<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="login-logo">
          <el-icon class="logo-icon"><Reading /></el-icon>
        </div>
        <h2 class="login-title">找回密码</h2>
        <p class="login-subtitle">通过绑定的邮箱重置您的密码</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        @submit.prevent="handleSubmit"
      >
        <el-form-item prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入注册邮箱"
            prefix-icon="Message"
            clearable
          />
        </el-form-item>

        <el-form-item prop="code" class="code-item">
          <div class="code-input-wrap">
            <el-input
              v-model="form.code"
              placeholder="请输入验证码"
              prefix-icon="Key"
              clearable
            />
            <el-button
              type="primary"
              plain
              class="send-code-btn"
              :disabled="countdown > 0"
              @click="handleSendCode"
              :loading="sendingCode"
            >
              {{ countdown > 0 ? `${countdown}s 后重发` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            placeholder="请输入新密码 (6-20位字符)"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次确认新密码"
            prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleSubmit"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="login-btn"
            :loading="loading"
            @click="handleSubmit"
          >
            重置密码
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <router-link to="/login" class="forgot-link">
          <el-icon><Back /></el-icon> 返回登录页
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Message, Key, Lock, Back, Reading } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)

const loading = ref(false)
const sendingCode = ref(false)

const countdown = ref(0)
let timer = null

const form = reactive({
  email: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ]
}

const handleSendCode = async () => {
  // 先单独校验 email 字段是否合格
  formRef.value.validateField('email', async (valid) => {
    if (!valid) return
    
    sendingCode.value = true
    // TODO: 接口后续提供，暂用 setTimeout 模拟延迟
    setTimeout(() => {
      sendingCode.value = false
      ElMessage.success('验证码已发送，请查收邮箱')
      
      // 开始倒计时 60s
      countdown.value = 60
      timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    }, 1000)
  })
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  loading.value = true
  // TODO: 重置密码接口后续提供，暂用 setTimeout 模拟延迟
  setTimeout(() => {
    loading.value = false
    ElMessage.success('密码重置成功，请重新登录')
    router.push('/login')
  }, 1500)
}

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a3a5c 0%, #2563a8 50%, #4da8ff 100%);
}

.login-card {
  width: 360px;
  max-height: 90vh;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.97);
  border-radius: 16px;
  padding: 40px 36px 32px;
  box-shadow: 0 20px 60px rgba(0, 50, 120, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.login-logo {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #1a3a5c, #2563a8);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon {
  font-size: 32px;
  color: #ffffff;
}

.login-title {
  font-size: 22px;
  font-weight: 700;
  color: #1a3a5c;
  margin-bottom: 6px;
}

.login-subtitle {
  font-size: 13px;
  color: #8fa8c8;
  letter-spacing: 2px;
}

.el-form-item {
  margin-bottom: 22px;
}

.code-input-wrap {
  width: 100%;
  display: flex;
  gap: 12px;
}

.code-input-wrap .el-input {
  flex: 1;
}

.send-code-btn {
  width: 110px;
}

.login-btn {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #1a3a5c, #2563a8);
  border: none;
  border-radius: 8px;
  margin-top: 4px;
}

.login-btn:hover {
  background: linear-gradient(135deg, #2563a8, #4da8ff);
}

.login-footer {
  text-align: center;
  margin-top: 16px;
}

.forgot-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #8fa8c8;
  text-decoration: none;
  transition: color 0.2s;
}

.forgot-link:hover {
  color: #4da8ff;
}
</style>
