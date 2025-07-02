package org.frostedstar.mbtisystem.service;

import lombok.RequiredArgsConstructor;
import org.frostedstar.mbtisystem.dto.QuestionDTO;
import org.frostedstar.mbtisystem.dto.OptionDTO;
import org.frostedstar.mbtisystem.model.MbtiDimension;
import org.frostedstar.mbtisystem.model.Option;
import org.frostedstar.mbtisystem.model.Question;
import org.frostedstar.mbtisystem.repository.OptionRepository;
import org.frostedstar.mbtisystem.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 问题服务类
 */
@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

    /**
     * 根据问卷ID获取所有问题
     */
    @Transactional(readOnly = true)
    public List<QuestionDTO> getQuestionsByQuestionnaireId(Long questionnaireId) {
        List<Question> questions = questionRepository.findByQuestionnaireIdOrderByQuestionOrder(questionnaireId);
        return questions.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * 根据ID获取问题详情
     */
    @Transactional(readOnly = true)
    public QuestionDTO getQuestionById(Long questionId) {
        Question question = questionRepository.findByIdWithOptions(questionId);
        if (question == null) {
            throw new RuntimeException("问题不存在，ID: " + questionId);
        }
        return convertToDTO(question);
    }

    /**
     * 创建新问题
     */
    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        Question question = convertToEntity(questionDTO);
        
        // 如果没有指定顺序，设置为最后一个
        if (question.getQuestionOrder() == null) {
            Short maxOrder = questionRepository.findMaxQuestionOrderByQuestionnaireId(question.getQuestionnaireId());
            question.setQuestionOrder((short) (maxOrder == null ? 1 : maxOrder + 1));
        }
        
        Question savedQuestion = questionRepository.save(question);
        
        // 保存选项
        if (questionDTO.getOptions() != null && !questionDTO.getOptions().isEmpty()) {
            List<Option> options = questionDTO.getOptions().stream()
                .map(optionDTO -> convertOptionToEntity(optionDTO, savedQuestion.getQuestionId()))
                .collect(Collectors.toList());
            List<Option> savedOptions = optionRepository.saveAll(options);
            savedQuestion.getOptions().addAll(savedOptions);
        }
        
        return convertToDTO(savedQuestion);
    }

    /**
     * 更新问题
     */
    public QuestionDTO updateQuestion(Long questionId, QuestionDTO questionDTO) {
        Question existingQuestion = questionRepository.findByIdWithOptions(questionId);
        if (existingQuestion == null) {
            throw new RuntimeException("问题不存在，ID: " + questionId);
        }
        
        // 确保选项集合已初始化
        if (existingQuestion.getOptions() == null) {
            existingQuestion.setOptions(new ArrayList<>());
        }
        
        // 更新问题基本信息
        existingQuestion.setContent(questionDTO.getContent());
        existingQuestion.setDimension(MbtiDimension.fromValue(questionDTO.getDimension()));
        existingQuestion.setQuestionOrder(questionDTO.getQuestionOrder());
        
        // 更新选项
        if (questionDTO.getOptions() != null) {
            // 清空现有选项（触发孤儿删除）
            existingQuestion.getOptions().clear();
            
            // 先保存问题以确保选项能正确引用
            questionRepository.save(existingQuestion);
            
            // 添加新选项
            List<Option> newOptions = questionDTO.getOptions().stream()
                .map(optionDTO -> convertOptionToEntity(optionDTO, questionId))
                .collect(Collectors.toList());
            
            // 保存选项并添加到问题中
            List<Option> savedOptions = optionRepository.saveAll(newOptions);
            existingQuestion.getOptions().addAll(savedOptions);
        }
        
        Question savedQuestion = questionRepository.save(existingQuestion);
        return convertToDTO(savedQuestion);
    }

    /**
     * 删除问题
     */
    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new RuntimeException("问题不存在，ID: " + questionId);
        }
        
        // 删除选项（通过级联删除）
        questionRepository.deleteById(questionId);
    }

    /**
     * 批量创建问题
     */
    public List<QuestionDTO> createQuestionsBatch(List<QuestionDTO> questionsDTO) {
        List<Question> questions = questionsDTO.stream()
            .map(this::convertToEntity)
            .collect(Collectors.toList());
        
        // 设置问题顺序
        Long questionnaireId = questions.get(0).getQuestionnaireId();
        Short maxOrder = questionRepository.findMaxQuestionOrderByQuestionnaireId(questionnaireId);
        short startOrder = (short) (maxOrder == null ? 1 : maxOrder + 1);
        
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getQuestionOrder() == null) {
                questions.get(i).setQuestionOrder((short) (startOrder + i));
            }
        }
        
        List<Question> savedQuestions = questionRepository.saveAll(questions);
        
        // 保存选项
        for (int i = 0; i < savedQuestions.size(); i++) {
            Question savedQuestion = savedQuestions.get(i);
            QuestionDTO questionDTO = questionsDTO.get(i);
            
            if (questionDTO.getOptions() != null && !questionDTO.getOptions().isEmpty()) {
                List<Option> options = questionDTO.getOptions().stream()
                    .map(optionDTO -> convertOptionToEntity(optionDTO, savedQuestion.getQuestionId()))
                    .collect(Collectors.toList());
                List<Option> savedOptions = optionRepository.saveAll(options);
                savedQuestion.getOptions().addAll(savedOptions);
            }
        }
        
        return savedQuestions.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * 更新问题顺序
     */
    public void reorderQuestions(Map<Long, Short> questionOrders) {
        for (Map.Entry<Long, Short> entry : questionOrders.entrySet()) {
            Long questionId = entry.getKey();
            Short newOrder = entry.getValue();
            
            Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("问题不存在，ID: " + questionId));
            
            question.setQuestionOrder(newOrder);
            questionRepository.save(question);
        }
    }

    /**
     * 实体转DTO
     */
    private QuestionDTO convertToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setQuestionId(question.getQuestionId());
        dto.setQuestionnaireId(question.getQuestionnaireId());
        dto.setContent(question.getContent());
        dto.setDimension(question.getDimension().getValue());
        dto.setQuestionOrder(question.getQuestionOrder());
        
        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            List<OptionDTO> optionDTOs = question.getOptions().stream()
                .map(this::convertOptionToDTO)
                .collect(Collectors.toList());
            dto.setOptions(optionDTOs);
        }
        
        return dto;
    }

    /**
     * DTO转实体
     */
    private Question convertToEntity(QuestionDTO dto) {
        Question question = new Question();
        question.setQuestionId(dto.getQuestionId());
        question.setQuestionnaireId(dto.getQuestionnaireId());
        question.setContent(dto.getContent());
        question.setDimension(MbtiDimension.fromValue(dto.getDimension()));
        question.setQuestionOrder(dto.getQuestionOrder());
        return question;
    }

    /**
     * 选项实体转DTO
     */
    private OptionDTO convertOptionToDTO(Option option) {
        OptionDTO dto = new OptionDTO();
        dto.setOptionId(option.getOptionId());
        dto.setQuestionId(option.getQuestionId());
        dto.setContent(option.getContent());
        dto.setScore(option.getScore());
        return dto;
    }

    /**
     * 选项DTO转实体
     */
    private Option convertOptionToEntity(OptionDTO dto, Long questionId) {
        Option option = new Option();
        option.setOptionId(dto.getOptionId());
        option.setQuestionId(questionId);
        option.setContent(dto.getContent());
        option.setScore(dto.getScore());
        return option;
    }
}
