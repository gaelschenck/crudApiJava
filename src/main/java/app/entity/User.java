package app.entity;

public class User {
    public enum Role {
        ADMIN, USER;
    }
    private int id;
    private String name;
    private String email;
    private String password;
    private Role role; // Ajout du rôle



    // Constructeur avec paramètres
    public User(int id, String name, String email, String password, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    // Constructeur sans mot de passe (utile pour les réponses API)
    public User(int id, String name, String email, String role) {
        this(id, name, email, null, Role.valueOf(role)); // On met `null` par défaut
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // Méthode toString
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}

