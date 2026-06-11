document.addEventListener('DOMContentLoaded', function () {

    // ── Abas ─────────────────────────────────────────────
    window.openTab = function (e, tabId) {
        document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.getElementById('tab-' + tabId).classList.add('active');
        if (e && e.currentTarget) e.currentTarget.classList.add('active');
    };

    // Abre aba correta pelo hash da URL (ex: /meu-perfil#senha)
    const hash = window.location.hash.replace('#', '');
    if (hash) {
        const btn = document.querySelector('.tab-btn[onclick*="' + hash + '"]');
        if (btn) btn.click();
    }

    // ── Validação de senha ────────────────────────────────
    const novaSenha     = document.getElementById('novaSenha');
    const confirmar     = document.getElementById('confirmarSenha');
    const confirmarErro = document.getElementById('confirmar-erro');

    const reqs = {
        tamanho:   { el: document.getElementById('req-tamanho'),   fn: v => v.length >= 8 },
        maiuscula: { el: document.getElementById('req-maiuscula'),  fn: v => /[A-Z]/.test(v) },
        numero:    { el: document.getElementById('req-numero'),     fn: v => /[0-9]/.test(v) },
        especial:  { el: document.getElementById('req-especial'),   fn: v => /[!@#$%^&*(),.?":{}|<>\-_=+\[\]\\/]/.test(v) }
    };

    function checarRequisitos(senha) {
        let valido = true;
        Object.values(reqs).forEach(r => {
            if (!r.el) return;
            const ok = r.fn(senha);
            r.el.style.color = ok ? '#6ee7b7' : 'rgba(255,255,255,0.4)';
            r.el.textContent = (ok ? '✓ ' : '✗ ') + r.el.textContent.substring(2);
            if (!ok) valido = false;
        });
        return valido;
    }

    if (novaSenha) {
        novaSenha.addEventListener('input', function () {
            checarRequisitos(novaSenha.value);
            if (confirmar && confirmar.value) checarConfirmar();
        });
    }

    function checarConfirmar() {
        if (!confirmar || !novaSenha) return true;
        const ok = confirmar.value === novaSenha.value;
        confirmar.style.borderColor = ok ? '#10b981' : '#ef4444';
        if (confirmarErro) confirmarErro.textContent = ok ? '' : 'As senhas não coincidem.';
        return ok;
    }

    if (confirmar) confirmar.addEventListener('input', checarConfirmar);

    const formSenha = document.getElementById('formSenha');
    if (formSenha) {
        formSenha.addEventListener('submit', function (e) {
            const valido = checarRequisitos(novaSenha.value);
            const confirmOk = checarConfirmar();
            if (!valido || !confirmOk) e.preventDefault();
        });
    }

    // ── Preview de foto ───────────────────────────────────
    const fotoInput   = document.getElementById('fotoInput');
    const fotoPreview = document.getElementById('fotoPreview');

    if (fotoInput && fotoPreview) {
        fotoInput.addEventListener('change', function () {
            const file = fotoInput.files[0];
            if (!file) return;
            const reader = new FileReader();
            reader.onload = e => fotoPreview.src = e.target.result;
            reader.readAsDataURL(file);
        });
    }

    // ── Máscara Telefone ──────────────────────────────────
    const telInput = document.querySelector('input[name="telefone"]');
    if (telInput) {
        telInput.addEventListener('input', function (e) {
            let v = e.target.value.replace(/\D/g, '').substring(0, 11);
            v = v.replace(/^(\d{2})(\d)/, '($1) $2');
            v = v.replace(/(\d{5})(\d)/, '$1-$2');
            e.target.value = v;
        });
    }

    // ── Busca CEP ─────────────────────────────────────────
    const cepInput = document.getElementById('cep');
    if (cepInput) {
        cepInput.addEventListener('input', function (e) {
            let v = e.target.value.replace(/\D/g, '').substring(0, 8);
            v = v.replace(/(\d{5})(\d)/, '$1-$2');
            e.target.value = v;
        });

        cepInput.addEventListener('blur', function () {
            const cep = cepInput.value.replace(/\D/g, '');
            const erroEl = document.getElementById('cep-erro');
            if (cep.length !== 8) return;

            cepInput.classList.remove('cep-ok', 'cep-err');
            cepInput.classList.add('cep-loading');
            if (erroEl) erroEl.textContent = '';
            document.getElementById('logradouro').value = 'Buscando...';

            fetch('/api/cep/' + cep)
                .then(r => { if (!r.ok) throw new Error(); return r.json(); })
                .then(data => {
                    cepInput.classList.remove('cep-loading');
                    cepInput.classList.add('cep-ok');
                    document.getElementById('logradouro').value = data.logradouro || '';
                    document.getElementById('bairro').value     = data.bairro     || '';
                    document.getElementById('cidade').value     = data.localidade  || '';
                    document.getElementById('estado').value     = data.uf          || '';
                    document.getElementById('numero').focus();
                })
                .catch(() => {
                    cepInput.classList.remove('cep-loading');
                    cepInput.classList.add('cep-err');
                    document.getElementById('logradouro').value = '';
                    if (erroEl) erroEl.textContent = 'CEP não encontrado.';
                });
        });
    }

    // ── Auto-fecha flash após 4 segundos ──────────────────
    const flashes = document.querySelectorAll('.flash');
    flashes.forEach(f => {
        setTimeout(() => {
            f.style.transition = 'opacity 0.5s';
            f.style.opacity = '0';
            setTimeout(() => f.remove(), 500);
        }, 4000);
    });

});