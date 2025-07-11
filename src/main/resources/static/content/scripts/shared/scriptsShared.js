function logout() {
    sessionStorage.removeItem("token");
    sessionStorage.removeItem("username");
    sessionStorage.removeItem("email");
    sessionStorage.removeItem("userType");
    window.location = "../acesso/login.html";
}

const setVisibilidade = (userType) => {
    if (!userType)
        return

    const buttonNewFeedback = document.querySelectorAll('[data-button-enviar-feedback]')
    const componentVisibilityAdmin = document.querySelectorAll('[data-visibility-admin]')

    if (buttonNewFeedback && userType == "GERENTE") {
        buttonNewFeedback.forEach((elemento) => {
            elemento.classList.remove('d-none')
        })
    }

    if (componentVisibilityAdmin && userType == "ADMIN") {
        componentVisibilityAdmin.forEach((elemento) => {
            elemento.classList.remove('d-none')
        })
    }
}