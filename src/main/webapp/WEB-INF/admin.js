document.addEventListener("DOMContentLoaded", async () => {
    const userList = document.getElementById("user-list");
    const bookList = document.getElementById("book-list");
    const userCount = document.getElementById("user-count");
    const bookCount = document.getElementById("book-count");
    const logoutBtn = document.getElementById("logout");

    const token = localStorage.getItem("token");
    const userRole = localStorage.getItem("role");

    // Redirection si l'utilisateur n'est pas admin
    if (!token || userRole !== "ADMIN") {
        window.location.href = "index.html";
    }

    // 📌 Charger les utilisateurs
    async function loadUsers() {
        const response = await fetch("http://localhost:5000/api/admin/users", {
            headers: { "Authorization": "Bearer " + token }
        });
        const users = await response.json();
        userCount.textContent = users.length;

        userList.innerHTML = "";
        users.forEach(user => {
            const userItem = document.createElement("div");
            userItem.innerHTML = `
                <p>👤 ${user.email} (${user.role})</p>
                <button onclick="deleteUser(${user.id})">❌ Supprimer</button>
            `;
            userList.appendChild(userItem);
        });
    }

    // 📌 Charger les livres
    async function loadBooks() {
        const response = await fetch("http://localhost:5000/api/admin/books", {
            headers: { "Authorization": "Bearer " + token }
        });
        const books = await response.json();
        bookCount.textContent = books.length;

        bookList.innerHTML = "";
        books.forEach(book => {
            const bookItem = document.createElement("div");
            bookItem.innerHTML = `
                <p>📖 <strong>${book.title}</strong> - ${book.author}</p>
                <button onclick="deleteBook(${book.id})">❌ Supprimer</button>
            `;
            bookList.appendChild(bookItem);
        });
    }

    // 📌 Supprimer un utilisateur
    window.deleteUser = async (id) => {
        await fetch("http://localhost:5000/api/admin/users/${id}", {
            method: "DELETE",
            headers: { "Authorization": "Bearer " + token }
        });
        loadUsers();
    };

    // 📌 Supprimer un livre
    window.deleteBook = async (id) => {
        await fetch("http://localhost:5000/api/admin/books/${id}", {
            method: "DELETE",
            headers: { "Authorization": "Bearer " + token }
        });
        loadBooks();
    };

    // 📌 Déconnexion
    logoutBtn.addEventListener("click", () => {
        localStorage.removeItem("token");
        window.location.href = "index.html";
    });

    // Charger les données au démarrage
    loadUsers();
    loadBooks();
});
