package com.single.agora_backend.controller;

import com.single.agora_backend.entity.Room;
import com.single.agora_backend.entity.Participant;
import com.single.agora_backend.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // 1) 방 생성
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        Long topicId = ((Number) body.get("topicId")).longValue();
        Long hostId = body.get("hostId") == null ? null : ((Number) body.get("hostId")).longValue();

        Room room = roomService.createRoom(topicId, hostId);
        System.out.println(topicId);
        System.out.println(hostId);
        return ResponseEntity.ok(room);
    }

    // 2) 참여코드로 방 조회
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {
        Room room = roomService.getByCode(code);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }

    // 3) 방 입장 (참가자 등록)
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody Map<String, Object> body) {
        Long roomId = ((Number) body.get("roomId")).longValue();
        Long userId = body.get("userId") == null ? null : ((Number) body.get("userId")).longValue();
        String nickname = (String) body.getOrDefault("nickname", "guest");

        Participant p = roomService.joinRoom(roomId, userId, nickname);
        List<Participant> list = roomService.listParticipants(roomId);

        return ResponseEntity.ok(
                Map.of(
                        "participant", p,
                        "participants", list
                )
        );
    }
}
