package com.cs.lms.controller;


import com.cs.lms.constant.LmsConstant;
import com.cs.lms.entity.Author;
import com.cs.lms.entity.Book;
import com.cs.lms.entity.BookIssue;
import com.cs.lms.entity.User;
import com.cs.lms.exception.BookNotFoundException;
import com.cs.lms.service.LibraryService;
import com.cs.lms.service.UserService;
import com.cs.lms.uimodel.BookUiModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class BookControllerTest {

    @Autowired
    BookController bookController;

    @Autowired
    LibraryService libraryService;
    @Autowired
    UserService userService;

    @BeforeAll
    public void setupDataBaseWithTestData(){
        //create users
        User users[] = {
                new User("lib-admin", "9999999999", "admin@lms.com", LmsConstant.ROLE_LIBRARIAN, true),
                new User("ajay", "8888888888", "ajay@lms.com",LmsConstant.ROLE_PATRON, true),
                new User("james", "7777777777", "james@lms.com",LmsConstant.ROLE_PATRON, true),
        };
        Arrays.asList(users).stream().forEach(user -> {
            userService.createUser(user);
        });

        // create book and author
        var books = new ArrayList<Book>();
        var book = new Book("ISBN-10000001", "Java Stream Programing", 70, 4,"A comprehensive book to master java stream features.");
        book.addAuthors(new Author("Ajay", "Java programmer"));
        books.add(book);

        book = new Book("ISBN-10000002", "Java Spring Boot", 220, 3,"A comprehensive guide for Spring boot programmers");
        book.addAuthors(new Author("James", "java programmer"));
        book.addAuthors(new Author("Martin", "java developer"));
        books.add(book);

        books.forEach((b)->{
            libraryService.createBook(b);
        });

        libraryService.issueBook(new BookIssue(books.get(0), users[0], 4));
    }

    @Test
    @Order(1)
    public void test_fetchBookCount(){
        Object data = bookController.getAllBooks().getBody().getData();
        Assertions.assertTrue(data instanceof List);
        List dataList = (List)data;
        Assertions.assertTrue(dataList.size() == 2);
    }

    @Test
    @Order(2)
    public void test_createNewBook(){
        var book = new Book("ISBN-10000003", "Java Test Book", 100, 5,"test book");
        book.addAuthors(new Author("Test Author", "Java programmer"));
        var createdBook = bookController.addBook(book);
        Assertions.assertEquals(createdBook.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertNotNull(createdBook.getBody().getData());
    }

    @Test
    @Order(3)
    public void test_createNewBook_Exception(){
        var book = new Book("ISBN-10000003", "Java Test Book", 100, 5,"test book");
        book.addAuthors(new Author("Test Author", "Java programmer"));
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            bookController.addBook(book);
        });
    }

    @Test
    @Order(4)
    public void test_updateBook() throws JsonProcessingException {
        BookUiModel.Isbn isbn = new BookUiModel.Isbn();
        isbn.setIsbn("ISBN-10000001");
        isbn.setExact("true");
        var bookByIsbn = bookController.getBookByIsbn(isbn);
        var book = (Book)((List)bookByIsbn.getBody().getData()).get(0);
        Assertions.assertTrue(book.getQuantity() == 4);
        book.setQuantity(7);
        var updateResponse = bookController.updateBook("ISBN-10000001", book);
        var updatedBook = (Book)updateResponse.getBody().getData();
        Assertions.assertEquals(updatedBook.getQuantity(), 7, "quantity should be 7 after updating the book");
    }

    @Test
    @Order(5)
    public void test_deleteBook() throws JsonProcessingException {
        Assertions.assertTrue(bookController.deleteBook("ISBN-10000001").getBody());
    }

    @Test
    @Order(6)
    public void test_deleteBook_Exception(){
        Assertions.assertThrows(BookNotFoundException.class, () -> {
            bookController.deleteBook("ISBN-10000001");
        });
    }
}
