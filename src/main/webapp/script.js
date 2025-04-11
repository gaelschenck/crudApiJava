document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("token");
    console.log("Token envoyé : ", token);
    const userRole = localStorage.getItem("role");
    const currentPage = window.location.pathname;

    if (!token && currentPage !== "/index.html") {
        window.location.href = "index.html"; // Rediriger vers la page de connexion
    }

    // Page de connexion
    if (currentPage === "/" || currentPage === "/index.html") {
        console.log("Page de connexion détectée !");
        initLoginPage();
    }    

    // Dashboard
    if (currentPage === "/DashboardUser.html" || currentPage === "/DashboardAdmin.html") {
        console.log("Page du tableau de bord détectée !");
        initDashboard();
    }

    // Page Mon Compte
    if (currentPage === "/moncompte.html") {
        console.log("Page Mon Compte détectée !");
        initMonComptePage();
    }

    // Page Emprunts
    if (currentPage === "/emprunts.html") {
        console.log("Page Emprunts détectée !");
        initEmpruntsPage();
    }

    if (currentPage === "/books.html") {
        console.log("Page de gestion des livres détectée !");
        initBooksPage(); // Initialisation des livres
    }
});

// === FONCTIONS PAR PAGE === //
function initBooksPage() {
    const bookForm = document.getElementById("book-form");
    const bookIdInput = document.getElementById("book-id");
    const titleInput = document.getElementById("title");
    const authorInput = document.getElementById("author");
    const logoutBtn = document.getElementById("logout");
    const token = localStorage.getItem("token");
    console.log("Token récupéré :", token);

    if (!token) {
        console.warn("Aucun token, redirection vers la page de login...");
        window.location.href = "index.html"; // Rediriger si non connecté
    }

    // Charger la liste des livres
    async function loadBooks() {
        if (!bookList) {
            console.error("Élément 'book-list' non trouvé.");
            return;
        }

        const response = await fetch("http://localhost:5000/api/books", {
            headers: { 
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
             }
        });

        if (!response.ok) {
            console.error("Erreur lors du chargement des livres : " + response.status);
            return;
        }

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

    // Ajouter un livre
    async function addBook(title, author) {
        try {
            const response = await fetch("http://localhost:5000/api/books", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: JSON.stringify({ title, author })
            });
        
            console.log(response);
            const data = await response.json();
            if (!response.ok) {
                throw new Error("Erreur lors de l'ajout du livre : " + response.status + " " + data.message);
            }
            // Recharge la liste des livres après l'ajout
            alert("Livre ajouté avec succès !");
            loadBooks();
        } catch (error) {
            console.error(error);
            alert("Une erreur est survenue lors de l'ajout du livre.");
        }
    }

    // Modifier un livre
    async function updateBook(id, title, author) {
        return fetch(`http://localhost:5000/api/books/${id}`, {
            method: "PUT",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ title, author })
        });
    }

    // Gérer l'ajout et la modification d'un livre
    bookForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        const id = bookIdInput.value;
        const title = titleInput.value;
        const author = authorInput.value;

        // Si un ID est défini, on met à jour le livre, sinon on l'ajoute
        const method = id ? updateBook(id, title, author) : addBook(title, author);
        await method;

        // Réinitialiser le formulaire
        bookIdInput.value = "";
        titleInput.value = "";
        authorInput.value = "";
    });

    // Pré-remplir le formulaire pour modifier un livre
    window.editBook = (id, title, author) => {
        bookIdInput.value = id;
        titleInput.value = title;
        authorInput.value = author;
    };

    // Supprimer un livre
    window.deleteBook = async (id) => {
        await fetch(`http://localhost:5000/api/books/${id}`, {
            method: "DELETE",
            headers: { "Authorization": "Bearer " + token }
        });
        loadBooks();
    };

    // Déconnexion
    logoutBtn.addEventListener("click", () => {
        localStorage.removeItem("token");
        window.location.href = "index.html";
    });

}

function initEmpruntsPage() {
    const token = localStorage.getItem("token");
    const bookList = document.getElementById("book-list");
    const editBookForm = document.getElementById("edit-book-form");
    const editBookIdInput = document.getElementById("edit-book-id");
    const editTitleInput = document.getElementById("edit-title");
    const editAuthorInput = document.getElementById("edit-author");
    const logoutBtn = document.getElementById("logout");

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

    // Modifier un livre
    window.editBook = (id, title, author) => {
        editBookIdInput.value = id;
        editTitleInput.value = title;
        editAuthorInput.value = author;
    };

    // Supprimer un livre
    window.deleteBook = async (id) => {
        await fetch(`http://localhost:5000/api/books/${id}`, {
            method: "DELETE",
            headers: { "Authorization": "Bearer " + token }
        });
        loadBooks();
    };

    // Mettre à jour un livre
    editBookForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        const id = editBookIdInput.value;
        const title = editTitleInput.value;
        const author = editAuthorInput.value;

        await fetch(`http://localhost:5000/api/books/${id}`, {
            method: "PUT",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ title, author })
        });

        editBookIdInput.value = "";
        editTitleInput.value = "";
        editAuthorInput.value = "";
        loadBooks(); // Recharge la liste des livres
    });

    // Déconnexion
    logoutBtn.addEventListener("click", () => {
        localStorage.clear();
        window.location.href = "index.html";
    });

    // Charger les livres au démarrage
    loadBooks();
}

function initLoginPage() {
    const form = document.getElementById("login-form");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        console.log("Tentative de connexion avec :", { email, password });

        const formData = new URLSearchParams();
        formData.append("email", email);
        formData.append("password", password);

        try {
            const response = await fetch("http://localhost:5000/login", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: formData.toString()
            });
            const data = await response.json();
            if (response.ok) {
                localStorage.setItem("userId", data.userId); 
                localStorage.setItem("token", data.token);
                localStorage.setItem("role", data.role);
                localStorage.setItem("email", data.email || ""); // si ton backend les envoie
                localStorage.setItem("name", data.name || "");

                console.log("Connexion réussie !");
                window.location.href = data.role === "USER" ? "DashboardUser.html" : "DashboardAdmin.html";
            } else {
                throw new Error(data.message);
            }
        } catch (error) {
            console.error("Erreur de connexion :", error);
            document.getElementById("error-message").textContent = error.message;
        }
    });
}

function initDashboard() {
    const monCompteBtn = document.getElementById("mon-compte");
    const mesLivresBtn = document.getElementById("ajout-livres");
    const empruntBtn = document.getElementById("emprunt-livres");
    const logoutBtn = document.getElementById("logout");

    if (monCompteBtn) {
        monCompteBtn.addEventListener("click", () => {
            window.location.href = "moncompte.html";
        });
    }

    if (mesLivresBtn) { 
        mesLivresBtn.addEventListener("click", () => {
            window.location.href = "books.html";  
        });
    }

    if (empruntBtn) { 
        empruntBtn.addEventListener("click", () => {
            window.location.href = "emprunts.html";  
        });
    }

    if (logoutBtn) {
        logoutBtn.addEventListener("click", () => {
            localStorage.clear();
            window.location.href = "index.html";
        });
    }

    const userInfo = document.getElementById("user-info");
    if (userInfo) {
        const email = localStorage.getItem("email");
        const name = localStorage.getItem("name");
        userInfo.textContent = `Bienvenue ${name || ""} (${email || "Utilisateur"})`;
    }
}

function initMonComptePage() {
    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "index.html";
        return;
    }

    const form = document.getElementById("account-form");
    const message = document.getElementById("update-message");

    // Pré-remplir les champs
    document.getElementById("email").value = localStorage.getItem("email") || "";
    document.getElementById("name").value = localStorage.getItem("name") || "";

    const userId = localStorage.getItem("userId");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const name = document.getElementById("name").value;
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        const payload = { name, email };
        if (password.trim() !== "") {
            payload.password = password;
        }

        try {
            const response = await fetch(`http://localhost:5000/api/user/${userId}/update`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || "Erreur lors de la mise à jour.");
            }

            localStorage.setItem("email", email);
            localStorage.setItem("name", name);

            message.textContent = "Informations mises à jour avec succès !";
            message.style.color = "green";
        } catch (err) {
            console.error(err);
            message.textContent = err.message;
            message.style.color = "red";
        }
    });
}
