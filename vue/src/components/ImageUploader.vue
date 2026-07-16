<template>
  <div class="image-uploader">
    <el-upload
      :auto-upload="false"
      :limit="1"
      :on-change="onFileChange"
      :on-remove="onFileRemove"
      accept="image/*"
      list-type="picture"
      :file-list="fileList"
    >
      <el-button size="small">选择图片</el-button>
      <template #tip>
        <div class="upload-tip">支持 JPG/PNG/GIF，最大 10MB</div>
      </template>
    </el-upload>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: { type: [File, null], default: null },
  initialUrl: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue'])
const fileList = ref([])

watch(() => props.initialUrl, (url) => {
  if (url) {
    fileList.value = [{ name: '封面图', url }]
  } else {
    fileList.value = []
  }
}, { immediate: true })

function onFileChange(file) {
  emit('update:modelValue', file.raw)
}

function onFileRemove() {
  emit('update:modelValue', null)
}

function clear() {
  fileList.value = []
  emit('update:modelValue', null)
}

defineExpose({ clear })
</script>

<style scoped>
.image-uploader { width: 100%; }
.upload-tip { font-size: 12px; color: var(--color-gray-500); margin-top: 4px; }
</style>

