document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("login-form");
    const errorMessage = document.getElementById("error-message");
    const userInfo = document.getElementById("user-info");
    const logoutBtn = document.getElementById("logout");

    // Gestion du formulaire de connexion
    if (form) {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            const email = document.getElementById("email").value;
            const password = document.getElementById("password").value;

            try {
                const response = await fetch("http://localhost:5000/api/login", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ email, password })
                });

                const data = await response.json();
                if (!response.ok) throw new Error(data.message);

                localStorage.setItem("token", data.token);
                window.location.href = "dashboard.html";
            } catch (error) {
                errorMessage.textContent = error.message;
            }
        });
    }

    // Affichage des infos sur le dashboard
   if (userInfo) {
           const token = localStorage.getItem("token");
           if (!token) {
               window.location.href = "index.html";
           } else {
               try {
                   const response = await fetch("http://localhost:5000/api/dashboard", {
                       method: "GET",
                       headers: { "Authorization": "Bearer " + token }
                   });
                   const data = await response.json();
                   userInfo.textContent = data.message;
               } catch (error) {
                   window.location.href = "index.html";
               }
           }
       }

       if (logoutBtn) {
           logoutBtn.addEventListener("click", () => {
               localStorage.removeItem("token");
               window.location.href = "index.html";
           });
       }
   });

   document.addEventListener("DOMContentLoaded", async () => {
       const userRole = localStorage.getItem("role");

       // Vérifier le rôle et afficher les boutons en fonction
       function updateUI() {
           document.querySelectorAll(".admin-only").forEach(btn => {
               btn.style.display = userRole === "ADMIN" ? "inline-block" : "none";
           });
       }

       updateUI();
   });

