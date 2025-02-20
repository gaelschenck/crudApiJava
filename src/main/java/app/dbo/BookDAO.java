package app.dbo;

import app.entity.Book;
import app.entity.User;
import app.entity.User.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public void addBook(Book book) {
        String sql = "INSERT INTO books (title, author, owner_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getOwner().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT books.*, users.id as owner_id, users.name as owner_name FROM books " +
                "JOIN users ON books.owner_id = users.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User owner = new User(rs.getInt("owner_id"), rs.getString("owner_name"), "", "", Role.USER);
                books.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), owner));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}

