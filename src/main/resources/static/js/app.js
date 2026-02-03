document.addEventListener('DOMContentLoaded', function() {
    // --- DOM Elements ---
    const randomBtn = document.getElementById('randomBtn');
    const filterForm = document.getElementById('filter-form');
    const loadingEl = document.getElementById('loading');
    const resultEl = document.getElementById('problem-result');
    const errorEl = document.getElementById('error-message');
    
    const presetBtns = document.querySelectorAll('.btn-preset');
    const minTierSelect = document.getElementById('minTier');
    const maxTierSelect = document.getElementById('maxTier');

    const tabBtns = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');
    const historyList = document.getElementById('history-list');
    const clearHistoryBtn = document.getElementById('clearHistoryBtn');

    const streakValue = document.getElementById('streakValue');
    const todaySolvedValue = document.getElementById('todaySolvedValue');
    const totalSolvedValue = document.getElementById('totalSolvedValue');
    const tierChart = document.getElementById('tier-chart');
    const mostSolvedTier = document.getElementById('mostSolvedTier');
    const recentSolvedCount = document.getElementById('recentSolvedCount');

    const themeToggleBtn = document.getElementById('themeToggle');
    const shortcutInfoBtn = document.getElementById('shortcutInfoBtn');
    const settingsBtn = document.getElementById('settingsBtn');
    const shortcutModal = document.getElementById('shortcutModal');
    const settingsModal = document.getElementById('settingsModal');
    const closeBtns = document.querySelectorAll('.close-modal');
    
    const soundToggle = document.getElementById('soundToggle');
    const confettiToggle = document.getElementById('confettiToggle');
    const resetAllDataBtn = document.getElementById('resetAllDataBtn');

    // --- State ---
    let timerInterval;
    let startTime;
    let elapsedTime = 0;
    let currentProblem = null;
    let userStats = safeLoad('userStats', {
        streak: 0,
        lastSolvedDate: null,
        todaySolved: 0,
        totalSolved: 0,
        tierCounts: {}
    });
    let settings = safeLoad('appSettings', { sound: true, confetti: true });

    // Audio
    const successSound = new Audio('https://assets.mixkit.co/active_storage/sfx/1435/1435-preview.mp3');

    // --- Initialization ---
    initTheme();
    updateStatsUI();
    loadHistory();
    applySettings();
    
    // --- Event Listeners ---
    randomBtn.addEventListener('click', fetchRandomProblem);
    
    presetBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const tier = btn.dataset.tier;
            setTierRange(tier);
        });
    });

    tabBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            tabBtns.forEach(b => b.classList.remove('active'));
            tabContents.forEach(c => c.style.display = 'none');
            
            btn.classList.add('active');
            const tabId = btn.dataset.tab;
            document.getElementById(`${tabId}-section`).style.display = 'block';
            
            if (tabId === 'stats') renderChart();
        });
    });

    clearHistoryBtn.addEventListener('click', () => {
        if(confirm('히스토리를 초기화하시겠습니까?')) {
            localStorage.removeItem('problemHistory');
            loadHistory();
        }
    });

    themeToggleBtn.addEventListener('click', toggleTheme);
    shortcutInfoBtn.addEventListener('click', () => openModal(shortcutModal));
    settingsBtn.addEventListener('click', () => openModal(settingsModal));
    
    closeBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            shortcutModal.style.display = 'none';
            settingsModal.style.display = 'none';
        });
    });

    window.addEventListener('click', (e) => {
        if (e.target === shortcutModal) shortcutModal.style.display = 'none';
        if (e.target === settingsModal) settingsModal.style.display = 'none';
    });

    soundToggle.addEventListener('change', (e) => {
        settings.sound = e.target.checked;
        saveSettings();
    });

    confettiToggle.addEventListener('change', (e) => {
        settings.confetti = e.target.checked;
        saveSettings();
    });

    resetAllDataBtn.addEventListener('click', () => {
        if(confirm('정말로 모든 데이터를 초기화하시겠습니까?')) {
            localStorage.clear();
            location.reload();
        }
    });

    document.addEventListener('keydown', (e) => {
        if (e.target.tagName === 'INPUT' || e.target.tagName === 'SELECT' || e.target.tagName === 'TEXTAREA') {
            if (e.target.tagName === 'TEXTAREA' && e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                e.target.blur();
            }
            return;
        }

        switch(e.code) {
            case 'Space':
                e.preventDefault();
                if (!randomBtn.disabled) randomBtn.click();
                break;
            case 'KeyT':
                toggleTimer();
                break;
            case 'KeyC':
                if (currentProblem) copyLink(currentProblem.url);
                break;
            case 'KeyM':
                e.preventDefault();
                const memoArea = document.querySelector('.memo-textarea');
                if (memoArea) memoArea.focus();
                break;
            case 'Escape':
                shortcutModal.style.display = 'none';
                settingsModal.style.display = 'none';
                break;
        }
    });

    // --- Core Functions ---

    function safeLoad(key, defaultValue) {
        try {
            const item = localStorage.getItem(key);
            return item ? JSON.parse(item) : defaultValue;
        } catch (e) {
            console.error(`Error loading ${key}:`, e);
            return defaultValue;
        }
    }

    function initTheme() {
        const savedTheme = localStorage.getItem('theme') || 'dark';
        document.documentElement.setAttribute('data-theme', savedTheme);
        updateThemeIcon(savedTheme);
    }

    function toggleTheme() {
        const currentTheme = document.documentElement.getAttribute('data-theme');
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        
        document.documentElement.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        updateThemeIcon(newTheme);
    }

    function updateThemeIcon(theme) {
        themeToggleBtn.textContent = theme === 'dark' ? '☀️' : '🌙';
    }

    function setTierRange(tier) {
        let min, max;
        switch(tier) {
            case 'BRONZE': min = 'BRONZE_5'; max = 'BRONZE_1'; break;
            case 'SILVER': min = 'SILVER_5'; max = 'SILVER_1'; break;
            case 'GOLD': min = 'GOLD_5'; max = 'GOLD_1'; break;
            case 'PLATINUM': min = 'PLATINUM_5'; max = 'PLATINUM_1'; break;
            case 'DIAMOND': min = 'DIAMOND_5'; max = 'DIAMOND_1'; break;
        }
        
        if (min && max) {
            minTierSelect.value = min;
            maxTierSelect.value = max;
            
            presetBtns.forEach(b => b.style.opacity = '0.5');
            const targetBtn = Array.from(presetBtns).find(b => b.dataset.tier === tier);
            if(targetBtn) targetBtn.style.opacity = '1';
            
            setTimeout(() => presetBtns.forEach(b => b.style.opacity = '1'), 300);
        }
    }

    async function fetchRandomProblem() {
        stopTimer();
        currentProblem = null;
        
        loadingEl.style.display = 'flex';
        resultEl.innerHTML = '';
        errorEl.style.display = 'none';
        randomBtn.disabled = true;

        const formData = new FormData(filterForm);
        const params = new URLSearchParams();

        params.append('minTier', formData.get('minTier'));
        params.append('maxTier', formData.get('maxTier'));

        const excludeUser = formData.get('excludeUser');
        if (excludeUser && excludeUser.trim()) {
            params.append('excludeUser', excludeUser.trim());
        }

        const selectedTags = formData.getAll('tags');
        selectedTags.forEach(tag => params.append('tags', tag));

        const excludedTags = formData.getAll('excludeTags');
        excludedTags.forEach(tag => params.append('excludeTags', tag));

        try {
            const response = await fetch(`/api/problems/random?${params.toString()}`);

            if (response.status === 404) {
                showError('⚠️ 조건에 맞는 문제를 찾을 수 없습니다.<br>티어 범위를 넓히거나 태그 조건을 변경해보세요.');
                return;
            }

            if (!response.ok) {
                const error = await response.json();
                showError(`⚠️ 오류 발생: ${error.message || '알 수 없는 오류'}`);
                return;
            }

            const problem = await response.json();
            currentProblem = problem;
            displayProblem(problem);
            addToHistory(problem);
            startTimer();

        } catch (error) {
            console.error('Fetch Error:', error);
            showError('🚫 <strong>서버와 연결할 수 없습니다.</strong><br>서버가 실행 중인지 확인해주세요.<br>(터미널에서 <code>gradlew bootRun</code> 실행)');
        } finally {
            loadingEl.style.display = 'none';
            randomBtn.disabled = false;
        }
    }

    function displayProblem(problem) {
        const tierClass = getTierClass(problem.tierLevel);
        const tierBadgeClass = getTierBadgeClass(problem.tierLevel);
        const savedMemo = getMemo(problem.problemId);
        
        // tierDisplayName 사용 (백엔드에서 추가됨)
        const tierName = problem.tierDisplayName || problem.tier; 

        const html = `
            <div class="problem-card ${tierClass}">
                <div class="problem-header">
                    <span class="tier-badge ${tierBadgeClass}">${tierName}</span>
                    <span class="problem-id">#${problem.problemId}</span>
                    <button type="button" class="btn-copy" onclick="copyLink('${problem.url}')" title="링크 복사 (C)">🔗</button>
                </div>

                <h2 class="problem-title">${escapeHtml(problem.title)}</h2>

                <div class="stopwatch">
                    <div class="time-display" id="timeDisplay">00:00:00</div>
                    <button type="button" class="btn-timer" onclick="toggleTimer()" id="timerBtn">일시정지</button>
                </div>

                <div class="memo-area">
                    <div class="memo-header">
                        <span>📝 메모장</span>
                        <span id="memoStatus" style="color: var(--success); font-size: 0.8rem;"></span>
                    </div>
                    <textarea class="memo-textarea" placeholder="풀이 아이디어, 반례 등을 기록하세요..." oninput="saveMemo(${problem.problemId}, this.value)">${escapeHtml(savedMemo)}</textarea>
                </div>

                <div class="spoiler-hint">👇 태그를 클릭하면 보입니다</div>
                <div class="problem-tags spoiler" onclick="this.classList.remove('spoiler')">
                    ${problem.tags.map(tag => `<span class="tag">${escapeHtml(tag)}</span>`).join('')}
                </div>

                <div class="problem-stats">
                    <span>맞은 사람: ${problem.solvedCount.toLocaleString()}명</span>
                    <span>평균 시도: ${problem.averageTries.toFixed(2)}회</span>
                </div>

                <div class="problem-actions">
                    <a href="${problem.url}" target="_blank" class="btn-solve">
                        백준에서 풀기 ↗
                    </a>
                    <button type="button" class="btn-complete" onclick="markAsSolved(${problem.problemId}, '${problem.tier}')">
                        ✅ 해결 완료
                    </button>
                    <button type="button" class="btn-next" onclick="document.getElementById('randomBtn').click()">
                        다른 문제 (Space)
                    </button>
                </div>
            </div>
        `;

        resultEl.innerHTML = html;
        
        window.toggleTimer = toggleTimer;
        window.copyLink = copyLink;
        window.saveMemo = saveMemo;
        window.markAsSolved = markAsSolved;
    }

    // --- Stats & Logic ---

    function saveStats() {
        localStorage.setItem('userStats', JSON.stringify(userStats));
        updateStatsUI();
    }

    function updateStatsUI() {
        const today = new Date().toDateString();
        if (userStats.lastSolvedDate) {
            const lastDate = new Date(userStats.lastSolvedDate);
            const diffTime = Math.abs(new Date(today) - lastDate);
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); 
            
            if (diffDays > 1) {
                userStats.streak = 0;
            }
            
            if (userStats.lastSolvedDate !== today) {
                userStats.todaySolved = 0;
            }
        }

        streakValue.textContent = userStats.streak;
        todaySolvedValue.textContent = userStats.todaySolved;
        totalSolvedValue.textContent = userStats.totalSolved;
    }

    window.markAsSolved = function(problemId, tierName) {
        const today = new Date().toDateString();
        
        if (userStats.lastSolvedDate !== today) {
            userStats.streak++;
            userStats.todaySolved = 0;
        }
        
        userStats.lastSolvedDate = today;
        userStats.todaySolved++;
        userStats.totalSolved++;
        
        // tierName이 "BRONZE_5" 같은 문자열로 올 수 있음
        const tierGroup = tierName.split('_')[0];
        userStats.tierCounts[tierGroup] = (userStats.tierCounts[tierGroup] || 0) + 1;
        
        saveStats();
        
        const historyItems = document.querySelectorAll('.history-item');
        historyItems.forEach(item => {
            if (item.querySelector('.problem-id').textContent === `#${problemId}`) {
                item.classList.add('solved');
            }
        });

        if (settings.sound) successSound.play().catch(() => {});
        if (settings.confetti) fireConfetti();
        
        const btn = document.querySelector('.btn-complete');
        if(btn) {
            btn.textContent = '🎉 축하합니다!';
            btn.disabled = true;
            btn.style.backgroundColor = '#6c5ce7';
        }
    };

    function renderChart() {
        const tiers = ['BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'DIAMOND', 'RUBY'];
        const colors = ['#ad5600', '#435f7a', '#ec9a00', '#27e2a4', '#00b4fc', '#ff0062'];
        
        let maxCount = 0;
        let mostSolved = '-';
        let maxVal = 0;

        tiers.forEach(t => {
            const count = userStats.tierCounts[t] || 0;
            if (count > maxCount) maxCount = count;
            if (count > maxVal) {
                maxVal = count;
                mostSolved = t;
            }
        });

        mostSolvedTier.textContent = mostSolved;
        recentSolvedCount.textContent = userStats.totalSolved;

        tierChart.innerHTML = tiers.map((tier, index) => {
            const count = userStats.tierCounts[tier] || 0;
            const height = maxCount > 0 ? (count / maxCount * 100) : 0;
            const displayHeight = Math.max(height, 2); 
            
            return `
                <div class="chart-bar-container">
                    <div class="chart-value">${count}</div>
                    <div class="chart-bar" style="height: ${displayHeight}%; background-color: ${colors[index]}"></div>
                    <div class="chart-label">${tier[0]}</div>
                </div>
            `;
        }).join('');
    }

    // --- Memo ---
    window.saveMemo = function(problemId, content) {
        const memos = safeLoad('problemMemos', {});
        memos[problemId] = content;
        localStorage.setItem('problemMemos', JSON.stringify(memos));
        
        const status = document.getElementById('memoStatus');
        status.textContent = '저장됨';
        setTimeout(() => status.textContent = '', 1000);
    };

    function getMemo(problemId) {
        const memos = safeLoad('problemMemos', {});
        return memos[problemId] || '';
    }

    // --- Settings ---
    function applySettings() {
        soundToggle.checked = settings.sound;
        confettiToggle.checked = settings.confetti;
    }

    function saveSettings() {
        localStorage.setItem('appSettings', JSON.stringify(settings));
    }

    function openModal(modal) {
        modal.style.display = 'block';
    }

    // --- Confetti ---
    function fireConfetti() {
        const canvas = document.getElementById('confetti-canvas');
        const ctx = canvas.getContext('2d');
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;

        const particles = [];
        const particleCount = 200;

        for (let i = 0; i < particleCount; i++) {
            particles.push({
                x: canvas.width / 2,
                y: canvas.height / 2,
                vx: (Math.random() - 0.5) * 20,
                vy: (Math.random() - 0.5) * 20,
                life: Math.random() * 100 + 60,
                color: `hsl(${Math.random() * 360}, 100%, 50%)`,
                size: Math.random() * 6 + 2,
                gravity: 0.5
            });
        }

        function animate() {
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            let activeParticles = 0;

            particles.forEach(p => {
                if (p.life > 0) {
                    p.x += p.vx;
                    p.y += p.vy;
                    p.vy += p.gravity;
                    p.life--;
                    p.size *= 0.96;
                    
                    ctx.fillStyle = p.color;
                    ctx.beginPath();
                    ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
                    ctx.fill();
                    activeParticles++;
                }
            });

            if (activeParticles > 0) {
                requestAnimationFrame(animate);
            } else {
                ctx.clearRect(0, 0, canvas.width, canvas.height);
            }
        }

        animate();
    }

    window.addEventListener('resize', () => {
        const canvas = document.getElementById('confetti-canvas');
        if(canvas) {
            canvas.width = window.innerWidth;
            canvas.height = window.innerHeight;
        }
    });

    // --- Helpers ---
    function startTimer() {
        startTime = Date.now() - elapsedTime;
        timerInterval = setInterval(updateTimer, 1000);
        updateTimer();
    }

    function stopTimer() {
        clearInterval(timerInterval);
        elapsedTime = 0;
    }

    function toggleTimer() {
        const btn = document.getElementById('timerBtn');
        if (!btn) return;

        if (timerInterval) {
            clearInterval(timerInterval);
            timerInterval = null;
            elapsedTime = Date.now() - startTime;
            btn.textContent = '재개';
            btn.style.color = '#ff7675';
            btn.style.borderColor = '#ff7675';
        } else {
            startTime = Date.now() - elapsedTime;
            timerInterval = setInterval(updateTimer, 1000);
            btn.textContent = '일시정지';
            btn.style.color = '';
            btn.style.borderColor = '';
        }
    }

    function updateTimer() {
        const now = Date.now();
        const diff = now - startTime;
        
        const hours = Math.floor(diff / 3600000);
        const minutes = Math.floor((diff % 3600000) / 60000);
        const seconds = Math.floor((diff % 60000) / 1000);
        
        const display = `${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;
        const displayEl = document.getElementById('timeDisplay');
        if (displayEl) displayEl.textContent = display;
    }

    function pad(num) {
        return num.toString().padStart(2, '0');
    }

    function copyLink(url) {
        navigator.clipboard.writeText(url).then(() => {
            const btn = document.querySelector('.btn-copy');
            if(btn) {
                const originalText = btn.innerHTML;
                btn.innerHTML = '✅';
                setTimeout(() => btn.innerHTML = originalText, 1000);
            }
        }).catch(err => {
            console.error('복사 실패:', err);
        });
    }

    function addToHistory(problem) {
        let history = safeLoad('problemHistory', []);
        history = history.filter(item => item.id !== problem.problemId);
        
        // tierDisplayName 사용
        const tierName = problem.tierDisplayName || problem.tier;

        history.unshift({
            id: problem.problemId,
            title: problem.title,
            tier: tierName,
            tierColor: problem.tierColor,
            url: problem.url,
            date: new Date().toLocaleDateString()
        });
        
        if (history.length > 20) history.pop();
        
        localStorage.setItem('problemHistory', JSON.stringify(history));
        loadHistory();
    }

    function loadHistory() {
        const history = safeLoad('problemHistory', []);
        
        if (history.length === 0) {
            historyList.innerHTML = '<p style="text-align:center; color:var(--text-muted); padding:1rem;">아직 기록이 없습니다.</p>';
            return;
        }
        
        historyList.innerHTML = history.map(item => `
            <a href="${item.url}" target="_blank" class="history-item">
                <div class="tier-dot" style="background-color: ${item.tierColor}"></div>
                <span class="problem-id">#${item.id}</span>
                <span class="title">${escapeHtml(item.title)}</span>
                <span class="time">${item.date}</span>
            </a>
        `).join('');
    }

    function getTierClass(level) {
        if (level >= 1 && level <= 5) return 'tier-bronze';
        if (level >= 6 && level <= 10) return 'tier-silver';
        if (level >= 11 && level <= 15) return 'tier-gold';
        if (level >= 16 && level <= 20) return 'tier-platinum';
        if (level >= 21 && level <= 25) return 'tier-diamond';
        if (level >= 26 && level <= 30) return 'tier-ruby';
        return '';
    }

    function getTierBadgeClass(level) {
        if (level >= 1 && level <= 5) return 'bronze';
        if (level >= 6 && level <= 10) return 'silver';
        if (level >= 11 && level <= 15) return 'gold';
        if (level >= 16 && level <= 20) return 'platinum';
        if (level >= 21 && level <= 25) return 'diamond';
        if (level >= 26 && level <= 30) return 'ruby';
        return '';
    }

    function showError(message) {
        errorEl.innerHTML = message;
        errorEl.style.display = 'block';
    }

    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
});
