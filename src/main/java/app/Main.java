package app;

import app.controllers.AuthController;
import app.controllers.BookController;
import app.controllers.DashboardController;
import app.dbo.AuthMiddleware;
import io.javalin.Javalin;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(5000);

        app.post("/api/login", AuthController::loginUser);
        app.get("/api/dashboard", new AuthMiddleware(), DashboardController::getDashboard);

        app.get("/api/books", BookController::getAllBooks);
        app.get("/api/books/:id", BookController::getBookById);
        app.post("/api/books", new AuthMiddleware(), BookController::createBook);
        app.put("/api/books/:id", new AuthMiddleware(), BookController::updateBook);
        app.delete("/api/books/:id", new AuthMiddleware(), BookController::deleteBook);

    }
}
