/* 하루 — main.js */

/* Avatar color by nickname */
const AVATAR_COLORS = [
  '#3b5bdb','#d9480f','#2b8a3e','#9c36b5',
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
  el.style.background = avatarColor(nick);
  if (!el.textContent.trim()) el.textContent = nick.charAt(0);
});

/* Like button */
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

/* Post item click → navigate */
document.querySelectorAll('.post-item[data-href]').forEach(item => {
  item.addEventListener('click', () => { location.href = item.dataset.href; });
});

/* Auto resize textarea */
document.querySelectorAll('textarea[data-autoresize]').forEach(ta => {
  ta.addEventListener('input', () => {
    ta.style.height = 'auto';
    ta.style.height = ta.scrollHeight + 'px';
  });
});
