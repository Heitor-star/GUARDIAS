// ── Modal Alterar Perfil ──────────────────────────────────
function abrirModal(id, nome, tipo) {
    document.getElementById('modalId').value         = id;
    document.getElementById('modalNome').textContent = nome;

    // Força a selecao correta no select
    const select = document.getElementById('modalTipo');
    select.value = '';           // limpa primeiro
    select.value = tipo;         // depois seta o valor atual

    // Garante que o option correto esta selecionado
    Array.from(select.options).forEach(opt => {
        opt.selected = (opt.value === tipo);
    });

    document.getElementById('modalPerfil').style.display = 'flex';

    // Foca no select apos abrir para garantir interacao
    setTimeout(() => select.focus(), 100);
}

function fecharModal() {
    document.getElementById('modalPerfil').style.display = 'none';
}

document.addEventListener('DOMContentLoaded', function () {

    // ── Abre modal ao clicar em Alterar Perfil ────────────
    document.querySelectorAll('.btn-abrir-modal').forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.preventDefault();
            abrirModal(this.dataset.id, this.dataset.nome, this.dataset.tipo);
        });
    });

    // ── Fecha modal clicando fora ─────────────────────────
    const modal = document.getElementById('modalPerfil');
    if (modal) {
        modal.addEventListener('click', function (e) {
            if (e.target === this) fecharModal();
        });
    }

    // ── Mascara CPF ───────────────────────────────────────
    const cpfInput = document.getElementById('cpf');
    if (cpfInput) {
        cpfInput.addEventListener('input', e => {
            let v = e.target.value.replace(/\D/g, '').substring(0, 11);
            v = v.replace(/(\d{3})(\d)/, '$1.$2');
            v = v.replace(/(\d{3})(\d)/, '$1.$2');
            v = v.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
            e.target.value = v;
        });
    }

    // ── Mascara Telefone ──────────────────────────────────
    const telInput = document.getElementById('telefone');
    if (telInput) {
        telInput.addEventListener('input', e => {
            let v = e.target.value.replace(/\D/g, '').substring(0, 11);
            v = v.replace(/^(\d{2})(\d)/, '($1) $2');
            v = v.replace(/(\d{5})(\d)/, '$1-$2');
            e.target.value = v;
        });
    }

    // ── Troca tamanho da pagina ───────────────────────────
    const selectSize = document.getElementById('selectSize');
    if (selectSize) {
        selectSize.addEventListener('change', function () {
            const url = new URL(window.location.href);
            url.searchParams.set('size', this.value);
            url.searchParams.set('page', '0');
            window.location.href = url.toString();
        });
    }
});