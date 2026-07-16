<template>
  <span ref="container" class="words-pull-up">
    <span
      v-for="(word, i) in words"
      :key="i"
      class="word-wrapper"
      :style="{
        transitionDelay: visible ? (i * 0.08) + 's' : '0s',
        opacity: visible ? 1 : 0,
        transform: visible ? 'translateY(0)' : 'translateY(20px)',
      }"
    >
      <span class="word" :class="wordClass">{{ word }}&nbsp;</span>
      <span
        v-if="showAsterisk && i === words.length - 1"
        class="asterisk"
        :style="{
          transitionDelay: visible ? ((words.length) * 0.08) + 's' : '0s',
          opacity: visible ? 1 : 0,
          transform: visible ? 'translateY(0)' : 'translateY(20px)',
        }"
      >*</span>
    </span>
  </span>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const props = defineProps({
  text: { type: String, required: true },
  showAsterisk: { type: Boolean, default: false },
  wordClass: { type: String, default: '' },
})

const visible = ref(false)
const container = ref(null)
const words = computed(() => props.text.split(' '))

onMounted(() => {
  const observer = new IntersectionObserver(
    ([entry]) => {
      if (entry.isIntersecting) {
        visible.value = true
        observer.disconnect()
      }
    },
    { threshold: 0.1 }
  )
  if (container.value) observer.observe(container.value)
})
</script>

<style scoped>
.words-pull-up {
  display: inline-flex;
  flex-wrap: wrap;
  align-items: baseline;
}
.word-wrapper {
  display: inline-flex;
  transition: opacity 0.6s cubic-bezier(0.16, 1, 0.3, 1), transform 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}
.word {
  white-space: pre;
}
.asterisk {
  font-size: 0.31em;
  position: relative;
  top: -0.65em;
  right: 0.3em;
  transition: opacity 0.6s cubic-bezier(0.16, 1, 0.3, 1), transform 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}
</style>
