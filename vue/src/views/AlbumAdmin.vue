<template>
  <div class="album-admin-page">
    <section class="admin-header">
      <div class="header-left">
        <router-link to="/album" class="back-link">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>
          返回相册
        </router-link>
        <h1 class="admin-title">相册管理</h1>
      </div>
      <el-button type="primary" @click="showUpload">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right:4px"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        导入照片
      </el-button>
    </section>

    <div class="batch-actions">
      <span class="selection-count">已选择 {{ selectedPhotos.length }} 张</span>
      <el-button size="small" type="success" plain :disabled="!selectedPhotos.length" @click="handleBatchVisibility(1)">
        一键公开
      </el-button>
      <el-button size="small" type="info" plain :disabled="!selectedPhotos.length" @click="handleBatchVisibility(0)">
        一键私密
      </el-button>
    </div>

    <el-table ref="tableRef" :data="photos" row-key="photoId" v-loading="loading" class="photo-table" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="46" />
      <el-table-column label="缩略图" width="80">
        <template #default="{ row }">
          <img v-if="row.url" :src="row.url" class="table-thumb" />
          <div v-else class="table-thumb-empty">--</div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="120" show-overflow-tooltip />
      <el-table-column prop="location" label="地点" min-width="100" show-overflow-tooltip />
      <el-table-column prop="photoDate" label="拍摄日期" width="110" />
      <el-table-column label="标签" min-width="140">
        <template #default="{ row }">
          <div class="tags-cell" v-if="row.tags">
            <el-tag v-for="t in row.tags.split(',')" :key="t" size="small" type="info" class="tag-item">{{ t.trim() }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 1" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column label="可见性" width="110" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.isPublic === 1" type="success" size="small">公开</el-tag>
          <el-tag v-else type="info" size="small" class="visibility-private">
            <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
            私密
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleDownload(row)">下载</el-button>
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Batch Upload Dialog -->
    <el-dialog v-model="uploadVisible" title="批量导入照片" width="720px" destroy-on-close class="admin-dialog batch-dialog">
      <!-- Drop zone -->
      <div
        class="drop-zone"
        :class="{ 'drop-zone--active': dropActive }"
        @dragover.prevent="onDragOver"
        @dragleave.prevent="onDragLeave"
        @drop.prevent="onDrop"
        @click="triggerFileInput"
      >
        <input
          ref="fileInputRef"
          type="file"
          accept="image/*"
          multiple
          style="display:none"
          @change="onFileInputChange"
        />
        <div class="drop-zone-content">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" class="drop-icon">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
            <polyline points="17 8 12 3 7 8"/>
            <line x1="12" y1="3" x2="12" y2="15"/>
          </svg>
          <p class="drop-text">拖放图片或文件夹到此处</p>
          <p class="drop-hint">或点击此区域选择文件 · 支持 JPG / PNG / GIF / WebP</p>
        </div>
      </div>

      <!-- Selected files preview -->
      <div v-if="batchFiles.length" class="batch-preview-section">
        <div class="batch-preview-header">
          <span class="batch-count">已选 {{ batchFiles.length }} 张图片</span>
          <el-button size="small" text type="danger" @click="clearBatchFiles">清空</el-button>
        </div>
        <div class="batch-preview-grid">
          <div v-for="(item, idx) in batchFiles" :key="idx" class="batch-preview-item">
            <img :src="item.preview" class="batch-preview-img" />
            <button class="batch-preview-remove" @click.stop="removeBatchFile(idx)">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
            <div class="batch-preview-name" :title="item.file.name">{{ item.file.name }}</div>
            <div v-if="item.status === 'uploading'" class="batch-progress-bar">
              <div class="batch-progress-fill" :style="{ width: item.progress + '%' }"></div>
            </div>
            <div v-if="item.status === 'success'" class="batch-status-icon batch-status-success">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
            </div>
            <div v-if="item.status === 'error'" class="batch-status-icon batch-status-error">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </div>
          </div>
        </div>
      </div>

      <!-- Common metadata -->
      <el-form v-if="batchFiles.length" :model="batchForm" label-width="80px" class="batch-meta-form">
        <el-form-item label="拍摄地点">
          <el-input v-model="batchForm.location" placeholder="如：云南·大理（可选，应用到所有照片）" />
        </el-form-item>
        <el-form-item label="拍摄日期">
          <el-date-picker v-model="batchForm.photoDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期（可选）" style="width:100%" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="batchForm.tags" placeholder="多个标签用逗号分隔，如：风景,旅行" />
        </el-form-item>
        <el-form-item label="默认公开">
          <el-switch v-model="batchForm.isPublic" active-text="允许所有人查看" />
        </el-form-item>
      </el-form>

      <!-- Upload result summary -->
      <div v-if="uploadResult" class="batch-result">
        <p>上传完成：成功 <strong>{{ uploadResult.successCount }}</strong> 张，失败 <strong>{{ uploadResult.failCount }}</strong> 张</p>
      </div>

      <template #footer>
        <el-button @click="uploadVisible = false">{{ uploading ? '关闭' : '取消' }}</el-button>
        <el-button v-if="!uploadResult" type="primary" :loading="uploading" :disabled="!batchFiles.length" @click="handleBatchUpload">
          {{ uploading ? '上传中...' : '开始上传' }}
        </el-button>
        <el-button v-else type="primary" @click="resetBatchAndClose">完成</el-button>
      </template>
    </el-dialog>

    <!-- Edit Dialog -->
    <el-dialog v-model="editVisible" title="编辑照片" width="600px" destroy-on-close class="admin-dialog">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="标题"><el-input v-model="editForm.title" placeholder="照片标题" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="editForm.description" type="textarea" :rows="2" placeholder="照片描述" /></el-form-item>
        <el-form-item label="拍摄地点"><el-input v-model="editForm.location" placeholder="如：云南·大理" /></el-form-item>
        <el-form-item label="拍摄日期"><el-date-picker v-model="editForm.photoDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:100%" /></el-form-item>
        <el-form-item label="标签"><el-input v-model="editForm.tags" placeholder="多个标签用逗号分隔" /></el-form-item>
        <el-form-item label="排序权重"><el-input-number v-model="editForm.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="公开照片"><el-switch v-model="editForm.isPublic" active-text="允许所有人查看" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPhotos, updatePhoto, deletePhoto, batchUploadPhotos, batchUpdateVisibility, getPhotoDownloadUrl } from '@/api/album'

const photos = ref([])
const loading = ref(false)
const uploading = ref(false)
const saving = ref(false)
const tableRef = ref(null)
const selectedPhotos = ref([])

const uploadVisible = ref(false)
const dropActive = ref(false)
const fileInputRef = ref(null)
const batchFiles = ref([])
const batchForm = reactive({ location: '', photoDate: '', tags: '', isPublic: false })
const uploadResult = ref(null)

const editVisible = ref(false)
const editForm = ref({ photoId: null, title: '', description: '', location: '', photoDate: '', tags: '', sortOrder: 0, isPublic: false })

async function loadPhotos() {
  loading.value = true
  try {
    const res = await getPhotos()
    if (res.code == 200) photos.value = res.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

// --- Batch upload logic ---
function showUpload() {
  batchFiles.value = []
  batchForm.location = ''
  batchForm.photoDate = ''
  batchForm.tags = ''
  batchForm.isPublic = false
  uploadResult.value = null
  uploadVisible.value = true
}

function triggerFileInput() {
  fileInputRef.value?.click()
}

function onFileInputChange(e) {
  const files = Array.from(e.target.files || [])
  addFiles(files)
  e.target.value = ''
}

function onDragOver() { dropActive.value = true }
function onDragLeave() { dropActive.value = false }

async function onDrop(e) {
  dropActive.value = false
  const items = e.dataTransfer.items
  if (!items || !items.length) return

  const fileEntries = []
  const promises = []

  for (let i = 0; i < items.length; i++) {
    const entry = items[i].webkitGetAsEntry?.() || items[i].getAsEntry?.()
    if (entry) {
      promises.push(traverseEntry(entry, fileEntries))
    }
  }

  await Promise.all(promises)
  const files = fileEntries.map(fe => fe.file)
  addFiles(files)
}

function traverseEntry(entry, results) {
  return new Promise((resolve) => {
    if (entry.isFile) {
      entry.file(file => {
        if (file.type && file.type.startsWith('image/')) {
          results.push({ entry, file })
        }
        resolve()
      }, () => resolve())
    } else if (entry.isDirectory) {
      const reader = entry.createReader()
      const readBatch = () => {
        reader.readEntries(async (entries) => {
          if (entries.length === 0) { resolve(); return }
          const subPromises = entries.map(e => traverseEntry(e, results))
          await Promise.all(subPromises)
          readBatch()
        }, () => resolve())
      }
      readBatch()
    } else {
      resolve()
    }
  })
}

function addFiles(files) {
  const imageFiles = files.filter(f => f.type && f.type.startsWith('image/'))
  const existingNames = new Set(batchFiles.value.map(bf => bf.file.name))
  for (const file of imageFiles) {
    if (existingNames.has(file.name)) continue
    existingNames.add(file.name)
    batchFiles.value.push({
      file,
      preview: URL.createObjectURL(file),
      status: 'pending',
      progress: 0
    })
  }
}

function removeBatchFile(idx) {
  const item = batchFiles.value[idx]
  if (item.preview) URL.revokeObjectURL(item.preview)
  batchFiles.value.splice(idx, 1)
}

function clearBatchFiles() {
  batchFiles.value.forEach(item => {
    if (item.preview) URL.revokeObjectURL(item.preview)
  })
  batchFiles.value = []
}

async function handleBatchUpload() {
  if (!batchFiles.value.length) { ElMessage.warning('请先选择图片'); return }
  uploading.value = true
  uploadResult.value = null

  const batchSize = 5
  let successCount = 0
  let failCount = 0

  for (let i = 0; i < batchFiles.value.length; i += batchSize) {
    const chunk = batchFiles.value.slice(i, i + batchSize)
    chunk.forEach(item => { item.status = 'uploading'; item.progress = 0 })

    const fd = new FormData()
    chunk.forEach(item => fd.append('files', item.file))
    if (batchForm.location) fd.append('location', batchForm.location)
    if (batchForm.photoDate) fd.append('photoDate', batchForm.photoDate)
    if (batchForm.tags) fd.append('tags', batchForm.tags)
    fd.append('is_public', batchForm.isPublic ? '1' : '0')

    try {
      const progressInterval = setInterval(() => {
        chunk.forEach(item => {
          if (item.progress < 90) item.progress += Math.random() * 15
        })
      }, 200)

      const res = await batchUploadPhotos(fd)
      clearInterval(progressInterval)

      if (res.code == 200 && res.data) {
        const results = res.data.results || []
        results.forEach((r, idx) => {
          if (chunk[idx]) {
            chunk[idx].progress = 100
            chunk[idx].status = r.success ? 'success' : 'error'
            if (r.success) successCount++
            else failCount++
          }
        })
      } else {
        chunk.forEach(item => { item.status = 'error'; item.progress = 100; failCount++ })
      }
    } catch (e) {
      chunk.forEach(item => { item.status = 'error'; item.progress = 100; failCount++ })
    }
  }

  uploadResult.value = { successCount, failCount }
  uploading.value = false

  if (successCount > 0) {
    ElMessage.success('成功导入 ' + successCount + ' 张照片')
    loadPhotos()
  }
  if (failCount > 0) {
    ElMessage.warning(failCount + ' 张照片上传失败')
  }
}

function resetBatchAndClose() {
  clearBatchFiles()
  uploadResult.value = null
  uploadVisible.value = false
}

// --- Edit & Delete ---
function showEdit(row) {
  editForm.value = {
    photoId: row.photoId, title: row.title || '', description: row.description || '',
    location: row.location || '', photoDate: row.photoDate || '', tags: row.tags || '', sortOrder: row.sortOrder || 0,
    isPublic: row.isPublic === 1
  }
  editVisible.value = true
}

async function handleEdit() {
  saving.value = true
  try {
    const { photoId, ...data } = editForm.value
    data.isPublic = data.isPublic ? 1 : 0
    const res = await updatePhoto(photoId, data)
    if (res.code == 200) { ElMessage.success('保存成功'); editVisible.value = false; loadPhotos() }
    else { ElMessage.error(res.msg || res.message || '保存失败') }
  } catch (e) { ElMessage.error('保存失败') }
  finally { saving.value = false }
}

async function toggleStatus(row) {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    const res = await updatePhoto(row.photoId, { status: newStatus })
    if (res.code == 200) { row.status = newStatus; ElMessage.success(newStatus === 1 ? '已显示' : '已隐藏') }
  } catch (e) { ElMessage.error('操作失败') }
}

async function handleDownload(row) {
  try {
    const res = await getPhotoDownloadUrl(row.photoId)
    if (res.code == 200 && res.data?.url) {
      const link = document.createElement('a')
      link.href = res.data.url
      link.rel = 'noopener'
      document.body.appendChild(link)
      link.click()
      link.remove()
    }
  } catch (e) {
    if (!e.isAuthExpired) ElMessage.error('下载地址获取失败')
  }
}

function handleSelectionChange(selection) {
  selectedPhotos.value = selection
}

async function handleBatchVisibility(isPublic) {
  if (!selectedPhotos.value.length) return
  try {
    const photoIds = selectedPhotos.value.map(photo => photo.photoId)
    const res = await batchUpdateVisibility(photoIds, isPublic)
    if (res.code == 200) {
      selectedPhotos.value.forEach(photo => { photo.isPublic = isPublic })
      tableRef.value?.clearSelection()
      ElMessage.success(isPublic === 1 ? '已批量设为公开' : '已批量设为私密')
    } else {
      ElMessage.error(res.msg || res.message || '批量操作失败')
    }
  } catch (e) {
    if (!e.isAuthExpired) ElMessage.error('批量操作失败')
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除这张照片吗？此操作不可撤销。', '删除确认', { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' })
    const res = await deletePhoto(row.photoId)
    if (res.code == 200) { ElMessage.success('已删除'); loadPhotos() }
    else { ElMessage.error(res.msg || res.message || '删除失败') }
  } catch (e) { /* cancelled */ }
}

onMounted(() => { loadPhotos() })
</script>

<style scoped>
.album-admin-page {
  padding-top: 70px; min-height: 100vh; background: var(--color-bg-black);
  padding-left: 16px; padding-right: 16px; padding-bottom: 80px;
}
@media (min-width: 768px) { .album-admin-page { padding-left: 48px; padding-right: 48px; } }

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

.batch-actions {
  display: flex; align-items: center; gap: 8px;
  max-width: 1200px; margin: 0 auto 12px;
}
.selection-count { margin-right: auto; font-size: 13px; color: var(--color-gray-400); }

.photo-table {
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
.photo-table :deep(.el-table__inner-wrapper::before) { display: none; }
.photo-table :deep(.el-table__empty-text) { color: var(--color-gray-500); }

.table-thumb { width: 60px; height: 44px; object-fit: cover; border-radius: 6px; display: block; }
.table-thumb-empty {
  width: 60px; height: 44px; display: flex; align-items: center; justify-content: center;
  background: var(--color-bg-card); border-radius: 6px; font-size: 11px; color: var(--color-gray-500);
}
.tags-cell { display: flex; flex-wrap: wrap; gap: 4px; }
.tag-item { max-width: 100px; }
.visibility-private :deep(.el-tag__content) { display: inline-flex; align-items: center; gap: 4px; }

.admin-dialog :deep(.el-dialog) { background: var(--color-bg-dark) !important; }
.admin-dialog :deep(.el-dialog__title) { color: var(--color-primary-text) !important; }
.admin-dialog :deep(.el-dialog__header) { border-bottom: 1px solid rgba(255,255,255,0.06); }
.admin-dialog :deep(.el-dialog__body) { padding-top: 20px; }

/* Batch upload styles */
.batch-dialog :deep(.el-dialog) { max-height: 85vh; overflow-y: auto; }

.drop-zone {
  border: 2px dashed rgba(255,255,255,0.12);
  border-radius: 12px;
  padding: 36px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: rgba(255,255,255,0.02);
}
.drop-zone:hover { border-color: var(--color-primary); background: rgba(222,219,200,0.04); }
.drop-zone--active { border-color: var(--color-primary); background: rgba(222,219,200,0.08); transform: scale(1.01); }
.drop-icon { color: var(--color-gray-400); margin-bottom: 12px; }
.drop-text { font-size: 15px; color: var(--color-primary-text); margin-bottom: 6px; }
.drop-hint { font-size: 12px; color: var(--color-gray-500); }

.batch-preview-section { margin-top: 20px; }
.batch-preview-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 10px; }
.batch-count { font-size: 13px; color: var(--color-gray-400); }
.batch-preview-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(100px, 1fr)); gap: 10px; max-height: 240px; overflow-y: auto; padding: 2px; }
.batch-preview-item { position: relative; border-radius: 8px; overflow: hidden; background: var(--color-bg-card); aspect-ratio: 1; }
.batch-preview-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.batch-preview-remove {
  position: absolute; top: 4px; right: 4px;
  width: 20px; height: 20px; border-radius: 50%;
  background: rgba(0,0,0,0.65); border: none;
  color: #fff; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  opacity: 0; transition: opacity 0.2s;
}
.batch-preview-item:hover .batch-preview-remove { opacity: 1; }
.batch-preview-name {
  position: absolute; bottom: 0; left: 0; right: 0;
  background: rgba(0,0,0,0.6); color: #ddd;
  font-size: 10px; padding: 3px 6px;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.batch-progress-bar {
  position: absolute; bottom: 20px; left: 4px; right: 4px;
  height: 3px; background: rgba(255,255,255,0.15); border-radius: 2px; overflow: hidden;
}
.batch-progress-fill { height: 100%; background: var(--color-primary); transition: width 0.3s; border-radius: 2px; }
.batch-status-icon {
  position: absolute; top: 4px; left: 4px;
  width: 22px; height: 22px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
}
.batch-status-success { background: rgba(34,197,94,0.85); color: #fff; }
.batch-status-error { background: rgba(239,68,68,0.85); color: #fff; }

.batch-meta-form { margin-top: 20px; }

.batch-result {
  margin-top: 16px; padding: 12px 16px;
  background: rgba(255,255,255,0.04); border-radius: 8px;
  text-align: center; font-size: 14px; color: var(--color-primary-text);
}
.batch-result strong { color: var(--color-primary); }
</style>
