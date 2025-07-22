const loggedUser = {
    token: sessionStorage.getItem("token") || null,
    type: sessionStorage.getItem("userType"),
    username: sessionStorage.getItem("username"),
    email: sessionStorage.getItem("email")
}

if (!loggedUser.token)
    window.location.href = "http://localhost:8080/acesso/login.html"

const nameUser = document.querySelectorAll('[data-name-user]');
nameUser.forEach(x => {
    x.textContent = loggedUser.username;
});
const emailUser = document.querySelectorAll('[data-email-user]');
emailUser.forEach(x => {
    x.textContent = loggedUser.email;
});

document.getElementById("displayEmail").value = loggedUser.email;
document.getElementById("displayFullName").value = loggedUser.username;

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

    const usersList = await getAllUsers();
    const encontrado = usersList.find(objeto => sessionStorage.getItem("email") === objeto.email);

    try {
        const response = await fetch(`http://localhost:8080/user/edit/${encontrado.id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${loggedUser.token}`,
            },
            body: JSON.stringify(dados)
        });
        const data = await response.json();
        console.log(data)
        setTimeout(() => {
            if (response.ok) {
                modal.hide();
                showAlert('Perfil atualizado com sucesso! Você será redirecionado para realizar login novamente', 'success');
                e.target.reset();

                setTimeout(() => {
                    logout();
                }, 2000)
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

document.getElementById('form-delete').addEventListener('submit', async (e) => {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData(form);
    const dados = Object.fromEntries(formData.entries());
    const modal = bootstrap.Modal.getInstance(document.getElementById('deleteAccountModal'));

    const usersList = await getAllUsers();
    const encontrado = usersList.find(objeto => sessionStorage.getItem("email") === objeto.email);

    try {
        const response = await fetch(`http://localhost:8080/user/delete/${encontrado.id}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${loggedUser.token}`,
            },
            body: JSON.stringify(dados)
        });
        const data = await response.json();
        setTimeout(() => {
            if (response.ok) {
                modal.hide();
                showAlert('Sua conta foi deletada. Você será desconectado em instantes', 'danger');
                e.target.reset();

                setTimeout(() => {
                    logout();
                }, 2000)
            }
            else {
                modal.hide();
                showAlert(data.error, 'warning');
                e.target.reset();
            }
        }, 1000)
    }
    catch (error) {
        modal.hide();
        showAlert(error, 'warning');
        e.target.reset();
    }
});

const getAllUsers = async () => {
    debugger
    const users = await fetch(`http://localhost:8080/user/list`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${loggedUser.token}`,
        }
    });

    const data = await users.json();
    return data;
}