// Bootstrap client-side form validation
(function () {
    'use strict';

    // Cross-field date validation: checkOut must be after checkIn
    var checkIn = document.getElementById('checkIn');
    var checkOut = document.getElementById('checkOut');
    if (checkIn && checkOut) {
        checkIn.addEventListener('change', function () {
            if (this.value) {
                checkOut.min = this.value;
                if (checkOut.value && checkOut.value <= this.value) {
                    checkOut.setCustomValidity('Data check-out trebuie sa fie dupa check-in.');
                } else {
                    checkOut.setCustomValidity('');
                }
            }
        });
        checkOut.addEventListener('change', function () {
            if (checkIn.value && this.value <= checkIn.value) {
                this.setCustomValidity('Data check-out trebuie sa fie dupa check-in.');
            } else {
                this.setCustomValidity('');
            }
        });
    }

    var forms = document.querySelectorAll('.needs-validation');

    Array.prototype.slice.call(forms).forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Page size selector
    var pageSizeSelect = document.getElementById('pageSizeSelect');
    if (pageSizeSelect) {
        pageSizeSelect.addEventListener('change', function () {
            var baseUrl = this.getAttribute('data-base-url');
            var sortBy = this.getAttribute('data-sort-by') || 'id';
            var sortDir = this.getAttribute('data-sort-dir') || 'asc';
            var size = this.value;
            window.location.href = baseUrl + '?page=0&size=' + size + '&sortBy=' + sortBy + '&sortDir=' + sortDir;
        });
    }
})();
