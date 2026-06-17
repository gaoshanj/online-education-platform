<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <router-link to="/" class="auth-logo">
          <span>📚</span>
          <span>在线教育平台</span>
        </router-link>
        <h2 class="auth-title">找回密码</h2>
        <p class="auth-subtitle">通过绑定的邮箱重置您的密码</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        size="large"
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
            class="submit-btn"
            :loading="loading"
            @click="handleSubmit"
          >
            重置密码
          </el-button>
        </el-form-item>
      </el-form>

      <div class="auth-footer">
        <router-link to="/login" class="back-link">
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
import { Message, Key, Lock, Back } from '@element-plus/icons-vue'
import { resetPassword } from '@/api/user'
import { sendForgotCode } from '@/api/auth'

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
    
    try {
      sendingCode.value = true
      await sendForgotCode(form.email)
      ElMessage.success('验证码已发送，请查收邮箱')
      
      // 开始倒计时 60s
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

const handleSubmit = async () => {
  await formRef.value.validate()
  
  loading.value = true
  try {
    const payload = {
      email: form.email,
      code: form.code,
      newPassword: form.newPassword
    }
    await resetPassword(payload)
    ElMessage.success('密码重置成功，请重新登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
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

/* 覆盖默认的 el-form-item  marginBottom 适配多表单项 */
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

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 2px;
  border-radius: 8px;
  margin-top: 8px;
}

.auth-footer {
  text-align: center;
  margin-top: 16px;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--text-secondary);
  text-decoration: none;
  transition: color 0.2s;
}

.back-link:hover {
  color: var(--primary);
}
</style>
