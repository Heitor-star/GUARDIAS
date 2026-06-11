/* visitantes.js
 * Gráfico inicializado com dados do Thymeleaf (últimos 30 dias).
 * Ao trocar o período, busca novos dados via AJAX em /admin/visitantes/dados
 * — endpoint protegido pelo Spring Security (ROLE_ADMIN ou ROLE_OPERADOR).
 */

document.addEventListener('DOMContentLoaded', function () {

    // ── Gráfico inicial (dados do Thymeleaf) ─────────────────────────────
    const chart = new Chart(document.getElementById('graficoVisitantes'), {
        type: 'bar',
        data: {
            labels: labelsData,
            datasets: [{
                label: 'Visitantes únicos',
                data: valoresData,
                backgroundColor: 'rgba(26, 115, 232, 0.2)',
                borderColor: '#1a73e8',
                borderWidth: 2,
                borderRadius: 6,
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: ctx => ` ${ctx.parsed.y} visitante(s)`
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { stepSize: 1 },
                    grid: { color: '#f0f0f0' }
                },
                x: {
                    grid: { display: false }
                }
            }
        }
    });

    // ── Referências DOM ──────────────────────────────────────────────────
    const btnsPeriodo   = document.querySelectorAll('.btn-periodo');
    const painelCustom  = document.getElementById('periodo-custom');
    const inputInicio   = document.getElementById('dataInicio');
    const inputFim      = document.getElementById('dataFim');
    const btnAplicar    = document.getElementById('btnAplicar');
    const titulo        = document.getElementById('grafico-titulo');

    // Define data máxima como hoje nos inputs
    const hoje = toISO(new Date());
    inputFim.max   = hoje;
    inputFim.value = hoje;
    inputInicio.value = toISO(diasAtras(30));

    // ── Botões de período rápido ─────────────────────────────────────────
    btnsPeriodo.forEach(btn => {
        btn.addEventListener('click', function () {
            btnsPeriodo.forEach(b => b.classList.remove('active'));
            this.classList.add('active');

            const periodo = this.dataset.periodo;

            if (periodo === 'custom') {
                painelCustom.style.display = 'flex';
                return; // aguarda clique em Aplicar
            }

            painelCustom.style.display = 'none';

            const { inicio, fim, label } = calcularPeriodo(periodo);
            titulo.textContent = label;
            buscarDados(inicio, fim);
        });
    });

    // ── Botão Aplicar (período personalizado) ────────────────────────────
    btnAplicar.addEventListener('click', function () {
        const inicio = inputInicio.value;
        const fim    = inputFim.value;

        if (!inicio || !fim) {
            alert('Selecione as duas datas.');
            return;
        }
        if (inicio > fim) {
            alert('A data inicial deve ser anterior à data final.');
            return;
        }

        titulo.textContent = `Visitantes por Dia — ${exibir(inicio)} até ${exibir(fim)}`;
        buscarDados(inicio, fim);
    });

    // ── AJAX — busca dados no backend ────────────────────────────────────
    function buscarDados(inicio, fim) {
        const url = `/admin/visitantes/dados?inicio=${inicio}&fim=${fim}`;

        fetch(url, {
            method: 'GET',
            credentials: 'same-origin', // envia cookies de sessão (Spring Security)
            headers: { 'Accept': 'application/json' }
        })
        .then(res => {
            if (res.status === 401 || res.status === 403) {
                alert('Sessão expirada. Faça login novamente.');
                window.location.href = '/login';
                return null;
            }
            if (!res.ok) throw new Error('Erro ao buscar dados.');
            return res.json();
        })
        .then(data => {
            if (!data) return;
            chart.data.labels = data.labels;
            chart.data.datasets[0].data = data.valores;
            chart.update();
        })
        .catch(err => {
            console.error('Erro:', err);
            alert('Não foi possível carregar os dados. Tente novamente.');
        });
    }

    // ── Utilitários ──────────────────────────────────────────────────────

    function calcularPeriodo(periodo) {
        const fim   = new Date();
        let inicio  = new Date();
        let label   = '';

        if (periodo === '30d') {
            inicio = diasAtras(30);
            label  = 'Visitantes por Dia — Últimos 30 dias';
        } else if (periodo === '6m') {
            inicio = new Date(fim);
            inicio.setMonth(inicio.getMonth() - 6);
            label  = 'Visitantes por Dia — Últimos 6 Meses';
        } else if (periodo === '1a') {
            inicio = new Date(fim);
            inicio.setFullYear(inicio.getFullYear() - 1);
            label  = 'Visitantes por Dia — Último Ano';
        }

        return { inicio: toISO(inicio), fim: toISO(fim), label };
    }

    function diasAtras(n) {
        const d = new Date();
        d.setDate(d.getDate() - n);
        return d;
    }

    // Formata Date → 'YYYY-MM-DD'
    function toISO(date) {
        return date.toISOString().split('T')[0];
    }

    // Formata 'YYYY-MM-DD' → 'DD/MM/YYYY'
    function exibir(str) {
        const [y, m, d] = str.split('-');
        return `${d}/${m}/${y}`;
    }

});