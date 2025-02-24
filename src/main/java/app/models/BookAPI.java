package app.models;

import app.entity.User;
import app.entity.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class BookAPI {
    private static List<Book> books = new ArrayList<>();
    private static int bookIdCounter = 1; // Pour auto-incrémenter les IDs

    // GET /books → Retourne tous les livres
    public static List<Book> getAllBooks() {
        return books;
    }

    // GET /books/{id} → Retourne un livre par son ID
    public static String getBookById(int id) {
        Optional<Book> book = books.stream().filter(b -> b.getId() == id).findFirst();
        return book.map(Book::toString).orElse("Livre non trouvé.");
    }
}

