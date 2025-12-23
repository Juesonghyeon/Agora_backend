/** @jsxImportSource @emotion/react */
import React, { useState, useEffect } from "react";
import * as s from "./styles";


export default function DebateArena() {
  const [step, setStep] = useState("enter"); // enter → topic → debate
  const [topic, setTopic] = useState("");
  const [turn, setTurn] = useState(null); // 1 or 2
  const [timeLeft, setTimeLeft] = useState(0);
  const [currentPhaseIndex, setCurrentPhaseIndex] = useState(0);
  const [messages, setMessages] = useState([]);

  // 전체 토론 단계 (시간은 초 단위)
  const PHASES = [
    { name: "팀1 입론", time: 4 * 60 },
    { name: "팀2 입론", time: 4 * 60 },
    { name: "팀 논의", time: 3 * 60 },
    { name: "팀1 반론", time: 4 * 60 },
    { name: "팀 논의", time: 1 * 60 },
    { name: "팀2 의견 변론", time: 4 * 60 },
    { name: "팀 논의", time: 1 * 60 },
    { name: "팀2 반론", time: 4 * 60 },
    { name: "팀 논의", time: 1 * 60 },
    { name: "팀1 의견 변론", time: 4 * 60 },
  ];

  // -------------------------
  // Step 1: 방 입장 화면
  // -------------------------
  if (step === "enter") {
    return (
      <div css={s.inputContainer}>
        <div css={s.title}>싱글 AI 토론에 오신걸 환영합니다</div>
        <button css={s.startBtn} onClick={() => setStep("topic")}>
          토론 시작하기
        </button>
      </div>
    );
  }

  // -------------------------
  // Step 2: 토론 주제 입력
  // -------------------------
  if (step === "topic") {
    return (
      <div css={s.inputContainer}>
        <div css={s.title}>토론할 주제를 입력하세요</div>
        <input
          css={s.inputBox}
          placeholder="예: AI는 인간의 일자리를 대체할까?"
          value={topic}
          onChange={(e) => setTopic(e.target.value)}
        />

        <button
          css={s.startBtn}
          onClick={() => {
            if (topic.trim() === "") return;

            // 선턴 후턴 랜덤 배정
            const who = Math.random() < 0.5 ? 1 : 2;
            setTurn(who);

            setMessages([
              {
                sender: "ai",
                text: `좋아요! "${topic}" 주제로 토론을 시작합니다.`,
              },
              {
                sender: "ai",
                text:
                  who === 1
                    ? "당신은 팀1입니다. 먼저 입론을 진행하게 됩니다."
                    : "당신은 팀2입니다. 상대 AI가 먼저 입론을 진행합니다.",
              },
            ]);

            setCurrentPhaseIndex(0);
            setTimeLeft(PHASES[0].time);
            setStep("debate");
          }}
        >
          토론 시작
        </button>
      </div>
    );
  }

  // -------------------------
  // Step 3: 토론 진행 화면
  // -------------------------

  // 타이머 작동
  useEffect(() => {
    if (step !== "debate") return;
    if (timeLeft <= 0) return;

    const timer = setInterval(() => {
      setTimeLeft((t) => t - 1);
    }, 1000);

    return () => clearInterval(timer);
  }, [step, timeLeft]);

  // 시간 다 되면 다음 단계 자동 전환
  useEffect(() => {
    if (timeLeft <= 0 && step === "debate") {
      const next = currentPhaseIndex + 1;
      if (next < PHASES.length) {
        setCurrentPhaseIndex(next);
        setTimeLeft(PHASES[next].time);

        setMessages((prev) => [
          ...prev,
          { sender: "ai", text: `다음 단계: ${PHASES[next].name}` },
        ]);
      } else {
        setMessages((prev) => [
          ...prev,
          { sender: "ai", text: "토론이 모두 종료되었습니다!" },
        ]);
      }
    }
  }, [timeLeft, step]);

  const phase = PHASES[currentPhaseIndex];

  const [input, setInput] = useState("");

  const sendMessage = () => {
    if (input.trim() === "") return;

    const newMsg = { sender: "me", text: input };
    setMessages((prev) => [...prev, newMsg]);

    // AI 반응 (임시)
    setTimeout(() => {
      setMessages((prev) => [
        ...prev,
        {
          sender: "ai",
          text: `흥미로운 의견이네요. "${phase.name}" 단계에 맞춰 더 설명해주실 수 있나요?`,
        },
      ]);
    }, 600);

    setInput("");
  };

  return (
    <div css={s.roomContainer}>
      <div css={s.roomTitle}>
        주제: {topic}
        <br />
        현재 단계: {phase.name}
        <br />
        남은 시간: {Math.floor(timeLeft / 60)}분 {timeLeft % 60}초
      </div>

      <div css={s.chatWrapper}>
        {messages.map((msg, i) => (
          <div
            key={i}
            css={[s.chatMessage, msg.sender === "me" && s.myMessage]}
          >
            {msg.text}
          </div>
        ))}
      </div>

      <div css={s.chatInputRow}>
        <input
          css={s.chatInput}
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="메시지를 입력하세요..."
        />
        <button css={s.sendButton} onClick={sendMessage}>
          전송
        </button>
      </div>
    </div>
  );
}
