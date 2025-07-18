<template>
  <div class="home-container">
    <div class="welcome-card">
      <div class="welcome-text">
        <h1 class="welcome-title">
          你想测试你的<span class="text-teal">MBTI</span>吗？点击下方按钮，选择你想回答的试卷吧！
        </h1>
      </div>

      <div class="action-section">
        <el-button 
          type="primary" 
          size="large" 
          class="start-test-btn"
          @click="handleStartTest"
        >
          开始MBTI测试
        </el-button>
      </div>

      <div class="carousel-section">
        <el-carousel 
          ref="carouselRef"
          :interval="4000" 
          :height="'450px'"
          indicator-position="none"
          arrow="hover"
          :transition="'card'"
          @change="handleCarouselChange"
        >
          <el-carousel-item v-for="item in carouselItems" :key="item.id">
            <div class="carousel-item">
              <div 
                class="carousel-half carousel-left"
                :style="{ 
                  backgroundColor: item.leftBgColor,
                  backgroundImage: `url(${item.leftBgImage || ''})`,
                  backgroundSize: 'cover',
                  backgroundPosition: 'center'
                }"
              >
                <div class="half-overlay"></div>
                <div class="half-content">
                  <h4>{{ item.leftTitle }}</h4>
                  <p>{{ item.leftDescription }}</p>
                </div>
              </div>
              
              <div 
                class="carousel-half carousel-right"
                :style="{ 
                  backgroundColor: item.rightBgColor,
                  backgroundImage: `url(${item.rightBgImage || ''})`,
                  backgroundSize: 'cover',
                  backgroundPosition: 'center'
                }"
              >
                <div class="half-overlay"></div>
                <div class="half-content">
                  <h4>{{ item.rightTitle }}</h4>
                  <p>{{ item.rightDescription }}</p>
                </div>
              </div>
            </div>
          </el-carousel-item>
          
          <div class="custom-indicators">
            <div 
              v-for="(item, index) in carouselItems" 
              :key="item.id"
              class="custom-dot"
              :class="{ 'active': index === currentSlide }"
              @click="goToSlide(index)"
            ></div>
          </div>
        </el-carousel>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 当前轮播项索引
const currentSlide = ref(0)
const carouselRef = ref<any>(null)

// 获取当前用户的 uid
const currentUid = computed(() => {
  return route.params.uid || (userStore.user?.userId?.toString())
})

// 轮播图数据
const carouselItems = ref([
  {
    id: 1,
    leftTitle: '内向 (I)',
    leftDescription: '倾向于内向思考，从独处中获得能量',
    leftBgColor: '#E0F7FA',
    leftBgImage: 'MBTI_LETTER/I.png',
    rightTitle: '外向 (E)',
    rightDescription: '倾向于外向交流，从互动中获得能量',
    rightBgColor: '#B2EBF2',
    rightBgImage: 'MBTI_LETTER/E.png'
  },
  {
    id: 2,
    leftTitle: '感知 (S)',
    leftDescription: '更依赖感官信息和具体事实',
    leftBgColor: '#F0F4C3',
    leftBgImage: 'MBTI_LETTER/S.png',
    rightTitle: '直觉 (N)',
    rightDescription: '更依赖直觉洞察和可能性',
    rightBgColor: '#DCEDC8',
    rightBgImage: 'MBTI_LETTER/N.png'
  },
  {
    id: 3,
    leftTitle: '思考 (T)',
    leftDescription: '在决策时更重视逻辑和客观分析',
    leftBgColor: '#FCE4EC',
    leftBgImage: 'MBTI_LETTER/T.png',
    rightTitle: '情感 (F)',
    rightDescription: '在决策时更重视情感和价值观',
    rightBgColor: '#F8BBD9',
    rightBgImage: 'MBTI_LETTER/F.png'
  },
  {
    id: 4,
    leftTitle: '判断 (J)',
    leftDescription: '更喜欢计划性和确定性的生活',
    leftBgColor: '#E8F5E8',
    leftBgImage: 'MBTI_LETTER/J.png',
    rightTitle: '感知 (P)',
    rightDescription: '更喜欢灵活性和开放性的生活',
    rightBgColor: '#C8E6C9',
    rightBgImage: 'MBTI_LETTER/P.png'
  }
])

// 事件处理
const handleStartTest = () => {
  console.log('开始MBTI测试')
  // 跳转到问卷选择页面，保持uid参数
  if (currentUid.value) {
    router.push({ 
      name: 'questionnaires', 
      params: { uid: currentUid.value }
    })
  } else {
    router.push({ name: 'questionnaires' })
  }
}

// 轮播变化事件处理
const handleCarouselChange = (newIndex: number) => {
  currentSlide.value = newIndex
}

// 跳转到指定轮播项
const goToSlide = (index: number) => {
  // 立即更新本地状态，确保圆点立即响应
  currentSlide.value = index
  
  // 使用 nextTick 确保DOM更新后再调用轮播组件方法
  if (carouselRef.value && carouselRef.value.setActiveItem) {
    carouselRef.value.setActiveItem(index)
  }
}
</script>

<style scoped>
.home-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 2rem;
  background-color: var(--color-background-soft);
}

.welcome-card {
  max-width: 65rem;
  width: 100%;
  background-color: var(--color-background);
  border-radius: 1.6rem;
  box-shadow: 0 0.8rem 3.2rem rgba(0, 0, 0, 0.1);
  padding: 4rem;
  border: 1px solid var(--color-border);
}


.welcome-text {
  text-align: center;
  margin-bottom: 3rem;
}

.welcome-title {
  font-size: 2.4rem;
  line-height: 1.5;
  color: var(--color-text-primary);
  font-weight: 600;
  margin: 0;
  letter-spacing: 0.05rem;
}

.welcome-title .text-teal {
  color: var(--primary-teal);
  font-weight: bold;
}


.action-section {
  display: flex;
  justify-content: center;
  margin-bottom: 4rem;
}

.start-test-btn {
  font-size: 1.6rem !important;
  padding: 1.2rem 3rem !important;
  border-radius: 0.8rem !important;
  font-weight: 600 !important;
  letter-spacing: 0.1rem;
  transition: all 0.3s ease !important;
  box-shadow: 0 0.4rem 1.2rem rgba(32, 178, 170, 0.3);
}

:deep(.el-button--primary) {
  background-color: var(--primary-teal) !important;
  border-color: var(--primary-teal) !important;
  color: #ffffff !important;
}

:deep(.el-button--primary:hover) {
  background-color: var(--primary-teal-dark) !important;
  border-color: var(--primary-teal-dark) !important;
  color: #ffffff !important;
  transform: translateY(-0.2rem);
  box-shadow: 0 0.6rem 1.6rem rgba(32, 178, 170, 0.4) !important;
}

:deep(.el-button--primary:active) {
  transform: translateY(0);
}


.carousel-section {
  margin-top: 2rem;
  position: relative;
}

/* 自定义圆点指示器 - 右下角 */
.custom-indicators {
  position: absolute;
  bottom: 1.5rem;
  right: 2rem;
  display: flex;
  gap: 0.6rem;
  z-index: 10;
}

.custom-dot {
  width: 0.8rem;
  height: 0.8rem;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: all 0.1s ease;
  border: 1px solid rgba(255, 255, 255, 0.8);
  will-change: transform, background-color;
}

.custom-dot:hover {
  background-color: rgba(255, 255, 255, 0.9);
  transform: scale(1.1);
}

.custom-dot:active {
  transform: scale(0.9);
  transition: all 0.05s ease;
}

.custom-dot.active {
  background-color: var(--primary-teal);
  border-color: var(--primary-teal);
  transform: scale(1.3);
  box-shadow: 0 0 0.6rem rgba(32, 178, 170, 0.6);
  transition: all 0.1s ease; /* 激活状态也加快 */
}

.carousel-item {
  height: 100%;
  display: flex;
  border-radius: 1.2rem;
  margin: 0 0.5rem;
  overflow: hidden;
}

/* 左右两半部分 */
.carousel-half {
  flex: 1;
  position: relative;
  display: flex;
  align-items: flex-end;
  overflow: hidden;
}

.carousel-left {
  border-radius: 1.2rem 0 0 1.2rem;
}

.carousel-right {
  border-radius: 0 1.2rem 1.2rem 0;
}

/* 渐变遮罩 */
.half-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    to top,
    rgba(0, 0, 0, 0.5) 0%,
    rgba(0, 0, 0, 0.3) 40%,
    rgba(0, 0, 0, 0.1) 70%,
    transparent 100%
  );
  z-index: 2;
}

/* 文字内容区域 */
.half-content {
  position: absolute;
  bottom: 1.5rem;
  left: 1.5rem;
  right: 1.5rem;
  color: white;
  z-index: 5;
}

.half-content h4 {
  font-size: 1.8rem; /* 18px */
  color: white;
  margin-bottom: 0.6rem;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.4);
  line-height: 1.2;
}

.half-content p {
  font-size: 1.2rem; /* 12px */
  color: rgba(255, 255, 255, 0.95);
  line-height: 1.4;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.4);
  margin: 0;
}

/* Element Plus轮播样式覆盖 */
:deep(.el-carousel__indicators) {
  display: none !important; /* 隐藏默认指示器 */
}

:deep(.el-carousel__indicator) {
  display: none !important;
}

:deep(.el-carousel__arrow) {
  background-color: rgba(255, 255, 255, 0.9) !important;
  color: var(--primary-teal) !important;
  border: 1px solid var(--color-border) !important;
}

:deep(.el-carousel__arrow:hover) {
  background-color: var(--primary-teal) !important;
  color: #ffffff !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .home-container {
    padding: 1rem;
  }
  
  .welcome-card {
    padding: 2rem;
    border-radius: 1.2rem;
  }
  
  .welcome-title {
    font-size: 1.8rem; /* 18px */
  }
  
  .start-test-btn {
    font-size: 1.4rem !important; /* 14px */
    padding: 1rem 2rem !important;
  }
  
  .half-content h4 {
    font-size: 1.5rem; /* 15px */
  }
  
  .half-content p {
    font-size: 1.1rem; /* 11px */
  }
  
  .half-content {
    bottom: 1.2rem;
    left: 1.2rem;
    right: 1.2rem;
  }
  
  .custom-indicators {
    bottom: 1.2rem;
    right: 1.5rem;
    gap: 0.5rem;
  }
  
  .custom-dot {
    width: 0.7rem;
    height: 0.7rem;
  }
}

@media (max-width: 480px) {
  .welcome-card {
    padding: 1.5rem;
  }
  
  .welcome-title {
    font-size: 1.6rem; /* 16px */
  }
  
  .action-section {
    margin-bottom: 2rem;
  }
  
  :deep(.el-carousel) {
    height: 280px !important; /* 保持16:9比例 */
  }
  
  .carousel-text {
    bottom: 1rem;
    left: 1rem;
    right: 5rem;
  }
  
  .carousel-text h3 {
    font-size: 1.6rem; /* 16px */
    margin-bottom: 0.5rem;
  }
  
  .carousel-text p {
    font-size: 1.1rem; /* 11px */
  }
  
  .custom-indicators {
    bottom: 1rem;
    right: 1rem;
    gap: 0.4rem;
  }
  
  .custom-dot {
    width: 0.6rem;
    height: 0.6rem;
  }
}
</style>