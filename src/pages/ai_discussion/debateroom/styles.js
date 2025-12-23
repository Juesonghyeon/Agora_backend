/** @jsxImportSource @emotion/react */
import { css } from "@emotion/react";

export const inputContainer = css`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(to bottom, #fdf8f0, #f2ece1);
  padding: 20px;
`;

export const title = css`
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
  color: #5a534f;
`;

export const inputBox = css`
  padding: 10px 15px;
  font-size: 1rem;
  border-radius: 8px;
  border: 1px solid #c8b496;
  width: 300px;
  margin-bottom: 15px;
  outline: none;
  &:focus {
    border-color: #bfa76f;
    box-shadow: 0 0 5px rgba(191, 167, 111, 0.5);
  }
`;

export const startBtn = css`
  padding: 10px 20px;
  font-size: 1rem;
  background-color: #bfa76f;
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
  &:hover {
    background-color: #a9905e;
  }
`;

export const roomContainer = css`
  display: flex;
  flex-direction: column;
  padding: 20px;
  height: 100vh;
  background-color: #fffaf0;
`;

export const roomTitle = css`
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 15px;
  color: #5a534f;
`;

export const chatWrapper = css`
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background-color: #fff8ec;
  border-radius: 10px;
  border: 1px solid #c8b496;
  margin-bottom: 10px;
`;

export const chatMessage = css`
  padding: 8px 12px;
  margin-bottom: 8px;
  border-radius: 12px;
  background-color: #f4efe6;
  max-width: 70%;
  word-break: break-word;
  color: #5a534f;
`;

export const myMessage = css`
  background-color: #bfa76f;
  color: #fff;
  margin-left: auto;
`;

export const chatInputRow = css`
  display: flex;
  gap: 10px;
`;

export const chatInput = css`
  flex: 1;
  padding: 10px 15px;
  border-radius: 8px;
  border: 1px solid #c8b496;
  outline: none;
  font-size: 1rem;
  &:focus {
    border-color: #bfa76f;
    box-shadow: 0 0 5px rgba(191, 167, 111, 0.5);
  }
`;

export const sendButton = css`
  padding: 10px 20px;
  background-color: #bfa76f;
  color: #fff;
  font-size: 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
  &:hover {
    background-color: #a9905e;
  }
`;
