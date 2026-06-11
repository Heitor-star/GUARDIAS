document.addEventListener('DOMContentLoaded', function () {

    let dataAtual = new Date();
    let apiData = { eventos: [], inscritos: [], logado: false };

    const grid  = document.getElementById('days-grid');
    const label = document.getElementById('mes-ano');
    const obs   = document.getElementById('obs-content');

    async function carregar() {
        try {
            const res = await fetch('/api/eventos');
            apiData = await res.json();

            // Normaliza a data de cada evento para string "YYYY-MM-DD"
            apiData.eventos = apiData.eventos.map(e => {
                if (Array.isArray(e.data)) {
                    const [ano, mes, dia] = e.data;
                    e.data = `${ano}-${String(mes).padStart(2,'0')}-${String(dia).padStart(2,'0')}`;
                } else if (typeof e.data === 'string') {
                    e.data = e.data.substring(0, 10);
                }
                return e;
            });

            render();

            // Mensagem de inscricao confirmada
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.has('inscrito')) {
                obs.innerHTML = "<div style='background:#10b981;color:white;padding:10px;border-radius:5px;margin-bottom:10px;text-align:center;'>&#x2705; Inscricao confirmada!</div>";
            }
        } catch (err) {
            console.error('Erro ao carregar eventos:', err);
        }
    }

    function render() {
        grid.innerHTML = '';

        const ano = dataAtual.getFullYear();
        const mes = dataAtual.getMonth();

        label.innerText = dataAtual.toLocaleDateString('pt-BR', {
            month: 'long', year: 'numeric'
        }).toUpperCase();

        const primeiroDia = new Date(ano, mes, 1).getDay();
        const ultimoDia   = new Date(ano, mes + 1, 0).getDate();

        // Celulas vazias antes do primeiro dia
        for (let i = 0; i < primeiroDia; i++) {
            const vazio = document.createElement('div');
            vazio.className = 'day empty';
            grid.appendChild(vazio);
        }

        // Dias do mes
        for (let dia = 1; dia <= ultimoDia; dia++) {
            const div = document.createElement('div');
            div.className = 'day';
            div.innerText = dia;

            const dataS = `${ano}-${String(mes + 1).padStart(2,'0')}-${String(dia).padStart(2,'0')}`;
            const evts  = apiData.eventos.filter(e => e.data === dataS);
            const estaInscrito = evts.some(e => apiData.inscritos && apiData.inscritos.includes(e.id));

            if (evts.length > 0) {
                div.classList.add('has-event');
                if (estaInscrito) div.classList.add('inscrito-dia');
                div.onclick = () => mostrarDetalhes(evts);
            } else {
                div.onclick = () => {
                    obs.innerHTML = "<p style='opacity:0.6'>Nenhum evento neste dia.</p>";
                };
            }

            grid.appendChild(div);
        }
    }

    function mostrarDetalhes(evts) {
        obs.innerHTML = evts.map(e => {
            const inscrito = apiData.inscritos && apiData.inscritos.includes(e.id);
            let acao = '';

            if (!apiData.logado) {
                acao = `<a href="/api/eventos/inscrever/${e.id}" class="btn-insc btn-inscrever"
                           style="text-align:center;text-decoration:none;display:block">
                            Login para Participar
                        </a>`;
            } else if (inscrito) {
                acao = `<form action="/api/eventos/cancelar" method="post">
                            <input type="hidden" name="eventoId" value="${e.id}">
                            <button class="btn-insc btn-cancelar">Cancelar Minha Vaga</button>
                        </form>`;
            } else {
                acao = `<a href="/api/eventos/inscrever/${e.id}" class="btn-insc btn-inscrever"
                           style="text-align:center;text-decoration:none;display:block">
                            Confirmar Inscricao
                        </a>`;
            }

            return `
                <div class="evt-card ${inscrito ? 'is-member' : ''}">
                    <h4 style="margin:0">${inscrito ? '&#11088; ' : ''}${e.titulo}</h4>
                    <p style="font-size:0.85em;margin:10px 0;">${e.descricao ?? ''}</p>
                    ${acao}
                </div>`;
        }).join('');
    }

    document.getElementById('btn-prev').onclick = () => {
        dataAtual.setMonth(dataAtual.getMonth() - 1);
        render();
    };

    document.getElementById('btn-next').onclick = () => {
        dataAtual.setMonth(dataAtual.getMonth() + 1);
        render();
    };

    carregar();
});