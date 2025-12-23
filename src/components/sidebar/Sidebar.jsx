/** @jsxImportSource @emotion/react */
import React from "react";
import { useNavigate } from "react-router-dom";
import * as s from "./styles";

export default function Sidebar({ sidebarOpen, setSidebarOpen }) {
  const navigate = useNavigate();

  // 로그아웃 함수
  const handleLogout = () => {
    // 로컬 스토리지 삭제
    localStorage.removeItem("token");
    localStorage.removeItem("userId");

    // 메인 페이지로 이동
    navigate("/");
  };

  return (
    <aside css={[s.sidebar, sidebarOpen ? s.sidebarOpen : s.sidebarClosed]}>
      <button
        css={s.sidebarToggle}
        onClick={() => setSidebarOpen(!sidebarOpen)}
      >
        {sidebarOpen ? "◀" : "▶"}
      </button>

      <ul css={s.menuList}>
        {sidebarOpen && (
          <>
            <li onClick={() => navigate("/main")}>참여한 토론 보기</li>
            <li onClick={() => navigate("/main")}>생성한 토론 목록</li>
            <li onClick={() => navigate("/profile")}>프로필</li>
            <li onClick={handleLogout}>로그아웃</li>
            <li>회원탈퇴</li>
          </>
        )}
      </ul>
    </aside>
  );
}
