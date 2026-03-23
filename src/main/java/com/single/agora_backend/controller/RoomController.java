package com.single.agora_backend.controller;

import com.single.agora_backend.entity.GameParticipant;
import com.single.agora_backend.entity.Room;
import com.single.agora_backend.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RoomController {

    private final RoomService roomService;
    public RoomController(RoomService roomService) { this.roomService = roomService; }

    @PostMapping("/api/rooms/create")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        Long topicId = ((Number) body.get("topicId")).longValue();
        Long hostId = body.get("hostId") == null ? null : ((Number) body.get("hostId")).longValue();
        return ResponseEntity.ok(roomService.createRoom(topicId, hostId));
    }

    @PostMapping("/api/rooms/enter/{code}")
    public ResponseEntity<?> enterRoom(@PathVariable String code, @RequestBody Map<String, Object> body) {
        Long topicId = ((Number) body.get("topicId")).longValue();
        Long hostId = body.get("hostId") == null ? null : ((Number) body.get("hostId")).longValue();
        return ResponseEntity.ok(roomService.getOrCreateRoomByCode(code, topicId, hostId));
    }

    @GetMapping("/api/game/join-room-players")
    public ResponseEntity<?> getPlayers(@RequestParam String gameCode) {
        return ResponseEntity.ok(roomService.listGameParticipants(gameCode));
    }

    @PostMapping("/api/game/join-room-action")
    public ResponseEntity<?> join(@RequestBody Map<String, Object> body) {
        String gameCode = (String) body.get("gameCode");
        Long userId = ((Number) body.get("userId")).longValue();

        System.out.println("🚨 프론트에서 넘어온 gameCode 값: [" + gameCode + "]");
        System.out.println("🚨 gameCode 길이: " + (gameCode != null ? gameCode.length() : 0));

        // ⭐ 역할(role)을 프론트에서 받지 않고 백엔드에서 알아서 판단하도록 변경
        roomService.joinGame(gameCode, userId);

        return ResponseEntity.ok(Map.of("participants", roomService.listGameParticipants(gameCode)));
    }

    @PostMapping("/join-room-action")
    public ResponseEntity<?> joinRoomAction(@RequestBody Map<String, Object> body) {
        String gameCode = (String) body.get("gameCode");
        Long userId = Long.valueOf(body.get("userId").toString());

        System.out.println("🚨 프론트에서 넘어온 gameCode 값: [" + gameCode + "]");
        System.out.println("✅ 컨트롤러 호출됨: Code=" + gameCode + ", User=" + userId);

        roomService.joinGame(gameCode, userId);
        return ResponseEntity.ok(Map.of("participants", roomService.listGameParticipants(gameCode)));
    }
}