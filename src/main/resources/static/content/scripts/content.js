function sendFeedback() {
    const form = document.getElementById('feedbackForm');
    const recipient = document.getElementById('recipientSelect').value;
    const title = document.getElementById('feedbackTitle').value;
    const message = document.getElementById('feedbackMessage').value;

    if (!recipient || !title || !message) {
        alert('Please fill in all required fields.');
        return;
    }

    const modal = bootstrap.Modal.getInstance(document.getElementById('newFeedbackModal'));
    modal.hide();

    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-success alert-dismissible fade show';
    alertDiv.innerHTML = `
                <i class="bi bi-check-circle me-2"></i>
                <strong>Feedback sent successfully!</strong> Your feedback has been delivered to ${recipient.replace('.', ' ').replace(/\b\w/g, l => l.toUpperCase())}.
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            `;

    const container = document.querySelector('.container-fluid.px-3.py-4');
    container.insertBefore(alertDiv, container.firstChild);

    form.reset();

    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}

document.getElementById('feedbackForm').addEventListener('submit', function (e) {
    e.preventDefault();
    sendFeedback();
});