package com.single.agora_backend.controller;

    import com.single.agora_backend.entity.Participant;
    import com.single.agora_backend.service.RoomService;
    import org.springframework.messaging.handler.annotation.MessageMapping;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.stereotype.Controller;

    import java.util.List;
    import java.util.Map;

@Controller
public class GameRoomController {
    private final SimpMessagingTemplate template;
    private final RoomService roomService;

    public GameRoomController(SimpMessagingTemplate template, RoomService roomService) {
        this.template = template;
        this.roomService = roomService;
    }

    // 클라이언트 -> /app/room/enter
    @MessageMapping("/room/enter")
    public void enter(Map<String, Object> msg) {
        // msg: { "roomId":123, "userId":12, "nickname":"철수" }
        Long roomId = ((Number)msg.get("roomId")).longValue();
        Long userId = msg.get("userId")==null?null:((Number)msg.get("userId")).longValue();
        String nickname = (String) msg.getOrDefault("nickname", "guest");

        Participant p = roomService.joinRoom(roomId, userId, nickname);
        List<Participant> participants = roomService.listParticipants(roomId);

        // room 상태 브로드캐스트
        template.convertAndSend("/topic/room/" + roomId + "/state", Map.of(
                "type", "ROOM_STATE",
                "roomId", roomId,
                "participants", participants
        ));

    }
    @MessageMapping("/lobby/chat")
    public void lobbyChat(Map<String, String> msg) {
        // msg: { "sender": "nickname", "content": "hello", "profileImageUrl": "..." }
        template.convertAndSend("/topic/lobby/chat", msg);
    }
}
