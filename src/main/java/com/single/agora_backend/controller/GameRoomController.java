package com.single.agora_backend.controller;

    import com.single.agora_backend.entity.GameParticipant;
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

    @MessageMapping("/lobby/chat")
    public void lobbyChat(Map<String, String> msg) {
        // msg: { "sender": "nickname", "content": "hello", "profileImageUrl": "..." }
        template.convertAndSend("/topic/lobby/chat", msg);
    }

    @MessageMapping("/room/enter")
    public void enter(Map<String, Object> msg) {
        // 1. 데이터 파싱 (여기서 gameCode를 받아와야 합니다. 프론트에서 보내줘야 함)
        String gameCode = (String) msg.get("gameCode");
        Long userId = ((Number)msg.get("userId")).longValue();
        String role = (String) msg.getOrDefault("role", "PARTICIPANT");

        // 2. 중요: 사진이 포함된 새로운 테이블에 저장 (User 객체는 서비스 내부에서 조회하도록 로직 보완 필요)
        // 지금은 간단히 하기 위해 기존 로직을 수정합니다.
        // (서비스에 joinGame(gameCode, userId, role) 형태의 오버로딩 메서드를 만드시는 걸 추천합니다)

        // 3. 사진이 포함된 리스트 가져오기
        List<GameParticipant> participants = roomService.listGameParticipants(gameCode);

        // 4. 전송 (주소도 gameCode 기반으로 하시는게 관리하기 편합니다)
        template.convertAndSend("/topic/room/" + gameCode + "/state", Map.of(
                "type", "ROOM_STATE",
                "participants", participants
        ));
    }
}
