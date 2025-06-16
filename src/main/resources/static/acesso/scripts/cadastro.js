document.getElementById('form-cadastro').addEventListener("submit", async (e) => {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData(form);
    const dados = Object.fromEntries(formData.entries())

    const response = await fetch("http://localhost:8080/auth/cadastro", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(dados)
    });

    if (response.ok) {
        console.log(response)
        alert("enviado")
    }
    else {
        alert("erro")
    }

})