document.getElementById('form-login').addEventListener("submit", async (e) => {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData(form);
    const dados = Object.fromEntries(formData.entries())

    const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(dados)
    });


    if (response.ok) {
        const btn = document.getElementById("js-btn-logar");
        btn.textContent = "Loading..."
        window.location = "http://localhost:8080/content/dashboard"
    }
    else {
        alert("erro")
    }

})