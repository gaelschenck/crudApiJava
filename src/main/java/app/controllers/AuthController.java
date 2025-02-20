package app.controllers;

import app.dbo.DatabaseConnection;
import io.javalin.http.Context;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class AuthController {
    public static void loginUser(Context ctx) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT role FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                // Générer un JWT avec le rôle
                String token = Jwts.builder()
                        .setSubject(email)
                        .claim("role", role)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1h
                        .signWith(SignatureAlgorithm.HS256, "secret")
                        .compact();

                ctx.json("{\"token\": \"" + token + "\", \"role\": \"" + role + "\"}");
            } else {
                ctx.status(401).json("{\"message\": \"Identifiants incorrects\"}");
            }
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }
}

