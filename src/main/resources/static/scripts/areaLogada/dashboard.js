const nameLoggedUser = document.querySelector('[data-name-user]')
nameLoggedUser.textContent = loggedUser.username;
const componentTotalPeople = document.querySelector('[data-total-usuarios]');
const componentTotalSquads = document.querySelector('[data-total-squads]')
const welcomeMessage = document.querySelector('[data-message-welcome]')
welcomeMessage.textContent = `Bem vindo, ${loggedUser.username}`
let allUsers = [];
let allSquads = [];
let mySquad = [];

const getTotalUsersNumber = async () => {
    const total = await fetch(`http://localhost:8080/user/count`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${loggedUser.token}`,
        }
    });

    const data = await total.json();
    componentTotalPeople.textContent = data.total;
}

const getTotalSquadsNumber = async () => {
    try {
        const total = await fetch(`http://localhost:8080/squads`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${loggedUser.token}`,
            }
        });

        if (!total.ok)
            throw new Error(total.status)

        const data = await total.json();

        componentTotalSquads.textContent = data.total || "0";
    }
    catch (e) {
        console.log(e);
    }
}

document.addEventListener("DOMContentLoaded", async (e) => {
    await getTotalUsersNumber();
    allUsers = await getAllUsers();

    if (loggedUser.type == "GERENTE") {
        await getTotalSquadsNumber();
        allSquads = await getAllSquads();
        await createAndFillTables();
        document.getElementById("users-tab").classList.add("active");
    }

    else if (loggedUser.type == "COLABORADOR") {
        allSquads = await getMySquad();
        await createAndFillTables();
        document.getElementById("teams-tab").click();
    }

    await createAndFillRows();

    setVisibilidade(loggedUser.type);
})

const createAndFillRows = async () => {
    const tbody = document.querySelector('[data-table-all-users]');
    tbody.innerHTML = "";

    if (!allUsers || allUsers.length <= 1) {
        const wrapper = document.querySelector("[data-empty-state-users]")
        return createEmptyState(wrapper, "Ainda não existem outros usuários registrados");
    }

    allUsers.forEach(user => {
        if (user.email !== loggedUser.email) {
            const tr = document.createElement("tr");
            const td1 = document.createElement("td");
            const divFlex = document.createElement("div");
            divFlex.className = "d-flex align-items-center";

            const avatar = document.createElement("div");
            avatar.className = "team-avatar bg-primary me-3";
            avatar.textContent = getInitials(user.username);

            const divInfo = document.createElement("div");
            const name = document.createElement("div");
            name.className = "fw-semibold";
            name.textContent = `${user.username}`;

            divInfo.appendChild(name);
            divFlex.appendChild(avatar);
            divFlex.appendChild(divInfo);
            td1.appendChild(divFlex);

            const td2 = document.createElement("td");
            td2.textContent = `${user.email}`;

            const td3 = document.createElement("td");
            const badge = document.createElement("span");
            badge.className = "badge bg-secondary";
            badge.textContent = user.userType;
            td3.appendChild(badge);

            const td4 = document.createElement("td");
            const statusIcon = document.createElement("i");
            statusIcon.className = "bi bi-circle-fill user-status active me-2";
            td4.appendChild(statusIcon);
            td4.append("Ativo");

            const td5 = document.createElement("td");
            td5.className = "text-center";

            const btnEdit = document.createElement("button");
            btnEdit.className = "btn btn-sm btn-outline-primary action-btn me-2";
            btnEdit.addEventListener("click", () => openModalEditUser(user));

            const iconEdit = document.createElement("i");
            iconEdit.className = "bi bi-pencil me-1";
            btnEdit.appendChild(iconEdit);
            btnEdit.id = "test-btn-edit-user";
            btnEdit.append("Editar");

            const btnDelete = document.createElement("button");
            btnDelete.className = "btn btn-sm btn-outline-danger action-btn";
            btnDelete.dataset.btnDeleteUser = "";
            btnDelete.addEventListener("click", () => {
                let textOptions = {
                    title: "Confirmação de exclusão",
                    text: `Tem certeza que deseja excluir o usuário <strong>${user.username}</strong>? `
                }
                let url = `user/delete/${user.id}`;
                const onConfirm = async () => {
                    allUsers = await getAllUsers();
                    await createAndFillRows();
                    await getTotalUsersNumber();
                }
                openGenericDeleteModal(textOptions, url, onConfirm);
            });

            const iconDelete = document.createElement("i");
            iconDelete.className = "bi bi-trash me-1";
            btnDelete.appendChild(iconDelete);
            btnDelete.append("Excluir");

            tr.appendChild(td1);
            tr.appendChild(td2);
            tr.appendChild(td3);
            tr.appendChild(td4);

            if (loggedUser.type === "ADMIN") {
                td5.appendChild(btnEdit);
                td5.appendChild(btnDelete);
                tr.appendChild(td5);
            }

            tbody.appendChild(tr);
        }
    });

};

const createAndFillTables = async () => {
    const container = document.querySelector('[data-table-all-squads]');
    container.innerHTML = "";

    if (!allSquads || allSquads.squads.length <= 0) {
        return createEmptyState(container, "Nenhum time encontrado");
    }

    allSquads.squads.forEach(squad => {
        const col = document.createElement("div");
        col.className = "col-lg-4 col-md-6 mb-4";

        const card = document.createElement("div");
        card.className = "card team-card h-100";

        const cardBody = document.createElement("div");
        cardBody.className = "card-body p-4";

        const topSection = document.createElement("div");
        topSection.className = "d-flex justify-content-between align-items-start mb-3";

        const titleSection = document.createElement("div");

        const teamTitle = document.createElement("h6");
        teamTitle.className = "card-title mb-1 fw-bold";
        teamTitle.textContent = squad.nome;

        titleSection.appendChild(teamTitle);

        const dropdown = document.createElement("div");
        dropdown.className = "dropdown";

        const dropdownBtn = document.createElement("button");
        dropdownBtn.className = "btn btn-sm btn-light";
        dropdownBtn.setAttribute("type", "button");
        dropdownBtn.setAttribute("data-bs-toggle", "dropdown");
        dropdownBtn.setAttribute("aria-expanded", "false");
        dropdownBtn.dataset.openOptionsDelete = "";
        dropdownBtn.dataset.visibilityGerente = "";

        const dotsIcon = document.createElement("i");
        dotsIcon.className = "bi bi-three-dots-vertical";

        dropdownBtn.appendChild(dotsIcon);

        const dropdownMenu = document.createElement("ul");
        dropdownMenu.className = "dropdown-menu dropdown-menu-end";

        const deleteItem = document.createElement("li");
        const deleteLink = document.createElement("a");
        deleteLink.className = "dropdown-item text-danger";
        deleteLink.href = "javascript:void(0)";
        deleteLink.dataset.openModalDeleteTeam = "";

        deleteLink.addEventListener("click", () => {
            let textOptions = {
                title: "Confirmação de exclusão de squad",
                text: `Tem certeza que deseja excluir a squad <strong>${squad.nome}</strong>? `
            }
            let url = `squads/${squad.id}`;
            const onConfirm = async () => {
                allSquads = await getAllSquads();
                await createAndFillTables();
                await getTotalSquadsNumber();
            }
            openGenericDeleteModal(textOptions, url, onConfirm);
        })

        deleteLink.innerHTML = `<i class="bi bi-trash me-2"></i>Apagar time`;
        deleteItem.appendChild(deleteLink);

        dropdownMenu.appendChild(deleteItem);

        dropdown.appendChild(dropdownBtn);
        dropdown.appendChild(dropdownMenu);

        topSection.appendChild(titleSection);
        topSection.appendChild(dropdown);

        const middleSection = document.createElement("div");
        middleSection.className = "mb-3";

        const memberLine = document.createElement("div");
        memberLine.className = "d-flex align-items-center mb-3";

        const peopleIcon = document.createElement("i");
        peopleIcon.className = "bi bi-people me-2 text-success";

        const memberCount = document.createElement("span");
        memberCount.className = "fw-semibold";
        memberCount.textContent = `${squad.composicao.totalMembros} Membros`;

        memberLine.appendChild(peopleIcon);
        memberLine.appendChild(memberCount);

        const avatarContainer = document.createElement("div");
        avatarContainer.className = "team-members-display";

        squad.composicao.membros.forEach(member => {
            const avatar = document.createElement("div");
            avatar.className = `team-avatar bg-warning`;
            avatar.textContent = getInitials(member.nome);
            avatar.title = member.nome;
            avatarContainer.appendChild(avatar);
        });

        middleSection.appendChild(memberLine);
        middleSection.appendChild(avatarContainer);

        const footer = document.createElement("div");
        footer.className = "d-flex justify-content-between align-items-center";

        const createdText = document.createElement("small");
        createdText.className = "text-muted";
        createdText.textContent = `Criado: ${formatDate(squad.dataCriacao)}`

        const manageBtn = document.createElement("button");
        manageBtn.dataset.visibilityGerente = "";
        manageBtn.dataset.editTeam = "";
        manageBtn.className = "btn btn-sm btn-primary";
        manageBtn.addEventListener("click", () => {
            openModalManageTeam(squad);
        })
        manageBtn.innerHTML = `<i class="bi bi-gear me-1"></i> Gerenciar`;

        footer.appendChild(createdText);
        footer.appendChild(manageBtn);

        cardBody.appendChild(topSection);
        cardBody.appendChild(middleSection);
        cardBody.appendChild(footer);

        card.appendChild(cardBody);
        col.appendChild(card);

        container.appendChild(col);
    })
}

function openModalEditUser(user) {
    const modal = document.getElementById('editUserModal');
    const modalBootstrap = new bootstrap.Modal(modal)
    const form = document.getElementById("editUserForm");

    form.elements["username"].value = user.username;
    form.elements["email"].value = user.email;
    modal.dataset.userId = user.id;

    modalBootstrap.show();
}

document.getElementById('editUserForm').addEventListener("submit", async (e) => {
    e.preventDefault();
    const modal = document.getElementById('editUserModal');
    const userId = modal.dataset.userId;
    const form = e.target;
    const formData = new FormData(form);
    const dados = Object.fromEntries(formData.entries());

    try {
        let response;

        response = await fetch(`http://localhost:8080/user/edit/${userId}`, {
            method: `PUT`,
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
            allUsers = await getAllUsers();
            await createAndFillRows();
            bootstrap.Modal.getInstance(modal).hide();

            showAlert(`Usuário <b>${data.username}</b> atualizado com sucesso`, "success", "[data-alerts-users-squads]");
        }

    }
    catch (error) {
        showAlert(error.message, "warning", "[data-error-wrapper-edit-user]");
    }
})

const openGenericDeleteModal = async (textOptions, url, onConfirm) => {
    const modal = document.getElementById('deleteConfirmModal');
    const modalBootstrap = new bootstrap.Modal(modal);
    const deleteModalTitle = document.querySelector("[data-delete-confirm-modal-title]");
    const deleteModalText = document.querySelector("[data-delete-confirm-modal-text]");
    const confirmDeleteBtn = document.getElementById("confirmDeleteBtn")

    const newBtn = confirmDeleteBtn.cloneNode(true);
    confirmDeleteBtn.parentNode.replaceChild(newBtn, confirmDeleteBtn);

    deleteModalTitle.textContent = textOptions.title;
    deleteModalText.innerHTML = textOptions.text;

    modalBootstrap.show();

    newBtn.addEventListener("click", async () => {
        try {
            let response;
            response = await fetch(`http://localhost:8080/${url}`, {
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

                await onConfirm();

                bootstrap.Modal.getInstance(modal).hide();
                showAlert("Deletado com sucesso!", "success", "[data-alerts-users-squads]");
            }

        }
        catch (error) {
            showAlert(error.message, "warning", "[data-error-wrapper-modal-delete]");
        }


    })

}

let selectedTeamMembers = [];
let editingTeam = null;

const setSelectedTeam = (team) => {
    editingTeam = team;
    selectedTeamMembers = [...team.composicao.membros];
};

const renderLists = (availableUsersContainer, actualParticipantsContainer, allUsers, loggedUser) => {
    availableUsersContainer.innerHTML = "";
    actualParticipantsContainer.innerHTML = "";

    allUsers.forEach(user => {
        const alreadySelected = selectedTeamMembers.some(u => u.email === user.email);
        if (!alreadySelected) {
            const item = createUserListItem(user, () => {
                selectedTeamMembers.push(user);
                renderLists(availableUsersContainer, actualParticipantsContainer, allUsers, loggedUser);
            });
            availableUsersContainer.appendChild(item);
        }
    });

    selectedTeamMembers.forEach(user => {
        const isRemovable = user.email !== loggedUser.email;
        const item = createSelectedMemberItem(user, isRemovable ? () => {
            selectedTeamMembers = selectedTeamMembers.filter(u => u.email !== user.email);
            renderLists(availableUsersContainer, actualParticipantsContainer, allUsers, loggedUser);
        } : null);
        actualParticipantsContainer.appendChild(item);
    });
};

const createUserListItem = (user, onAdd) => {
    const item = document.createElement("div");
    item.className = "list-group-item d-flex justify-content-between align-items-center bg-transparent border-0 px-0";

    const left = document.createElement("div");
    left.className = "d-flex align-items-center";

    const avatar = document.createElement("div");
    avatar.className = "team-avatar bg-danger me-3";

    avatar.textContent = getInitials(user.username ?? user.nome);

    const info = document.createElement("div");
    const name = document.createElement("div");
    name.className = "fw-semibold";
    name.textContent = user.username;

    info.appendChild(name);
    left.appendChild(avatar);
    left.appendChild(info);

    const button = document.createElement("button");
    button.className = "btn btn-sm btn-outline-success";
    button.innerHTML = '<i class="bi bi-plus"></i>';
    button.addEventListener("click", onAdd);

    item.appendChild(left);
    item.appendChild(button);

    return item;
};

const createSelectedMemberItem = (user, onRemove) => {
    const item = document.createElement("div");
    item.className = "list-group-item d-flex justify-content-between align-items-center bg-transparent border-0 px-0";

    const left = document.createElement("div");
    left.className = "d-flex align-items-center";

    const avatar = document.createElement("div");
    avatar.className = "team-avatar bg-primary me-3";

    avatar.textContent = getInitials(user.nome ?? user.username);

    const info = document.createElement("div");
    const name = document.createElement("div");
    name.className = "fw-semibold";
    name.textContent = user.nome ?? user.username;

    info.appendChild(name);
    left.appendChild(avatar);
    left.appendChild(info);

    item.appendChild(left);

    if (onRemove) {
        const button = document.createElement("button");
        button.className = "btn btn-sm btn-outline-danger";
        button.innerHTML = '<i class="bi bi-dash"></i>';
        button.addEventListener("click", onRemove);
        item.appendChild(button);
    }

    return item;
};

let originalTeamMembers = [];

const openModalManageTeam = (team = null) => {
    const modal = document.getElementById("manageTeamModal");
    const modalBootstrap = new bootstrap.Modal(modal);
    const availableUsers = document.querySelector("[data-list-collaborators]");
    const actualParticipants = document.querySelector("[data-team-members]");
    let teamName = document.getElementById("teamName");

    const loggedUser = {
        username: sessionStorage.getItem("username"),
        email: sessionStorage.getItem("email")
    };

    if (team) {
        setSelectedTeam(team);
        modal.dataset.squadId = team.id;
        teamName.required = false;
        originalTeamMembers = [...team.composicao.membros];
        selectedTeamMembers = [...team.composicao.membros];

    } else {
        originalTeamMembers = [];
        teamName.required = true;
        const userFromAll = allUsers.find(u => u.email === loggedUser.email);
        selectedTeamMembers = [userFromAll ?? loggedUser];
        modal.dataset.squadId = "";
    }

    renderLists(availableUsers, actualParticipants, allUsers, loggedUser);
    modalBootstrap.show();
};

document.getElementById('manageTeamModal').addEventListener("submit", async (e) => {
    e.preventDefault();
    const modal = document.getElementById('manageTeamModal');
    const isCreate = modal.dataset.squadId == "" ? true : false;
    const form = e.target;
    const formData = new FormData(form);
    const dados = Object.fromEntries(formData.entries());
    const squadId = modal.dataset.squadId;

    const url = `http://localhost:8080/squads${isCreate ? `` : `/edit/${squadId}`}`;
    const method = isCreate ? "POST" : "PUT";

    const currentIds = selectedTeamMembers.map(user => user.id);
    const originalIds = originalTeamMembers.map(user => user.id);
    const memberIds = currentIds.filter(id => !originalIds.includes(id));
    const membersToRemove = originalIds.filter(id => !currentIds.includes(id));
    const name = dados.name;
    const newName = dados.name;

    const payload = {
        ...(isCreate ? { name } : { newName }),
        ...(isCreate ? { memberIds: currentIds } : {}),
        ...(!isCreate && memberIds.length > 0 ? { memberIds } : {}),
        ...(!isCreate && membersToRemove.length > 0 ? { membersToRemove } : {})
    };

    try {
        const response = await fetch(url, {
            method: `${method}`,
            headers: {
                "Content-Type": "application/json",
                "Authorization": `${loggedUser.token}`,
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMsg = await response.json();
            throw new Error(errorMsg.error);

        } else {
            const data = await response.json();
            allSquads = await getAllSquads();
            await createAndFillTables();
            await getTotalSquadsNumber();

            bootstrap.Modal.getInstance(modal).hide();
            form.reset();

            if (isCreate)
                showAlert(`Time <b>"${data.nome}"</b> criado com sucesso!`, "success", "[data-alerts-users-squads]");
            else
                showAlert(`Time <b>"${data.nome}"</b> editado com sucesso!`, "success", "[data-alerts-users-squads]");
        }

    } catch (error) {
        showAlert(error.message, "warning", "[data-error-wrapper-add-team]");
    }
});

