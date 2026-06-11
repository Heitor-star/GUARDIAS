document.addEventListener('DOMContentLoaded', function () {

    // ── Mascara telefone ──────────────────────────────────
    const telInput = document.getElementById('telefone');
    if (telInput) {
        telInput.addEventListener('input', e => {
            let v = e.target.value.replace(/\D/g, '').substring(0, 11);
            v = v.replace(/^(\d{2})(\d)/, '($1) $2');
            v = v.replace(/(\d{5})(\d)/, '$1-$2');
            e.target.value = v;
        });
    }

    // ── Contador de caracteres ────────────────────────────
    const textarea  = document.getElementById('mensagem');
    const charCount = document.getElementById('charCount');
    if (textarea && charCount) {
        textarea.addEventListener('input', function () {
            const len = this.value.length;
            charCount.textContent = len;
            charCount.style.color = len > 1800 ? '#ef4444' : '#6b7280';
        });
    }

    // ── Feedback no botao ao enviar ───────────────────────
    const form    = document.getElementById('formContato');
    const btnEnviar = document.getElementById('btnEnviar');
    if (form && btnEnviar) {
        form.addEventListener('submit', function () {
            btnEnviar.disabled = true;
            btnEnviar.innerHTML = `
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
                     stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10" stroke-dasharray="31.4" stroke-linecap="round">
                        <animateTransform attributeName="transform" type="rotate"
                            from="0 12 12" to="360 12 12" dur=".8s" repeatCount="indefinite"/>
                    </circle>
                </svg>
                Enviando...`;
        });
    }

    // ── Validacao basica antes de enviar ──────────────────
    if (form) {
        form.addEventListener('submit', function (e) {
            const assunto = document.getElementById('assunto');
            if (assunto && !assunto.value) {
                e.preventDefault();
                assunto.style.borderColor = '#ef4444';
                assunto.focus();
                btnEnviar.disabled = false;
                btnEnviar.innerHTML = `
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
                         stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="22" y1="2" x2="11" y2="13"/>
                        <polygon points="22 2 15 22 11 13 2 9 22 2"/>
                    </svg>
                    Enviar Mensagem`;
            }
        });
    }
});