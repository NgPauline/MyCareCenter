// ============================
// FORMS – Validations
// ============================

function validateDateRange(startInput, endInput) {
    if (endInput.value && startInput.value && endInput.value < startInput.value) {
        alert("La date de fin doit être postérieure à la date de début.");
        return false;
    }
    return true;
}