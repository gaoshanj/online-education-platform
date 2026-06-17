<template>
  <div class="ai-assistant-container">
    <!-- 左侧会话列表 -->
    <aside class="ai-sidebar">
      <div class="sidebar-header">
        <el-button type="primary" class="new-chat-btn" @click="handleNewChat">
          <el-icon><Plus /></el-icon> 开启新会话
        </el-button>
      </div>
      
      <div class="session-list" v-loading="sessionLoading">
        <div 
          v-for="session in sessionList" 
          :key="session.id"
          class="session-item"
          :class="{ active: currentSessionId === session.id }"
          @click="selectSession(session.id)"
        >
          <div class="session-title-wrap">
            <el-icon class="msg-icon"><ChatLineSquare /></el-icon>
            <span class="session-title" :title="session.title">{{ session.title || '新会话' }}</span>
          </div>
          <div class="session-actions" @click.stop v-if="currentSessionId === session.id">
            <el-popconfirm title="确定删除这个会话吗？" @confirm="handleDeleteSession(session.id)">
              <template #reference>
                <el-icon class="delete-icon"><Delete /></el-icon>
              </template>
            </el-popconfirm>
          </div>
        </div>
        
        <el-empty v-if="!sessionLoading && sessionList.length === 0" description="暂无历史会话" :image-size="60" />
      </div>
    </aside>

    <!-- 右侧主聊天区 -->
    <main class="ai-main">
      <div class="chat-header">
        <h2>{{ currentSessionTitle }}</h2>
      </div>

      <!-- 消息列表滚动区 -->
      <div class="chat-message-list" ref="messageListRef" v-loading="messageLoading">
        
        <!-- 空会话/欢迎语 -->
        <div class="welcome-screen" v-if="messages.length === 0 && !messageLoading">
          <div class="welcome-icon-box">🤖</div>
          <h3>你好！我是小智 AI 助手</h3>
          <p>请在下方输入你的问题，我会尽力为你解答有关课程、学习或技术的任何疑问。</p>
        </div>

        <!-- 聊天气泡 -->
        <div 
          v-for="msg in messages" 
          :key="msg.id || msg.tempId"
          class="message-row"
          :class="{ 'is-user': msg.role === 'user', 'is-ai': msg.role !== 'user' }"
        >
          <!-- 头像 -->
          <div class="msg-avatar">
            <el-avatar v-if="msg.role === 'user'" :size="36" :src="userAvatar">{{ userNameChar }}</el-avatar>
            <div v-else class="ai-avatar-box">🤖</div>
          </div>
          
          <!-- 消息体 -->
          <div class="msg-content-wrapper">
            <div class="msg-author">{{ msg.role === 'user' ? '你' : '小智 AI' }}</div>
            <div class="msg-bubble">
              <template v-if="msg.role === 'user'">{{ msg.content }}</template>
              <div v-else v-html="parseMarkdown(msg.content)" class="markdown-body"></div>
            </div>
            <!-- 等待回复 loading 动画 -->
            <div class="msg-loading" v-if="msg.status === 'loading'">
              <span class="dot"></span><span class="dot"></span><span class="dot"></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部输入区 -->
      <div class="chat-input-area">
        <div class="input-box-wrapper">
          <el-input
            v-model="inputContent"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 6 }"
            placeholder="输入您的问题... (Enter发送，Shift+Enter换行)"
            resize="none"
            class="chat-input"
            @keydown="handleKeydown"
          />
          <el-button 
            type="primary" 
            class="send-btn" 
            :loading="isGenerating" 
            :disabled="!inputContent.trim() || isGenerating"
            @click="sendMessage"
          >
            <el-icon><Position /></el-icon>
          </el-button>
        </div>
        <div class="chat-footer-text">AI 助手可能会生成错误信息，请核实重要内容。</div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue'
import { marked } from 'marked'
import { Plus, ChatLineSquare, Delete, Position } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getChatSessionPage, getChatSessionDetail, deleteChatSession, sendChatMessage, getChatResult } from '@/api/chat'
import { MSG_LOGIN_REQUIRED } from '@/utils/constants'

// 配置 marked：启用换行符转 <br>
marked.use({ breaks: true, async: false })

// Markdown 解析（仅用于 AI 回复气泡）
const parseMarkdown = (text) => {
  if (!text) return ''
  return marked.parse(text)
}

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 用户头像及标识
const userAvatar = computed(() => userStore.userInfo?.avatar)
const userNameChar = computed(() => userStore.userInfo?.username?.charAt(0)?.toUpperCase() || 'U')

// 侧边栏状态
const sessionList = ref([])
const sessionLoading = ref(false)
const currentSessionId = ref(null)

// 聊天区状态
const messages = ref([])
const messageLoading = ref(false)
const inputContent = ref('')
const isGenerating = ref(false)
const messageListRef = ref(null)
let pollTimer = null       // 轮询定时器
let typewriterTimer = null // 打字机定时器

onUnmounted(() => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
  if (typewriterTimer) {
    clearInterval(typewriterTimer)
    typewriterTimer = null
  }
})

// 计算当前标题
const currentSessionTitle = computed(() => {
  if (!currentSessionId.value) return '新会话'
  const target = sessionList.value.find(s => s.id === currentSessionId.value)
  return target ? target.title : '聊天记录'
})

onMounted(() => {
  if (userStore.isLoggedIn()) {
    fetchSessionList()
  }
})

// === 滚动到底部 ===
const scrollToBottom = async () => {
  await nextTick()
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

// === 拉取会话列表 ===
const fetchSessionList = async () => {
  sessionLoading.value = true
  try {
    const res = await getChatSessionPage({ page: 1, size: 50 }) // 假设先拉 50 条
    sessionList.value = res.data?.records || []
    
    // 如果没有选中的会话，且列表不为空，自动选中第一条
    if (!currentSessionId.value && sessionList.value.length > 0) {
      selectSession(sessionList.value[0].id)
    }
  } catch (error) {
    console.error('获取会话列表失败', error)
  } finally {
    sessionLoading.value = false
  }
}

// === 选择并拉取某会话聊天记录 ===
const selectSession = async (id) => {
  if (currentSessionId.value === id) return
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
  if (typewriterTimer) {
    clearInterval(typewriterTimer)
    typewriterTimer = null
  }
  isGenerating.value = false
  currentSessionId.value = id
  messageLoading.value = true
  try {
    const res = await getChatSessionDetail(id)
    messages.value = res.data?.messages || []
    scrollToBottom()
  } catch (error) {
    console.error('获取消息失败', error)
    ElMessage.error('无法加载聊天记录')
  } finally {
    messageLoading.value = false
  }
}

// === 新建会话 ===
const handleNewChat = () => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
  if (typewriterTimer) {
    clearInterval(typewriterTimer)
    typewriterTimer = null
  }
  isGenerating.value = false
  currentSessionId.value = null
  messages.value = []
}

// === 删除会话 ===
const handleDeleteSession = async (id) => {
  try {
    await deleteChatSession(id)
    ElMessage.success('已删除')
    
    // 如果删的是当前选中的，清除状态并重新指向第一条或新会话
    if (currentSessionId.value === id) {
      currentSessionId.value = null
      messages.value = []
    }
    
    fetchSessionList()
  } catch (error) {
    console.error(error)
  }
}

// === 处理键盘发送 ===
const handleKeydown = (e) => {
  // 阻止输入法选词时的回车
  if (e.isComposing || e.keyCode === 229) return

  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

// === 打字机效果：逐步展示 AI 回复 ===
const typewriterEffect = (fullText, msgObj) => {
  if (typewriterTimer) {
    clearInterval(typewriterTimer)
    typewriterTimer = null
  }

  msgObj.content = ''
  let index = 0
  const CHARS_PER_TICK = 2  // 每次追加 3 个字符
  const INTERVAL_MS = 40    // 间隔 10ms ≈ 300字符/秒

  typewriterTimer = setInterval(() => {
    const end = Math.min(index + CHARS_PER_TICK, fullText.length)
    msgObj.content = fullText.slice(0, end)
    index = end

    // 轻量滚动，不走 nextTick 避免频繁开销
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }

    if (index >= fullText.length) {
      clearInterval(typewriterTimer)
      typewriterTimer = null
      isGenerating.value = false
    }
  }, INTERVAL_MS)
}

// === 轮询 AI 回复 ===
const pollAiReply = (sessionId) => {
  if (pollTimer) {
    clearInterval(pollTimer)
  }
  
  pollTimer = setInterval(async () => {
    try {
      const res = await getChatResult(sessionId)
      const data = res.data
      
      if (data && data.assistantMessage) {
        // AI 回复完成，停止轮询
        clearInterval(pollTimer)
        pollTimer = null
        
        // 取出 loading 占位消息
        const lastMsg = messages.value[messages.value.length - 1]
        if (lastMsg && lastMsg.status === 'loading') {
          lastMsg.status = 'success'
          lastMsg.id = data.assistantMessage.id
          // 启动打字机（打字完成后才设 isGenerating = false）
          typewriterEffect(data.assistantMessage.content, lastMsg)
        } else {
          isGenerating.value = false
        }
        
        // 同步用户消息 ID
        const userMsg = messages.value[messages.value.length - 2]
        if (userMsg && !userMsg.id && data.userMessage) {
          userMsg.id = data.userMessage.id
        }
        
        scrollToBottom()
      }
    } catch (error) {
      console.error('轮询回复失败', error)
      clearInterval(pollTimer)
      pollTimer = null
      
      const lastMsg = messages.value[messages.value.length - 1]
      if (lastMsg && lastMsg.status === 'loading') {
        lastMsg.content = '获取回复失败，请稍后重试'
        lastMsg.status = 'error'
      }
      isGenerating.value = false
      scrollToBottom()
      ElMessage.error('获取 AI 回复失败，请重试')
    }
  }, 2000)
}

// === 发送消息 ===
const sendMessage = async () => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning(MSG_LOGIN_REQUIRED)
    router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return
  }

  const text = inputContent.value.trim()
  if (!text) return
  
  // 1. 本地立即 push 用户消息
  const tempUserId = Date.now()
  messages.value.push({
    tempId: tempUserId,
    role: 'user',
    content: text
  })
  
  // 2. 本地 push 一个假的 AI loading 消息
  const tempAiId = Date.now() + 1
  messages.value.push({
    tempId: tempAiId,
    role: 'assistant',
    content: '',
    status: 'loading'
  })
  
  inputContent.value = ''
  isGenerating.value = true
  scrollToBottom()

  try {
    const payload = {
      content: text
    }
    // 如果在已有会话里发送，带入 sessionId
    if (currentSessionId.value) {
      payload.sessionId = currentSessionId.value
    }
    
    // 3. 调接口，异步调用，只拿 sessionId
    const res = await sendChatMessage(payload)
    // 根据文档，返回值中的 data 即为新返回的 sessionId
    const sid = res.data
    
    // 如果是第一句话自动创建了新会话
    if (!currentSessionId.value && sid) {
      currentSessionId.value = sid
      fetchSessionList()
    }
    
    const targetSessionId = currentSessionId.value || sid
    if (targetSessionId) {
      // 开启轮询去获取答复
      pollAiReply(targetSessionId)
    } else {
      throw new Error('未获取到有效的 sessionId')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('发送请求失败，请重试')
    // 删掉 loading 假消息
    messages.value.pop()
    isGenerating.value = false
    scrollToBottom()
  }
}
</script>

<style scoped>
/* 整个页面满屏，不再受主滚动条影响 */
.ai-assistant-container {
  display: flex;
  height: calc(100vh - 60px); /* 减去顶部 Header 高度 */
  overflow: hidden;
  background-color: var(--bg-page);
}

/* ================= 侧边会话栏 ================= */
.ai-sidebar {
  width: 260px;
  background-color: #f7f7f9;
  border-right: 1px solid #e5e5e5;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.sidebar-header {
  padding: 20px 16px;
}

.new-chat-btn {
  width: 100%;
  justify-content: flex-start;
  font-weight: 500;
  border-radius: 8px;
  height: 44px;
}

.session-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.session-list::-webkit-scrollbar {
  width: 6px;
}
.session-list::-webkit-scrollbar-thumb {
  background-color: #dcdfe6;
  border-radius: 3px;
}

.session-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  color: #303133;
  transition: background-color 0.2s;
  font-size: 14px;
}
.session-item:hover {
  background-color: #ebedf0;
}
.session-item.active {
  background-color: #e5eaf3;
  color: var(--primary);
  font-weight: 500;
}

.session-title-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
  overflow: hidden;
  flex: 1;
}

.msg-icon {
  font-size: 16px;
  color: #909399;
}
.session-item.active .msg-icon {
  color: var(--primary);
}

.session-title {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.session-actions {
  opacity: 0;
  transition: opacity 0.2s;
  padding-left: 8px;
}
.session-item:hover .session-actions {
  opacity: 1;
}
.delete-icon {
  color: #f56c6c;
  font-size: 16px;
}

/* ================= 主聊天区 ================= */
.ai-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  min-width: 0;
}

/* Header */
.chat-header {
  height: 60px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  padding: 0 24px;
}
.chat-header h2 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

/* 消息滚动区 */
.chat-message-list {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  scroll-behavior: smooth;
}

/* 欢迎页 */
.welcome-screen {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #606266;
  text-align: center;
}
.welcome-icon-box {
  font-size: 64px;
  margin-bottom: 20px;
  background: #f2f6fc;
  width: 100px;
  height: 100px;
  line-height: 100px;
  border-radius: 50%;
}
.welcome-screen h3 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 12px;
}
.welcome-screen p {
  color: #909399;
  max-width: 400px;
  line-height: 1.6;
}

/* 气泡布局 */
.message-row {
  display: flex;
  gap: 16px;
  margin-bottom: 32px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
}
.message-row.is-user {
  flex-direction: row-reverse;
}

.ai-avatar-box {
  width: 36px;
  height: 36px;
  background: #e1f3d8;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.msg-content-wrapper {
  flex: 1;
  min-width: 0;
}
.message-row.is-user .msg-content-wrapper {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.msg-author {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}

.msg-bubble {
  display: inline-block;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 15px;
  line-height: 1.6;
  word-break: break-word;
}

/* ---- Markdown 渲染样式（AI 气泡内部） ---- */
.msg-bubble .markdown-body {
    text-align: left;
  font-size: 15px;
  line-height: 1.75;
  color: inherit;
}
.msg-bubble .markdown-body > *:first-child { margin-top: 0; }
.msg-bubble .markdown-body > *:last-child  { margin-bottom: 0; }

.msg-bubble .markdown-body h1,
.msg-bubble .markdown-body h2,
.msg-bubble .markdown-body h3,
.msg-bubble .markdown-body h4 {
  font-weight: 700;
  margin: 14px 0 6px;
  line-height: 1.4;
}
.msg-bubble .markdown-body h1 { font-size: 1.4em; }
.msg-bubble .markdown-body h2 { font-size: 1.2em; }
.msg-bubble .markdown-body h3 { font-size: 1.05em; }

.msg-bubble .markdown-body p {
  margin: 6px 0;
}

/* 核心：统一对齐 */
.msg-bubble .markdown-body ul,
.msg-bubble .markdown-body ol {
  list-style-position: outside;
  padding-left: 1.5em;
  margin: 6px 0;
}

.msg-bubble .markdown-body li {
  margin: 4px 0;
}
.msg-bubble .markdown-body strong {
  font-weight: 700;
}
.msg-bubble .markdown-body em {
  font-style: italic;
}

.msg-bubble .markdown-body code {
  font-family: 'Fira Code', 'Consolas', monospace;
  font-size: 0.9em;
  background: rgba(0, 0, 0, 0.06);
  padding: 1px 5px;
  border-radius: 4px;
}

.msg-bubble .markdown-body pre {
  background: #1e1e2e;
  color: #cdd6f4;
  border-radius: 8px;
  padding: 14px 16px;
  overflow-x: auto;
  margin: 10px 0;
}
.msg-bubble .markdown-body pre code {
  background: transparent;
  padding: 0;
  font-size: 0.88em;
  color: inherit;
}

.msg-bubble .markdown-body blockquote {
  border-left: 3px solid #dcdfe6;
  margin: 8px 0;
  padding: 4px 12px;
  color: #606266;
  background: rgba(0,0,0,0.03);
  border-radius: 0 4px 4px 0;
}

.msg-bubble .markdown-body hr {
  border: none;
  border-top: 1px solid #ebeef5;
  margin: 12px 0;
}

.msg-bubble .markdown-body a {
  color: var(--primary);
  text-decoration: none;
}
.msg-bubble .markdown-body a:hover {
  text-decoration: underline;
}

.msg-bubble .markdown-body table {
  border-collapse: collapse;
  width: 100%;
  margin: 10px 0;
  font-size: 14px;
}
.msg-bubble .markdown-body th,
.msg-bubble .markdown-body td {
  border: 1px solid #dcdfe6;
  padding: 6px 12px;
  text-align: left;
}
.msg-bubble .markdown-body th {
  background: #f2f6fc;
  font-weight: 600;
}

/* AI 的气泡 */
.message-row.is-ai .msg-bubble {
  background-color: #f7f7f8;
  color: #303133;
  border-bottom-left-radius: 2px;
}

/* 用户的气泡 */
.message-row.is-user .msg-bubble {
  background-color: var(--primary);
  color: #ffffff;
  border-bottom-right-radius: 2px;
}

/* Loading Dot Animation */
.msg-loading {
  margin-top: 8px;
  padding: 12px 16px;
  background-color: #f7f7f8;
  border-radius: 8px;
  border-bottom-left-radius: 2px;
  display: inline-block;
}
.dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  background-color: #909399;
  border-radius: 50%;
  margin: 0 3px;
  animation: bounce 1.4s infinite ease-in-out both;
}
.dot:nth-child(1) { animation-delay: -0.32s; }
.dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

/* ================= 底部输入区 ================= */
.chat-input-area {
  padding: 0 24px 20px;
  max-width: 848px;
  width: 100%;
  margin: 0 auto;
}

.input-box-wrapper {
  position: relative;
  background: white;
  border: 1px solid #dcdfe6;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  padding: 12px;
  display: flex;
  align-items: flex-end;
  gap: 12px;
  transition: border-color 0.2s;
}
.input-box-wrapper:focus-within {
  border-color: var(--primary);
}

.chat-input {
  flex: 1;
}
:deep(.chat-input .el-textarea__inner) {
  border: none;
  box-shadow: none !important;
  padding: 0;
  font-size: 15px;
  line-height: 1.5;
  background: transparent;
}
:deep(.chat-input .el-textarea__inner:focus) {
  box-shadow: none !important;
}

.send-btn {
  border-radius: 8px;
  height: 36px;
  width: 36px;
  padding: 0;
}

.chat-footer-text {
  text-align: center;
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 12px;
}
</style>
