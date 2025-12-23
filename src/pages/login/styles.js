import { css } from "@emotion/react";

/* ------------------------- */
/*         기본 레이아웃       */
/* ------------------------- */

export const container = css`
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(to bottom, #f0ece3, #d9d6cc);
  padding: 16px;
  box-sizing: border-box;
`;

export const card = css`
  background: rgba(255, 255, 245, 0.88);
  padding: 52px 40px;
  border-radius: 18px;
  border: 2px solid rgba(200, 180, 150, 0.4);
  width: 400px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.25);
  text-align: center;
`;

export const logo = css`
  width: 88px;
  margin-bottom: 26px;
`;

/* ------------------------- */
/*        탭 (로그인/회원)      */
/* ------------------------- */

export const tabContainer = css`
  display: flex;
  justify-content: center;
  margin-bottom: 32px;
  gap: 12px;
`;

export const tabButton = css`
  flex: 1;
  padding: 12px 0;
  font-size: 17px;
  font-weight: 600;
  background: transparent;
  border: none;
  border-bottom: 2px solid transparent;
  color: #5a534f;
  cursor: pointer;

  &:hover {
    color: #bfa76f;
  }
`;

export const activeTab = css`
  border-color: #bfa76f;
  color: #bfa76f;
`;

/* ------------------------- */
/*         입력 폼 레이아웃      */
/* ------------------------- */

export const form = css`
  display: flex;
  flex-direction: column;
  gap: 8px; /* error 메시지와 간격 조절 */
`;

export const inputField = css`
  padding: 14px 18px;
  border-radius: 8px;
  border: 1px solid #c8b496;
  background-color: #fffaf0;
  color: #5a534f;
  font-size: 15px;

  &:focus {
    border-color: #bfa76f;
    box-shadow: 0 0 0 2px rgba(191, 167, 111, 0.2);
  }
`;

export const submitButton = css`
  padding: 13px;
  font-size: 17px;
  font-weight: 600;
  border-radius: 8px;
  border: none;
  margin-top: 8px;
  background-color: rgba(191, 167, 111, 0.9);
  color: #f5f5f5;
  cursor: pointer;

  &:hover {
    background-color: rgba(170, 140, 80, 0.95);
  }
`;

/* ------------------------- */
/*         오류 메시지          */
/* ------------------------- */

export const errorText = css`
  color: #d9534f;
  font-size: 13px;
  text-align: left;
  margin-left: 4px;
`;

/* ------------------------- */
/*   메인: 아이디 / 비밀번호 찾기 */
/* ------------------------- */

export const findRow = css`
  margin-top: 22px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 14px;
`;

export const findButton = css`
  background: none;
  border: none;
  color: #867a6b;
  font-size: 15px;
  cursor: pointer;

  &:hover {
    color: #bfa76f;
  }
`;

export const findDivider = css`
  color: #9b9185;
  font-size: 15px;
`;

/* ------------------------- */
/*           모달 UI          */
/* ------------------------- */

export const modalOverlay = css`
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.55);
  display: flex;
  justify-content: center;
  align-items: center;
`;

export const modalCard = css`
  background: #fffaf0;
  padding: 38px 32px;
  border-radius: 14px;
  width: 360px;
  box-shadow: 0 10px 26px rgba(0, 0, 0, 0.3);
  border: 1px solid #c8b496;
  text-align: center;
`;

export const modalTitle = css`
  font-size: 20px;
  color: #5a534f;
  margin-bottom: 26px;
`;

export const modalInput = css`
  width: 100%;
  max-width: 360px;
  padding: 12px 16px;
  background: #fffdf7;
  border: 1px solid #c8b496;
  border-radius: 8px;
  margin: 0 auto 16px auto;
  display: block;
  box-sizing: border-box;
  color: #5a534f;

  &:focus {
    border-color: #bfa76f;
  }
`;

export const modalSendBtn = css`
  width: 100%;
  padding: 12px;
  margin-bottom: 12px;
  background: #bfa76f;
  color: white;
  border: none;
  font-size: 16px;
  border-radius: 8px;
  cursor: pointer;

  &:disabled {
    background: #d4c8aa;
    cursor: default;
  }
`;

export const countdownText = css`
  font-size: 14px;
  color: #8a7f70;
  margin-bottom: 16px;
`;

export const closeButton = css`
  width: 100%;
  padding: 10px;
  background: #d0c5b2;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  color: #4a433e;
  cursor: pointer;

  &:hover {
    background: #c3b49c;
  }
`;
