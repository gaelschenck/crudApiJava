package app.dbo;

import app.entity.User;


public class AuthService {
    private UserDAO userDAO = new UserDAO();

    public String register(String name, String email, String password, User.Role role) {
        if (userDAO.getUserByEmail(email) != null) {
            return "Email déjà utilisé !";
        }
        User newUser = new User(0, name, email, password, role);
        userDAO.registerUser(newUser);
        return "Inscription réussie !";
    }

    public String login(String email, String password) {
        User user = userDAO.authenticateUser(email, password);
        if (user == null) {
            return "Identifiants incorrects !";
        }
        return JwtUtil.generateToken(user); // Retourne un jeton JWT
    }
}

