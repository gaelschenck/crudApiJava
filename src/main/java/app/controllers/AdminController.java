package app.controllers;

import app.dbo.DatabaseConnection;
import app.entity.User;
import app.entity.Book;
import io.javalin.http.Context;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminController {

    // Récupérer tous les utilisateurs
    public static void getAllUsers(Context ctx) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, name, email, role FROM users";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role") // On ne retourne pas le mot de passe
                ));
            }
            ctx.json(users);
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }

    // Récupérer un utilisateur par ID
    public static void getUserById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, name, email, role FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("role"));
                ctx.json(user);
            } else {
                ctx.status(404).json("{\"message\": \"Utilisateur introuvable\"}");
            }
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }

    // 📌 Créer un nouvel utilisateur
    public static void createUser(Context ctx) {
        User user = ctx.bodyAsClass(User.class);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword()); // ⚠️ Devrait être hashé en prod
            stmt.setString(4, user.getRole().toString());

            stmt.executeUpdate();
            ctx.status(201).json("{\"message\": \"Utilisateur créé\"}");
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }

    // 📌 Mettre à jour un utilisateur
    public static void updateUser(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        User user = ctx.bodyAsClass(User.class);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE users SET name = ?, email = ?, role = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole().toString());
            stmt.setInt(4, id);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                ctx.json("{\"message\": \"Utilisateur mis à jour\"}");
            } else {
                ctx.status(404).json("{\"message\": \"Utilisateur introuvable\"}");
            }
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }

    // 📌 Supprimer un utilisateur
    public static void deleteUser(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                ctx.json("{\"message\": \"Utilisateur supprimé\"}");
            } else {
                ctx.status(404).json("{\"message\": \"Utilisateur introuvable\"}");
            }
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }

    // Récupérer tous les livres
    public static void getAllBooks(Context ctx) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT books.id, books.title, books.author, users.name AS owner_name " +
                    "FROM books " +
                    "JOIN users ON books.owner = users.id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            List<Book> books = new ArrayList<>();
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("owner") // Maintenant on récupère le `name` du propriétaire
                ));
            }
            ctx.json(books);
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }

    // Récupérer un livre par ID
    public static void getBookById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM books WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Book book = new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getString("owner_name"));
                ctx.json(book);
            } else {
                ctx.status(404).json("{\"message\": \"Livre introuvable\"}");
            }
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }

    // 📌 Mettre à jour un livre
    public static void updateBook(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Lire les données envoyées dans le corps de la requête
            Book updatedBook = ctx.bodyAsClass(Book.class);

            // Préparer la requête SQL de mise à jour
            String sql = "UPDATE books SET title = ?, author = ?, owner_name = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, updatedBook.getTitle());
            stmt.setString(2, updatedBook.getAuthor());
            stmt.setString(3, updatedBook.getOwner());
            stmt.setInt(4, id);

            // Exécuter la requête et vérifier si une ligne a été mise à jour
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                ctx.json("{\"message\": \"Livre mis à jour\"}");
            } else {
                ctx.status(404).json("{\"message\": \"Livre introuvable\"}");
            }
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }

    // 📌 Supprimer un livre
    public static void deleteBook(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM books WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                ctx.json("{\"message\": \"Livre supprimé\"}");
            } else {
                ctx.status(404).json("{\"message\": \"Livre introuvable\"}");
            }
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }
}
