document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('formContato');
    const btnEnviar = document.getElementById('btnEnviar');

    if (form && btnEnviar) {
        form.addEventListener('submit', function (event) {
            // Validação preliminar simples via JS antes do envio seguro ao Java
            const emailInput = document.getElementById('email').value.trim();
            
            if (!validarEmail(emailInput)) {
                event.preventDefault();
                alert('Por favor, insira um endereço de e-mail válido.');
                return false;
            }

            // SEGURANÇA / UX: Evita múltiplos cliques seguidos (Double Submit)
            btnEnviar.disabled = true;
            btnEnviar.innerText = 'Enviando com segurança...';
        });
    }

    // Regex simples para validação estrutural de e-mail no front-end
    function validarEmail(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    }
});