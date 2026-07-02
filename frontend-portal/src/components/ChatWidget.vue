<template>
  <div class="chat-widget-container">
    <!-- 悬浮按钮 -->
    <div class="chat-float-btn" @click="toggleChat" :class="{ hidden: isOpen }">
      <el-icon :size="28"><ChatDotRound /></el-icon>
      <span>AI 助手</span>
    </div>

    <!-- 聊天窗口 -->
    <Transition name="chat-slide">
      <div v-if="isOpen" class="chat-window">
        <!-- 头部 -->
        <div class="chat-header">
          <div class="chat-header-left">
            <el-icon :size="20"><Monitor /></el-icon>
            <span>AI 学习助手</span>
          </div>
          <div class="chat-header-right">
            <el-button text size="small" @click="clearMessages" title="清空聊天">
              <el-icon :size="16"><Delete /></el-icon>
            </el-button>
            <el-button text size="small" @click="toggleChat" title="关闭">
              <el-icon :size="16"><Close /></el-icon>
            </el-button>
          </div>
        </div>

        <!-- 消息列表 -->
        <div ref="messageListRef" class="chat-messages">
          <div v-if="messages.length === 0" class="chat-empty">
            <el-icon :size="48" color="#409eff"><ChatLineRound /></el-icon>
            <p>你好！我是 AI 学习助手</p>
            <p class="chat-empty-hint">可以问我任何关于课程、学习、职业发展的问题</p>
          </div>
          <div
            v-for="(msg, index) in messages"
            :key="index"
            class="chat-message"
            :class="{ 'is-user': msg.role === 'user' }"
          >
            <div class="message-avatar">
              <!-- 使用文字头像替代图标 -->
              <div v-if="msg.role === 'user'" class="avatar-text avatar-user">我</div>
              <div v-else class="avatar-text avatar-ai">AI</div>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(msg.content)"></div>
            </div>
          </div>
          <div v-if="loading" class="chat-message is-ai">
            <div class="message-avatar">
              <div class="avatar-text avatar-ai">AI</div>
            </div>
            <div class="message-content">
              <div class="message-loading">
                <span class="dot"></span>
                <span class="dot"></span>
                <span class="dot"></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chat-input-area">
          <el-input
            v-model="inputText"
            type="textarea"
            :rows="2"
            placeholder="输入您的问题..."
            resize="none"
            @keydown.enter.exact.prevent="sendMessage"
            :disabled="loading"
          />
          <el-button
            type="primary"
            circle
            @click="sendMessage"
            :loading="loading"
            :disabled="!inputText.trim()"
            class="send-btn"
          >
            <el-icon :size="16"><Promotion /></el-icon>
          </el-button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  ChatDotRound,
  ChatLineRound,
  Monitor,
  Delete,
  Close,
  Promotion
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const isOpen = ref(false)
const inputText = ref('')
const loading = ref(false)
const messages = ref([])
const messageListRef = ref(null)

// 系统提示词
const systemPrompt = `你是一个企业AI与云能力人才培养平台的智能助手，名为"小智"。你的职责包括：
1. 课程推荐：根据用户需求推荐合适的课程（云计算、AI开发、DevOps、网络安全等）
2. 学习路径：为用户规划学习路线
3. 行业咨询：回答关于云计算、人工智能、数字化转型等行业问题
4. 平台使用：帮助用户了解平台功能和使用方法

请用简洁友好的中文回复，每次回答控制在200字以内。如果不确定的问题，建议用户联系人工客服。`

function toggleChat() {
  isOpen.value = !isOpen.value
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  // 添加用户消息
  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  loading.value = true

  // 滚动到底部
  await scrollToBottom()

  try {
    // 构建消息历史（带系统提示）
    const apiMessages = [
      { role: 'system', content: systemPrompt },
      ...messages.value.filter(m => m.role === 'user' || m.role === 'assistant').map(m => ({
        role: m.role,
        content: m.content
      }))
    ]

    const res = await request.post('/app/public-chat/message', {
      messages: apiMessages
    })

    if (res.code === 200) {
      messages.value.push({ role: 'assistant', content: res.data })
    } else {
      ElMessage.error(res.message || 'AI 服务暂时不可用')
    }
  } catch (error) {
    console.error('Chat error:', error)
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

function clearMessages() {
  messages.value = []
}

function formatMessage(text) {
  if (!text) return ''
  return text
    .replace(/\n/g, '<br>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
}

async function scrollToBottom() {
  await nextTick()
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}
</script>

<style scoped>
.chat-widget-container {
  position: fixed;
  z-index: 9999;
  bottom: 24px;
  right: 24px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.chat-float-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #409eff, #337ecc);
  color: #fff;
  border-radius: 24px;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.4);
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  user-select: none;
}

.chat-float-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(64, 158, 255, 0.5);
}

.chat-float-btn.hidden {
  opacity: 0;
  pointer-events: none;
  transform: scale(0.8);
}

.chat-window {
  width: 380px;
  height: 560px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  background: linear-gradient(135deg, #409eff, #337ecc);
  color: #fff;
  flex-shrink: 0;
}

.chat-header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
}

.chat-header-right {
  display: flex;
  align-items: center;
  gap: 4px;
}

.chat-header-right .el-button {
  color: rgba(255, 255, 255, 0.85);
}

.chat-header-right .el-button:hover {
  color: #fff;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.chat-messages::-webkit-scrollbar {
  width: 5px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.chat-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #909399;
}

.chat-empty p {
  margin: 0;
  font-size: 15px;
}

.chat-empty-hint {
  font-size: 12px !important;
  color: #c0c4cc;
}

.chat-message {
  display: flex;
  gap: 10px;
  max-width: 88%;
  animation: fadeInUp 0.3s ease;
}

.chat-message.is-user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

/* 文字头像样式 */
.avatar-text {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.avatar-user {
  background: #409eff;
  color: #fff;
}

.avatar-ai {
  background: linear-gradient(135deg, #409eff, #337ecc);
  color: #fff;
}

.message-content {
  min-width: 0;
}

.message-text {
  padding: 10px 14px;
  border-radius: 12px;
  line-height: 1.6;
  font-size: 13.5px;
  word-break: break-word;
}

.is-user .message-text {
  background: #409eff;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.is-ai .message-text {
  background: #fff;
  color: #303133;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.message-text code {
  background: rgba(0, 0, 0, 0.06);
  padding: 1px 5px;
  border-radius: 4px;
  font-size: 12px;
}

.is-user .message-text code {
  background: rgba(255, 255, 255, 0.2);
}

.message-loading {
  padding: 12px 18px;
  background: #fff;
  border-radius: 12px;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  display: flex;
  gap: 5px;
  align-items: center;
}

.dot {
  width: 7px;
  height: 7px;
  background: #409eff;
  border-radius: 50%;
  animation: bounce 1.4s infinite both;
}

.dot:nth-child(2) { animation-delay: 0.16s; }
.dot:nth-child(3) { animation-delay: 0.32s; }

.chat-input-area {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  padding: 12px 14px;
  background: #fff;
  border-top: 1px solid #ebeef5;
  flex-shrink: 0;
}

.chat-input-area :deep(.el-textarea__inner) {
  border-radius: 10px;
  font-size: 13.5px;
  box-shadow: none;
  border-color: #dcdfe6;
}

.chat-input-area :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
}

.send-btn {
  flex-shrink: 0;
  width: 38px;
  height: 38px;
}

/* 动画 */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

/* 窗口滑入动画 */
.chat-slide-enter-active,
.chat-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.chat-slide-enter-from,
.chat-slide-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

@media (max-width: 480px) {
  .chat-widget-container {
    bottom: 12px;
    right: 12px;
  }
  .chat-window {
    width: calc(100vw - 32px);
    height: calc(100vh - 160px);
  }
}
</style>
