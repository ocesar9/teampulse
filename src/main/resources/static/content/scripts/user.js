const token = sessionStorage.getItem("token") || null;

if (!token)
    window.location.href = "http://localhost:8080/acesso/login.html"


function deleteAccount() {
    const confirmText = document.getElementById('confirmDelete').value;

    if (confirmText !== 'DELETE') {
        alert('Digite "DELETAR" para confirmar a exclusão');
        return;
    }

    const modal = bootstrap.Modal.getInstance(document.getElementById('deleteAccountModal'));
    modal.hide();

    showAlert('Exclusão de conta iniciada. Você será deslogado em instantes', 'warning');

    setTimeout(() => {
        logout();
    }, 3000);
}

document.getElementById('confirmDelete').addEventListener('input', function () {
    const deleteBtn = document.getElementById('deleteBtn');
    const confirmText = this.value;

    if (confirmText === 'DELETAR') {
        deleteBtn.disabled = false;
    } else {
        deleteBtn.disabled = true;
    }
});

function showAlert(message, type) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
                <i class="bi bi-${type === 'success' ? 'check-circle' : 'exclamation-triangle'} me-2"></i>
                <strong>${message}</strong>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            `;

    const container = document.querySelector('.container-fluid.px-3.py-4');
    container.insertBefore(alertDiv, container.firstChild);

    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}


document.getElementById('form-edicao').addEventListener('submit', async (e) => {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData(form);
    const dados = Object.fromEntries(formData.entries());
    const modal = bootstrap.Modal.getInstance(document.getElementById('editProfileModal'));

    try {
        const response = await fetch(`http://localhost:8080/auth/`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${token}`,
            },
            body: JSON.stringify(dados)
        });
        setTimeout(() => {
            if (response.ok) {
                modal.hide();
                showAlert('Perfil atualizado com sucesso!', 'success');
                e.target.reset();
            }
            else {
                alert("Erro. Tente novamente")
            }
        }, 1000)
    }
    catch (error) {
        alert(error)
    }

});

function logout() {
    sessionStorage.removeItem("token");
    window.location = "../acesso/login.html";
}