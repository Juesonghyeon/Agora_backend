package com.single.agora_backend.service;

import com.single.agora_backend.entity.*;
import com.single.agora_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final GameParticipantRepository gameParticipantRepository;
    private final UserRepository userRepository;

    private final SecureRandom rnd = new SecureRandom();
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LEN = 6;

    public RoomService(RoomRepository roomRepository,
                       GameParticipantRepository gameParticipantRepository,
                       UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.gameParticipantRepository = gameParticipantRepository;
        this.userRepository = userRepository;
    }

    private String genUniqueCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < CODE_LEN; i++) sb.append(CHARS.charAt(rnd.nextInt(CHARS.length())));
            code = sb.toString();
        } while (roomRepository.existsByParticipationCode(code));
        return code;
    }

    @Transactional
    public Room createRoom(Long topicId, Long hostId) {
        Room room = new Room();
        room.setTopicId(topicId);
        room.setHostId(hostId);
        room.setParticipationCode(genUniqueCode());
        room.setStatus("WAITING");
        room.setCreatedAt(LocalDateTime.now());
        return roomRepository.save(room);
    }

    @Transactional
    public Room getOrCreateRoomByCode(String code, Long topicId, Long hostId) {
        return roomRepository.findByParticipationCode(code)
                .orElseGet(() -> {
                    Room room = new Room();
                    room.setParticipationCode(code);
                    room.setTopicId(topicId);
                    room.setHostId(hostId);
                    room.setStatus("WAITING");
                    room.setCreatedAt(LocalDateTime.now());
                    return roomRepository.save(room);
                });
    }

    public List<GameParticipant> listGameParticipants(String gameCode) {
        return gameParticipantRepository.findByGameCode(gameCode);
    }

    @Transactional
    public void joinGame(String gameCode, Long userId) {
        System.out.println("DEBUG: [joinGame] gameCode=[" + gameCode + "], userId=" + userId);

        if (gameCode == null || gameCode.trim().isEmpty() || gameCode.equals("undefined")) {
            throw new RuntimeException("유효하지 않은 게임 코드입니다.");
        }

        if (gameParticipantRepository.existsByGameCodeAndUserId(gameCode, userId)) return;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        Room room = roomRepository.findByParticipationCode(gameCode)
                .orElseGet(() -> {
                    System.out.println("INFO: 방이 없어 자동 생성합니다. Code: " + gameCode);
                    Room newRoom = new Room();
                    newRoom.setParticipationCode(gameCode);
                    newRoom.setHostId(userId);
                    newRoom.setTopicId(0L);
                    newRoom.setStatus("WAITING");
                    newRoom.setCreatedAt(LocalDateTime.now());
                    return roomRepository.save(newRoom);
                });

        // 🌟 해결: 첫 번째 입장객이 무조건 HOST가 되도록 변경
        List<GameParticipant> existingParticipants = gameParticipantRepository.findByGameCode(gameCode);
        String role = existingParticipants.isEmpty() ? "HOST" : "PARTICIPANT";

        String userImage = (user.getUserProfile() != null && user.getUserProfile().getProfileImageUrl() != null)
                ? user.getUserProfile().getProfileImageUrl()
                : "/default_profile.png";

        GameParticipant gp = new GameParticipant();
        gp.setGameCode(gameCode);
        gp.setUserId(userId);
        gp.setUsername(user.getUsername());
        gp.setRole(role);
        gp.setProfileImageUrl(userImage);

        gameParticipantRepository.save(gp);
    }
}