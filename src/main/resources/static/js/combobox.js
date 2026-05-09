document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-combobox]').forEach(wrapper => {
        const inputId   = wrapper.dataset.inputId;
        const hiddenId  = wrapper.dataset.hiddenId;
        const dropdownId = wrapper.dataset.dropdownId;
        const hiddenVal = document.getElementById(hiddenId)?.value;

        if (hiddenVal) {
            document.querySelectorAll('#' + dropdownId + ' li').forEach(li => {
                if (li.dataset.id === hiddenVal) {
                    document.getElementById(inputId).value = li.dataset.nom;
                }
            });
        }
    });
});

function ouvrirDropdown(dropdownId) {
    document.getElementById(dropdownId).style.display = 'block';
}

function fermerDropdown(dropdownId) {
    setTimeout(() => {
        document.getElementById(dropdownId).style.display = 'none';
    }, 150);
}

function filtrer(inputId, dropdownId) {
    const recherche = document.getElementById(inputId).value.toLowerCase();
    document.querySelectorAll('#' + dropdownId + ' li').forEach(li => {
        li.style.display = li.dataset.nom.toLowerCase().includes(recherche) ? '' : 'none';
    });
    ouvrirDropdown(dropdownId);
}

function selectionner(li, inputId, hiddenId, dropdownId) {
    document.getElementById(inputId).value = li.dataset.nom;
    document.getElementById(hiddenId).value = li.dataset.id;
    document.getElementById(dropdownId).style.display = 'none';
}