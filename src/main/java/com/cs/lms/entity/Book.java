package com.cs.lms.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

@Entity
@Table(name = "lms_books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotEmpty(message = "Book ISBN can not be empty")
    @Column(name = "isbn", length = 50, nullable = false, unique = true)
    private String isbn;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "cost", nullable = false)
    private double cost;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "description", length = 250, nullable = true)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @JoinTable(name = "lms_books_authors", joinColumns = { @JoinColumn(name = "book_id") }, inverseJoinColumns = {
            @JoinColumn(name = "author_id") })
    private Set<Author> authors = new HashSet<>();

    public Book(){}
    public Book(String isbn, String title, double cost, int quantity, String description) {
        this.isbn = isbn;
        this.title = title;
        this.cost = cost;
        this.quantity = quantity;
        this.description = description;
    }

    public void addAuthors(Author author) {
        this.authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthors(Author author) {
        this.authors.remove(author);
        author.getBooks().remove(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return isbn.equals(book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Book.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("isbn='" + isbn + "'")
                .add("title='" + title + "'")
                .add("cost=" + cost)
                .add("quantity=" + quantity)
                .add("description='" + description + "'")
                .toString();
    }
}
