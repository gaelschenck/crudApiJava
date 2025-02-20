package app.controllers;

import app.dbo.DatabaseConnection;
import io.javalin.http.Context;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;

public class UserController {
    public static void getAllUsers(Context ctx) {
        String userRole = ctx.attribute("userRole");

        if (!"ADMIN".equals(userRole)) {
            ctx.status(403).json("{\"message\": \"Accès refusé\"}");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, email, role FROM users";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            List<Map<String, Object>> users = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("id", rs.getInt("id"));
                user.put("email", rs.getString("email"));
                user.put("role", rs.getString("role"));
                users.add(user);
            }

            ctx.json(users);
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }
    public static void deleteUser(Context ctx) {
        String userRole = ctx.attribute("userRole");

        if (!"ADMIN".equals(userRole)) {
            ctx.status(403).json("{\"message\": \"Accès refusé\"}");
            return;
        }

        int userId = Integer.parseInt(ctx.pathParam("id"));

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.executeUpdate();

            ctx.json("{\"message\": \"Utilisateur supprimé\"}");
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }


}
