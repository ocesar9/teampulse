const token = sessionStorage.getItem("token") || null;

if (!token)
    window.location.href = "http://localhost:8080/acesso/login.html"

const selectPeople = document.querySelector('[data-select-user-name]')
const nameUser = document.querySelector('[data-name-user]')
nameUser.textContent = sessionStorage.getItem('username')

function sendFeedback() {
    const form = document.getElementById('feedbackForm');

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

    return data;
}

const allUsers = getAllUsers();

const fillSelectUsers = async () => {
    if (allUsers && Array.isArray(allUsers)) {
        allUsers.forEach(user => {

            const option = document.createElement("option");
            option.value = user.id;
            option.textContent = user.username;

            selectPeople.appendChild(option);
        })
    }
}

const getAllUserFeedbacks = async () => {
    debugger
    const usersList = await allUsers;
    const encontrado = usersList.find(objeto => sessionStorage.getItem("email") === objeto.email);
    const total = await fetch(`http://localhost:8080/feedback/user/${encontrado.id}}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${token}`,
        }
    });

    const allFeedbacks = await total.json();
    console.log(allFeedbacks);
}

document.addEventListener("DOMContentLoaded", (event) => {
    getAllUserFeedbacks()
})


function logout() {
    sessionStorage.removeItem("token");
    sessionStorage.removeItem("username");
    sessionStorage.removeItem("email");
    window.location = "../acesso/login.html";
}