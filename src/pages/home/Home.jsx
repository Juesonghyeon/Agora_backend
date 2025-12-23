/** @jsxImportSource @emotion/react */
import { motion } from "framer-motion";
import { Sparkles } from "lucide-react";
import { useNavigate } from "react-router-dom";
import * as s from "./styles"; // styles 존재한다고 가정

export default function Home() {
  const navigate = useNavigate();

  const handleStartClick = () => {
    setTimeout(() => navigate("/login"), 600);
  };

  return (
    <div css={s.container}>
      <motion.div
        initial={{ opacity: 0, scale: 0.8 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.6 }}
        css={s.card}
      >
        <motion.h1
          initial={{ y: -20, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          transition={{ delay: 0.2, duration: 0.6 }}
          css={s.title}
        >
          AGORA
        </motion.h1>
        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.4, duration: 0.6 }}
          css={s.subtitle}
        >
          AI 기반 실시간 토론 플랫폼
        </motion.p>
        <motion.button
          onClick={handleStartClick}
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.6, duration: 0.5 }}
          css={s.startButton}
        >
          <Sparkles className="icon" />
          시작하기
        </motion.button>
      </motion.div>
    </div>
  );
}