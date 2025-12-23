import { Routes, Route } from "react-router-dom";
import Home from "../../pages/home/Home.jsx";
import Main from "../../pages/main/Main.jsx";
import Profile from "../../pages/profile/Profile.jsx";
import Sidebar from "../../components/sidebar/Sidebar.jsx";
import React from "react";
import DebateArena from "../../pages/ai_discussion/debateroom/DebateArena.jsx";
import Login from "../../pages/login/login.jsx";

/**
 * Sidebar + 페이지를 감싸는 Wrapper 컴포넌트
 */
function WithSidebar({ children }) {
  const [sidebarOpen, setSidebarOpen] = React.useState(false);

  const handleLogout = () => {
    console.log("로그아웃 처리");
    // 실제 로그아웃 로직 넣기
  };

  return (
    <div style={{ display: "flex" }}>
      <Sidebar
        sidebarOpen={sidebarOpen}
        setSidebarOpen={setSidebarOpen}
        handleLogout={handleLogout}
      />
      <div style={{ flex: 1 }}>
        {children}
      </div>
    </div>
  );
}

export default function MainRouter() {
  return (
    <Routes>
      {/* Home / Login 페이지는 Sidebar 없음 */}
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />

      {/* Sidebar 적용 페이지 */}
      <Route
        path="/main"
        element={
          <WithSidebar>
            <Main />
          </WithSidebar>
        }
      />
      <Route
        path="/profile"
        element={
          <WithSidebar>
            <Profile />
          </WithSidebar>
        }
      />

      <Route
        path="/AIDiscussion"
        element={<DebateArena/>}/>
    </Routes>
  );
}
