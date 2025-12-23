/** @jsxImportSource @emotion/react */
import React, { useState, useEffect, useRef } from "react";
import * as s from "./styles";
import { IoMdMore } from "react-icons/io";
import { useNavigate } from "react-router-dom";
import axios from "axios";

export default function Main() {
  const navigate = useNavigate();

  const [topics, setTopics] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editingTopic, setEditingTopic] = useState(null);

  const [newTopic, setNewTopic] = useState({
    title: "",
    type: "멀티",
    scale: "소규모",
    difficulty: "쉬움",
    participationCode: "",
  });

  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");

  const [openMenuId, setOpenMenuId] = useState(null);
  const menuRef = useRef(null);

  const toggleMenu = (id) => {
    setOpenMenuId(openMenuId === id ? null : id);
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setOpenMenuId(null);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const fetchTopics = async () => {
    try {
      if (!userId) return;
      const res = await axios.get(
        `http://localhost:8080/api/topics/user/${userId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setTopics(res.data);
    } catch (err) {
      console.error("Failed to fetch topics:", err);
    }
  };

  useEffect(() => {
    fetchTopics();
  }, [userId]);

  const generateParticipationCode = () => {
    const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    let code = "";
    for (let i = 0; i < 12; i++) {
      code += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return code;
  };

  const handleSaveTopic = async () => {
    if (!newTopic.title) return;

    const topicPayload = {
      ...newTopic,
      userId,
      type:
        newTopic.type === "AI" || newTopic.type === "ai" || newTopic.type === "Ai"
          ? "AI"
          : "멀티",
    };

    if (editingTopic) {
      try {
        const res = await axios.patch(
          `http://localhost:8080/api/topics/${editingTopic.id}`,
          topicPayload,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        setTopics(topics.map((t) => (t.id === editingTopic.id ? res.data : t)));
      } catch (err) {
        console.error(err);
      }
    } else {
      try {
        const res = await axios.post(
          "http://localhost:8080/api/topics",
          topicPayload,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        setTopics([...topics, res.data]);
      } catch (err) {
        console.error(err);
      }
    }

    setShowModal(false);
    setEditingTopic(null);
    setNewTopic({
      title: "",
      type: "멀티",
      scale: "소규모",
      difficulty: "쉬움",
      participationCode: "",
    });
  };

  const handleDeleteTopic = async (id) => {
    if (!window.confirm("삭제하시겠습니까?")) return;
    try {
      await axios.delete(`http://localhost:8080/api/topics/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setTopics(topics.filter((t) => t.id !== id));
    } catch (err) {
      console.error(err);
    }
  };

  // 🔥 수정된 참여 핸들러
  const handleParticipation = (topic) => {
    if (!topic) {
      alert("토론 정보를 불러올 수 없습니다.");
      return;
    }

    const type = String(topic.type).toUpperCase(); // 대문자로 강제 통일

    // 🔥 AI 모드 이동
    if (type === "AI") {
      navigate(`/AIDiscussion?id=${topic.id}`);
      return;
    }

    // 🔥 멀티 모드 이동
    if (topic.participationCode) {
      navigate(`/topic/${topic.participationCode}`);
    } else {
      alert("참여코드가 없습니다!");
    }
  };

  return (
    <div css={s.container}>
      <main css={s.mainContent}>
        <h2 css={s.sectionTitle}>토론 목록</h2>

        {topics.length === 0 && <p css={s.noTopics}>생성된 토론이 없습니다.</p>}

        <div css={s.topicList}>
          {topics.map((topic) => (
            <div key={topic.id} css={s.topicCard}>
              <p css={s.topicText}>{topic.title}</p>

              <div css={s.topicActions}>
                <button
                  css={{ ...s.participateButton, minWidth: "100px" }}
                  onClick={() => handleParticipation(topic)}
                >
                  참여하기
                </button>

                {topic.type !== "AI" && topic.participationCode && (
                  <div css={{ display: "flex", gap: "8px", alignItems: "center" }}>
                    <input
                      type="text"
                      css={s.participationInput}
                      value={topic.participationCode}
                      readOnly
                    />
                    <button
                      css={s.copyButton}
                      onClick={() => {
                        navigator.clipboard.writeText(topic.participationCode);
                        alert("참여코드가 복사되었습니다!");
                      }}
                    >
                      복사
                    </button>
                  </div>
                )}

                <IoMdMore
                  size={24}
                  css={s.moreIcon}
                  onClick={() => toggleMenu(topic.id)}
                />
              </div>

              {openMenuId === topic.id && (
                <div ref={menuRef} css={[s.moreMenu, s.moreMenuOpen]}>
                  <div
                    css={s.moreMenuItem}
                    onClick={() => {
                      setEditingTopic(topic);
                      setNewTopic(topic);
                      setShowModal(true);
                      setOpenMenuId(null);
                    }}
                  >
                    수정
                  </div>
                  <div
                    css={s.moreMenuItem}
                    onClick={() => handleDeleteTopic(topic.id)}
                  >
                    삭제
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>

        <div css={s.addButtonWrapper} style={{ display: "flex", gap: "10px" }}>
          <button
            css={s.addButton}
            onClick={() => {
              setEditingTopic(null);
              setNewTopic({
                title: "",
                type: "멀티",
                scale: "소규모",
                difficulty: "쉬움",
                participationCode: generateParticipationCode(),
              });
              setShowModal(true);
            }}
          >
            추가하기
          </button>

          <button
            css={{ ...s.participateButton, minWidth: "100px" }}
            onClick={() => {
              const code = prompt("참여코드를 입력하세요");
              if (code) navigate(`/topic/${code}`);
            }}
          >
            참여하기
          </button>
        </div>
      </main>

      {showModal && (
        <div css={s.modalOverlay}>
          <div css={s.modalCard}>
            <h2 css={s.modalTitle}>{editingTopic ? "토론 수정" : "토론 추가"}</h2>

            <input
              type="text"
              placeholder="제목"
              css={s.modalInput}
              value={newTopic.title}
              onChange={(e) => setNewTopic({ ...newTopic, title: e.target.value })}
            />

            <select
              css={s.modalInput}
              value={newTopic.type}
              onChange={(e) => setNewTopic({ ...newTopic, type: e.target.value })}
              disabled={!!editingTopic}
            >
              <option value="멀티">멀티</option>
              <option value="AI">AI</option>
            </select>

            {newTopic.type === "멀티" && (
              <>
                <select
                  css={s.modalInput}
                  value={newTopic.scale}
                  onChange={(e) => setNewTopic({ ...newTopic, scale: e.target.value })}
                >
                  <option value="소규모">소규모 (2~6명)</option>
                  <option value="중규모">중규모 (7~15명)</option>
                  <option value="대규모">대규모 (16~99명)</option>
                </select>

                <div css={{ display: "flex", gap: "8px", alignItems: "center" }}>
                  <input
                    type="text"
                    placeholder="참여코드"
                    css={s.participationInput}
                    value={newTopic.participationCode}
                    readOnly
                  />
                  <button
                    css={s.copyButton}
                    onClick={() => {
                      navigator.clipboard.writeText(newTopic.participationCode);
                      alert("참여코드가 복사되었습니다!");
                    }}
                  >
                    복사
                  </button>
                </div>
              </>
            )}

            {newTopic.type === "AI" && (
              <select
                css={s.modalInput}
                value={newTopic.difficulty}
                onChange={(e) =>
                  setNewTopic({ ...newTopic, difficulty: e.target.value })
                }
              >
                <option value="쉬움">쉬움</option>
                <option value="보통">보통</option>
                <option value="어려움">어려움</option>
              </select>
            )}

            <div css={s.modalButtonRow}>
              <button css={s.modalSendBtn} onClick={handleSaveTopic}>
                {editingTopic ? "수정" : "만들기"}
              </button>
              <button css={s.closeButton} onClick={() => setShowModal(false)}>
                닫기
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
