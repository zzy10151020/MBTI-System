<template>
  <div class="admin-questions-container">
    <div class="admin-card">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <div class="header-text">
            <h1 class="page-title">
              问题管理
              <el-tag v-if="questionnaireTitle" type="info" class="questionnaire-tag">
                {{ questionnaireTitle }}
              </el-tag>
            </h1>
            <p class="page-subtitle">管理问卷中的问题和选项</p>
          </div>
          <div class="header-actions">
            <el-button @click="$router.back()">
              <el-icon><ArrowLeft /></el-icon>
              返回问卷列表
            </el-button>
            <el-button type="primary" @click="showCreateDialog = true">
              <el-icon><Plus /></el-icon>
              添加问题
            </el-button>
          </div>
        </div>
      </div>

      <!-- 问题列表 -->
      <div class="questions-section">
        <el-table
          :data="questions"
          style="width: 100%"
          v-loading="loading"
          row-key="questionId"
        >
          <el-table-column prop="questionOrder" label="顺序" width="80" />
          
          <el-table-column prop="content" label="问题内容" min-width="300">
            <template #default="{ row }">
              <div class="question-content">
                <p class="question-text">{{ row.content }}</p>
                <el-tag size="small" :type="getDimensionTagType(row.dimension)">
                  {{ getDimensionLabel(row.dimension) }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="选项" min-width="400">
            <template #default="{ row }">
              <div class="options-list">
                <div 
                  v-for="option in row.options" 
                  :key="option.optionId"
                  class="option-item"
                >
                  <span class="option-content">{{ option.content }}</span>
                  <el-tag 
                    size="small" 
                    :type="option.score > 0 ? 'success' : 'warning'"
                    class="score-tag"
                  >
                    {{ option.score > 0 ? '+' : '' }}{{ option.score }}
                  </el-tag>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button 
                text 
                type="primary" 
                @click="editQuestion(row)"
                size="small"
              >
                编辑
              </el-button>
              
              <el-popconfirm
                title="确定要删除这个问题吗？"
                @confirm="deleteQuestion(row.questionId)"
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
      </div>
    </div>

    <!-- 创建/编辑问题弹窗 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="isEditing ? '编辑问题' : '创建问题'"
      width="800px"
    >
      <el-form
        ref="questionFormRef"
        :model="questionForm"
        :rules="questionRules"
        label-width="100px"
      >
        <el-form-item label="问题内容" prop="content">
          <el-input 
            v-model="questionForm.content" 
            type="textarea"
            :rows="3"
            placeholder="请输入问题内容"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="MBTI维度" prop="dimension">
          <el-select v-model="questionForm.dimension" placeholder="请选择MBTI维度">
            <el-option label="外向性/内向性 (E/I)" value="E/I" />
            <el-option label="感觉/直觉 (S/N)" value="S/N" />
            <el-option label="思考/情感 (T/F)" value="T/F" />
            <el-option label="判断/知觉 (J/P)" value="J/P" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="问题顺序" prop="questionOrder">
          <el-input-number 
            v-model="questionForm.questionOrder" 
            :min="1" 
            :max="100"
            placeholder="问题显示顺序"
          />
        </el-form-item>
        
        <el-divider>选项设置</el-divider>
        
        <div class="options-form">
          <div 
            v-for="(option, index) in questionForm.options" 
            :key="index"
            class="option-form-item"
          >
            <el-form-item 
              :label="`选项 ${String.fromCharCode(65 + index)}`"
              :prop="`options.${index}.content`"
              :rules="[
                { required: true, message: '请输入选项内容', trigger: 'blur' },
                { min: 1, max: 500, message: '选项内容长度应在1-500个字符', trigger: 'blur' }
              ]"
            >
              <div class="option-input-group">
                <el-input 
                  v-model="option.content" 
                  placeholder="选项内容"
                  class="option-content-input"
                />
                <el-input-number 
                  v-model="option.score" 
                  :min="-1" 
                  :max="1"
                  placeholder="分数"
                  class="option-score-input"
                />
                <el-button 
                  v-if="questionForm.options.length > 2"
                  text 
                  type="danger" 
                  @click="removeOption(index)"
                  class="remove-option-btn"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </el-form-item>
          </div>
          
          <el-button 
            v-if="questionForm.options.length < 6"
            text 
            type="primary" 
            @click="addOption"
            class="add-option-btn"
          >
            <el-icon><Plus /></el-icon>
            添加选项
          </el-button>
        </div>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resetForm">取消</el-button>
          <el-button 
            type="primary" 
            @click="saveQuestion"
            :loading="saving"
          >
            {{ isEditing ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Plus,
  Delete
} from '@element-plus/icons-vue'
import { questionApi } from '@/api'
import type { Question, CreateQuestionRequest } from '@/api/question'

const router = useRouter()
const route = useRoute()

// 获取路由参数
const questionnaireId = computed(() => parseInt(route.params.id as string))
const questionnaireTitle = computed(() => route.query.title as string || '')

// 响应式状态
const questions = ref<Question[]>([])
const loading = ref(false)
const saving = ref(false)

// 弹窗状态
const showCreateDialog = ref(false)
const isEditing = ref(false)
const editingQuestionId = ref<number | null>(null)

// 表单相关
const questionFormRef = ref<any>(null)
const questionForm = ref<CreateQuestionRequest>({
  content: '',
  dimension: '',
  questionOrder: 1,
  options: [
    { content: '', score: 1 },
    { content: '', score: -1 }
  ]
})

const questionRules = {
  content: [
    { required: true, message: '请输入问题内容', trigger: 'blur' },
    { min: 5, max: 1000, message: '问题内容长度应在5-1000个字符', trigger: 'blur' }
  ],
  dimension: [
    { required: true, message: '请选择MBTI维度', trigger: 'change' }
  ],
  questionOrder: [
    { required: true, message: '请输入问题顺序', trigger: 'blur' }
  ]
}

// 生命周期
onMounted(async () => {
  await fetchQuestions()
})

// 方法
const fetchQuestions = async () => {
  try {
    loading.value = true
    questions.value = await questionApi.getQuestionsByQuestionnaireId(questionnaireId.value)
  } catch (error: any) {
    console.error('获取问题列表失败:', error)
    ElMessage.error('获取问题列表失败')
  } finally {
    loading.value = false
  }
}

const getDimensionLabel = (dimension: string) => {
  const labels: Record<string, string> = {
    'E/I': '外向性/内向性',
    'S/N': '感觉/直觉',
    'T/F': '思考/情感',
    'J/P': '判断/知觉'
  }
  return labels[dimension] || dimension
}

const getDimensionTagType = (dimension: string): "primary" | "success" | "warning" | "danger" | "info" => {
  const types: Record<string, "primary" | "success" | "warning" | "danger" | "info"> = {
    'E/I': 'primary',
    'S/N': 'success',
    'T/F': 'warning',
    'J/P': 'danger'
  }
  return types[dimension] || 'info'
}

const addOption = () => {
  questionForm.value.options.push({ content: '', score: 0 })
}

const removeOption = (index: number) => {
  questionForm.value.options.splice(index, 1)
}

const editQuestion = (question: Question) => {
  isEditing.value = true
  editingQuestionId.value = question.questionId!
  questionForm.value = {
    content: question.content,
    dimension: question.dimension,
    questionOrder: question.questionOrder,
    options: question.options.map(opt => ({
      content: opt.content,
      score: opt.score
    }))
  }
  showCreateDialog.value = true
}

const saveQuestion = async () => {
  try {
    await questionFormRef.value?.validate()
    
    saving.value = true
    
    if (isEditing.value && editingQuestionId.value) {
      // 更新问题
      await questionApi.updateQuestion(questionnaireId.value, editingQuestionId.value, questionForm.value)
      ElMessage.success('问题更新成功')
    } else {
      // 创建问题
      await questionApi.createQuestion(questionnaireId.value, questionForm.value)
      ElMessage.success('问题创建成功')
    }
    
    resetForm()
    await fetchQuestions()
  } catch (error: any) {
    console.error('保存问题失败:', error)
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const deleteQuestion = async (questionId: number) => {
  try {
    await questionApi.deleteQuestion(questionnaireId.value, questionId)
    ElMessage.success('删除成功')
    await fetchQuestions()
  } catch (error: any) {
    console.error('删除问题失败:', error)
    ElMessage.error(error.message || '删除失败')
  }
}

const resetForm = () => {
  showCreateDialog.value = false
  isEditing.value = false
  editingQuestionId.value = null
  questionForm.value = {
    content: '',
    dimension: '',
    questionOrder: (questions.value.length || 0) + 1,
    options: [
      { content: '', score: 1 },
      { content: '', score: -1 }
    ]
  }
  questionFormRef.value?.resetFields()
}
</script>

<style scoped>
.admin-questions-container {
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
  display: flex;
  align-items: center;
  gap: 1rem;
}

.questionnaire-tag {
  font-size: 1.2rem;
}

.page-subtitle {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 1.4rem;
}

.header-actions {
  flex-shrink: 0;
  display: flex;
  gap: 1rem;
}

/* 问题内容 */
.question-content {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.question-text {
  margin: 0;
  color: var(--color-text-primary);
  font-size: 1.4rem;
  line-height: 1.5;
}

/* 选项列表 */
.options-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.option-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.5rem 1rem;
  background-color: var(--color-background-soft);
  border-radius: 0.6rem;
  border: 1px solid var(--color-border);
}

.option-content {
  flex: 1;
  color: var(--color-text-primary);
  font-size: 1.3rem;
}

.score-tag {
  font-size: 1.1rem;
  margin-left: 0.5rem;
}

/* 表单样式 */
.options-form {
  border: 1px solid var(--color-border);
  border-radius: 0.8rem;
  padding: 1.5rem;
  background-color: var(--color-background-soft);
}

.option-form-item {
  margin-bottom: 1rem;
}

.option-input-group {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.option-content-input {
  flex: 1;
}

.option-score-input {
  width: 120px;
}

.remove-option-btn {
  flex-shrink: 0;
}

.add-option-btn {
  width: 100%;
  margin-top: 1rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-questions-container {
    padding: 1rem;
  }
  
  .admin-card {
    padding: 2rem;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .option-input-group {
    flex-direction: column;
    align-items: stretch;
  }
  
  .option-score-input {
    width: 100%;
  }
}
</style>
