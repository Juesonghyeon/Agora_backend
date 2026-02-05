package com.single.agora_backend.dto.game;

import lombok.Getter;

public class GameRequests {

    @Getter
    public static class JoinRequest {
        private String nickname;
        private String socketId;
    }

    @Getter
    public static class OpinionRequest {
        private String socketId;
        private String opinion;
    }

    @Getter
    public static class VoteRequest {
        private String voterId;     // 투표하는 사람
        private String candidateId; // 투표 받은 사람
    }

    @Getter
    public static class ActionRequest {
        private String leaderId; // 팀장만 보낼 수 있음
        private String content;  // 발언 내용
        private String team;     // [추가] "team1" or "team2" (슈퍼 유저 식별용)
    }
}