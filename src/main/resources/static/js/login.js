document.addEventListener('DOMContentLoaded', function () {

    // ── Mostrar/ocultar senha ─────────────────────────────
    const toggleSenha = document.getElementById('toggleSenha');
    const inputSenha  = document.getElementById('password');
    const iconeOlho   = document.getElementById('icone-olho');

    if (toggleSenha && inputSenha) {
        toggleSenha.addEventListener('click', function () {
            const visivel = inputSenha.type === 'text';
            inputSenha.type = visivel ? 'password' : 'text';
            iconeOlho.innerHTML = visivel
                ? '<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>'
                : '<path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>' +
                  '<path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>' +
                  '<line x1="1" y1="1" x2="23" y2="23"/>';
        });
    }

    // ── Validacao basica antes de enviar ──────────────────
    const form      = document.getElementById('formLogin');
    const emailEl   = document.getElementById('username');
    const senhaEl   = document.getElementById('password');
    const emailErro = document.getElementById('email-erro');
    const senhaErro = document.getElementById('senha-erro');

    if (form) {
        form.addEventListener('submit', function (e) {
            let valido = true;

            if (!emailEl.value.trim()) {
                emailErro.textContent = 'Informe seu e-mail.';
                emailEl.style.borderColor = '#ef4444';
                valido = false;
            } else {
                emailErro.textContent = '';
                emailEl.style.borderColor = '';
            }

            if (!senhaEl.value.trim()) {
                senhaErro.textContent = 'Informe sua senha.';
                senhaEl.style.borderColor = '#ef4444';
                valido = false;
            } else {
                senhaErro.textContent = '';
                senhaEl.style.borderColor = '';
            }

            if (!valido) e.preventDefault();
        });
    }

    // ── Limpa erro ao digitar ─────────────────────────────
    if (emailEl) {
        emailEl.addEventListener('input', function () {
            emailErro.textContent = '';
            emailEl.style.borderColor = '';
        });
    }
    if (senhaEl) {
        senhaEl.addEventListener('input', function () {
            senhaErro.textContent = '';
            senhaEl.style.borderColor = '';
        });
    }

});

/* ================================= */
/* NAVBAR EFEITO GLASS SCROLL        */
/* ================================= */
const navbar = document.querySelector('.navbar');
window.addEventListener('scroll', () => {
    if (window.scrollY > 50) {
        navbar.classList.add('scrolled');
    } else {
        navbar.classList.remove('scrolled');
    }
});