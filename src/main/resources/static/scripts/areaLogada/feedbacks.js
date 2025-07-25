// Definição de variáveis com escopo global
const loggedUser = {
    token: sessionStorage.getItem("token") || null,
    type: sessionStorage.getItem("userType"),
    username: sessionStorage.getItem("username"),
    email: sessionStorage.getItem("email")
}

const nameLoggedUser = document.querySelector('[data-name-user]')
nameLoggedUser.textContent = loggedUser.username;

let allUsers = [];

//Redireciona para o dashboard caso seja ADMIN (apenas gestores e colaboradores podem acessar essa tela)
if (loggedUser.type == "ADMIN")
    window.location.href = "http://localhost:8080/dashboard"

//Função que pega a lista de todos os usuários cadastrados
const getAllUsers = async () => {
    try {
        const users = await fetch(`http://localhost:8080/user/list`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${loggedUser.token}`,
            }
        });

        const data = await users.json();
        allUsers = data;

    } catch (error) {
        console.error("Erro ao buscar usuários:", error);
    }
};

//Cria todos os selects que terão a lista de usuários como options e os preenche como tal
const createAndFillSelects = async () => {
    const userViewSelects = document.querySelectorAll('[data-select-user]');
    userViewSelects.forEach(select => {
        select.innerHTML = "";
        const defaultOption = document.createElement("option");
        defaultOption.value = "";
        defaultOption.disabled = true;
        defaultOption.selected = true;
        defaultOption.textContent = "- Selecione um usuário -";
        select.appendChild(defaultOption);

        allUsers.forEach(user => {
            if (user.email !== loggedUser.email) {
                const option = document.createElement("option");
                option.value = user.id;
                option.textContent = user.username;
                select.appendChild(option);
            }
        });
    });
};

document.addEventListener("DOMContentLoaded", async (event) => {
    await getAllUsers();
    await createAndFillSelects();

    if (loggedUser.type == "GERENTE") {
        await getSentFeedbacks();
        await getDrafts();
        document.getElementById("sent-tab").click();
    }

    if (loggedUser.type == "COLABORADOR")
        await getReceivedFeedbacks();


    await setVisibilidade(loggedUser.type);
});

const getReceivedFeedbacks = async () => {
    const container = document.querySelector("[data-feedbacks-received-container]")
    container.innerHTML = "";
    try {

        const response = await fetch(`http://localhost:8080/feedback/received`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${loggedUser.token}`,
            }
        });

        if (!response.ok)
            throw new Error(response.status)

        const result = await response.json();
        if (result.feedbacks && result.feedbacks.length > 0) {
            renderCards(result.feedbacks, document.querySelector("[data-feedbacks-received-container]"), {
                feedbackType: "received"
            });
        }
        else {
            createEmptyState(container, "Você ainda não recebeu nenhum feedback :(");
        }
    }
    catch (error) {
        console.log(error);
    }
}

const getSentFeedbacks = async () => {
    const container = document.querySelector("[data-feedbacks-sent-container]")
    container.innerHTML = "";

    try {

        const response = await fetch(`http://localhost:8080/feedback/sent`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${loggedUser.token}`,
            }
        });

        if (!response.ok)
            throw new Error(response.status)

        const result = await response.json();

        if (result.feedbacks && result.feedbacks.length > 0) {
            renderCards(result.feedbacks, document.querySelector("[data-feedbacks-sent-container]"), {
                feedbackType: "sent"
            });
        }
        else {
            createEmptyState(container, "Você ainda não enviou nenhum feedback");
        }
    }
    catch (error) {
        console.log(error)
    }
}

const getDrafts = async () => {
    const container = document.querySelector("[data-feedbacks-drafts-container]")
    container.innerHTML = "";

    try {

        const response = await fetch(`http://localhost:8080/feedback/drafts`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${loggedUser.token}`,
            }
        });

        if (!response.ok)
            throw new Error(response.status)

        const result = await response.json();
        if (result.drafts && result.drafts.length > 0) {
            renderCards(result.drafts, document.querySelector("[data-feedbacks-drafts-container]"), {

                feedbackType: "draft",
                showEditButton: true,
                showSendButton: true,
                onSend: (feedback) => sendFeedback(feedback),
                onEdit: (feedback) => openModalEditDraft(feedback)
            })
        }
        else {
            createEmptyState(container, "Você não possui rascunhos criados");
        }
    }
    catch (error) {
        console.log(error)
    }
}

async function createEmptyState(wrapper, msg) {
    const parentElement = wrapper;
    parentElement.innerHTML = "";

    const emptyState = document.createElement("div");
    emptyState.className = "col-lg-12 mb-4";

    emptyState.innerHTML = `
            <div class="text-center text-muted mt-4">
                <i class="bi bi-inbox" style="font-size: 2rem;"></i>
                <p class="mt-2 fs-5">${msg}</p>
            </div>`;

    parentElement.appendChild(emptyState);
}

const deleteButton = document.querySelector("[data-btn-delete-draft]")
const actionButtonModal = document.getElementById("actionFeedbackText");
const modalFooter = document.querySelector(".modal-footer")

const openModalNewFeedback = () => {
    modalFooter.classList.add("justify-content-end");
    modalFooter.classList.remove("justify-content-between");
    const modal = document.getElementById("feedbackModal");
    const form = document.getElementById("feedbackForm");
    form.reset();
    modal.dataset.mode = "create";
    actionButtonModal.textContent = "Salvar como rascunho";
    deleteButton.classList.add("d-none");
    new bootstrap.Modal(modal).show();
}

const openModalEditDraft = (feedback) => {
    deleteButton.classList.remove("d-none");
    modalFooter.classList.add("justify-content-between");
    modalFooter.classList.remove("justify-content-end");
    const modal = document.getElementById("feedbackModal");
    modal.dataset.mode = "send";
    modal.dataset.feedbackId = feedback.id;
    const form = document.getElementById("feedbackForm");
    actionButtonModal.textContent = "Atualizar rascunho";

    form.elements["comment"].value = feedback.comment;
    form.elements["rating"].value = feedback.rating;
    form.elements["userId"].value = feedback.user.id;

    new bootstrap.Modal(modal).show();
}

async function sendFeedback(draft) {
    console.log(draft)
    const form = document.getElementById("feedbackForm");

    const dados = {
        feedbackId: draft.id
    }

    try {
        let response;

        response = await fetch(`http://localhost:8080/feedback/send`, {
            method: `POST`,
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${loggedUser.token}`,
            },
            body: JSON.stringify(dados)
        });

        if (!response.ok) {
            const errorMsg = await response.json();
            throw new Error(errorMsg.error);
        }

        else {
            const data = await response.json();
            await getDrafts();

            await getSentFeedbacks();

            const alertDiv = document.createElement('div');
            alertDiv.className = 'alert alert-success alert-dismissible fade show';
            alertDiv.innerHTML = `<i class="bi bi-check-circle me-2"></i>
            <strong>Sucesso!</strong> ${data.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>`;

            const container = document.querySelector('.container-fluid.px-3.py-4');
            container.insertBefore(alertDiv, container.firstChild);

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

        const container = document.querySelector('.container-fluid.px-3.py-4');
        container.insertBefore(alertDiv, container.firstChild);

        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.remove();
            }
        }, 5000);
    }
}

document.getElementById('feedbackForm').addEventListener("submit", async (e) => {
    e.preventDefault();

    const modal = document.getElementById("feedbackModal");
    const errorContainer = document.querySelector("[data-error-wrapper]");
    const mode = modal.dataset.mode;
    const feedbackId = modal.dataset.feedbackId;
    const form = e.target;
    const formData = new FormData(form);
    const dados = Object.fromEntries(formData.entries());

    const isCreate = mode === "create";
    const url = `http://localhost:8080/feedback/draft${isCreate ? "" : `/${feedbackId}`}`;
    const method = isCreate ? "POST" : "PUT";

    try {
        let response;

        response = await fetch(url, {
            method: `${method}`,
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${loggedUser.token}`,
            },
            body: JSON.stringify(dados)
        });

        if (!response.ok) {
            const errorMsg = await response.json();
            throw new Error(errorMsg.error);
        }

        else {
            const data = await response.json();
            await getDrafts();

            if (!isCreate)
                await getSentFeedbacks();

            const alertDiv = document.createElement('div');
            alertDiv.className = 'alert alert-success alert-dismissible fade show';
            alertDiv.innerHTML = `<i class="bi bi-check-circle me-2"></i>
            <strong>Sucesso!</strong> ${data.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>`;

            const container = document.querySelector('.container-fluid.px-3.py-4');
            container.insertBefore(alertDiv, container.firstChild);

            bootstrap.Modal.getInstance(modal).hide();
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

const deleteDraft = async () => {
    const modal = document.getElementById("feedbackModal");
    const draftId = modal.dataset.feedbackId;
    const errorContainer = document.querySelector("[data-error-wrapper]");

    try {
        let response;

        response = await fetch(`http://localhost:8080/feedback/draft/${draftId}`, {
            method: `DELETE`,
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${loggedUser.token}`,
            }
        });

        if (!response.ok) {
            const errorMsg = await response.json();
            throw new Error(errorMsg.error);
        }

        else {
            const data = await response.json();
            await getDrafts();

            const alertDiv = document.createElement('div');
            alertDiv.className = 'alert alert-success alert-dismissible fade show';
            alertDiv.innerHTML = `<i class="bi bi-check-circle me-2"></i>
            <strong>Sucesso!</strong> ${data.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>`;

            const container = document.querySelector('.container-fluid.px-3.py-4');
            container.insertBefore(alertDiv, container.firstChild);

            bootstrap.Modal.getInstance(modal).hide();
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
}

const renderCards = (feedbacks, containerElement, options = {}) => {
    containerElement.innerHTML = "";

    feedbacks.forEach(feedback => {
        const feedbackWrapper = document.createElement("div");
        feedbackWrapper.className = "col-lg-6 mb-4";

        const card = document.createElement("div");
        card.className = "card feedback-card shadow-sm mb-3";
        card.classList.add(options.feedbackType)

        const cardBody = document.createElement("div");
        cardBody.className = "card-body";

        const header = document.createElement("div");
        header.className = "d-flex justify-content-between align-items-start mb-3";

        const userInfo = document.createElement("div");
        userInfo.className = "d-flex align-items-center";

        const avatar = document.createElement("div");
        avatar.className = "feedback-avatar me-3";

        const userTexts = document.createElement("div");
        const userName = document.createElement("h6");
        const recipientName = document.createElement("h6")

        userName.className = "mb-0";

        if (options.feedbackType == "draft") {
            avatar.classList.add("bg-secondary");
            avatar.textContent = getInitials("...");
            userName.textContent = "Ainda não enviado";
            userName.classList.add("text-muted")

            recipientName.className = "text-muted mt-1 fs-7"
            recipientName.textContent = `Para: ${feedback.author?.username}`
        }
        else {
            avatar.classList.add("bg-success");
            avatar.textContent = getInitials(feedback.author?.username || "Usuário");
            userName.textContent = feedback.author?.username || "Usuário";
        }

        userTexts.appendChild(userName);

        if (options.feedbackType == "draft")
            userTexts.appendChild(recipientName)

        userInfo.appendChild(avatar);
        userInfo.appendChild(userTexts);
        header.appendChild(userInfo);

        const ratingStars = document.createElement("div");
        ratingStars.className = "mb-2";
        ratingStars.textContent = "⭐".repeat(feedback.rating ?? 0);

        const comment = document.createElement("p");
        comment.className = "card-text";
        comment.textContent = feedback.comment;

        const footer = document.createElement("div");
        footer.className = "d-flex justify-content-between align-items-center";

        const dateText = document.createElement("small");
        dateText.className = "text-muted";
        dateText.innerHTML = `<i class="bi bi-calendar me-1"></i>${formatDate(feedback.createdAt, options.feedbackType)}`;

        footer.appendChild(dateText);

        const actionButtonsWrapper = document.createElement("div");
        actionButtonsWrapper.className = "d-flex g-5"

        if (options.showEditButton) {
            const editBtn = document.createElement("button");
            editBtn.className = "btn btn-sm btn-secondary";
            editBtn.innerHTML = "<i class='bi bi-pencil-square'></i> Editar";
            editBtn.addEventListener("click", () => {
                options.onEdit?.(feedback);
            });
            editBtn.dataset.editDraftBtn = "";
            actionButtonsWrapper.appendChild(editBtn);
        }

        if (options.showSendButton) {
            const sendBtn = document.createElement("button");
            sendBtn.className = "btn btn-sm btn-primary";
            sendBtn.innerHTML = "<i class='bi bi-send'></i> Enviar";
            sendBtn.addEventListener("click", () => {
                options.onSend?.(feedback);
            });
            sendBtn.dataset.sendDraftBtn = "";
            actionButtonsWrapper.appendChild(sendBtn);
        }

        if (options.showSendButton || options.showEditButton) {
            footer.appendChild(actionButtonsWrapper);
        }

        cardBody.appendChild(header);
        cardBody.appendChild(ratingStars);
        cardBody.appendChild(comment);
        cardBody.appendChild(footer);

        card.appendChild(cardBody);
        feedbackWrapper.appendChild(card);
        containerElement.appendChild(feedbackWrapper);
    });
}

function getInitials(fullName) {
    const names = fullName.trim().split(" ");
    if (names.length === 1) return names[0][0].toUpperCase();
    return (names[0][0] + names[names.length - 1][0]).toUpperCase();
}

function formatDate(isoDate, msgType) {
    const date = new Date(isoDate);
    const today = new Date();
    const diffTime = Math.abs(today - date);
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
    if (msgType != "draft")
        return diffDays === 0 ? "Hoje" : `${diffDays} dia(s) atrás`;
    return diffDays === 0 ? "Criado hoje" : `Criado ${diffDays} dia(s) atrás`
}
