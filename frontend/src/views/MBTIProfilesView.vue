<template>
  <div class="mbti-profiles-container">
    <div class="profiles-card">
      <div class="profiles-header">
        <h1 class="profiles-title">MBTI 16型人格介绍</h1>
        <p class="profiles-subtitle">了解每一种性格类型，发现你的独特之处</p>
      </div>
          <div class="filter-section">
            <div class="dimension-filters">
              <div class="dimension-group">
                <span class="dimension-label">能量来源</span>
                <el-radio-group v-model="filterE" size="small" class="dimension-radio">
                  <el-radio-button :label="''">全部</el-radio-button>
                  <el-radio-button label="E">E</el-radio-button>
                  <el-radio-button label="I">I</el-radio-button>
                </el-radio-group>
              </div>
              <div class="dimension-group">
                <span class="dimension-label">认知方式</span>
                <el-radio-group v-model="filterS" size="small" class="dimension-radio">
                  <el-radio-button :label="''">全部</el-radio-button>
                  <el-radio-button label="S">S</el-radio-button>
                  <el-radio-button label="N">N</el-radio-button>
                </el-radio-group>
              </div>
              <div class="dimension-group">
                <span class="dimension-label">决策方式</span>
                <el-radio-group v-model="filterT" size="small" class="dimension-radio">
                  <el-radio-button :label="''">全部</el-radio-button>
                  <el-radio-button label="T">T</el-radio-button>
                  <el-radio-button label="F">F</el-radio-button>
                </el-radio-group>
              </div>
              <div class="dimension-group">
                <span class="dimension-label">生活态度</span>
                <el-radio-group v-model="filterJ" size="small" class="dimension-radio">
                  <el-radio-button :label="''">全部</el-radio-button>
                  <el-radio-button label="J">J</el-radio-button>
                  <el-radio-button label="P">P</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </div>
      <div class="profile-content-section">
        <transition name="fade" mode="out-in">
          <div v-if="currentProfile" :key="currentProfile.type" class="profile-page profile-flex">
            <div class="profile-img-wrap">
              <img :src="getProfileImage(currentProfile.type)" :alt="currentProfile.type" class="profile-img-large" @error="e => { if (e.target) (e.target as HTMLImageElement).src = '/MBTI_PERSONALITY/default.png' }" />
            </div>
            <div class="profile-info-wrap">
              <div class="profile-info-header">
                <div class="profile-type">{{ currentProfile.type }}</div>
                <div class="profile-name">{{ mbtiDescription.name || currentProfile.name }}</div>
              </div>
              <div class="profile-desc">{{ mbtiDescription.description || currentProfile.description }}</div>
              <div class="profile-tags">
                <el-tag v-for="tag in currentProfile.tags" :key="tag" type="info" class="profile-tag">{{ tag }}</el-tag>
              </div>
              <div v-if="mbtiDescription.strengths && mbtiDescription.strengths.length" class="profile-section">
                <span class="profile-section-title">优势：</span>
                <span class="profile-section-list">{{ mbtiDescription.strengths.join('、') }}</span>
              </div>
              <div v-if="mbtiDescription.weaknesses && mbtiDescription.weaknesses.length" class="profile-section">
                <span class="profile-section-title">劣势：</span>
                <span class="profile-section-list">{{ mbtiDescription.weaknesses.join('、') }}</span>
              </div>
              <div v-if="mbtiDescription.careers && mbtiDescription.careers.length" class="profile-section">
                <span class="profile-section-title">适合职业：</span>
                <span class="profile-section-list">{{ mbtiDescription.careers.join('、') }}</span>
              </div>
            </div>
          </div>
        </transition>
      </div>
      <div class="profile-pagination-section">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :total="filteredProfiles.length"
          :page-size="1"
          :current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

import { ref, computed, watch } from 'vue'
import { useTestStore } from '@/stores/testStore'
const testStore = useTestStore()
const filterE = ref('')
const filterS = ref('')
const filterT = ref('')
const filterJ = ref('')
const currentPage = ref(1)

const mbtiProfiles = [
  { type: 'ISTJ', name: '责任感强的现实主义者', description: '严谨、务实、可靠，注重细节和规则，喜欢有序的生活。', tags: ['内向(I)', '实感(S)', '思考(T)', '判断(J)'] },
  { type: 'ISFJ', name: '忠诚的守护者', description: '温和、细心、乐于助人，重视传统和安全感。', tags: ['内向(I)', '实感(S)', '情感(F)', '判断(J)'] },
  { type: 'INFJ', name: '富有洞察力的理想主义者', description: '富有同理心，理想主义，善于洞察他人需求。', tags: ['内向(I)', '直觉(N)', '情感(F)', '判断(J)'] },
  { type: 'INTJ', name: '战略家', description: '独立、理性、善于规划，喜欢制定长远目标。', tags: ['内向(I)', '直觉(N)', '思考(T)', '判断(J)'] },
  { type: 'ISTP', name: '冷静的实干家', description: '务实、灵活、喜欢动手解决问题，适应力强。', tags: ['内向(I)', '实感(S)', '思考(T)', '感知(P)'] },
  { type: 'ISFP', name: '温柔的艺术家', description: '安静、敏感、追求和谐，喜欢自由和美感。', tags: ['内向(I)', '实感(S)', '情感(F)', '感知(P)'] },
  { type: 'INFP', name: '忠于自我的理想主义者', description: '理想主义、富有想象力，重视个人价值观。', tags: ['内向(I)', '直觉(N)', '情感(F)', '感知(P)'] },
  { type: 'INTP', name: '逻辑学家', description: '理性、好奇、善于分析，喜欢探索理论和概念。', tags: ['内向(I)', '直觉(N)', '思考(T)', '感知(P)'] },
  { type: 'ESTP', name: '活力四射的实干家', description: '果断、现实、喜欢冒险，善于应对突发状况。', tags: ['外向(E)', '实感(S)', '思考(T)', '感知(P)'] },
  { type: 'ESFP', name: '热情的表演者', description: '外向、乐观、喜欢社交，享受当下生活。', tags: ['外向(E)', '实感(S)', '情感(F)', '感知(P)'] },
  { type: 'ENFP', name: '充满活力的激励者', description: '热情、富有想象力，善于激励他人。', tags: ['外向(E)', '直觉(N)', '情感(F)', '感知(P)'] },
  { type: 'ENTP', name: '机智的辩论家', description: '聪明、好奇、善于辩论，喜欢创新。', tags: ['外向(E)', '直觉(N)', '思考(T)', '感知(P)'] },
  { type: 'ESTJ', name: '高效的管理者', description: '务实、果断、善于组织和管理，注重效率。', tags: ['外向(E)', '实感(S)', '思考(T)', '判断(J)'] },
  { type: 'ESFJ', name: '热心的照顾者', description: '友好、负责任，乐于助人，重视团队合作。', tags: ['外向(E)', '实感(S)', '情感(F)', '判断(J)'] },
  { type: 'ENFJ', name: '富有魅力的领袖', description: '善于沟通、关心他人，具有领导力。', tags: ['外向(E)', '直觉(N)', '情感(F)', '判断(J)'] },
  { type: 'ENTJ', name: '果断的指挥官', description: '自信、理性、善于决策和领导。', tags: ['外向(E)', '直觉(N)', '思考(T)', '判断(J)'] }
]

const filteredProfiles = computed(() => {
  return mbtiProfiles.filter(p => {
    if (filterE.value && p.type[0] !== filterE.value) return false;
    if (filterS.value && p.type[1] !== filterS.value) return false;
    if (filterT.value && p.type[2] !== filterT.value) return false;
    if (filterJ.value && p.type[3] !== filterJ.value) return false;
    return true;
  })
})

const currentProfile = computed(() => {
  return filteredProfiles.value[currentPage.value - 1] || null
})

const mbtiDescription = computed(() => {
  if (!currentProfile.value) return {}
  return testStore.getMbtiDescription(currentProfile.value.type) || {}
})

const handlePageChange = (page: number) => {
  currentPage.value = page
}

function getProfileImage(type: string): string {
  // 新图片直接用type全称
  return `/MBTI_PERSONALITY/${type}.png`
}
// 任一维度筛选变化时重置分页
watch([filterE, filterS, filterT, filterJ], () => {
  currentPage.value = 1
})
</script>

<style scoped>
.mbti-profiles-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 2rem;
  background-color: var(--color-background-soft);
}

.profiles-card {
  max-width: 65rem;
  width: 100%;
  background-color: var(--color-background);
  border-radius: 1.6rem;
  box-shadow: 0 0.8rem 3.2rem rgba(0, 0, 0, 0.1);
  padding: 4rem;
  border: 1px solid var(--color-border);
}

.profiles-header {
  text-align: center;
  margin-bottom: 2.5rem;
}

.profiles-title {
  font-size: 2.2rem;
  color: var(--color-text-primary);
  font-weight: 600;
  margin: 0 0 0.5rem 0;
}

.profiles-subtitle {
  color: var(--color-text-secondary);
  font-size: 1.3rem;
  margin: 0;
}

.profile-content-section {
  min-height: 28rem;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}
.profile-page.profile-flex {
  width: 100%;
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  background: linear-gradient(135deg, var(--primary-teal-light), #fff 80%);
  border-radius: 1.2rem;
  box-shadow: 0 0.4rem 1.2rem rgba(32, 178, 170, 0.08);
  padding: 2.5rem 2rem 2rem 2rem;
  min-height: 24rem;
  gap: 2.5rem;
}
.profile-img-wrap {
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.profile-img-large {
  width: 12rem;
  height: 12rem;
  object-fit: contain;
  border-radius: 1.2rem;
  box-shadow: 0 0.2rem 0.8rem rgba(32,178,170,0.10);
  margin-bottom: 0.5rem;
}
.profile-info-wrap {
  flex: 1 1 0;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  min-width: 0;
}
.profile-info-header {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  margin-bottom: 1.2rem;
  gap: 0.5rem;
}
.profile-type {
  font-size: 2.2rem;
  font-weight: 700;
  color: var(--primary-teal);
}
.profile-name {
  font-size: 1.3rem;
  color: var(--color-text-secondary);
}
.profile-desc {
  font-size: 1.15rem;
  color: var(--color-text-primary);
  margin-bottom: 1.2rem;
  line-height: 1.7;
  text-align: left;
  word-break: break-all;
  text-indent: 2em;
}
.profile-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.7rem;
  margin-bottom: 0.7rem;
}
.profile-tag {
  font-size: 1rem;
  border-radius: 0.7rem;
  background: var(--primary-teal-light);
  color: var(--primary-teal);
  border: none;
}
.profile-section {
  margin-bottom: 0.5rem;
  font-size: 1.08rem;
  color: var(--color-text-primary);
}
.profile-section-title {
  font-weight: 600;
  color: var(--primary-teal-dark);
}
.profile-section-list {
  margin-left: 0.5rem;
}
.profile-desc {
  font-size: 1.15rem;
  color: var(--color-text-primary);
  text-align: left;
  margin-bottom: 1.2rem;
  line-height: 1.7;
}

.profile-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.7rem;
  justify-content: center;
}

.profile-tag {
  font-size: 1rem;
  border-radius: 0.7rem;
  background: var(--primary-teal-light);
  color: var(--primary-teal);
  border: none;
}

.profile-pagination-section {
  margin-top: 2.5rem;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .profiles-card {
    padding: 2rem;
    border-radius: 1.2rem;
  }
  .profile-page {
    padding: 1.5rem 0.5rem;
  }
  .profile-img {
    width: 4rem;
    height: 4rem;
  }
  .profile-type {
    font-size: 1.5rem;
  }
}
.filter-section {
  margin-bottom: 2.5rem;
}
.dimension-filters {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(20rem, 1fr));
  gap: 2.2rem;
  justify-content: center;
  align-items: flex-start;
  background: var(--primary-teal-light);
  border-radius: 1.2rem;
  padding: 1.2rem 4rem;
  box-shadow: 0 0.2rem 0.8rem rgba(32,178,170,0.08);
}
.dimension-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.dimension-label {
  font-size: 1.15rem;
  color: var(--primary-teal-dark);
  font-weight: 600;
  margin-right: 0.3rem;
}
.dimension-radio :deep(.el-radio-button__inner) {
  background: #fff;
  color: var(--primary-teal);
  border-color: var(--primary-teal);
  font-weight: 600;
  transition: all 0.2s;
}
.dimension-radio :deep(.el-radio-button__orig-radio:checked + .el-radio-button__inner) {
  background: var(--primary-teal);
  color: #fff;
  border-color: var(--primary-teal-dark);
}
.dimension-radio :deep(.el-radio-button__inner:hover) {
  background: var(--primary-teal-light);
  color: var(--primary-teal-dark);
}
</style>
