package com.single.agora_backend.service;

import com.single.agora_backend.entity.Room;
import com.single.agora_backend.entity.Participant;
import com.single.agora_backend.repository.RoomRepository;
import com.single.agora_backend.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;
    private final SecureRandom rnd = new SecureRandom();
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LEN = 6;

    public RoomService(RoomRepository roomRepository, ParticipantRepository participantRepository) {
        this.roomRepository = roomRepository;
        this.participantRepository = participantRepository;
    }

    // 랜덤 코드 생성
    private String genCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LEN; i++) {
            sb.append(CHARS.charAt(rnd.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    // 중복 없는 참여코드 생성
    private String genUniqueCode() {
        String code;
        do {
            code = genCode();
        } while (roomRepository.existsByParticipationCode(code));
        return code;
    }

    // 방 생성
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

    // 참여코드로 방 조회
    public Room getByCode(String code) {
        return roomRepository.findByParticipationCode(code).orElse(null);
    }

    // 참가자 추가
    @Transactional
    public Participant joinRoom(Long roomId, Long userId, String nickname) {
        Participant p = new Participant();
        p.setRoomId(roomId);
        p.setUserId(userId);
        p.setNickname(nickname);
        p.setJoinedAt(LocalDateTime.now());
        p.setConnected(true);
        p.setIsLeader(false);
        return participantRepository.save(p);
    }

    // 방 참가자 목록 조회
    public List<Participant> listParticipants(Long roomId) {
        return participantRepository.findAllByRoomId(roomId);
    }

    // 특정 방 참가자 삭제 (필요 시)
    @Transactional
    public void removeParticipants(Long roomId) {
        participantRepository.deleteAllByRoomId(roomId);
    }
}
