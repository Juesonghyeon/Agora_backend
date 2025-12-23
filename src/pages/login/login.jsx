/** @jsxImportSource @emotion/react */
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import * as s from "./styles";

const logoPath = "/천칭.png";

export default function Login() {
  const navigate = useNavigate();

  // 로그인/회원가입
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [passwordCheck, setPasswordCheck] = useState("");

  const [usernameError, setUsernameError] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [passwordCheckError, setPasswordCheckError] = useState("");

  // 아이디/비밀번호 찾기 모달
  const [showFindId, setShowFindId] = useState(false);
  const [showFindPw, setShowFindPw] = useState(false);

  // 아이디 찾기 입력값
  const [findIdEmail, setFindIdEmail] = useState("");
  const [findIdPassword, setFindIdPassword] = useState("");
  const [idSent, setIdSent] = useState(false);
  const [idVerificationCode, setIdVerificationCode] = useState("");
  const [idCountdown, setIdCountdown] = useState(0);

  // 비밀번호 찾기 입력값
  const [findPwEmail, setFindPwEmail] = useState("");
  const [findPwUsername, setFindPwUsername] = useState("");
  const [pwSent, setPwSent] = useState(false);
  const [pwVerificationCode, setPwVerificationCode] = useState("");
  const [pwCountdown, setPwCountdown] = useState(0);

  // ----------------- 실시간 검증 -----------------
  useEffect(() => {
    if (!isLogin) {
      const usernameRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;
      setUsernameError(
        username && !usernameRegex.test(username)
          ? "아이디는 8자리 이상이며 숫자와 영문 대소문자를 포함해야 합니다."
          : ""
      );
    } else {
        setUsernameError(""); // 로그인 상태일 때 에러 초기화
    }
  }, [username, isLogin]);

  useEffect(() => {
    if (!isLogin) {
      const passwordRegex =
        /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?])[A-Za-z\d!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]{8,}$/;
      setPasswordError(
        password && !passwordRegex.test(password)
          ? "비밀번호는 8자리 이상이며 숫자, 영문 대소문자, 특수문자를 포함해야 합니다."
          : ""
      );

      setPasswordCheckError(
        passwordCheck && passwordCheck !== password
          ? "비밀번호가 일치하지 않습니다."
          : ""
      );
    } else {
        setPasswordError(""); // 로그인 상태일 때 에러 초기화
        setPasswordCheckError("");
    }
  }, [password, passwordCheck, isLogin]);

  // ----------------- 로그인 / 회원가입 탭 전환 -----------------
  const handleTabChange = (isLoginPage) => {
    setIsLogin(isLoginPage);
    // 탭 전환 시 모든 입력 필드 및 에러 상태 초기화
    setUsername("");
    setPassword("");
    setPasswordCheck("");
    setUsernameError("");
    setPasswordError("");
    setPasswordCheckError("");
  };

  // ----------------- 로그인 / 회원가입 -----------------
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!isLogin) {
      if (usernameError || passwordError || passwordCheckError) {
        alert("회원가입 조건을 확인해주세요.");
        return;
      }
    }

    try {
      const url = isLogin
        ? "http://localhost:8080/api/users/login"
        : "http://localhost:8080/api/users/register";

      const res = await axios.post(
        url,
        { username, password },
        { headers: { "Content-Type": "application/json" } }
      );

      if (isLogin) {
        localStorage.setItem("token", res.data.token);
        localStorage.setItem("userId", res.data.userId);

        const card = document.querySelector("#login-card");
        card.style.transition = "all 0.5s ease";
        card.style.opacity = 0;
        card.style.transform = "translateY(-20px)";

        setTimeout(() => {
          navigate("/main");
        }, 500);
      } else {
        alert("회원가입 성공");
        handleTabChange(true); // 성공 후 로그인 탭으로 이동 및 초기화
      }

    } catch (err) {
      console.error(err.response || err);
      alert(err.response?.data || "오류가 발생했습니다.");
    }
  };

  // ----------------- 아이디 찾기 -----------------
  const sendFindIdCode = async () => {
    try {
      await axios.post(
        "http://localhost:8080/api/auth/find-username",
        { email: findIdEmail, password: findIdPassword },
        { headers: { "Content-Type": "application/json" } }
      );
      setIdSent(true);
      setIdCountdown(60); // 일반적인 재전송 대기 시간을 60초로 설정하는 것을 권장합니다. (10초는 너무 짧습니다)
    } catch (err) {
      alert(err.response?.data || "오류가 발생했습니다.");
    }
  };

  const verifyFindIdCode = async () => {
    try {
      await axios.post(
        "http://localhost:8080/api/auth/verify-id-code",
        { email: findIdEmail, code: idVerificationCode },
        { headers: { "Content-Type": "application/json" } }
      );
      alert("아이디 확인 완료!");
      setShowFindId(false);
      setIdSent(false);
      setIdVerificationCode("");
      setIdCountdown(0);
    } catch (err) {
      alert(err.response?.data || "코드가 올바르지 않습니다.");
    }
  };

  // ----------------- 비밀번호 찾기 -----------------
  const sendFindPwCode = async () => {
    try {
      await axios.post(
        "http://localhost:8080/api/auth/reset-password",
        { email: findPwEmail, username: findPwUsername },
        { headers: { "Content-Type": "application/json" } }
      );
      setPwSent(true);
      setPwCountdown(60); // 일반적인 재전송 대기 시간을 60초로 설정하는 것을 권장합니다.
    } catch (err) {
      alert(err.response?.data || "오류가 발생했습니다.");
    }
  };

  const verifyFindPwCode = async () => {
    try {
      await axios.post(
        "http://localhost:8080/api/auth/verify-pw-code",
        { email: findPwEmail, code: pwVerificationCode },
        { headers: { "Content-Type": "application/json" } }
      );
      alert("비밀번호 재설정 완료!");
      setShowFindPw(false);
      setPwSent(false);
      setPwVerificationCode("");
      setPwCountdown(0);
    } catch (err) {
      alert(err.response?.data || "코드가 올바르지 않습니다.");
    }
  };

  // ----------------- Countdown -----------------
  useEffect(() => {
    if (idCountdown > 0) {
      const timer = setTimeout(() => setIdCountdown(idCountdown - 1), 1000);
      return () => clearTimeout(timer);
    }
  }, [idCountdown]);

  useEffect(() => {
    if (pwCountdown > 0) {
      const timer = setTimeout(() => setPwCountdown(pwCountdown - 1), 1000);
      return () => clearTimeout(timer);
    }
  }, [pwCountdown]);

  return (
    <div css={s.container}>
      <div css={s.card} id="login-card">
        <img src={logoPath} alt="Balance" css={s.logo} />

        <div css={s.tabContainer}>
          <button
            css={[s.tabButton, isLogin && s.activeTab]}
            onClick={() => handleTabChange(true)} // handleTabChange 사용
          >
            로그인
          </button>
          <button
            css={[s.tabButton, !isLogin && s.activeTab]}
            onClick={() => handleTabChange(false)} // handleTabChange 사용
          >
            회원가입
          </button>
        </div>

        <form css={s.form} onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="닉네임"
            css={s.inputField}
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          {!isLogin && usernameError && (
            <div css={s.errorText}>{usernameError}</div>
          )}

          <input
            type="password"
            placeholder="비밀번호"
            css={s.inputField}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          {!isLogin && passwordError && (
            <div css={s.errorText}>{passwordError}</div>
          )}

          {!isLogin && (
            <>
              <input
                type="password"
                placeholder="비밀번호 확인"
                css={s.inputField}
                value={passwordCheck}
                onChange={(e) => setPasswordCheck(e.target.value)}
                required
              />
              {passwordCheckError && (
                <div css={s.errorText}>{passwordCheckError}</div>
              )}
            </>
          )}

          <button type="submit" css={s.submitButton}>
            {isLogin ? "로그인" : "회원가입"}
          </button>
        </form>

        {isLogin && (
          <div css={s.findRow}>
            <button css={s.findButton} onClick={() => setShowFindId(true)}>
              아이디 찾기
            </button>
            <span css={s.findDivider}>|</span>
            <button css={s.findButton} onClick={() => setShowFindPw(true)}>
              비밀번호 찾기
            </button>
          </div>
        )}

        {/* ----------------- 아이디 찾기 모달 ----------------- */}
        {showFindId && (
          <div css={s.modalOverlay}>
            <div css={s.modalCard}>
              <div css={s.modalTitle}>아이디 찾기</div>
              {!idSent ? (
                <>
                  <input
                    type="email"
                    placeholder="등록된 이메일"
                    css={s.modalInput}
                    value={findIdEmail}
                    onChange={(e) => setFindIdEmail(e.target.value)}
                  />
                  <input
                    type="password"
                    placeholder="비밀번호"
                    css={s.modalInput}
                    value={findIdPassword}
                    onChange={(e) => setFindIdPassword(e.target.value)}
                  />
                  <button
                    css={s.modalSendBtn}
                    onClick={sendFindIdCode}
                    disabled={idCountdown > 0}
                  >
                    {idCountdown > 0 ? `${idCountdown}초 후 재전송` : "전송"}
                  </button>
                </>
              ) : (
                <>
                  <div css={s.countdownText}>이메일로 메시지를 보냈습니다.</div>
                  <input
                    type="text"
                    placeholder="인증 코드 입력"
                    css={s.modalInput}
                    value={idVerificationCode}
                    onChange={(e) => setFindIdVerificationCode(e.target.value)}
                  />
                  <button css={s.modalSendBtn} onClick={verifyFindIdCode}>
                    확인
                  </button>
                </>
              )}
              <button css={s.closeButton} onClick={() => {
                setShowFindId(false);
                setIdSent(false);
                setIdVerificationCode("");
                setFindIdEmail("");
                setFindIdPassword("");
                setIdCountdown(0);
              }}>
                닫기
              </button>
            </div>
          </div>
        )}

        {/* ----------------- 비밀번호 찾기 모달 ----------------- */}
        {showFindPw && (
          <div css={s.modalOverlay}>
            <div css={s.modalCard}>
              <div css={s.modalTitle}>비밀번호 재설정</div>
              {!pwSent ? (
                <>
                  <input
                    type="email"
                    placeholder="등록된 이메일"
                    css={s.modalInput}
                    value={findPwEmail}
                    onChange={(e) => setFindPwEmail(e.target.value)}
                  />
                  <input
                    type="text"
                    placeholder="아이디 입력"
                    css={s.modalInput}
                    value={findPwUsername}
                    onChange={(e) => setFindPwUsername(e.target.value)}
                  />
                  <button
                    css={s.modalSendBtn}
                    onClick={sendFindPwCode}
                    disabled={pwCountdown > 0}
                  >
                    {pwCountdown > 0 ? `${pwCountdown}초 후 재전송` : "전송"}
                  </button>
                </>
              ) : (
                <>
                  <div css={s.countdownText}>이메일로 메시지를 보냈습니다.</div>
                  <input
                    type="text"
                    placeholder="인증 코드 입력"
                    css={s.modalInput}
                    value={pwVerificationCode}
                    onChange={(e) => setPwVerificationCode(e.target.value)}
                  />
                  <button css={s.modalSendBtn} onClick={verifyFindPwCode}>
                    확인
                  </button>
                </>
              )}
              <button css={s.closeButton} onClick={() => {
                setShowFindPw(false);
                setPwSent(false);
                setPwVerificationCode("");
                setFindPwEmail("");
                setFindPwUsername("");
                setPwCountdown(0);
              }}>
                닫기
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}