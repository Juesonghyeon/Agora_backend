/** @jsxImportSource @emotion/react */
import React, { useState, useEffect } from "react";
import * as s from "./styles";
import axios from "axios";

export default function Profile() {
  const userId = localStorage.getItem("userId");
  const [activeMenu, setActiveMenu] = useState("친구 관리");

  const [user, setUser] = useState({
    username: "",
    email: "",
    profileImageUrl: "",
    emailVerified: false,
  });

  const [tempEmail, setTempEmail] = useState(""); // 인증 전 임시 이메일
  const [newUsername, setNewUsername] = useState("");
  const [passwords, setPasswords] = useState({ current: "", new: "", confirm: "" });
  const [emailCode, setEmailCode] = useState("");
  const [friends, setFriends] = useState([]);
  const [tempProfileImage, setTempProfileImage] = useState(""); // 미리보기

  // 초기 데이터 로드
  useEffect(() => {
    if (!userId) return;
    axios
      .get(`http://localhost:8080/api/profile/info?userId=${userId}`)
      .then(res => {
        setUser({
          username: res.data.username || "",
          email: res.data.email || "",
          profileImageUrl: res.data.profileImageUrl || "",
          emailVerified: res.data.emailVerified || false,
        });
        setTempEmail(res.data.email || "");
      })
      .catch(() => console.log("프로필 정보 가져오기 실패"));
  }, [userId]);

  // 프로필 이미지 선택
  const handleProfileClick = () => document.getElementById("profileFileInput").click();
  const handleProfileChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => setTempProfileImage(reader.result || "");
    reader.readAsDataURL(file);
  };

  // 프로필 이미지 적용
  const applyProfileImage = async () => {
    if (!tempProfileImage) return alert("적용할 사진을 선택해주세요");
    const fileInput = document.getElementById("profileFileInput");
    const file = fileInput.files[0];
    if (!file) return alert("파일이 없습니다");

    const formData = new FormData();
    formData.append("userId", userId);
    formData.append("file", file);

    try {
      const res = await axios.post("http://localhost:8080/api/profile/upload-image", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      // 서버에서 반환한 URL로 업데이트
      setUser(prev => ({ ...prev, profileImageUrl: res.data }));
      setTempProfileImage("");
      alert("프로필 사진 적용 완료");
    } catch {
      alert("프로필 사진 적용 실패");
    }
  };

  // 아이디 변경
  const changeUsername = async () => {
    if (!newUsername) return alert("새 아이디를 입력해주세요");
    await axios.post("http://localhost:8080/api/profile/change-username", { userId, newUsername });
    alert("아이디 변경 완료");
  };

  // 비밀번호 변경
  const changePassword = async () => {
    if (passwords.new !== passwords.confirm) return alert("새 비밀번호 불일치");
    await axios.post("http://localhost:8080/api/profile/change-password", {
      userId,
      oldPassword: passwords.current,
      newPassword: passwords.new,
    });
    alert("비밀번호 변경 완료");
  };

  // 이메일 코드 발송
  const sendEmailCode = async () => {
    if (!tempEmail) return alert("이메일을 입력해주세요");
    try {
      await axios.post("http://localhost:8080/api/profile/email/send", { userId, email: tempEmail });
      alert("인증 코드 발송됨 (SMTP 필요)");
    } catch {
      alert("이메일 전송 실패");
    }
  };

  // 이메일 인증
  const verifyEmail = async () => {
    if (!emailCode) return alert("코드를 입력해주세요");
    try {
      const res = await axios.post("http://localhost:8080/api/profile/email/verify", { userId, code: emailCode });
      if (res.data === "VERIFIED") {
        setUser(prev => ({ ...prev, email: tempEmail, emailVerified: true }));
        setEmailCode("");
        alert("이메일 인증 성공!");
      } else {
        alert("인증 실패");
      }
    } catch {
      alert("이메일 인증 실패");
    }
  };

  const renderContent = () => {
    switch (activeMenu) {
      case "아이디 변경":
        return (
          <div css={s.inputGroup}>
            <label>새 아이디</label>
            <input type="text" value={newUsername || ""} onChange={e => setNewUsername(e.target.value)} />
            <button onClick={changeUsername}>변경</button>
          </div>
        );
      case "비밀번호 변경":
        return (
          <div css={s.inputGroup}>
            <label>현재 비밀번호</label>
            <input type="password" value={passwords.current || ""} onChange={e => setPasswords({ ...passwords, current: e.target.value })} />
            <label>새 비밀번호</label>
            <input type="password" value={passwords.new || ""} onChange={e => setPasswords({ ...passwords, new: e.target.value })} />
            <label>새 비밀번호 확인</label>
            <input type="password" value={passwords.confirm || ""} onChange={e => setPasswords({ ...passwords, confirm: e.target.value })} />
            <button onClick={changePassword}>변경</button>
          </div>
        );
      case "친구 관리":
        return <div><h3>친구 목록</h3>{friends.length === 0 ? <p>친구 없음</p> : null}</div>;
      case "이메일 등록":
        return (
          <div css={s.inputGroup}>
            <label>이메일</label>
            <input
              type="email"
              value={tempEmail || ""}
              onChange={e => { setTempEmail(e.target.value); setUser(prev => ({ ...prev, emailVerified: false })); }}
              disabled={user.emailVerified}
            />
            <button onClick={sendEmailCode} disabled={user.emailVerified}>인증 코드 발송</button>
            <input
              type="text"
              placeholder="인증 코드 입력"
              value={emailCode || ""}
              onChange={e => setEmailCode(e.target.value)}
              disabled={user.emailVerified}
            />
            <button onClick={verifyEmail} disabled={user.emailVerified}>인증 확인</button>
            {user.emailVerified && <p style={{ color: "green" }}>이메일 인증 완료!</p>}
          </div>
        );
      default: return null;
    }
  };

  return (
    <div css={s.profileContainer}>
      <div css={s.contentWrapper}>
        <div css={s.sidebar}>
          <div css={s.profileImageWrapper}>
            <img
              src={tempProfileImage || user.profileImageUrl || "/default-profile.png"}
              alt="Profile"
              onClick={handleProfileClick}
            />
            <input id="profileFileInput" type="file" style={{ display: "none" }} onChange={handleProfileChange} />
            {tempProfileImage && <button onClick={applyProfileImage}>적용</button>}
          </div>
          <div css={s.username}>{user.username || "이름 없음"}</div>
          <div css={s.email}>{user.email || "이메일 미등록"}</div>
          <div css={s.sidebarMenu}>
            {["친구 관리","비밀번호 변경","아이디 변경","이메일 등록"].map(menu => (
              <div key={menu} css={s.sidebarItem(activeMenu===menu)} onClick={() => setActiveMenu(menu)}>{menu}</div>
            ))}
          </div>
        </div>
        <div css={s.mainPanel}>{renderContent()}</div>
      </div>
    </div>
  );
}
