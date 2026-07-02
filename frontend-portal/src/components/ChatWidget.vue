<template>
  <div class="chat-widget">
    <!-- 悬浮按钮 -->
    <div class="chat-fab" @click="toggleOpen" v-if="!open">
      <el-icon :size="24"><ChatDotRound /></el-icon>
      <span class="fab-text">AI 助手</span>
    </div>

    <!-- 聊天窗口 -->
    <div class="chat-window" v-if="open">
      <!-- 头部 -->
      <div class="chat-header">
        <div class="header-left">
          <el-icon :size="18"><Avatar /></el-icon>
          <span>AI 学习助手</span>
        </div>
        <div class="header-right">
          <el-button text @click="clearMessages" title="清空对话">
            <el-icon :size="16"><Delete /></el-icon>
          </el-button>
          <el-button text @click="toggleOpen" title="关闭">
            <el-icon :size="16"><Close /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- 消息区域 -->
      <div class="chat-messages" ref="messagesContainer">
        <!-- 欢迎消息 -->
        <div class="message system-message" v-if="messages.length === 0">
          <div class="message-avatar system-avatar">
            <el-icon :size="20"><Avatar /></el-icon>
          </div>
          <div class="message-content">
            <div class="message-bubble system-bubble">
              您好！我是 AI 学习助手，有任何关于课程、学习路径的问题，欢迎随时提问 😊
            </div>
          </div>
        </div>

        <!-- 消息列表 -->
        <div
          v-for="(msg, index) in messages"
          :key="index"
          class="message"
          :class="msg.role === 'user' ? 'user-message' : 'assistant-message'"
        >
          <div class="message-avatar" :class="msg.role === 'user' ? 'user-avatar' : 'assistant-avatar'">
            <el-icon :size="18">{{ msg.role === 'user' ? UserFilled : Avatar }}</el-icon>
          </div>
          <div class="message-content">
            <div class="message-bubble" :class="msg.role === 'user' ? 'user-bubble' : 'assistant-bubble'">
              {{ msg.content }}
            </div>
          </div>
        </div>

        <!-- 加载中 -->
        <div class="message assistant-message" v-if="loading">
          <div class="message-avatar assistant-avatar">
            <el-icon :size="18"><Avatar /></el-icon>
          </div>
          <div class="message-content">
            <div class="message-bubble assistant-bubble loading-bubble">
              <span class="dot-flashing"></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          placeholder="输入您的问题..."
          @keyup.enter="sendMessage"
          :disabled="loading"
          clearable
        />
        <el-button type="primary" @click="sendMessage" :loading="loading" :disabled="!inputMessage.trim()">
          <el-icon><Promotion /></el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ChatDotRound, Avatar, Delete, Close, UserFilled, Promotion } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const open = ref(false)
const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesContainer = ref(null)

function toggleOpen() {
  open.value = !open.value
}

function clearMessages() {
  messages.value = []
}

async function sendMessage() {
  if (!inputMessage.value.trim() || loading.value) return

  const userMsg = inputMessage.value.trim()
  inputMessage.value = ''

  // 添加用户消息
  messages.value.push({ role: 'user', content: userMsg })

  // 滚动到底部
  await nextTick()
  scrollToBottom()

  // 调用AI
  loading.value = true
  try {
    // 构造消息历史（包含系统提示）
    const apiMessages = [
      { role: 'system', content: '你是一个企业AI与云能力人才培养平台的智能助手，专门回答关于课程、学习路径、认证考试、企业培训等问题。回答要简洁专业，不超过200字。' },
      ...messages.value.map(m => ({ role: m.role, content: m.content }))
    ]

    const res = await request.post('/api/app/public-chat/message', {
      messages: apiMessages
    })

    if (res.code === 200 || res.code === 0) {
      messages.value.push({ role: 'assistant', content: res.data })
    } else {
      ElMessage.error(res.message || '发送失败')
      messages.value.pop()
    }
  } catch (e) {
    ElMessage.error('网络错误，请稍后重试')
    messages.value.pop()
  } finally {
    loading.value = false
    await nextTick()
    scrollToBottom()
  }
}

function scrollToBottom() {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}
</script>

<style scoped>
.chat-widget {
  position: fixed;
  z-index: 9999;
  bottom: 24px;
  right: 24px;
}

/* 悬浮按钮 */
.chat-fab {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff, #79bbff);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  transition: all 0.3s;
  position: relative;
}

.chat-fab:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.6);
}

.fab-text {
  position: absolute;
  right: 64px;
  background: #303133;
  color: #fff;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  opacity: 0;
  transition: opacity 0.3s;
  pointer-events: none;
}

.chat-fab:hover .fab-text {
  opacity: 1;
}

/* 聊天窗口 */
.chat-window {
  width: 360px;
  height: 500px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 头部 */
.chat-header {
  background: linear-gradient(135deg, #409eff, #79bbff);
  color: #fff;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 14px;
}

.header-right {
  display: flex;
  gap: 4px;
}

.header-right .el-button {
  color: #fff;
  padding: 4px;
}

.header-right .el-button:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px 12px;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-avatar {
  background: #409eff;
  color: #fff;
}

.assistant-avatar,
.system-avatar {
  background: #67c23a;
  color: #fff;
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 13px;
  line-height: 1.5;
  word-break: break-word;
}

.user-bubble {
  background: #409eff;
  color: #fff;
  border-top-right-radius: 4px;
}

.assistant-bubble,
.system-bubble {
  background: #fff;
  color: #303133;
  border-top-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.system-bubble {
  background: #f0f9eb;
  color: #67c23a;
  font-size: 12px;
}

/* 加载动画 */
.loading-bubble {
  padding: 12px 18px;
}

.dot-flashing {
  position: relative;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #909399;
  animation: dot-flashing 1s infinite linear;
  display: inline-block;
}

.dot-flashing::before,
.dot-flashing::after {
  content: '';
  position: absolute;
  top: 0;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #909399;
  animation: dot-flashing 1s infinite linear;
}

.dot-flashing::before {
  left: -12px;
  animation-delay: 0.2s;
}

.dot-flashing::after {
  left: 12px;
  animation-delay: 0.4s;
}

@keyframes dot-flashing {
  0% { background-color: #909399; }
  50%, 100% { background-color: #dcdfe6; }
}

/* 输入区域 */
.chat-input {
  padding: 12px;
  background: #fff;
  border-top: 1px solid #ebeef5;
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.chat-input .el-input {
  flex: 1;
}

.chat-input .el-button {
  border-radius: 8px;
}
</style>
