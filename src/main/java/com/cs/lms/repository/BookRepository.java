package com.cs.lms.repository;

import com.cs.lms.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE b.title LIKE %?1%  OR b.isbn LIKE %?1% OR b.description LIKE %?1%")
    public List<Book> search(String keyword);

    @Query("SELECT b FROM Book b WHERE b.title = ?1 OR b.isbn = ?1")
    public List<Book> searchExact(String keyword);

    @Query("SELECT b FROM Book b WHERE b.title LIKE %?1%")
    public List<Book> searchByTitle(String title);

    @Query("SELECT b FROM Book b WHERE b.title = ?1")
    public List<Book> searchByTitleExact(String title);

    @Query("SELECT b FROM Book b WHERE b.isbn LIKE %?1%")
    public List<Book> searchByIsbn(String keyword);

    @Query("SELECT b FROM Book b WHERE b.isbn = ?1")
    public Book searchByIsbnExact(String keyword);

    @Query("SELECT sum(b.quantity) FROM Book b")
    public Integer totalBooksAvailable();

    @Query("SELECT b.quantity FROM Book b where b.title = ?1")
    public Integer totalBooksForTitle(String title);

    @Query("SELECT b.quantity FROM Book b where b.isbn = ?1")
    public Integer totalBooksForIsbn(String isbn);

    @Query("SELECT b.quantity FROM Book b where b.title = ?1 and b.isbn = ?2")
    public Integer totalBooksForTitleAndIsbn(String title, String isbn);
}
