const usuarioLogado = sessionStorage.getItem("token") || null;

if (!usuarioLogado)
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