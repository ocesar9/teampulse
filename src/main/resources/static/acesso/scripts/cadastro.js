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
    console.log(dados)
    try {
        const response = await fetch("http://localhost:8080/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dados)
        });
        setTimeout(() => {
            if (response.ok) {
                msgSuccess.classList.remove("d-none");
                msgSuccess.textContent = "Usuário cadastrado com sucesso";
                e.target.reset();
            }
            else {
                msgError.classList.remove("d-none");
                msgError.textContent = "Erro. Tente novamente"
            }
            setTimeout(() => {
                btnRegister.disabled = false;
                btnTexto.textContent = "Cadastrar";
                msgSuccess.classList.add("d-none");
                btnSpinner.classList.add("d-none");
            }, 400)
        }, 1000)
    }
    catch (error) {
        msgError.classList.remove("d-none");
        msgError.textContent = "Erro na conexão. Tente novamente";
    }
})