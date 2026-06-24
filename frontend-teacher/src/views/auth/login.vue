<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="login-logo">
          <img src="/logo.png" alt="神州数码" class="logo-img" />
        </div>
        <h2 class="login-title">在线教育讲师端</h2>
        <p class="login-subtitle">Teacher Console</p>
      </div>

      <el-form
        ref="loginRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            placeholder="请输入密码"
            type="password"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <router-link to="/forgot-password" class="forgot-link">忘记密码？</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { useUserStore } from '@/store/user'
import { User, Lock, Reading } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loginRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  loginType: 'teacher'
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginRef.value) return
  
  await loginRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        userStore.setToken(res.token)
        userStore.setUserInfo(res.userInfo)
        userStore.setRoles(res.roles)
        
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
  })
}
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
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-logo .logo-img {
  height: 40px;
  width: auto;
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

.login-form {
  margin-top: 8px;
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
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 16px;
}

.forgot-link {
  font-size: 14px;
  color: #8fa8c8;
  text-decoration: none;
  transition: color 0.2s;
}

.forgot-link:hover {
  color: #4da8ff;
}
</style>
