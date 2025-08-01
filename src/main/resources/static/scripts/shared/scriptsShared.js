// Definição de variáveis com escopo global
const loggedUser = {
    token: sessionStorage.getItem("token") || null,
    type: sessionStorage.getItem("userType"),
    username: sessionStorage.getItem("username"),
    email: sessionStorage.getItem("email")
}

if (!loggedUser.token)
    window.location.href = "http://localhost:8080/login"


function logout() {
    sessionStorage.removeItem("token");
    sessionStorage.removeItem("username");
    sessionStorage.removeItem("email");
    sessionStorage.removeItem("userType");
    window.location = "../login";
}

const setVisibilidade = async (userType) => {
    if (!userType)
        return

    const allElements = document.querySelectorAll("[data-visibility-colaborador], [data-visibility-gerente], [data-visibility-admin]");

    allElements.forEach(el => {
        el.classList.add("d-none")
    })

    if (userType == "COLABORADOR") {
        document.querySelectorAll('[data-visibility-colaborador]').forEach((elemento) => {
            elemento.classList.remove('d-none')
        })
    }

    if (userType == "ADMIN") {
        document.querySelectorAll('[data-visibility-admin]').forEach((elemento) => {
            elemento.classList.remove('d-none')
        })
    }

    if (userType == "GERENTE") {
        document.querySelectorAll('[data-visibility-gerente]').forEach((elemento) => {
            elemento.classList.remove('d-none')
        })
    }

}

//Função que pega a lista de todos os usuários cadastrados
const getAllUsers = async () => {
    try {
        const users = await fetch(`http://localhost:8080/user/list`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${loggedUser.token}`,
            }
        });

        const data = await users.json();
        return data;

    } catch (error) {
        console.error("Erro ao buscar usuários:", error);
    }
};

//Função que pega a lista de todos os squads cadastrados
const getAllSquads = async () => {
    try {
        const users = await fetch(`http://localhost:8080/squads`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${loggedUser.token}`,
            }
        });

        const data = await users.json();
        return data;

    } catch (error) {
        console.error("Erro ao buscar usuários:", error);
    }
};

function getInitials(fullName) {
    const names = fullName.trim().split(" ");
    if (names.length === 1) return names[0][0].toUpperCase();
    return (names[0][0] + names[names.length - 1][0]).toUpperCase();
}

function formatDate(isoDate, msgType) {
    const date = new Date(isoDate);
    const today = new Date();
    const diffTime = Math.abs(today - date);
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
    if (msgType != "draft")
        return diffDays === 0 ? "Hoje" : `${diffDays} dia(s) atrás`;
    return diffDays === 0 ? "Criado hoje" : `Criado ${diffDays} dia(s) atrás`
}

async function createEmptyState(wrapper, msg) {
    const parentElement = wrapper;
    parentElement.innerHTML = "";

    const emptyState = document.createElement("div");
    emptyState.className = "col-lg-12 mb-4";

    emptyState.innerHTML = `
            <div class="text-center text-muted mt-4 p-2">
                <i class="bi bi-inbox" style="font-size: 2rem;"></i>
                <p class="mt-2 fs-5">${msg}</p>
            </div>`;

    parentElement.appendChild(emptyState);
}

function showAlert(message, type, dataContainer) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
                <i class="bi bi-${type === 'success' ? 'check-circle' : type === 'info' ? 'info-circle' : 'exclamation-triangle'} me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            `;

    const container = document.querySelector(dataContainer);
    container.insertBefore(alertDiv, container.firstChild);

    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}