<template>
  <div class="travels-admin-page">
    <section class="admin-header">
      <div class="header-left">
        <router-link to="/travels" class="back-link">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>
          返回游记
        </router-link>
        <h1 class="admin-title">游记管理</h1>
      </div>
      <el-button type="primary" @click="showCreate">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right:4px"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        新建游记
      </el-button>
    </section>

    <el-table :data="notes" v-loading="loading" class="admin-table">
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <img v-if="row.coverUrl" :src="row.coverUrl" class="table-thumb" />
          <div v-else class="table-thumb-empty">--</div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="140" show-overflow-tooltip />
      <el-table-column prop="location" label="地点" min-width="100" show-overflow-tooltip />
      <el-table-column prop="travelDate" label="旅行日期" width="110" />
      <el-table-column label="标签" min-width="140">
        <template #default="{ row }">
          <div class="tags-cell" v-if="row.tags">
            <el-tag v-for="t in row.tags.split(',')" :key="t" size="small" type="info" class="tag-item">{{ t.trim() }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="70" align="center" />
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 1" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" align="center" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Create/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEditing ? '编辑游记' : '新建游记'" width="800px" destroy-on-close class="admin-dialog">
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="游记标题" />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="一句话描述（卡片展示用）" />
        </el-form-item>
        <el-form-item label="封面图">
          <ImageUploader v-model="form.coverFile" :initial-url="form.coverUrl" />
        </el-form-item>
        <el-form-item label="正文内容">
          <div class="content-editor">
            <div class="editor-toolbar">
              <el-button size="small" @click="insertImage" :loading="imageUploading">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right:4px"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>
                插入图片
              </el-button>
              <span class="editor-hint">支持 Markdown，图片会插入到光标位置</span>
            </div>
            <el-input v-model="form.content" type="textarea" :rows="12" placeholder="使用 Markdown 格式书写..." ref="contentRef" />
          </div>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="旅行地点">
              <el-input v-model="form.location" placeholder="如：云南·大理" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="旅行日期">
              <el-date-picker v-model="form.travelDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="标签">
              <el-input v-model="form.tags" placeholder="多个标签用逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="阅读时长">
              <el-input v-model="form.readTime" placeholder="如：8 min" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">{{ isEditing ? '保存' : '创建' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNotes, createNote, updateNote, deleteNote, uploadNoteImage } from '@/api/travel'
import ImageUploader from '@/components/ImageUploader.vue'

const notes = ref([])
const loading = ref(false)
const saving = ref(false)
const imageUploading = ref(false)
const dialogVisible = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const contentRef = ref(null)

const form = reactive({
  title: '', summary: '', content: '', coverUrl: '', coverFile: null,
  location: '', travelDate: '', tags: '', readTime: ''
})

async function loadNotes() {
  loading.value = true
  try {
    const res = await getNotes()
    if (res.code == 200) notes.value = res.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

function resetForm() {
  form.title = ''; form.summary = ''; form.content = ''; form.coverUrl = ''; form.coverFile = null
  form.location = ''; form.travelDate = ''; form.tags = ''; form.readTime = ''
}

function showCreate() {
  resetForm(); isEditing.value = false; editingId.value = null; dialogVisible.value = true
}

function showEdit(row) {
  isEditing.value = true; editingId.value = row.noteId
  form.title = row.title || ''; form.summary = row.summary || ''; form.content = row.content || ''
  form.coverUrl = row.coverUrl || ''; form.coverFile = null
  form.location = row.location || ''; form.travelDate = row.travelDate || ''
  form.tags = row.tags || ''; form.readTime = row.readTime || ''
  dialogVisible.value = true
}

async function insertImage() {
  const input = document.createElement('input')
  input.type = 'file'; input.accept = 'image/*'
  input.onchange = async () => {
    const file = input.files[0]; if (!file) return
    imageUploading.value = true
    try {
      const fd = new FormData(); fd.append('file', file)
      const res = await uploadNoteImage(editingId.value || 0, fd)
      if (res.code == 200) {
        const url = res.data
        const markdown = '\n![](' + url + ')\n'
        form.content += markdown
        ElMessage.success('图片已插入')
      } else { ElMessage.error(res.msg || res.message || '上传失败') }
    } catch (e) { ElMessage.error('上传失败') }
    finally { imageUploading.value = false }
  }
  input.click()
}

async function handleSave() {
  if (!form.title.trim()) { ElMessage.warning('请输入标题'); return }
  saving.value = true
  try {
    const fd = new FormData()
    fd.append('title', form.title)
    if (form.summary) fd.append('summary', form.summary)
    fd.append('content', form.content || '')
    if (form.location) fd.append('location', form.location)
    if (form.travelDate) fd.append('travelDate', form.travelDate)
    if (form.tags) fd.append('tags', form.tags)
    if (form.readTime) fd.append('readTime', form.readTime)
    if (form.coverFile) fd.append('coverFile', form.coverFile)

    let res
    if (isEditing.value) {
      res = await updateNote(editingId.value, fd)
    } else {
      fd.append('status', '1')
      res = await createNote(fd)
    }
    if (res.code == 200) {
      ElMessage.success(isEditing.value ? '保存成功' : '创建成功')
      dialogVisible.value = false; loadNotes()
    } else { ElMessage.error(res.msg || res.message || '操作失败') }
  } catch (e) { ElMessage.error('操作失败') }
  finally { saving.value = false }
}

async function toggleStatus(row) {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    const fd = new FormData(); fd.append('status', String(newStatus))
    const res = await updateNote(row.noteId, fd)
    if (res.code == 200) { row.status = newStatus; ElMessage.success(newStatus === 1 ? '已显示' : '已隐藏') }
  } catch (e) { ElMessage.error('操作失败') }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除这篇游记吗？此操作不可撤销。', '删除确认', { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' })
    const res = await deleteNote(row.noteId)
    if (res.code == 200) { ElMessage.success('已删除'); loadNotes() }
    else { ElMessage.error(res.msg || res.message || '删除失败') }
  } catch (e) { /* cancelled */ }
}

onMounted(() => { loadNotes() })
</script>

<style scoped>
.travels-admin-page {
  padding-top: 70px; min-height: 100vh; background: var(--color-bg-black);
  padding-left: 16px; padding-right: 16px; padding-bottom: 80px;
}
@media (min-width: 768px) { .travels-admin-page { padding-left: 48px; padding-right: 48px; } }

.admin-header {
  display: flex; align-items: flex-end; justify-content: space-between;
  max-width: 1200px; margin: 0 auto; padding: 40px 0 24px; gap: 16px; flex-wrap: wrap;
}
.header-left { display: flex; flex-direction: column; gap: 8px; }
.back-link {
  display: inline-flex; align-items: center; gap: 4px;
  font-size: 13px; color: var(--color-gray-400); text-decoration: none; transition: color 0.3s;
}
.back-link:hover { color: var(--color-primary); }
.admin-title { font-size: 24px; font-weight: 700; color: var(--color-primary-text); }

.admin-table {
  max-width: 1200px; margin: 0 auto;
  --el-table-bg-color: var(--color-bg-dark);
  --el-table-tr-bg-color: var(--color-bg-dark);
  --el-table-header-bg-color: #0a0a0a;
  --el-table-row-hover-bg-color: rgba(255,255,255,0.04);
  --el-table-border-color: rgba(255,255,255,0.06);
  --el-table-text-color: var(--color-primary-text);
  --el-table-header-text-color: var(--color-gray-400);
  --el-table-current-row-bg-color: rgba(255,255,255,0.06);
  --el-table-expanded-cell-bg-color: var(--color-bg-dark);
}
.admin-table :deep(.el-table__inner-wrapper::before) { display: none; }
.admin-table :deep(.el-table__empty-text) { color: var(--color-gray-500); }

.table-thumb { width: 60px; height: 44px; object-fit: cover; border-radius: 6px; display: block; }
.table-thumb-empty {
  width: 60px; height: 44px; display: flex; align-items: center; justify-content: center;
  background: var(--color-bg-card); border-radius: 6px; font-size: 11px; color: var(--color-gray-500);
}
.tags-cell { display: flex; flex-wrap: wrap; gap: 4px; }
.tag-item { max-width: 100px; }

.content-editor { width: 100%; }
.editor-toolbar { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.editor-hint { font-size: 12px; color: var(--color-gray-500); }

.admin-dialog :deep(.el-dialog) { background: var(--color-bg-dark) !important; }
.admin-dialog :deep(.el-dialog__title) { color: var(--color-primary-text) !important; }
.admin-dialog :deep(.el-dialog__header) { border-bottom: 1px solid rgba(255,255,255,0.06); }
.admin-dialog :deep(.el-dialog__body) { padding-top: 20px; }
</style>

