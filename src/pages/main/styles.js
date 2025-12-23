/** @jsxImportSource @emotion/react */
import { css } from "@emotion/react";

export const container = css`
  display: flex;
  height: 100vh;
  background: linear-gradient(to bottom, #f0ece3, #d9d6cc);
  font-family: 'Noto Sans KR', sans-serif;
`;

export const mainContent = css`
  flex: 1;
  padding: 24px;
  overflow-y: auto;
`;

export const sectionTitle = css`
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #5a534f;
`;

export const noTopics = css`color: #8a7f70;`;

export const topicList = css`
  display: flex;
  flex-direction: column;
  gap: 14px;
`;

export const topicCard = css`
  background: #fffaf0;
  border: 1px solid #c8b496;
  border-radius: 12px;
  padding: 16px;
  color: #5a534f;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
`;

export const topicText = css`font-size: 16px;`;

export const topicActions = css`
  display: flex;
  align-items: center;
  gap: 10px;
`;

export const participateButton = css`
  background: #bfa76f;
  color: white;
  border: none;
  padding: 6px 10px;
  border-radius: 6px;
  cursor: pointer;
  &:hover {
    background: #a9905e;
  }
`;

export const moreIcon = css`
  cursor: pointer;
  color: #5a534f;
  &:hover {
    color: #bfa76f;
  }
`;

export const moreMenu = css`
  position: absolute;
  top: 100%;
  margin-top: 2px;
  right: 0;
  background: #fffaf0;
  border: 1px solid #c8b496;
  border-radius: 6px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  min-width: 100px;
  overflow: hidden;
  z-index: 100;
  opacity: 0;
  transform: translateY(-5px);
  transition: opacity 0.15s ease, transform 0.15s ease;
`;

export const moreMenuOpen = css`
  opacity: 1;
  transform: translateY(0);
`;

export const moreMenuItem = css`
  padding: 8px 12px;
  cursor: pointer;
  white-space: nowrap;
  text-align: left;
  font-size: 14px;
  color: #5a534f;
  &:hover {
    background: #bfa76f;
    color: #fff;
  }
`;

export const addButtonWrapper = css`
  position: fixed;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
`;

export const addButton = css`
  padding: 14px 28px;
  background: #bfa76f;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
`;

export const modalOverlay = css`
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.55);
  display: flex;
  justify-content: center;
  align-items: center;
`;

export const modalCard = css`
  background: #fffaf0;
  padding: 32px;
  border-radius: 12px;
  width: 400px;
  border: 1px solid #c8b496;
`;

export const modalTitle = css`
  font-size: 20px;
  margin-bottom: 16px;
  color: #5a534f;
`;

export const modalInput = css`
  width: 100%;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #c8b496;
  margin-bottom: 12px;
  font-size: 15px;
`;

export const modalButtonRow = css`
  display: flex;
  justify-content: space-between;
  margin-top: 16px;
`;

export const modalSendBtn = css`
  background: #bfa76f;
  color: white;
  border: none;
  padding: 10px 16px;
  border-radius: 6px;
  cursor: pointer;
`;

export const closeButton = css`
  background: #d0c5b2;
  color: #4a433e;
  border: none;
  padding: 10px 16px;
  border-radius: 6px;
  cursor: pointer;
`;

export const participationInput = css`
  flex: 1;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #c8b496;
  font-size: 15px;
  height: 38px;
`;

export const copyButton = css`
  background: #bfa76f;
  color: white;
  border: none;
  padding: 0 12px;
  height: 38px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s, transform 0.1s;

  &:hover {
    background: #a9905e;
    transform: translateY(-1px);
  }

  &:active {
    transform: translateY(0);
  }
`;
