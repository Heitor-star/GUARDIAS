// Atualiza a quantidade exibida mantendo os demais filtros da URL
document.getElementById('selectLimite').addEventListener('change', function () {
    const url = new URL(window.location.href);
    url.searchParams.set('limite', this.value);
    window.location.href = url.toString();
});