package app;

import app.controllers.AuthController;
import app.controllers.BookController;
import app.controllers.DashboardController;
import app.controllers.AdminController;
import app.dbo.AuthMiddleware;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("./src/main/webapp", Location.EXTERNAL);
        }).start(5000);

        app.before("/api/", new AuthMiddleware());

        app.post("/api/login", AuthController::loginUser);
        app.get("/api/dashboard", DashboardController::getDashboard);
        app.get("/api/books", BookController::getAllBooks);
        app.get("/api/books/{id}", BookController::getBookById);
        app.post("/api/books", BookController::createBook);
        app.put("/api/books/{id}", BookController::updateBook);
        app.delete("/api/books/{id}", BookController::deleteBook);

        // Gestion des livres par l'admin
        app.post("/api/admin/books", BookController::createBook);
        app.get("/api/admin/books", BookController::getAllBooks);
        app.get("/api/admin/books/{id}", BookController::getBookById);
        app.put("/api/admin/books/{id}", BookController::updateBook);
        app.delete("/api/admin/books/{id}", BookController::deleteBook);

// Gestion des utilisateurs par l'admin
        app.post("/api/admin/users", AdminController::createUser);
        app.get("/api/admin/users", AdminController::getAllUsers);
        app.get("/api/admin/users/{id}", AdminController::getUserById);
        app.put("/api/admin/users/{id}", AdminController::updateUser);
        app.delete("/api/admin/users/{id}", AdminController::deleteUser);

    }
}
