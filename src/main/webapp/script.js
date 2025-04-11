document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("token");
    const currentPage = window.location.pathname;

    console.log("Page actuelle :", currentPage);
    console.log("Token présent :", token);

    // Gestion de la redirection
    if (!token && currentPage !== "/index.html") {
        window.location.href = "index.html";
        return;
    }

    // Page de connexion
    if (currentPage === "/index.html") {
        console.log("Page de connexion détectée !");
        const form = document.getElementById("login-form");
        if (form) {
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
                        localStorage.setItem("token", data.token);
                        localStorage.setItem("role", data.role);
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
    }

    // Gestion du tableau de bord utilisateur
    if (currentPage === "/DashboardUser.html" || currentPage === "/DashboardAdmin.html") {
        if (!token) {
            console.error("Accès interdit sans connexion !");
            window.location.href = "index.html";
            return;
        }
        console.log("Page du tableau de bord détectée !");
        // Vous pouvez ajouter ici les fonctionnalités spécifiques au tableau de bord
    }
});