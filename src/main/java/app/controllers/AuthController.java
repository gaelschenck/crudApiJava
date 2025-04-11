package app.controllers;

import app.dbo.DatabaseConnection;
import app.dbo.JwtUtil;
import app.entity.User;
import io.javalin.http.Context;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthController {
    public static void loginUser(Context ctx) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try (Connection conn = DatabaseConnection.getConnection()) {
            //Récupérer l'utilisateur par email et mot de passe
            System.out.println("Email reçu : " + email + ", Mot de passe reçu : " + password);
            User user = getUserByEmailAndPassword(conn, email, password);

            if (user != null) {
                //Générer le token via JwtUtil
                String token = JwtUtil.generateToken(user);

                ctx.json("{\"token\": \"" + token + "\", \"role\": \"" + user.getRole() + "\"}");
            } else {
                ctx.status(401).json("{\"message\": \"Identifiants incorrects\"}");
            }
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }

    private static User getUserByEmailAndPassword(Connection conn, String email, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, email);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            System.out.println("Utilisateur trouvé : " + rs.getString("email"));

            return new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role")
            );
        }else {
            System.out.println("Aucun utilisateur trouvé avec ces identifiants.");
        }

        return null;
    }
}



