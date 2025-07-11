//pega token e verifica se há usuário logado
const email = sessionStorage.getItem("email") || null;
const userType = sessionStorage.getItem("userType");
const token = sessionStorage.getItem("token") || null;

if (!token)
    window.location.href = "http://localhost:8080/acesso/login.html"


const nameUser = document.querySelector('[data-name-user]')
nameUser.textContent = sessionStorage.getItem('username')

//Função que busca todos os usuários do sistema
let globalUserList = [];

const getUsersList = async () => {
    try {
        const users = await fetch(`http://localhost:8080/user/list`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${token}`,
            }
        });

        const data = await users.json();
        globalUserList = data;

    } catch (error) {
        console.error("Erro ao buscar usuários:", error);
    }
};

document.addEventListener("DOMContentLoaded", async (event) => {
    await getUsersList();
    await getAllUserFeedbacks();
    await fillSelectUsers();
    await setVisibilidade(userType);
})

const fillSelectUsers = async () => {
    // Preencher selects da tela principal (para GERENTE visualizar)
    const userViewSelects = document.querySelectorAll('[data-select-user-name]');
    userViewSelects.forEach(select => {
        if (!select.dataset.listenerAdded) {
            select.addEventListener("change", async (e) => {
                const selectedEmail = e.target.value;
                await getAllUserFeedbacks(selectedEmail);
                await setVisibilidade(userType);
            });
            select.dataset.listenerAdded = "true";
        }

        select.innerHTML = "";
        const defaultOption = document.createElement("option");
        defaultOption.value = "";
        defaultOption.disabled = true;
        defaultOption.selected = true;
        defaultOption.textContent = "Selecione um usuário...";
        select.appendChild(defaultOption);

        globalUserList.forEach(user => {
            if (user.email !== email) {
                const option = document.createElement("option");
                option.value = user.id;
                option.textContent = user.username;
                select.appendChild(option);
            }
        });
    });

    // Preencher selects de envio (modal de novo feedback)
    const recipientSelects = document.querySelectorAll('[data-select-recipient]');
    recipientSelects.forEach(select => {
        select.innerHTML = "";
        const defaultOption = document.createElement("option");
        defaultOption.value = "";
        defaultOption.disabled = true;
        defaultOption.selected = true;
        defaultOption.textContent = "Selecione um destinatário...";
        select.appendChild(defaultOption);

        globalUserList.forEach(user => {
            if (user.email !== email) {
                const option = document.createElement("option");
                option.value = user.id;
                option.textContent = user.username;
                select.appendChild(option);
            }
        });
    });
};

const getAllUserFeedbacks = async (user) => {
    const feedbackContainer = document.querySelector("[data-feedbacks-container]");
    feedbackContainer.innerHTML = "";
    let url = "";
    if (!user) {
        const encontrado = globalUserList.find(pessoa => email === pessoa.email);
        url = encontrado.id;

        if (!encontrado) {
            console.error("Usuário com email não encontrado.");
            return;
        }

    }
    else {
        url = user;
    }

    const total = await fetch(`http://localhost:8080/feedback/user/${url}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${token}`,
        }
    });

    const allFeedbacks = await total.json();

    if (allFeedbacks.feedbacks && allFeedbacks.feedbacks.length > 0) {
        allFeedbacks.feedbacks.forEach(feedback => {
            const feedbackWrapper = document.createElement("div");
            feedbackWrapper.className = "col-lg-6 mb-4";

            const card = document.createElement("div");
            card.className = "card feedback-card received shadow-sm mb-3";

            const cardBody = document.createElement("div");
            cardBody.className = "card-body";

            const cardBodyExcluir = document.createElement("div");
            cardBodyExcluir.className = "d-none card-body d-flex justify-content-center";
            cardBodyExcluir.style.height = "184px";

            const header = document.createElement("div");
            header.className = "d-flex justify-content-between align-items-start mb-3";

            const userInfo = document.createElement("div");
            userInfo.className = "d-flex align-items-center";

            const avatar = document.createElement("div");
            avatar.className = "feedback-avatar bg-success me-3";
            avatar.textContent = getInitials(feedback.author.username);

            const userTexts = document.createElement("div");

            const userName = document.createElement("h6");
            userName.className = "mb-0";
            userName.textContent = feedback.author.username;

            const team = document.createElement("small");
            team.className = "text-muted";
            team.textContent = "Equipe";

            const wrapperExcluir = document.createElement("div")
            wrapperExcluir.className = "d-flex align-items-center d-flex align-items-center justify-content-center flex-column"

            const textoExcluir = document.createElement("h6");
            textoExcluir.textContent = "Tem certeza que deseja excluir o feedback?"

            const wrapperButtonsDeleteEdit = document.createElement("div")
            wrapperButtonsDeleteEdit.className = "d-flex g-01 mt-3"

            const deleteButton = document.createElement("button")
            deleteButton.className = "btn btn-danger"
            deleteButton.textContent = "Deletar"
            deleteButton.addEventListener("click", () => deleteFeedback(feedback, feedbackWrapper))

            const cancelButton = document.createElement("button")
            cancelButton.className = "btn btn-secondary"
            cancelButton.textContent = "Cancelar"
            cancelButton.addEventListener("click", () => cancelDeletarFeedback(cardBody, cardBodyExcluir))

            wrapperExcluir.appendChild(textoExcluir)

            wrapperButtonsDeleteEdit.appendChild(deleteButton);
            wrapperButtonsDeleteEdit.appendChild(cancelButton);

            wrapperExcluir.appendChild(wrapperButtonsDeleteEdit)

            userTexts.appendChild(userName);
            userTexts.appendChild(team);

            userInfo.appendChild(avatar);
            userInfo.appendChild(userTexts);

            const actions = document.createElement("div");
            actions.className = "flex g-01 d-none";
            actions.dataset.visibilityGerente = "";

            const editIcon = document.createElement("i");
            editIcon.className = "bi bi-pencil-square me-2 cursor-pointer";
            editIcon.addEventListener("click", () => editFeedback(feedback));

            const deleteIcon = document.createElement("i");
            deleteIcon.className = "bi bi-trash3-fill me-2 cursor-pointer";
            deleteIcon.addEventListener("click", () => confirmarDeletar(cardBody, cardBodyExcluir));

            actions.appendChild(editIcon);
            actions.appendChild(deleteIcon);

            header.appendChild(userInfo);
            header.appendChild(actions);

            const ratingStars = document.createElement("div");
            ratingStars.className = "mb-2";
            ratingStars.textContent = "⭐".repeat(feedback.rating);

            const comment = document.createElement("p");
            comment.className = "card-text";
            comment.textContent = feedback.comment;

            const footer = document.createElement("div");
            footer.className = "d-flex justify-content-between align-items-center";

            const dateText = document.createElement("small");
            dateText.className = "text-muted";
            dateText.innerHTML = `<i class="bi bi-calendar me-1"></i>${formatDate(feedback.createdAt)}`;

            footer.appendChild(dateText);
            cardBodyExcluir.appendChild(wrapperExcluir)
            cardBody.appendChild(header);
            cardBody.appendChild(ratingStars);
            cardBody.appendChild(comment);
            cardBody.appendChild(footer);
            card.appendChild(cardBody);
            card.appendChild(cardBodyExcluir);
            feedbackWrapper.appendChild(card)
            feedbackContainer.appendChild(feedbackWrapper);
        });
    }
    else {
        gerarEmptyState('[data-feedbacks-container]');
    }
};

// Função que envia feedback
document.getElementById('feedbackForm').addEventListener("submit", async (e) => {
    e.preventDefault();
    const errorContainer = document.querySelector("[data-error-wrapper]");
    const form = e.target;
    const formData = new FormData(form);
    const dados = Object.fromEntries(formData.entries())

    try {
        const response = await fetch("http://localhost:8080/feedback/send", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${token}`,
            },
            body: JSON.stringify(dados)
        });

        if (!response.ok) {
            const errorMsg = await response.json();
            throw new Error(errorMsg.error);
        }
        else {
            const data = await response.json();
            const modal = bootstrap.Modal.getInstance(document.getElementById('newFeedbackModal'));
            modal.hide();

            const alertDiv = document.createElement('div');
            alertDiv.className = 'alert alert-success alert-dismissible fade show';
            alertDiv.innerHTML = `<i class="bi bi-check-circle me-2"></i>
                <strong>Sucesso!</strong> ${data.message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>`;

            const container = document.querySelector('.container-fluid.px-3.py-4');
            container.insertBefore(alertDiv, container.firstChild);

            form.reset();

            setTimeout(() => {
                if (alertDiv.parentNode) {
                    alertDiv.remove();
                }
            }, 5000);
        }

    }
    catch (error) {
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-warning alert-dismissible fade show';
        alertDiv.innerHTML = `<i class="bi bi-exclamation-octagon me-2"></i> ${error.message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>`;

        errorContainer.appendChild(alertDiv);

        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.remove();
            }
        }, 5000);
    }
})

function getInitials(nomeCompleto) {
    const nomes = nomeCompleto.trim().split(" ");
    if (nomes.length === 1) return nomes[0][0].toUpperCase();
    return (nomes[0][0] + nomes[nomes.length - 1][0]).toUpperCase();
}

function formatDate(dataIso) {
    const data = new Date(dataIso);
    const hoje = new Date();
    const diffTime = Math.abs(hoje - data);
    const diffDias = Math.floor(diffTime / (1000 * 60 * 60 * 24));
    return diffDias === 0 ? "Hoje" : `${diffDias} dia(s) atrás`;
}

const editFeedback = (feedback) => {
    const modal = bootstrap.Modal.getOrCreateInstance(document.getElementById('editFeedbackModal'));
    modal.show();

    document.getElementById("editFeedbackMessage").value = feedback.comment;
    document.getElementById("editCategorySelect").value = feedback.rating;
    document.getElementById("editRecipientSelect").value = feedback.user.id;

    //edita feedback
    document.getElementById('editFeedbackForm').addEventListener("submit", async (e) => {
        e.preventDefault();
        const errorContainer = document.querySelector("[data-error-wrapper-edit]");
        const form = e.target;
        const formData = new FormData(form);
        const dados = Object.fromEntries(formData.entries())

        try {
            const response = await fetch(`http://localhost:8080/feedback/${feedback.id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `${token}`,
                },
                body: JSON.stringify(dados)
            });

            if (!response.ok) {
                const errorMsg = await response.json();
                throw new Error(errorMsg.error);
            }
            else {
                const data = await response.json();
                console.log(data)
                const modal = bootstrap.Modal.getInstance(document.getElementById('editFeedbackModal'));
                modal.hide();

                const alertDiv = document.createElement('div');
                alertDiv.className = 'alert alert-success alert-dismissible fade show';
                alertDiv.innerHTML = `<i class="bi bi-check-circle me-2"></i>
                <strong>Sucesso!</strong> Seu feedback foi editado!
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>`;

                const container = document.querySelector('.container-fluid.px-3.py-4');
                container.insertBefore(alertDiv, container.firstChild);

                form.reset();

                window.location = "";

                setTimeout(() => {
                    if (alertDiv.parentNode) {
                        alertDiv.remove();
                    }
                }, 5000);
            }

        }
        catch (error) {
            const alertDiv = document.createElement('div');
            alertDiv.className = 'alert alert-warning alert-dismissible fade show';
            alertDiv.innerHTML = `<i class="bi bi-exclamation-octagon me-2"></i> ${error.message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>`;

            errorContainer.appendChild(alertDiv);

            setTimeout(() => {
                if (alertDiv.parentNode) {
                    alertDiv.remove();
                }
            }, 5000);
        }
    })
}

const confirmarDeletar = (carBody, cardBodyExcluir) => {
    carBody.classList.add("d-none");
    cardBodyExcluir.classList.remove("d-none")
}

const cancelDeletarFeedback = (carBody, cardBodyExcluir) => {
    carBody.classList.remove("d-none");
    cardBodyExcluir.classList.add("d-none")
}

const deleteFeedback = async (feedback, wrapper) => {
    try {

        const deleteFeedback = await fetch(`http://localhost:8080/feedback/${feedback.id}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${token}`,
            }
        });
        const result = await deleteFeedback.json();

        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-success alert-dismissible fade show';
        alertDiv.innerHTML = `<i class="bi bi-check-circle me-2"></i>
                <strong>Sucesso!</strong> ${result.message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>`;

        const container = document.querySelector('.container-fluid.px-3.py-4');
        container.insertBefore(alertDiv, container.firstChild);
        wrapper.remove();

        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.remove();
            }
        }, 5000);


    }
    catch (error) {
        console.log(error)
    }
}

async function gerarEmptyState(wrapper) {
    const container = document.querySelector(wrapper);
    container.innerHTML = ""; // limpa o conteúdo anterior

    const empty = document.createElement("div");
    empty.className = "col-lg-12 mb-4";

    if (userType === "COLABORADOR") {
        empty.innerHTML = `
            <div class="text-center text-muted mt-4" data-visibility-colaborador>
                <i class="bi bi-inbox" style="font-size: 2rem;"></i>
                <p class="mt-2">Nenhum feedback recebido</p>
            </div>`;
        container.appendChild(empty);
    } else if (userType === "GERENTE") {
        const instruction = document.createElement("p");
        instruction.className = "text-left text-muted mt-1";
        instruction.textContent = "Selecione um usuário para ver os seus feedbacks";

        const select = document.createElement("select");
        select.className = "form-select mt-2";
        select.setAttribute("data-select-user-name", "");

        empty.appendChild(instruction);
        empty.appendChild(select);
        container.appendChild(empty);

        await fillSelectUsers();
    }
}