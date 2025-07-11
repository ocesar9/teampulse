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

        if (!response.ok) {
            const errorMsg = await response.text();
            throw new Error(errorMsg);
        }
        else {
            const data = await response.json();
            setTimeout(() => {
                console.log(data)
                sessionStorage.setItem('token', data.token)
                sessionStorage.setItem('email', data.email)
                sessionStorage.setItem('username', data.username)
                sessionStorage.setItem('userType', data.type)
                window.location.href = "http://localhost:8080/content/dashboard.html"
            }, 1000)
        }

    }
    catch (error) {
        msgError.classList.remove("d-none");
        btnTexto.textContent = "Entrar";
        btnSpinner.classList.add("d-none");
        btnLogin.disabled = false;
        msgError.textContent = error.message;
    }
})