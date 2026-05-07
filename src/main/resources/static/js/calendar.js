function initPlanningCalendar(elementId, events) {

    const calendarEl = document.getElementById(elementId);

    if (!Array.isArray(events)) {
        try { events = JSON.parse(events); }
        catch (e) { events = []; }
    }

    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'fr',
        height: "auto",

        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek'
        },

        events: events.map(e => ({
            id: e.id,
            title: e.title + (e.activite ? " — " + e.activite : ""),
            start: e.start,
            end: e.end,
            color: e.color,
            className: "soignant-" + e.soignantId
        })),

        // 👉 Clic sur un événement
        eventClick: function(info) {
            if (!info.event || !info.event.id) return;
            window.location.href = "/plannings/" + info.event.id;
        },

        // 👉 Clic sur une case du calendrier
        dateClick: function(info) {
            if (!info.dateStr) return;
            window.location.href = "/plannings/new?date=" + info.dateStr;
        }
    });

    calendar.render();
}


// ============================
// LÉGENDE DES SOIGNANTS
// ============================

function generateLegend(events) {

    const map = new Map();

    events.forEach(e => {
        if (!map.has(e.soignantId)) {
            map.set(e.soignantId, {
                name: e.title,
                color: e.color
            });
        }
    });

    const legendDiv = document.getElementById("calendar-legend");
    if (!legendDiv) return;

    legendDiv.innerHTML = "";

    map.forEach((value) => {
        const item = document.createElement("div");
        item.classList.add("legend-item");

        item.innerHTML = `
            <span class="legend-color" style="background:${value.color}"></span>
            <span>${value.name}</span>
        `;

        legendDiv.appendChild(item);
    });
}
