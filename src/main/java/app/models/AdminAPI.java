package app.models;

import app.entity.User;
import app.entity.Book;
import app.entity.User.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminAPI {
    // Gestion des livres
    private static List<Book> books = new ArrayList<>();
    private static int bookIdCounter = 1; // Auto-incrémentation des IDs

    // GET /admin/books → Retourne tous les livres
    public static List<Book> getAllBooksAdmin(User admin) {
        if (admin.getRole() != Role.ADMIN) return null;
        return books;
    }

    // GET /admin/books/{id} -> Retourne un livre par son ID
    public static String getBookByIdAdmin(User admin, int id) {
        if (admin.getRole() != Role.ADMIN) return "Accès refusé.";
        Optional<Book> book = books.stream().filter(b -> b.getId() == id).findFirst();
        return book.map(Book::toString).orElse("Livre non trouvé.");
    }

    // POST /admin/books → Ajoute un livre au nom de quelqu’un
    public static String addBookAdmin(User admin, String title, String author) {
        if (admin.getRole() != Role.ADMIN) return "Accès refusé.";
        Book newBook = new Book(bookIdCounter++, title, author);
        books.add(newBook);
        return "Livre ajouté par admin : " + newBook;
    }

    // PUT /admin/books/{id} -> Modifier un livre (admin peut tout modifier)
    public static String updateBookAdmin(User admin, int bookId, String newTitle, String newAuthor) {
        if (admin.getRole() != Role.ADMIN) return "Accès refusé.";
        for (Book book : books) {
            if (book.getId() == bookId) {
                book.setTitle(newTitle);
                book.setAuthor(newAuthor);
                return "Livre modifié par admin : " + book;
            }
        }
        return "Livre non trouvé.";
    }

    // DELETE /admin/books/{id} -> Supprimer un livre
    public static String deleteBookAdmin(User admin, int bookId) {
        if (admin.getRole() != Role.ADMIN) return "Accès refusé.";
        for (Book book : books) {
            if (book.getId() == bookId) {
                books.remove(book);
                return "Livre supprimé par admin.";
            }
        }
        return "Livre non trouvé.";
    }
// Gestion des utilisateurs
    private static List<User> users = new ArrayList<>();
    private static int userIdCounter = 1; // Auto-incrémentation des IDs

    // GET /admin/users → Retourne tous les utilisateurs
    public static List<User> getAllUsersAdmin(User admin) {
        if (admin.getRole() != Role.ADMIN) return null;
        return users;
    }

    // GET /admin/users/{id} → Retourne un utilisateur par son ID
    public static String getUserByIdAdmin(User admin, int id) {
        if (admin.getRole() != Role.ADMIN) return "Accès refusé.";
        Optional<User> user = users.stream().filter(u -> u.getId() == id).findFirst();
        return user.map(User::toString).orElse("Utilisateur non trouvé.");
    }

    // POST /admin/users → Ajoute un utilisateur
    public static String addUserAdmin(User admin, String name, String email, String password, Role role) {
        if (admin.getRole() != Role.ADMIN) return "Accès refusé.";
        User newUser = new User(userIdCounter++, name, email, password, role);
        users.add(newUser);
        return "Utilisateur ajouté par admin : " + newUser;
    }

    // PUT /admin/users/{id} → Modifier un utilisateur
    public static String updateUserAdmin(User admin, int userId, String newName, String newEmail, Role newRole) {
        if (admin.getRole() != Role.ADMIN) return "Accès refusé.";
        for (User user : users) {
            if (user.getId() == userId) {
                user.setName(newName);
                user.setEmail(newEmail);
                user.setRole(newRole);
                return "Utilisateur mis à jour par admin : " + user;
            }
        }
        return "Utilisateur non trouvé.";
    }

    // DELETE /admin/users/{id} → Supprimer un utilisateur
    public static String deleteUserAdmin(User admin, int userId) {
        if (admin.getRole() != Role.ADMIN) return "Accès refusé.";
        for (User user : users) {
            if (user.getId() == userId) {
                users.remove(user);
                return "Utilisateur supprimé par admin.";
            }
        }
        return "Utilisateur non trouvé.";
    }
}


