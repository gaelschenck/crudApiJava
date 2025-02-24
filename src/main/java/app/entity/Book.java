package app.entity;

public class Book {
    private int id;
    private String title;
    private String author;
    private String owner; // L'utilisateur qui a posté le livre

    public Book(int id, String title, String author, String owner) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.owner = owner;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", owner=" + owner +
                '}';
    }
}
