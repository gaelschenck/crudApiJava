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

    // POST /books → Ajoute un livre (seuls les utilisateurs USER peuvent poster)
    public static <Book> String addBook(User user, String title, String author) {
        if (user.getRole() != User.Role.USER) {
            return "Accès refusé : seuls les utilisateurs avec le rôle USER peuvent ajouter des livres.";
        }
        Book newBook = new Book(bookIdCounter++, title, author, user);
        books.add(newBook);
        return "Livre ajouté avec succès : " + newBook;
    }

    // PUT /books/{id} -> Modifier un livre (seul l'utilisateur qui l'a posté peut le modifier)
    public static String updateBook(User user, int bookId, String newTitle, String newAuthor) {
        for (Book book : books) {
            if (book.getId() == bookId) {
                if (!book.getOwner().equals(user)) {
                    return "Accès refusé : vous ne pouvez modifier que vos propres livres.";
                }
                book.setTitle(newTitle);
                book.setAuthor(newAuthor);
                return "Livre mis à jour : " + book;
            }
        }
        return "Livre non trouvé.";
    }

    // DELETE /books/{id} -> Supprime un livre (seul l'utilisateur qui l'a posté peut le supprimer)
    public static String deleteBook(User user, int bookId) {
        for (Book book : books) {
            if (book.getId() == bookId) {
                if (!book.getOwner().equals(user)) {
                    return "Accès refusé : vous ne pouvez supprimer que vos propres livres.";
                }
                books.remove(book);
                return "Livre supprimé avec succès.";
            }
        }
        return "Livre non trouvé.";
    }
}

