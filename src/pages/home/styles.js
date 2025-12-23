// styles.ts
import { css } from "@emotion/react";

/* 전체 컨테이너 */
export const container = css`
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 100vh;
  background: linear-gradient(
    to bottom,
    #f0ece3,
    #d9d6cc
  ); /* 고대 건물 벽 느낌 */
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16px;
  box-sizing: border-box;

  &::before {
    content: "";
    position: absolute;
    inset: 0;
    background-image: url("/images/agora-symbol.svg");
    background-repeat: no-repeat;
    background-position: center;
    background-size: 250px;
    opacity: 0.08;
    pointer-events: none;
    z-index: 0;
    filter: grayscale(100%) contrast(120%);
  }
`;

/* 가운데 카드 */
export const card = css`
  position: relative;
  text-align: center;
  background: rgba(255, 255, 245, 0.85); /* 고전적 밝은 돌 느낌 */
  padding: 48px;
  border-radius: 16px;
  border: 2px solid rgba(200, 180, 150, 0.4);
  max-width: 450px;
  width: 100%;
  box-sizing: border-box;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.25);
`;

/* 제목 */
export const title = css`
  color: #bfa76f; /* 금빛 느낌 */
  font-size: 44px;
  font-weight: 700;
  font-family: "Merriweather", serif;
  text-shadow: 0 0 6px rgba(191, 167, 111, 0.8);
  margin-bottom: 16px;
`;

/* 부제목 */
export const subtitle = css`
  color: #5a534f; /* 돌 느낌 */
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 36px;
  line-height: 1.6;
`;

/* 시작 버튼 */
export const startButton = css`
  width: 100%;
  padding: 16px;
  font-size: 18px;
  border-radius: 12px;
  border: none;
  cursor: pointer;

  color: #f5f5f5;
  background: rgba(191, 167, 111, 0.9); /* 고전적 금속 느낌 */
  backdrop-filter: blur(4px);

  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;

  font-weight: 600;
  transition: 0.25s;

  &:hover {
    background: rgba(170, 140, 80, 0.95);
  }

  & > .icon {
    width: 24px;
    height: 24px;
  }
`;
