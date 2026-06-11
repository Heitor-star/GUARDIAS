document.addEventListener('DOMContentLoaded', function () {
    const form      = document.getElementById('formCadastro');
    const senha     = document.getElementById('senha');
    const confirmar = document.getElementById('confirmarSenha');
    const cpfInput  = document.getElementById('cpf');
    const telInput  = document.getElementById('telefone');

    const reqs = {
        tamanho:   { el: document.getElementById('req-tamanho'),   fn: v => v.length >= 8 },
        maiuscula: { el: document.getElementById('req-maiuscula'),  fn: v => /[A-Z]/.test(v) },
        minuscula: { el: document.getElementById('req-minuscula'),  fn: v => /[a-z]/.test(v) },
        numero:    { el: document.getElementById('req-numero'),     fn: v => /[0-9]/.test(v) },
        especial:  { el: document.getElementById('req-especial'),   fn: v => /[!@#$%^&*(),.?":{}|<>\-_=+\[\]\\/]/.test(v) }
    };

    function validarSenha() {
        const val = senha.value;
        let ok = true;
        Object.values(reqs).forEach(r => {
            if (r.fn(val)) {
                r.el.style.color = '#4ade80';
                r.el.innerHTML = '✓ ' + r.el.innerText.substring(2);
            } else {
                r.el.style.color = 'rgba(255,255,255,0.4)';
                r.el.innerHTML = '✗ ' + r.el.innerText.substring(2);
                ok = false;
            }
        });
        return ok;
    }

    if (senha) senha.addEventListener('input', validarSenha);

    // Máscaras
    if (cpfInput) {
        cpfInput.addEventListener('input', e => {
            let v = e.target.value.replace(/\D/g, '');
            v = v.replace(/(\d{3})(\d)/, '$1.$2');
            v = v.replace(/(\d{3})(\d)/, '$1.$2');
            v = v.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
            e.target.value = v;
        });
    }

    if (telInput) {
        telInput.addEventListener('input', e => {
            let v = e.target.value.replace(/\D/g, '');
            v = v.replace(/^(\d{2})(\d)/, '($1) $2');
            v = v.replace(/(\d{5})(\d)/, '$1-$2');
            e.target.value = v;
        });
    }

    // Bloqueio de envio se houver erro
    form.addEventListener('submit', function (e) {
        const senhasIguais = senha.value === confirmar.value;
        if (!validarSenha() || !senhasIguais) {
            e.preventDefault();
            alert('Verifique os requisitos da senha e se elas coincidem.');
        }
    });
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