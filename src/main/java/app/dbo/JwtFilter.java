package app.dbo;

import app.entity.User.Role;
import io.jsonwebtoken.Claims;

public class JwtFilter {
    public static boolean isTokenValid(String token) {
        try {
            Claims claims = JwtUtil.decodeToken(token);
            return claims.getSubject() != null; // Vérifie si l’email est présent
        } catch (Exception e) {
            return false; // Token invalide ou expiré
        }
    }

    public static String getUserEmailFromToken(String token) {
        return JwtUtil.decodeToken(token).getSubject();
    }

    public static Role getUserRoleFromToken(String token) {
        return Role.valueOf(JwtUtil.decodeToken(token).get("role", String.class));
    }
}
