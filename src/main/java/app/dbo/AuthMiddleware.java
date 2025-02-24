package app.dbo;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

public class AuthMiddleware implements Handler {
    private static final String SECRET_KEY = "mon_secret";

    @Override
    public void handle(Context ctx) throws Exception {
        try {
            String token = ctx.header("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                ctx.status(401).json("{\"message\": \"Token manquant\"}");
                return;
            }

            token = token.substring(7); // Supprime "Bearer "
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            ctx.attribute("userEmail", decodedJWT.getSubject()); // Stocke l'email de l'utilisateur dans la requête
            ctx.req(); // Continue vers la route demandée
        } catch (Exception e) {
            ctx.status(401).json("{\"message\": \"Token invalide\"}");
        }
    }
}

