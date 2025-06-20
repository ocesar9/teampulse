const token = sessionStorage.getItem("token") || null;
const selectPeople = document.querySelector('[data-select-user-name]')

if (!token)
    window.location.href = "http://localhost:8080/acesso/login.html"

function sendFeedback() {
    const form = document.getElementById('feedbackForm');
    const recipientId = document.getElementById('recipientSelect').value;
    const recipientName = document.getElementById('recipientSelect').textContent;
    const title = document.getElementById('feedbackTitle').value;
    const message = document.getElementById('feedbackMessage').value;

    if (!recipientId || !title || !message) {
        alert('Preencha todos os campos');
        return;
    }

    const modal = bootstrap.Modal.getInstance(document.getElementById('newFeedbackModal'));
    modal.hide();

    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-success alert-dismissible fade show';
    alertDiv.innerHTML = `<i class="bi bi-check-circle me-2"></i>
                <strong>Sucesso!</strong> Seu feedback foi enviado!
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>`;

    const container = document.querySelector('.container-fluid.px-3.py-4');
    container.insertBefore(alertDiv, container.firstChild);

    form.reset();

    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}

const getAllUsers = async () => {
    const users = await fetch(`http://localhost:8080/user/list`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${token}`,
        }
    });

    const data = await users.json();

    if (data && Array.isArray(data)) {
        data.forEach(user => {

            const option = document.createElement("option");
            option.value = user.id;
            option.textContent = user.username;

            selectPeople.appendChild(option);
        })
    }
}

// document.getElementById('feedbackForm').addEventListener('submit', function (e) {
//     e.preventDefault();
//     sendFeedback();
// });

function logout() {
    sessionStorage.removeItem("token");
    window.location = "../acesso/login.html";
}