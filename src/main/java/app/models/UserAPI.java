package app.models;


import app.entity.User;

public class UserAPI {

    // Simule l'action GET /me (récupérer les infos de l'utilisateur)
    public static String getMe(User user) {
        if (user.getRole() == User.Role.USER) {
            return "Profil de l'utilisateur : " + user.toString();
        } else {
            return "Accès refusé : seuls les utilisateurs avec le rôle USER peuvent accéder à cette route.";
        }
    }

    // Simule l'action PUT /me (mettre à jour les infos)
    public static String updateMe(User user, String newName, String newEmail) {
        if (user.getRole() == User.Role.USER) {
            user.setName(newName);
            user.setEmail(newEmail);
            return "Mise à jour réussie ! Nouveau profil : " + user.toString();
        } else {
            return "Accès refusé : seuls les utilisateurs avec le rôle USER peuvent modifier leur profil.";
        }
    }
}

