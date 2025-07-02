<template>
  <div class="admin-questionnaires-container">
    <div class="admin-card">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <div class="header-text">
            <h1 class="page-title">问卷管理</h1>
            <p class="page-subtitle">管理系统中的所有MBTI问卷</p>
          </div>
          <div class="header-actions">
            <el-button type="primary" @click="showCreateDialog = true">
              <el-icon><Plus /></el-icon>
              创建问卷
            </el-button>
          </div>
        </div>
      </div>

      <!-- 统计概览 -->
      <div class="stats-overview">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-content">
            <h3>{{ questionnaires.length }}</h3>
            <p>问卷总数</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><View /></el-icon>
          </div>
          <div class="stat-content">
            <h3>{{ publishedCount }}</h3>
            <p>已发布</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Edit /></el-icon>
          </div>
          <div class="stat-content">
            <h3>{{ draftCount }}</h3>
            <p>草稿</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><UserFilled /></el-icon>
          </div>
          <div class="stat-content">
            <h3>{{ totalAnswers }}</h3>
            <p>总答题数</p>
          </div>
        </div>
      </div>

      <!-- 问卷列表 -->
      <div class="questionnaires-section">
        <div class="section-header">
          <h2 class="section-title">问卷列表</h2>
          <div class="section-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索问卷..."
              prefix-icon="Search"
              style="width: 300px"
              @input="handleSearch"
            />
          </div>
        </div>

        <!-- 表格 -->
        <el-table
          :data="filteredQuestionnaires"
          style="width: 100%"
          v-loading="loading"
        >
          <el-table-column prop="questionnaireId" label="ID" width="80" />
          
          <el-table-column prop="title" label="问卷标题" min-width="200">
            <template #default="{ row }">
              <div class="questionnaire-title">
                <strong>{{ row.title }}</strong>
                <p class="description">{{ row.description }}</p>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="creatorUsername" label="创建者" width="120" />
          
          <el-table-column prop="isPublished" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.isPublished ? 'success' : 'warning'">
                {{ row.isPublished ? '已发布' : '草稿' }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="answerCount" label="答题数" width="100">
            <template #default="{ row }">
              <span class="answer-count">{{ row.answerCount }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="createdAt" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="{ row }">
              <el-button 
                text 
                type="primary" 
                @click="editQuestionnaire(row)"
                size="small"
              >
                编辑
              </el-button>
              
              <el-button 
                text 
                type="info" 
                @click="manageQuestions(row)"
                size="small"
              >
                管理问题
              </el-button>
              
              <el-button 
                text 
                :type="row.isPublished ? 'warning' : 'success'"
                @click="togglePublishStatus(row)"
                size="small"
              >
                {{ row.isPublished ? '取消发布' : '发布' }}
              </el-button>
              
              <el-button 
                text 
                type="info" 
                @click="viewDetails(row)"
                size="small"
              >
                详情
              </el-button>
              
              <el-popconfirm
                title="确定要删除这个问卷吗？"
                @confirm="deleteQuestionnaire(row.questionnaireId)"
              >
                <template #reference>
                  <el-button 
                    text 
                    type="danger" 
                    size="small"
                  >
                    删除
                  </el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-section">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            :total="totalItems"
            layout="total, sizes, prev, pager, next, jumper"
            :background="true"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 创建问卷弹窗 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建新问卷"
      width="600px"
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="80px"
      >
        <el-form-item label="问卷标题" prop="title">
          <el-input 
            v-model="createForm.title" 
            placeholder="请输入问卷标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="问卷描述" prop="description">
          <el-input 
            v-model="createForm.description" 
            type="textarea"
            :rows="4"
            placeholder="请输入问卷描述"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="createQuestionnaire"
            :loading="creating"
          >
            创建
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑问卷弹窗 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑问卷"
      width="600px"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="80px"
      >
        <el-form-item label="问卷标题" prop="title">
          <el-input 
            v-model="editForm.title" 
            placeholder="请输入问卷标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="问卷描述" prop="description">
          <el-input 
            v-model="editForm.description" 
            type="textarea"
            :rows="4"
            placeholder="请输入问卷描述"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="updateQuestionnaire"
            :loading="editing"
          >
            更新
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 问卷详情弹窗 -->
    <el-dialog
      v-model="showDetailsDialog"
      :title="`问卷详情 - ${selectedQuestionnaire?.title}`"
      width="900px"
    >
      <div v-if="selectedQuestionnaire" class="questionnaire-details">
        <div class="details-section">
          <h3>基本信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <label>标题：</label>
              <span>{{ selectedQuestionnaire.title }}</span>
            </div>
            <div class="info-item">
              <label>描述：</label>
              <span>{{ selectedQuestionnaire.description }}</span>
            </div>
            <div class="info-item">
              <label>创建者：</label>
              <span>{{ selectedQuestionnaire.creatorUsername }}</span>
            </div>
            <div class="info-item">
              <label>状态：</label>
              <el-tag :type="selectedQuestionnaire.isPublished ? 'success' : 'warning'">
                {{ selectedQuestionnaire.isPublished ? '已发布' : '草稿' }}
              </el-tag>
            </div>
            <div class="info-item">
              <label>答题数：</label>
              <span>{{ selectedQuestionnaire.answerCount }}</span>
            </div>
            <div class="info-item">
              <label>创建时间：</label>
              <span>{{ formatDate(selectedQuestionnaire.createdAt) }}</span>
            </div>
          </div>
        </div>

        <!-- 这里可以扩展显示问卷的问题列表 -->
        <div class="details-section">
          <h3>操作日志</h3>
          <p class="placeholder-text">问卷操作历史记录将在此显示...</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Document,
  View,
  Edit,
  UserFilled,
  Search
} from '@element-plus/icons-vue'
import { questionnaireApi } from '@/api'
import type { Questionnaire, CreateQuestionnaireRequest } from '@/api/types'

const router = useRouter()
const route = useRoute()

// 响应式状态
const questionnaires = ref<Questionnaire[]>([])
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)

// 弹窗状态
const showCreateDialog = ref(false)
const showEditDialog = ref(false)
const showDetailsDialog = ref(false)
const creating = ref(false)
const editing = ref(false)
const selectedQuestionnaire = ref<Questionnaire | null>(null)

// 表单相关
const createFormRef = ref<any>(null)
const editFormRef = ref<any>(null)
const createForm = ref({
  title: '',
  description: ''
})

const editForm = ref({
  title: '',
  description: ''
})

const createRules = {
  title: [
    { required: true, message: '请输入问卷标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度应在2-100个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入问卷描述', trigger: 'blur' },
    { min: 5, max: 500, message: '描述长度应在5-500个字符', trigger: 'blur' }
  ]
}

const editRules = {
  title: [
    { required: true, message: '请输入问卷标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度应在2-100个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入问卷描述', trigger: 'blur' },
    { min: 5, max: 500, message: '描述长度应在5-500个字符', trigger: 'blur' }
  ]
}

// 计算属性
const publishedCount = computed(() => {
  return questionnaires.value.filter(q => q.isPublished).length
})

const draftCount = computed(() => {
  return questionnaires.value.filter(q => !q.isPublished).length
})

const totalAnswers = computed(() => {
  return questionnaires.value.reduce((sum, q) => sum + q.answerCount, 0)
})

const filteredQuestionnaires = computed(() => {
  let filtered = questionnaires.value
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(q => 
      q.title.toLowerCase().includes(keyword) ||
      q.description.toLowerCase().includes(keyword) ||
      q.creatorUsername.toLowerCase().includes(keyword)
    )
  }
  
  // 分页
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filtered.slice(start, end)
})

// 生命周期
onMounted(async () => {
  await fetchQuestionnaires()
})

// 方法
const fetchQuestionnaires = async () => {
  try {
    loading.value = true
    const response = await questionnaireApi.getQuestionnaireList({
      page: 0,
      size: 1000 // 获取所有问卷用于前端分页和搜索
    })
    questionnaires.value = response.content
    totalItems.value = response.totalElements
  } catch (error: any) {
    console.error('获取问卷列表失败:', error)
    ElMessage.error('获取问卷列表失败')
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const handleSearch = () => {
  currentPage.value = 1
  updatePagination()
}

const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize
  currentPage.value = 1
  updatePagination()
}

const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage
  updatePagination()
}

const updatePagination = () => {
  const filtered = questionnaires.value.filter(q => {
    if (!searchKeyword.value) return true
    const keyword = searchKeyword.value.toLowerCase()
    return q.title.toLowerCase().includes(keyword) ||
           q.description.toLowerCase().includes(keyword) ||
           q.creatorUsername.toLowerCase().includes(keyword)
  })
  totalItems.value = filtered.length
}

const createQuestionnaire = async () => {
  try {
    await createFormRef.value?.validate()
    
    creating.value = true
    
    const createData: CreateQuestionnaireRequest = {
      title: createForm.value.title,
      description: createForm.value.description
    }
    
    await questionnaireApi.createQuestionnaire(createData)
    
    ElMessage.success('问卷创建成功')
    showCreateDialog.value = false
    createForm.value = { title: '', description: '' }
    
    // 刷新列表
    await fetchQuestionnaires()
  } catch (error: any) {
    console.error('创建问卷失败:', error)
    ElMessage.error(error.message || '创建失败')
  } finally {
    creating.value = false
  }
}

const editQuestionnaire = (questionnaire: Questionnaire) => {
  selectedQuestionnaire.value = questionnaire
  editForm.value = {
    title: questionnaire.title,
    description: questionnaire.description
  }
  showEditDialog.value = true
}

const updateQuestionnaire = async () => {
  try {
    await editFormRef.value?.validate()
    
    editing.value = true
    
    const updateData = {
      title: editForm.value.title,
      description: editForm.value.description
    }
    
    const updatedQuestionnaire = await questionnaireApi.updateQuestionnaire(
      selectedQuestionnaire.value!.questionnaireId,
      updateData
    )
    
    // 更新本地数据
    const index = questionnaires.value.findIndex(q => q.questionnaireId === selectedQuestionnaire.value!.questionnaireId)
    if (index !== -1) {
      questionnaires.value[index] = updatedQuestionnaire
    }
    
    ElMessage.success('问卷更新成功')
    showEditDialog.value = false
    editForm.value = { title: '', description: '' }
    selectedQuestionnaire.value = null
  } catch (error: any) {
    console.error('更新问卷失败:', error)
    ElMessage.error(error.message || '更新失败')
  } finally {
    editing.value = false
  }
}

const togglePublishStatus = async (questionnaire: Questionnaire) => {
  try {
    const action = questionnaire.isPublished ? '取消发布' : '发布'
    await ElMessageBox.confirm(
      `确定要${action}问卷"${questionnaire.title}"吗？`,
      `${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用API切换发布状态
    const updatedQuestionnaire = await questionnaireApi.updateQuestionnaireStatus(
      questionnaire.questionnaireId, 
      !questionnaire.isPublished
    )
    
    // 更新本地状态
    const index = questionnaires.value.findIndex(q => q.questionnaireId === questionnaire.questionnaireId)
    if (index !== -1) {
      questionnaires.value[index] = updatedQuestionnaire
    }
    
    ElMessage.success(`${action}成功`)
  } catch (error: any) {
    if (error.message && error.message !== 'cancel') {
      console.error(`${questionnaire.isPublished ? '取消发布' : '发布'}失败:`, error)
      ElMessage.error(error.message || `${questionnaire.isPublished ? '取消发布' : '发布'}失败`)
    }
  }
}

const manageQuestions = (questionnaire: Questionnaire) => {
  // 跳转到问题管理页面，传递问卷ID
  router.push({
    path: `/admin/questionnaires/${questionnaire.questionnaireId}/questions`,
    query: {
      title: questionnaire.title
    }
  })
}

const viewDetails = (questionnaire: Questionnaire) => {
  selectedQuestionnaire.value = questionnaire
  showDetailsDialog.value = true
}

const deleteQuestionnaire = async (questionnaireId: number) => {
  try {
    // 调用删除API
    await questionnaireApi.deleteQuestionnaire(questionnaireId)
    
    // 从本地列表中移除
    questionnaires.value = questionnaires.value.filter(q => q.questionnaireId !== questionnaireId)
    
    ElMessage.success('删除成功')
    updatePagination()
  } catch (error: any) {
    console.error('删除问卷失败:', error)
    ElMessage.error(error.message || '删除失败')
  }
}
</script>

<style scoped>
.admin-questionnaires-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 2rem;
  background-color: var(--color-background-soft);
}

.admin-card {
  max-width: 120rem;
  width: 100%;
  background-color: var(--color-background);
  border-radius: 1.6rem;
  box-shadow: 0 0.8rem 3.2rem rgba(0, 0, 0, 0.1);
  padding: 3rem;
  border: 1px solid var(--color-border);
}

/* 页面头部 */
.page-header {
  margin-bottom: 3rem;
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 2rem;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 2rem;
}

.page-title {
  font-size: 2.8rem;
  color: var(--color-text-primary);
  margin: 0 0 0.5rem 0;
  font-weight: 600;
}

.page-subtitle {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 1.4rem;
}

.header-actions {
  flex-shrink: 0;
}

/* 统计概览 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(20rem, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.stat-card {
  background-color: var(--color-background-soft);
  border-radius: 1.2rem;
  padding: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  border: 1px solid var(--color-border);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-0.2rem);
  box-shadow: 0 0.4rem 1.2rem rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 5rem;
  height: 5rem;
  border-radius: 1rem;
  background-color: var(--primary-teal-light);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon .el-icon {
  font-size: 2rem;
  color: var(--primary-teal);
}

.stat-content h3 {
  margin: 0 0 0.5rem 0;
  font-size: 2rem;
  color: var(--color-text-primary);
  font-weight: bold;
}

.stat-content p {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 1.3rem;
}

/* 问卷列表区域 */
.questionnaires-section {
  margin-top: 3rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.section-title {
  font-size: 2rem;
  color: var(--color-text-primary);
  margin: 0;
}

.section-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

/* 表格样式 */
.questionnaire-title strong {
  color: var(--color-text-primary);
  font-size: 1.4rem;
}

.questionnaire-title .description {
  margin: 0.5rem 0 0 0;
  color: var(--color-text-secondary);
  font-size: 1.2rem;
  line-height: 1.4;
}

.answer-count {
  font-weight: 600;
  color: var(--primary-teal);
}

/* 分页 */
.pagination-section {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
}

/* 弹窗内容 */
.questionnaire-details {
  max-height: 60vh;
  overflow-y: auto;
}

.details-section {
  margin-bottom: 2rem;
}

.details-section h3 {
  margin: 0 0 1rem 0;
  color: var(--color-text-primary);
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 0.5rem;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(25rem, 1fr));
  gap: 1rem;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.info-item label {
  font-weight: 600;
  color: var(--color-text-secondary);
  min-width: 8rem;
}

.placeholder-text {
  color: var(--color-text-secondary);
  font-style: italic;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-questionnaires-container {
    padding: 1rem;
  }
  
  .admin-card {
    padding: 2rem;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .stats-overview {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .section-header {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .stats-overview {
    grid-template-columns: 1fr;
  }
  
  .page-title {
    font-size: 2.2rem;
  }
}
</style>
