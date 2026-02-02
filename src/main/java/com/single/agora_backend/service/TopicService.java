package com.single.agora_backend.service;

import com.single.agora_backend.dto.TopicRequest;
import com.single.agora_backend.entity.GameState;
import com.single.agora_backend.entity.Topic;
import com.single.agora_backend.entity.User;
import com.single.agora_backend.repository.GameStateRepository;
import com.single.agora_backend.repository.TopicRepository;
import com.single.agora_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor // 생성자 주입 자동화
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final GameStateRepository gameStateRepository; // 🔥 추가

    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 12;
    private final SecureRandom random = new SecureRandom();

    private String generateUniqueCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++) {
                sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
            }
            code = sb.toString();
        } while (topicRepository.existsByParticipationCode(code));
        return code;
    }

    public List<Topic> getTopicsByUser(Long userId) {
        return topicRepository.findAllByUserId(userId);
    }

    @Transactional
    public Topic createTopic(TopicRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        Topic topic = new Topic();
        topic.setTitle(request.getTitle());
        topic.setType(request.getType());
        topic.setScale(request.getScale());
        topic.setDifficulty(request.getDifficulty());
        topic.setUser(user);

        // 코드 생성
        String code = null;
        if (!"Ai".equalsIgnoreCase(request.getType())) {
            code = (request.getParticipationCode() != null && !request.getParticipationCode().isEmpty())
                    ? request.getParticipationCode() : generateUniqueCode();
            topic.setParticipationCode(code);
        }

        Topic savedTopic = topicRepository.save(topic);

        // 🔥 [해결] GameState 테이블에도 동일한 ID와 코드를 넣어줌
        if (code != null) {
            GameState state = new GameState();
            state.setLobbyId(savedTopic.getId()); // Topic의 ID를 공유
            state.setParticipationCode(code);
            state.setPhase("TOPIC_SELECT");
            state.setTimeLeft(300);
            gameStateRepository.save(state);
        }

        return savedTopic;
    }

    @Transactional
    public Topic updateTopic(Long topicId, TopicRequest request) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("토픽 없음"));

        topic.setTitle(request.getTitle());
        topic.setScale(request.getScale());
        topic.setDifficulty(request.getDifficulty());
        // 타입 변경은 복잡해지므로 기존 로직 유지하거나 제한 권장

        return topicRepository.save(topic);
    }

    @Transactional
    public void deleteTopic(Long topicId) {
        // 🔥 [해결] 자식 테이블(GameState)부터 삭제해야 외래키 에러가 안 남
        gameStateRepository.deleteById(topicId);
        topicRepository.deleteById(topicId);
    }
}