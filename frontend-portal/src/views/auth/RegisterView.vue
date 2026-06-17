<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <router-link to="/" class="auth-logo">
          <span>📚</span>
          <span>在线教育平台</span>
        </router-link>
        <h2 class="auth-title">创建账号</h2>
        <p class="auth-subtitle">加入我们，开始学习之旅</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        size="large"
        @submit.prevent="handleSubmit"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名（3-20个字符）"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码（6位以上）"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item prop="email">
          <div class="code-input-wrap">
            <el-input
              v-model="form.email"
              placeholder="请输入您的邮箱地址"
              prefix-icon="Message"
              clearable
            />
          </div>
        </el-form-item>

        <el-form-item prop="code">
          <div class="code-input-wrap">
            <el-input
              v-model="form.code"
              placeholder="请输入邮箱验证码"
              prefix-icon="Key"
              clearable
            />
            <el-button
              class="code-btn"
              type="primary"
              plain
              :disabled="countdown > 0"
              :loading="sendingCode"
              @click="handleSendCode"
            >
              {{ countdown > 0 ? `${countdown}s 后获取` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="submit-btn"
            :loading="loading"
            @click="handleSubmit"
          >
            立即注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="auth-footer">
        <span>已有账号？</span>
        <router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { register, sendRegisterCode } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  code: ''
})

const sendingCode = ref(false)
const countdown = ref(0)
let timer = null

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3-20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
  ],
  code: [
    { required: true, message: '请输入获取的验证码', trigger: 'blur' },
    { min: 4, message: '验证码格式不正确', trigger: 'blur' }
  ]
}

async function handleSendCode() {
  formRef.value.validateField('email', async (valid) => {
    if (!valid) return
    sendingCode.value = true
    try {
      // 假设使用我们配置的 sendRegisterCode，或使用系统通用的
      await sendRegisterCode(form.email)
      ElMessage.success('验证码已发送，请登录邮箱查看')
      countdown.value = 60
      timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    } finally {
      sendingCode.value = false
    }
  })
}

async function handleSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const payload = {
      username: form.username,
      password: form.password,
      email: form.email,
      emailCode: form.code
    }
    await register(payload)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8f4fd 0%, #dbeeff 50%, #c9e6ff 100%);
}

.auth-card {
  width: 420px;
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 8px 40px rgba(64, 158, 255, 0.15);
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.auth-logo {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: var(--primary);
  text-decoration: none;
  margin-bottom: 16px;
}

.auth-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.auth-subtitle {
  font-size: 13px;
  color: var(--text-secondary);
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
}

.code-input-wrap {
  width: 100%;
  display: flex;
  gap: 12px;
}
.code-input-wrap .el-input {
  flex: 1;
}
.code-btn {
  width: 120px;
}

.auth-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 13px;
  color: var(--text-secondary);
}
.auth-footer a {
  margin-left: 4px;
  color: var(--primary);
  font-weight: 600;
}
</style>
