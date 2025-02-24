package app.controllers;

import app.dbo.DatabaseConnection;
import app.controllers.UserController;
import app.entity.Book;
import app.entity.User;
import io.javalin.http.Context;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookController {

    // Récupérer tous les livres
    public static void getAllBooks(Context ctx) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM books";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            List<Book> books = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String userName = rs.getString("user_name"); // Récupération du propriétaire sous forme de nom

                books.add(new Book(id, title, author, userName));
            }
            ctx.json(books);
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }

    // Fonction pour récupérer un utilisateur par son email
    private static User getUserByName(Connection conn, String name) throws SQLException {
        String sql = "SELECT * FROM users WHERE name = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("role"));
        }
        return null; // Si l'utilisateur n'existe pas
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
                int bookId = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String userName = rs.getString("user_name"); // Récupération du propriétaire

                Book book = new Book(bookId, title, author, userName);
                ctx.json(book);
            } else {
                ctx.status(404).json("{\"message\": \"Livre introuvable\"}");
            }
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }

    // Ajouter un livre (seulement pour utilisateurs connectés)
    public static void createBook(Context ctx) {
        String userName = ctx.attribute("userName");
        if (userName == null) {
            ctx.status(401).json("{\"message\": \"Non autorisé\"}");
            return;
        }

        String title = ctx.formParam("title");
        String author = ctx.formParam("author");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO books (title, author, user_name) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, userName);
            stmt.executeUpdate();

            ctx.status(201).json("{\"message\": \"Livre ajouté\"}");
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }

    // Modifier un livre (seulement si l’utilisateur est le propriétaire)
    public static void updateBook(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        String userName = ctx.attribute("userName");
        String title = ctx.formParam("title");
        String author = ctx.formParam("author");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE books SET title = ?, author = ? WHERE id = ? AND user_name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setInt(3, id);
            stmt.setString(4, userName);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                ctx.json("{\"message\": \"Livre mis à jour\"}");
            } else {
                ctx.status(403).json("{\"message\": \"Non autorisé ou livre introuvable\"}");
            }
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }

    // Supprimer un livre (seulement si l’utilisateur est le propriétaire)
    public static void deleteBook(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        String userName = ctx.attribute("userName");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM books WHERE id = ? AND user_name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setString(2, userName);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                ctx.json("{\"message\": \"Livre supprimé\"}");
            } else {
                ctx.status(403).json("{\"message\": \"Non autorisé ou livre introuvable\"}");
            }
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }
}


