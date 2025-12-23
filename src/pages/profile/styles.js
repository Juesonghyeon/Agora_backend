import { css } from "@emotion/react";

export const profileContainer = css`
  display: flex;
  justify-content: center;
  width: 100%;
  min-height: 100vh;
  background: #f0ece3;
  font-family: 'Noto Sans KR', sans-serif;
  padding: 2rem 1rem;
  box-sizing: border-box;
`;

export const contentWrapper = css`
  display: flex;
  width: 100%;
  max-width: 1200px;
  gap: 2rem;
`;

export const sidebar = css`
  width: 260px;
  background: #fffaf0;
  border: 1px solid #c8b496;
  border-radius: 12px;
  padding: 2rem 1.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
`;

export const profileImageWrapper = css`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;

  img {
    width: 140px;
    height: 140px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #c8b496;
    cursor: pointer;
  }

  input[type="file"] {
    display: none;
  }
`;

export const username = css`
  font-size: 1.2rem;
  font-weight: 600;
  color: #5a534f;
`;

export const email = css`
  font-size: 1rem;
  color: #5a534f;
  margin-bottom: 1.5rem;
`;

export const sidebarMenu = css`
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: 0.5rem;
`;

export const sidebarItem = (active) => css`
  padding: 0.6rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  color: ${active ? "#fff" : "#5a534f"};
  background: ${active ? "#bfa76f" : "transparent"};
  text-align: center;

  &:hover {
    background: ${active ? "#bfa76f" : "#e5dfd1"};
  }
`;

export const mainPanel = css`
  flex: 1;
  background: #fffaf0;
  border: 1px solid #c8b496;
  border-radius: 12px;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
`;

export const inputGroup = css`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;

  label {
    font-weight: 500;
    color: #5a534f;
    font-size: 1rem;
  }

  input {
    padding: 0.5rem 0.8rem;
    font-size: 0.95rem;
    border-radius: 6px;
    border: 1px solid #c8b496;
  }

  button {
    margin-top: 0.5rem;
    padding: 0.5rem 1rem;
    background: #bfa76f;
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 0.95rem;

    &:hover {
      background: #a9905e;
    }
  }
`;
