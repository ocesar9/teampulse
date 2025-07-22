const loggedUser = {
    token: sessionStorage.getItem("token") || null,
    type: sessionStorage.getItem("userType"),
    username: sessionStorage.getItem("username")
}

nameLoggedUser.textContent = loggedUser.username;
const componentTotalPeople = document.querySelector('[data-total-pessoas-time]');

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

document.addEventListener("DOMContentLoaded", async (e) => {
    await getTotalUsersNumber();
    setVisibilidade(loggedUser.type);
})
