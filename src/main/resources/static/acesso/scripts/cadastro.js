const path = window.location.pathname;
const registerType = path.includes("cadastroAdmin") ? "register/admin" : "register";
const token = sessionStorage.getItem("token") || null;

document.getElementById('form-cadastro').addEventListener("submit", async (e) => {
    e.preventDefault();

    const btnRegister = document.querySelector("[data-register-btn]");
    const btnSpinner = document.querySelector("[data-register-spinner]");
    const btnTexto = document.querySelector("[data-register-text]");
    const msgError = document.querySelector("[data-register-error-list]");
    const msgSuccess = document.querySelector("[data-register-success]");

    btnRegister.disabled = true;
    btnTexto.textContent = "";
    btnSpinner.classList.remove("d-none");
    msgError.classList.add("d-none");

    const form = e.target;
    const formData = new FormData(form);
    const dados = Object.fromEntries(formData.entries());

    try {
        const response = await fetch(`http://localhost:8080/auth/${registerType}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                ...(registerType !== "register/admin" && { "Authorization": `${token}` })
            },
            body: JSON.stringify(dados)
        });
        setTimeout(() => {
            if (response.ok) {
                console.log(response)
                msgSuccess.classList.remove("d-none");
                msgSuccess.textContent = "Cadastro realizado com sucesso";
                e.target.reset();
            }
            else {
                msgError.classList.remove("d-none");
                msgError.textContent = "Erro. Tente novamente"
            }

            setTimeout(() => {
                btnRegister.disabled = false;
                btnTexto.textContent = "Cadastrar";
                btnSpinner.classList.add("d-none");
            }, 300)

            setTimeout(() => {
                msgSuccess.classList.add("d-none");
            }, 2000)
        }, 1000)
    }
    catch (error) {
        btnRegister.disabled = false;
        btnTexto.textContent = "Cadastrar";
        btnSpinner.classList.add("d-none");
        msgError.classList.remove("d-none");
        msgError.textContent = error;
    }
})
