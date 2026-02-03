document.addEventListener('DOMContentLoaded', function() {
    const randomBtn = document.getElementById('randomBtn');
    const filterForm = document.getElementById('filter-form');
    const loadingEl = document.getElementById('loading');
    const resultEl = document.getElementById('problem-result');
    const errorEl = document.getElementById('error-message');

    randomBtn.addEventListener('click', fetchRandomProblem);

    async function fetchRandomProblem() {
        // Show loading, hide others
        loadingEl.style.display = 'flex';
        resultEl.innerHTML = '';
        errorEl.style.display = 'none';
        randomBtn.disabled = true;

        // Build query params
        const formData = new FormData(filterForm);
        const params = new URLSearchParams();

        params.append('minTier', formData.get('minTier'));
        params.append('maxTier', formData.get('maxTier'));

        const excludeUser = formData.get('excludeUser');
        if (excludeUser && excludeUser.trim()) {
            params.append('excludeUser', excludeUser.trim());
        }

        // Get all selected tags
        const selectedTags = formData.getAll('tags');
        selectedTags.forEach(tag => params.append('tags', tag));

        try {
            const response = await fetch(`/api/problems/random?${params.toString()}`);

            if (response.status === 404) {
                showError('조건에 맞는 문제를 찾을 수 없습니다. 검색 조건을 조정해보세요.');
                return;
            }

            if (!response.ok) {
                const error = await response.json();
                showError(error.message || '문제를 가져오는 중 오류가 발생했습니다.');
                return;
            }

            const problem = await response.json();
            displayProblem(problem);

        } catch (error) {
            console.error('Error:', error);
            showError('서버와 연결할 수 없습니다. 잠시 후 다시 시도해주세요.');
        } finally {
            loadingEl.style.display = 'none';
            randomBtn.disabled = false;
        }
    }

    function displayProblem(problem) {
        const tierClass = getTierClass(problem.tierLevel);
        const tierBadgeClass = getTierBadgeClass(problem.tierLevel);

        const html = `
            <div class="problem-card ${tierClass}">
                <div class="problem-header">
                    <span class="tier-badge ${tierBadgeClass}">${problem.tier.displayName}</span>
                    <span class="problem-id">#${problem.problemId}</span>
                </div>

                <h2 class="problem-title">${escapeHtml(problem.title)}</h2>

                <div class="problem-tags">
                    ${problem.tags.map(tag => `<span class="tag">${escapeHtml(tag)}</span>`).join('')}
                </div>

                <div class="problem-stats">
                    <span>맞은 사람: ${problem.solvedCount.toLocaleString()}명</span>
                    <span>평균 시도: ${problem.averageTries.toFixed(2)}회</span>
                </div>

                <div class="problem-actions">
                    <a href="${problem.url}" target="_blank" class="btn-solve">
                        백준에서 풀기
                    </a>
                    <button type="button" class="btn-next" onclick="document.getElementById('randomBtn').click()">
                        다른 문제
                    </button>
                </div>
            </div>
        `;

        resultEl.innerHTML = html;
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
        errorEl.textContent = message;
        errorEl.style.display = 'block';
    }

    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
});
