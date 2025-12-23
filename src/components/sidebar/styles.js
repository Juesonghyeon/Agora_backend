import { css } from "@emotion/react";

/* 사이드바 */
export const sidebar = css`
  width: 200px;
  background: #fffaf0;
  border-right: 1px solid #c8b496;
  transition: width 0.3s ease;
  position: relative;
  overflow: hidden;
`;

export const sidebarOpen = css`width: 200px;`;
export const sidebarClosed = css`width: 50px;`;

export const sidebarToggle = css`
  width: 100%;
  padding: 10px;
  background: #bfa76f;
  border: none;
  color: #fff;
  cursor: pointer;
  font-weight: bold;
`;

export const menuList = css`
  list-style: none;
  padding: 20px 10px;
  li {
    margin-bottom: 16px;
    cursor: pointer;
    color: #5a534f;
    &:hover {
      color: #bfa76f;
    }
  }
`;
