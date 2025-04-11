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

                books.add(new Book(id, title, author));
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
                int bookId = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");

                Book book = new Book(bookId, title, author);
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
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Connexion établie avec la base de données.");
            Book newBook = ctx.bodyAsClass(Book.class);
            System.out.println("Données reçues : " + newBook);
            String title = newBook.getTitle();
            String author = newBook.getAuthor();
            String sql = "INSERT INTO books (title, author) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.executeUpdate();

            ctx.status(201).json("{\"message\": \"Livre ajouté\"}");
        } catch (SQLException e) {
            ctx.status(500).json("{\"message\": \"Erreur serveur\"}");
        }
    }

    // Modifier un livre (seulement si l’utilisateur est le propriétaire)
    public static void updateBook(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        String title = ctx.formParam("title");
        String author = ctx.formParam("author");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE books SET title = ?, author = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setInt(3, id);
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

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM books WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
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


