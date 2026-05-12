// Bootstrap client-side form validation
(function () {
    'use strict';

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
