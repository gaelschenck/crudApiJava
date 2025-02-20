document.addEventListener("DOMContentLoaded", async () => {
    const bookList = document.getElementById("book-list");
    const bookForm = document.getElementById("book-form");
    const bookIdInput = document.getElementById("book-id");
    const titleInput = document.getElementById("title");
    const authorInput = document.getElementById("author");
    const logoutBtn = document.getElementById("logout");

    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "index.html"; // Rediriger si non connecté
    }

    // 📌 Charger la liste des livres
    async function loadBooks() {
        const response = await fetch("http://localhost:5000/api/books", {
            headers: { "Authorization": "Bearer " + token }
        });
        const books = await response.json();

        bookList.innerHTML = "";
        books.forEach(book => {
            const bookItem = document.createElement("div");
            bookItem.innerHTML = `
                <p><strong>${book.title}</strong> - ${book.author}</p>
                <button onclick="editBook(${book.id}, '${book.title}', '${book.author}')">Modifier</button>
                <button onclick="deleteBook(${book.id})">Supprimer</button>
            `;
            bookList.appendChild(bookItem);
        });
    }

    // 📌 Ajouter ou modifier un livre
    bookForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        const id = bookIdInput.value;
        const title = titleInput.value;
        const author = authorInput.value;

        const method = id ? "PUT" : "POST";
        const url = id ? `http://localhost:5000/api/books/${id}` : "http://localhost:5000/api/books";

        await fetch(url, {
            method: method,
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({ title, author })
        });

        bookIdInput.value = "";
        titleInput.value = "";
        authorInput.value = "";
        loadBooks();
    });

    // 📌 Pré-remplir le formulaire pour modifier un livre
    window.editBook = (id, title, author) => {
        bookIdInput.value = id;
        titleInput.value = title;
        authorInput.value = author;
    };

    // 📌 Supprimer un livre
    window.deleteBook = async (id) => {
        await fetch(`http://localhost:5000/api/books/${id}`, {
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

    // Charger les livres au démarrage
    loadBooks();
});
