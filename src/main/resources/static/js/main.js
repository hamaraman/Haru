/* 하루 — main.js */

/* ── 닉네임 기반 아바타 색상 ── */
const AVATAR_COLORS = [
  '#6c4cf0','#4263eb','#d9480f','#2b8a3e','#9c36b5',
  '#1971c2','#c2255c','#0c8599','#e67700'
];

function avatarColor(text) {
  if (!text) return AVATAR_COLORS[0];
  let h = 0;
  for (let i = 0; i < text.length; i++) h = text.charCodeAt(i) + ((h << 5) - h);
  return AVATAR_COLORS[Math.abs(h) % AVATAR_COLORS.length];
}

document.querySelectorAll('[data-avatar]').forEach(el => {
  const nick = el.dataset.avatar || '';
  // 프로필 사진(background-image)이 지정된 경우 색상으로 덮어쓰지 않는다
  if (!el.style.backgroundImage && !el.querySelector('img')) {
    el.style.background = avatarColor(nick);
    if (!el.textContent.trim()) el.textContent = nick.charAt(0);
  }
});

/* ── 프로필 드롭다운 토글 (바깥 클릭 시 닫힘) ── */
const profileMenu = document.getElementById('profileMenu');
const profileTrigger = document.getElementById('profileTrigger');
if (profileMenu && profileTrigger) {
  profileTrigger.addEventListener('click', (e) => {
    e.stopPropagation();
    profileMenu.classList.toggle('open');
  });
  document.addEventListener('click', (e) => {
    if (!profileMenu.contains(e.target)) profileMenu.classList.remove('open');
  });
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') profileMenu.classList.remove('open');
  });
}

/* ── 좋아요 버튼 ── */
const likeBtn = document.getElementById('like-btn');
if (likeBtn) {
  likeBtn.addEventListener('click', async () => {
    const postId = likeBtn.dataset.postId;
    try {
      const res = await fetch(`/post/${postId}/like`, { method: 'POST' });
      if (res.status === 401) {
        alert('로그인이 필요합니다.');
        location.href = '/user/login';
        return;
      }
      const data = await res.json();
      document.getElementById('like-count').textContent = data.likeCount;
      likeBtn.classList.toggle('liked', data.liked);
      const icon = likeBtn.querySelector('.like-icon');
      if (icon) icon.textContent = data.liked ? '♥' : '♡';
    } catch (e) {
      console.error(e);
    }
  });
}

/* ── 카드/리스트 클릭 → 이동 ── */
document.querySelectorAll('[data-href]').forEach(item => {
  item.addEventListener('click', () => { location.href = item.dataset.href; });
});

/* ── textarea 자동 높이 ── */
document.querySelectorAll('textarea[data-autoresize]').forEach(ta => {
  ta.addEventListener('input', () => {
    ta.style.height = 'auto';
    ta.style.height = ta.scrollHeight + 'px';
  });
});
