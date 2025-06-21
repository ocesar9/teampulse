const token = sessionStorage.getItem("token") || null;

document.getElementById('form-login').addEventListener("submit", async (e) => {
    e.preventDefault();

    const btnLogin = document.querySelector("[data-login-btn]")
    const btnSpinner = document.querySelector("[data-login-spinner]")
    const btnTexto = document.querySelector("[data-login-text]")
    const msgError = document.querySelector("[data-login-error-list]")

    btnLogin.disabled = true;
    btnTexto.textContent = "";
    btnSpinner.classList.remove("d-none");
    msgError.classList.add("d-none");

    const form = e.target;
    const formData = new FormData(form);
    const dados = Object.fromEntries(formData.entries())

    try {
        const response = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dados)
        });

        const data = await response.json();

        if (response.ok) {
            setTimeout(() => {
                sessionStorage.setItem('token', data.token)
                sessionStorage.setItem('email', data.email)
                sessionStorage.setItem('username', data.username)
                window.location.href = "http://localhost:8080/content/dashboard.html"
            }, 1000)
        }
        else {
            btnLogin.disabled = false;
            btnTexto.textContent = "Entrar";
            btnSpinner.classList.add("d-none");
            msgError.classList.remove("d-none");
            msgError.textContent = "Erro. Tente novamente"
        }

    }
    catch (error) {
        msgError.classList.remove("d-none");
        btnTexto.textContent = "Entrar";
        btnSpinner.classList.add("d-none");
        btnLogin.disabled = false;
        msgError.textContent = "Erro na conexão. Tente novamente"
    }
})