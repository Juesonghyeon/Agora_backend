// 3. MessageController.java (Controller)
package com.single.agora_backend.controller;

import com.single.agora_backend.entity.DirectMessage;
import com.single.agora_backend.repository.DirectMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final DirectMessageRepository messageRepository;

    // 대화 내역 조회
    @GetMapping
    public ResponseEntity<List<DirectMessage>> getMessages(@RequestParam Long user1, @RequestParam Long user2) {
        return ResponseEntity.ok(messageRepository.findConversation(user1, user2));
    }

    // 메시지 전송
    @PostMapping
    public ResponseEntity<DirectMessage> sendMessage(@RequestBody DirectMessage message) {
        DirectMessage saved = messageRepository.save(message);
        return ResponseEntity.ok(saved);
    }

    // 메시지 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}