var mapa = L.map('mapa').setView([-14.2350, -51.9253], 4);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap'
}).addTo(mapa);


var locais = [

{
nome: "Uberlândia",
coords: [-18.9186, -48.2772],
pagina: "uberlandia.html"
},

{
nome: "Brasília",
coords: [-15.7801, -47.9292],
pagina: "brasilia.html"
},

{
nome: "Rio de Janeiro",
coords: [-22.9068, -43.1729],
pagina: "rio.html"
},

{
nome: "Porto Alegre",
coords: [-30.0346, -51.2177],
pagina: "portoalegre.html"
}

];


locais.forEach(function(local){

var marcador = L.marker(local.coords).addTo(mapa);

marcador.bindPopup(`
<b>${local.nome}</b><br>
<a href="${local.pagina}">Ver mais</a>
`);

});