<template>
  <div class="plan-admin-page">
    <section class="admin-header">
      <div class="header-left">
        <router-link to="/plan" class="back-link">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>
          返回计划
        </router-link>
        <h1 class="admin-title">计划管理</h1>
      </div>
      <el-button type="primary" @click="showCreate">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right:4px"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        新建计划
      </el-button>
    </section>

    <el-table :data="plans" v-loading="loading" class="admin-table">
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <img v-if="row.coverUrl" :src="row.coverUrl" class="table-thumb" />
          <div v-else class="table-thumb-empty">--</div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="140" show-overflow-tooltip />
      <el-table-column prop="planDate" label="计划时间" width="120" />
      <el-table-column label="状态" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)" size="small">{{ statusTextMap[row.status] || row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
      <el-table-column label="操作" width="140" align="center" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Create/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEditing ? '编辑计划' : '新建计划'" width="600px" destroy-on-close class="admin-dialog">
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="计划标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="计划描述" />
        </el-form-item>
        <el-form-item label="封面图">
          <ImageUploader v-model="form.coverFile" :initial-url="form.coverUrl" />
        </el-form-item>
        <el-form-item label="计划时间">
          <el-input v-model="form.planDate" placeholder="如：2026年8月" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="即将到来" value="upcoming" />
            <el-option label="规划中" value="planning" />
            <el-option label="愿望清单" value="dreaming" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序权重">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
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
import { getPlans, createPlan, updatePlan, deletePlan } from '@/api/plan'
import ImageUploader from '@/components/ImageUploader.vue'

const plans = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const form = reactive({
  title: '', description: '', coverUrl: '', coverFile: null,
  planDate: '', status: 'planning', sortOrder: 0
})

const statusTextMap = {
  upcoming: '即将到来',
  planning: '规划中',
  dreaming: '愿望清单',
  completed: '已完成'
}

function statusTagType(status) {
  if (status === 'upcoming') return 'warning'
  if (status === 'completed') return 'success'
  return 'info'
}

async function loadPlans() {
  loading.value = true
  try {
    const res = await getPlans()
    if (res.code == 200) plans.value = res.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

function resetForm() {
  form.title = ''; form.description = ''; form.coverUrl = ''; form.coverFile = null
  form.planDate = ''; form.status = 'planning'; form.sortOrder = 0
}

function showCreate() {
  resetForm(); isEditing.value = false; editingId.value = null; dialogVisible.value = true
}

function showEdit(row) {
  isEditing.value = true; editingId.value = row.planId
  form.title = row.title || ''; form.description = row.description || ''
  form.coverUrl = row.coverUrl || ''; form.coverFile = null
  form.planDate = row.planDate || ''; form.status = row.status || 'planning'
  form.sortOrder = row.sortOrder || 0
  dialogVisible.value = true
}

async function handleSave() {
  if (!form.title.trim()) { ElMessage.warning('请输入标题'); return }
  saving.value = true
  try {
    const fd = new FormData()
    fd.append('title', form.title)
    if (form.description) fd.append('description', form.description)
    if (form.planDate) fd.append('planDate', form.planDate)
    fd.append('status', form.status)
    fd.append('sortOrder', String(form.sortOrder))
    if (form.coverFile) fd.append('coverFile', form.coverFile)

    let res
    if (isEditing.value) {
      res = await updatePlan(editingId.value, fd)
    } else {
      res = await createPlan(fd)
    }
    if (res.code == 200) {
      ElMessage.success(isEditing.value ? '保存成功' : '创建成功')
      dialogVisible.value = false; loadPlans()
    } else { ElMessage.error(res.msg || res.message || '操作失败') }
  } catch (e) { ElMessage.error('操作失败') }
  finally { saving.value = false }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除这个计划吗？此操作不可撤销。', '删除确认', { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' })
    const res = await deletePlan(row.planId)
    if (res.code == 200) { ElMessage.success('已删除'); loadPlans() }
    else { ElMessage.error(res.msg || res.message || '删除失败') }
  } catch (e) { /* cancelled */ }
}

onMounted(() => { loadPlans() })
</script>

<style scoped>
.plan-admin-page {
  padding-top: 70px; min-height: 100vh; background: var(--color-bg-black);
  padding-left: 16px; padding-right: 16px; padding-bottom: 80px;
}
@media (min-width: 768px) { .plan-admin-page { padding-left: 48px; padding-right: 48px; } }

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

.admin-dialog :deep(.el-dialog) { background: var(--color-bg-dark) !important; }
.admin-dialog :deep(.el-dialog__title) { color: var(--color-primary-text) !important; }
.admin-dialog :deep(.el-dialog__header) { border-bottom: 1px solid rgba(255,255,255,0.06); }
.admin-dialog :deep(.el-dialog__body) { padding-top: 20px; }
</style>

