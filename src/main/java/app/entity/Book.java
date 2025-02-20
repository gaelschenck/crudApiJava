package app.entity;

import app.entity.User;

public class Book {
    private int id;
    private String title;
    private String author;
    private User owner; // L'utilisateur qui a posté le livre

    public Book(int id, String title, String author, User owner) {
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", owner=" + owner.getName() +
                '}';
    }
}
