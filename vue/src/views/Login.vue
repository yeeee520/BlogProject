<template>
  <div class="login-page">
    <div class="login-card">
      <h2 class="login-title">欢迎回来</h2>
      <p class="login-subtitle">登录以继续你的旅程</p>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="handleLogin" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" class="login-btn" round>登 录</el-button>
        </el-form-item>
        <div class="login-hint">
          测试账号: admin / 123456
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const form = reactive({ username: 'admin', password: '123456' })
const loading = ref(false)

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await authStore.login(form)
    ElMessage.success('登录成功')
    router.push('/manage')
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: calc(100vh - 64px); margin-top: 64px;
  display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  background: #fff; border-radius: 20px; padding: 48px 40px 36px;
  width: 420px; max-width: 90vw;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
  animation: cardIn 0.5s cubic-bezier(0.25,0.46,0.45,0.94);
}
@keyframes cardIn {
  from { opacity: 0; transform: translateY(20px) scale(0.96); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
.login-title { font-size: 26px; font-weight: 700; color: #1a1a2e; margin-bottom: 6px; text-align: center; }
.login-subtitle { font-size: 14px; color: #a0a4b8; margin-bottom: 32px; text-align: center; }
.login-form { width: 100%; }
.login-btn { width: 100%; margin-top: 4px; }
.login-hint { text-align: center; font-size: 12px; color: #c0c4d0; margin-top: 8px; }
</style>
