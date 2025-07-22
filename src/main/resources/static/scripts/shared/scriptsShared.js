const usuarioLogado = sessionStorage.getItem("token") || null;

if (!usuarioLogado)
    window.location.href = "http://localhost:8080/acesso/login"


function logout() {
    sessionStorage.removeItem("token");
    sessionStorage.removeItem("username");
    sessionStorage.removeItem("email");
    sessionStorage.removeItem("userType");
    window.location = "../acesso/login";
}

const setVisibilidade = async (userType) => {
    if (!userType)
        return

    const componentVisibilityGerente = document.querySelectorAll('[data-visibility-gerente]')
    const componentVisibilityColaborador = document.querySelectorAll('[data-visibility-colaborador]')
    const componentVisibilityAdmin = document.querySelectorAll('[data-visibility-admin]')

    if (componentVisibilityColaborador && userType == "COLABORADOR") {
        componentVisibilityColaborador.forEach((elemento) => {
            elemento.classList.remove('d-none')
        })
    }

    if (componentVisibilityAdmin && userType == "ADMIN") {
        componentVisibilityAdmin.forEach((elemento) => {
            elemento.classList.remove('d-none')
        })
    }

    if (componentVisibilityGerente && userType == "GERENTE") {
        componentVisibilityGerente.forEach((elemento) => {
            elemento.classList.remove('d-none')
        })
    }
}