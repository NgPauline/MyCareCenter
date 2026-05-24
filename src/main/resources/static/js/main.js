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
            if (!target) return;

            const isVisible = target.style.display !== "none";
            target.style.display = isVisible ? "none" : "block";

            // ✅ Focus sur l'input quand la barre s'ouvre
            if (!isVisible) {
                const input = target.querySelector('input[type="text"]');
                if (input) input.focus();
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
  if (!window.dashboardData) return;

  const { labels, facturesMontants, facturesPayees, residentsData } = window.dashboardData;

  const gold    = '#C9973A';
  const goldBg  = 'rgba(201,151,58,0.15)';
  const green   = '#22c55e';
  const greenBg = 'rgba(34,197,94,0.15)';

  // Graphique factures — uniquement si le canvas existe (DIRECTEUR, FINANCE)
  const ctxFactures = document.getElementById('chartFactures');
  if (ctxFactures) {
    new Chart(ctxFactures, {
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
  }

  // Graphique résidents — uniquement si le canvas existe (tous sauf FINANCE)
  const ctxResidents = document.getElementById('chartResidents');
  if (ctxResidents) {
    new Chart(ctxResidents, {
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
  }
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



document.addEventListener('DOMContentLoaded', function() {
    // Configuration commune pour adoucir et réduire la taille
    const swalConfig = {
        title: 'Confirmation',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Supprimer',
        cancelButtonText: 'Annuler',
        // --- Ajustements "soft & small" ---
        width: '380px',                 // taille réduite (défaut 500px)
        padding: '1.2rem',              // moins d'espace interne
        backdrop: 'rgba(0,0,0,0.2)',    // fond plus léger
        confirmButtonColor: '#f8d7da',  // rouge très doux
        cancelButtonColor: '#e2e3e5',   // gris clair doux
        confirmButtonTextColor: '#ce767f', // texte rouge foncé lisible
        cancelButtonTextColor: '#383d41',   // texte gris foncé
        customClass: {
            popup: 'soft-swal-popup',
            title: 'soft-swal-title',
            confirmButton: 'soft-confirm-btn',
            cancelButton: 'soft-cancel-btn'
        }
    };

    // Pour les formulaires
    document.querySelectorAll('form[onsubmit*="confirm"]').forEach(form => {
        const original = form.getAttribute('onsubmit');
        const msgMatch = original.match(/confirm\(['"](.+?)['"]\)/);
        const message = msgMatch ? msgMatch[1] : 'Cette action est irréversible.';
        
        form.removeAttribute('onsubmit');
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const result = await Swal.fire({
                ...swalConfig,
                text: message
            });
            if (result.isConfirmed) form.submit();
        });
    });

    // Pour les liens
    document.querySelectorAll('a[onclick*="confirm"]').forEach(link => {
        const original = link.getAttribute('onclick');
        const msgMatch = original.match(/confirm\(['"](.+?)['"]\)/);
        const message = msgMatch ? msgMatch[1] : 'Cette action est irréversible.';
        
        link.removeAttribute('onclick');
        link.addEventListener('click', async (e) => {
            e.preventDefault();
            const result = await Swal.fire({
                ...swalConfig,
                text: message
            });
            if (result.isConfirmed) window.location.href = link.href;
        });
    });
});

document.addEventListener("DOMContentLoaded", function () {

    // Dates
    flatpickr(".datepicker", {
        locale: "fr",
        dateFormat: "d/m/Y",
        allowInput: true,
        disableMobile: true
    });

    // Heures
    flatpickr(".timepicker", {
        locale: "fr",
        enableTime: true,
        noCalendar: true,
        time_24hr: true,
        dateFormat: "H:i",
        disableMobile: true
    });

    // Date + heure (consultations)
    flatpickr(".datetimepicker", {
        locale: "fr",
        enableTime: true,
        time_24hr: true,
        dateFormat: "d/m/Y H:i",
        disableMobile: true
    });

});
