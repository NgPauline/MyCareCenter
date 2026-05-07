// ============================
// MAIN JS – Global interactions
// ============================

document.addEventListener("DOMContentLoaded", () => {
    console.log("CareCenter UI loaded.");
});

// static/js/main.js

document.addEventListener('DOMContentLoaded', () => {
    // Exemple : ajout d'une classe sur body si JS actif
    document.body.classList.add('js-enabled');
});


// ============================
// GLOBAL SEARCH TOGGLE
// ============================

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("[data-search-toggle]").forEach(button => {
        button.addEventListener("click", () => {
            const target = document.getElementById(button.dataset.searchToggle);
            if (target) {
                target.style.display = target.style.display === "none" ? "block" : "none";
            }
        });
    });
});

function showImage(src) {
    const lightbox = document.getElementById('lightbox');
    const img = document.getElementById('lightbox-img');
    img.src = src;
    lightbox.classList.add('show');
}


/* ── Dashboard charts ────────────────────────────────── */

(function () {
  if (!document.getElementById('chartFactures')) return;

  const { labels, facturesMontants, facturesPayees, residentsData } = window.dashboardData;

  const gold    = '#C9973A';
  const goldBg  = 'rgba(201,151,58,0.15)';
  const green   = '#22c55e';
  const greenBg = 'rgba(34,197,94,0.15)';

  new Chart(document.getElementById('chartFactures'), {
    type: 'bar',
    data: {
      labels,
      datasets: [
        { label: 'Total (€)',  data: facturesMontants, backgroundColor: goldBg,  borderColor: gold,  borderWidth: 1.5, borderRadius: 4 },
        { label: 'Payées (€)', data: facturesPayees,   backgroundColor: greenBg, borderColor: green, borderWidth: 1.5, borderRadius: 4 }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { display: false } },
        y: { beginAtZero: true, ticks: { callback: v => v + ' €' } }
      }
    }
  });

  new Chart(document.getElementById('chartResidents'), {
    type: 'line',
    data: {
      labels,
      datasets: [{
        data: residentsData,
        fill: true,
        backgroundColor: goldBg,
        borderColor: gold,
        borderWidth: 2,
        tension: 0.4,
        pointRadius: 3
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { display: false } },
        y: { beginAtZero: false, precision: 0 }
      }
    }
  });
}());


document.addEventListener('DOMContentLoaded', function () {

    document.querySelectorAll('.activite-checkbox').forEach(checkbox => {
        checkbox.addEventListener('change', function () {
            if (this.checked) {

                // Décocher les autres
                document.querySelectorAll('.activite-checkbox').forEach(cb => {
                    if (cb !== this) cb.checked = false;
                });

                // Remplir les champs
                document.getElementById('date').value       = this.dataset.date;
                document.getElementById('heureDebut').value = this.dataset.heureDebut;
                document.getElementById('heureFin').value   = this.dataset.heureFin;

            } else {
                // Vider si décoché
                document.getElementById('date').value       = '';
                document.getElementById('heureDebut').value = '';
                document.getElementById('heureFin').value   = '';
            }
        });
    });

});