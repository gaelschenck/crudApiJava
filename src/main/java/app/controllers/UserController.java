package app.controllers;

import app.dbo.DatabaseConnection;
import app.entity.User;
import io.javalin.http.Context;
import java.sql.*;
import java.util.*;

import java.sql.Connection;

public class UserController {

    // 📌 Créer un utilisateur avec ID et rôle
    public static void createUser(Context ctx) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Connexion établie avec la base de données.");
            User newUser = ctx.bodyAsClass(User.class);
            System.out.println("Données reçues : " + newUser);

            // Récupérer le plus grand ID existant dans la table
            String idQuery = "SELECT MAX(id) AS maxId FROM users";
            PreparedStatement idStmt = conn.prepareStatement(idQuery);
            ResultSet idResult = idStmt.executeQuery();

            int newId = 1; // Valeur par défaut si la table est vide
            if (idResult.next()) {
                newId = idResult.getInt("maxId") + 1; // ID le plus élevé +1
            }

            // Log SQL
            String sql = "INSERT INTO users (id, name, email, password, role) VALUES (?, ?, ?, ?, ?)";
            System.out.println("Requête SQL : " + sql);

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, newId); // ID fourni par le client
            stmt.setString(2, newUser.getName());
            stmt.setString(3, newUser.getEmail());
            stmt.setString(4, newUser.getPassword()); // ⚠️ Hacher le mot de passe dans une vraie application !
            stmt.setString(5, "USER"); // Convertir l'enum en chaîne de caractères

            // Exécuter la requête
            int rowsInserted = stmt.executeUpdate();
            System.out.println("Lignes insérées : " + rowsInserted);

            if (rowsInserted > 0) {
                ctx.status(201).json("{\"message\": \"Utilisateur créé avec succès\"}");
            } else {
                ctx.status(400).json("{\"message\": \"Erreur lors de la création de l'utilisateur\"}");
            }
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }


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
    // 📌 Voir le profil utilisateur
    public static void getUserProfile(Context ctx) {
        String userRole = ctx.attribute("userRole");
        int userId = Integer.parseInt(ctx.attribute("userId")); // Supposons que l'ID de l'utilisateur soit inclus dans les attributs.

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, email, role FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> userProfile = new HashMap<>();
                userProfile.put("id", rs.getInt("id"));
                userProfile.put("email", rs.getString("email"));
                userProfile.put("role", rs.getString("role"));
                ctx.json(userProfile);
            } else {
                ctx.status(404).json("{\"message\": \"Utilisateur introuvable\"}");
            }
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }
    // 📌 Modifier le profil utilisateur
    public static void updateUserProfile(Context ctx) {
        int userId = Integer.parseInt(Objects.requireNonNull(ctx.attribute("userId"))); // Supposons que l'utilisateur puisse modifier uniquement son propre profil.

        try (Connection conn = DatabaseConnection.getConnection()) {
            Map<String, Object> updatedFields = ctx.bodyAsClass(Map.class); // Récupération des données envoyées dans le corps de la requête.

            // Construire la requête SQL dynamiquement
            StringBuilder sqlBuilder = new StringBuilder("UPDATE users SET ");
            List<String> columns = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            updatedFields.forEach((column, value) -> {
                columns.add(column + " = ?");
                values.add(value);
            });

            if (columns.isEmpty()) {
                ctx.status(400).json("{\"message\": \"Aucun champ à mettre à jour\"}");
                return;
            }

            sqlBuilder.append(String.join(", ", columns));
            sqlBuilder.append(" WHERE id = ?");
            values.add(userId);

            PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString());
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                ctx.json("{\"message\": \"Profil utilisateur mis à jour\"}");
            } else {
                ctx.status(404).json("{\"message\": \"Utilisateur introuvable\"}");
            }
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
