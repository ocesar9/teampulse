const path = window.location.pathname;
const registerType = path.includes("cadastroadmin") ? "register/admin" : "register";

const loggedUser = {
    token: sessionStorage.getItem("token") || null,
    type: sessionStorage.getItem("userType")
}

if (registerType == "register") {
    if (loggedUser.type != "ADMIN")
        window.location = "http://localhost:8080/login"
}

document.getElementById('form-cadastro').addEventListener("submit", async (e) => {
    e.preventDefault();

    const btnRegister = document.querySelector("[data-register-btn]");
    const btnSpinner = document.querySelector("[data-register-spinner]");
    const btnTexto = document.querySelector("[data-register-text]");

    btnRegister.disabled = true;
    btnTexto.textContent = "";
    btnSpinner.classList.remove("d-none");

    const form = e.target;
    const formData = new FormData(form);
    const dados = Object.fromEntries(formData.entries());

    try {
        const response = await fetch(`http://localhost:8080/auth/${registerType}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                ...(registerType !== "register/admin" && { "Authorization": `${loggedUser.token}` })
            },
            body: JSON.stringify(dados)
        });

        if (!response.ok) {
            const errorMsg = await response.text();
            throw new Error(errorMsg)
        }
        else {
            showAlert("Cadastro realizado com sucesso", "success", "[data-alerts]")
            e.target.reset();
        }

        setTimeout(() => {
            btnRegister.disabled = false;
            btnTexto.textContent = "Cadastrar";
            btnSpinner.classList.add("d-none");
        }, 300)

    }
    catch (error) {
        btnRegister.disabled = false;
        showAlert(error.message, "warning", "[data-alerts]")
        btnTexto.textContent = "Cadastrar";
        btnSpinner.classList.add("d-none");
    }
})

function showAlert(message, type, dataContainer) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `<i class="bi bi-${type === 'success' ? 'check-circle' : type === 'info' ? 'info-circle' : 'exclamation-triangle'} me-2"></i>${message}<button type="button" class="btn-close" data-bs-dismiss="alert" data-close-alert aria-label="Close"></button>`;

    const container = document.querySelector(dataContainer);
    container.insertBefore(alertDiv, container.firstChild);

    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}