<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'

type Role = 'system' | 'user' | 'assistant'
interface ChatMessage { role: Role; content: string }

const apiBase = (import.meta as any).env.VITE_API_BASE || '/api'

const messages = ref<ChatMessage[]>([
  { role: 'system', content: '你是一个专业的面试官，擅长结构化提问与基于STAR法的追问，回答请简洁、条理清晰，并在必要时给出改进建议。' }
])

const input = ref('')
const loading = ref(false)
const error = ref('')

async function send() {
  const content = input.value.trim()
  if (!content || loading.value) return

  input.value = ''
  messages.value.push({ role: 'user', content })
  loading.value = true
  error.value = ''

  try {
    const resp = await axios.post(`${apiBase}/chat`, {
      messages: messages.value.filter(m => m.role !== 'system')
    })
    const reply = resp.data?.choices?.[0]?.message?.content || '抱歉，未获得回复。'
    messages.value.push({ role: 'assistant', content: reply })
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || '请求失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="max-w-screen-md mx-auto h-[calc(100vh-120px)] px-4 py-3 flex flex-col">
    <div class="flex-1 overflow-y-auto space-y-3">
      <div v-for="(m, idx) in messages" :key="idx" v-show="m.role !== 'system'">
        <div :class="m.role === 'user' ? 'text-right' : 'text-left'">
          <div :class="['inline-block px-3 py-2 rounded-2xl text-sm leading-relaxed', m.role === 'user' ? 'bg-blue-600 text-white rounded-br-sm' : 'bg-white text-gray-800 rounded-bl-sm shadow']">
            {{ m.content }}
          </div>
        </div>
      </div>
      <div v-if="loading" class="text-center text-sm text-gray-500">正在思考...</div>
      <div v-if="error" class="text-center text-sm text-red-500">{{ error }}</div>
    </div>

    <div class="mt-3">
      <form @submit.prevent="send" class="flex gap-2">
        <input
          v-model="input"
          :disabled="loading"
          type="text"
          placeholder="请输入你的面试问题..."
          class="flex-1 border rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
        />
        <button
          type="submit"
          :disabled="loading || !input.trim()"
          class="px-4 py-2 text-sm rounded-xl text-white bg-blue-600 hover:bg-blue-700 disabled:opacity-60"
        >
          发送
        </button>
      </form>
      <p class="mt-2 text-xs text-gray-400">提示：不暴露 API Key，所有请求经由后端代理。</p>
    </div>
  </div>
</template>
