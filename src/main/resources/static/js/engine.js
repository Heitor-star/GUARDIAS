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

/* ================================= */
/* SIDEBAR TOGGLE + EMPURRA LAYOUT   */
/* ================================= */
const SIDEBAR_WIDTH = '280px';

function aplicarDeslocamento(sidebar) {
    const main   = document.querySelector('main');
    const footer = document.querySelector('footer');

    const aberta = !sidebar.classList.contains('collapsed');
    const mobile = window.innerWidth <= 768;
    const valor  = (!mobile && aberta) ? SIDEBAR_WIDTH : '0px';

    if (main)   main.style.marginLeft   = valor;
    if (footer) footer.style.marginLeft = valor;
}

function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    sidebar.classList.toggle('collapsed');
    aplicarDeslocamento(sidebar);
}

document.addEventListener('DOMContentLoaded', function () {
    const sidebar = document.querySelector('.sidebar');
    if (!sidebar) return;

    aplicarDeslocamento(sidebar);

    window.addEventListener('resize', function () {
        aplicarDeslocamento(sidebar);
    });
});


/* ================================= */
/* ACCORDION DA SIDEBAR              */
/* ================================= */
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.accordion-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const content = this.nextElementSibling;
            const arrow   = this.querySelector('.accordion-arrow');
            const aberto  = content.style.maxHeight && content.style.maxHeight !== '0px';

            content.style.maxHeight  = aberto ? '0px'   : content.scrollHeight + 'px';
            content.style.opacity    = aberto ? '0'     : '1';
            if (arrow) arrow.style.transform = aberto ? 'rotate(0deg)' : 'rotate(180deg)';
        });
    });
});