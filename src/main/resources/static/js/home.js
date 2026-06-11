// Confirmação de logout (só executa se o botão existir na página)
const formLogout = document.getElementById("formLogout");
if (formLogout) {
    formLogout.addEventListener("submit", function (e) {
        let confirmar = confirm("Deseja realmente sair?");
        if (!confirmar) {
            e.preventDefault();
        }
    });
}

// Accordion — abre/fecha ao clicar no botão
document.querySelectorAll('.accordion-btn').forEach(function (btn) {
    btn.addEventListener('click', function () {
        const content = this.nextElementSibling;
        const isOpen  = this.classList.contains('open');

        // Fecha todos os outros
        document.querySelectorAll('.accordion-btn').forEach(function (other) {
            other.classList.remove('open');
            other.nextElementSibling.style.maxHeight = null;
        });

        // Abre o clicado (se estava fechado)
        if (!isOpen) {
            this.classList.add('open');
            content.style.maxHeight = content.scrollHeight + 'px';
        }
    });
});