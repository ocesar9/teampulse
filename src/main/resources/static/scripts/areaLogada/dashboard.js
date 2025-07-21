const token = sessionStorage.getItem("token") || null;
const componentTotalPeople = document.querySelector('[data-total-pessoas-time]');
const nameUser = document.querySelector('[data-name-user]')
nameUser.textContent = sessionStorage.getItem('username')
const userType = sessionStorage.getItem("userType");

if (!token)
    window.location.href = "http://localhost:8080/acesso/login"

const getTotalUsersNumber = async () => {
    const total = await fetch(`http://localhost:8080/user/count`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${token}`,
        }
    });

    const data = await total.json();
    componentTotalPeople.textContent = data.total;
}

document.addEventListener("DOMContentLoaded", async (e) => {
    await getTotalUsersNumber();
    setVisibilidade(userType);
})
