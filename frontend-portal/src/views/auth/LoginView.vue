<template>
  <div class="auth-page">
    <div class="auth-card">
      <!-- 头部 -->
      <div class="auth-header">
        <router-link to="/" class="auth-logo">
          <img src="/logo.png" alt="神州数码" class="logo-img" />
          <span>企业AI + 云能力人才培养平台</span>
        </router-link>
        <h2 class="auth-title">欢迎回来</h2>
        <p class="auth-subtitle">登录以继续学习</p>
      </div>

      <!-- 表单 -->
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
            placeholder="请输入用户名"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
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
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 底部链接 -->
      <div class="auth-footer">
        <div class="footer-links">
          <router-link to="/forgot-password" class="forgot-link">忘记密码？</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  loginType: 'app'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功，欢迎回来！')
    
    // 获取重定向参数或默认跳转首页
    const redirect = route.query.redirect || '/'
    router.push(redirect)
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

.auth-logo .logo-img {
  height: 36px;
  width: auto;
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
  letter-spacing: 2px;
  border-radius: 8px;
}

.auth-footer {
  margin-top: 20px;
  font-size: 13px;
  color: var(--text-secondary);
}

.footer-links {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.forgot-link {
  color: #909399;
  text-decoration: none;
  transition: color 0.2s;
}

.forgot-link:hover {
  color: var(--primary);
}

.register-link {
  margin-left: 4px;
  color: var(--primary);
  font-weight: 600;
  text-decoration: none;
}
</style>
