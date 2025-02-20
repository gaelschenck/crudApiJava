package app.controllers;

import io.javalin.http.Context;

public class DashboardController {
    public static void getDashboard(Context ctx) {
        String userEmail = ctx.attribute("userEmail");
        ctx.json("{\"message\": \"Bienvenue, " + userEmail + "!\"}");
    }
}
