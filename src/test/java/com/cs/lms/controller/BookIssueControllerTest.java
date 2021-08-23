package com.cs.lms.controller;


import com.cs.lms.constant.LmsConstant;
import com.cs.lms.entity.Author;
import com.cs.lms.entity.Book;
import com.cs.lms.entity.BookIssue;
import com.cs.lms.entity.User;
import com.cs.lms.exception.BookAlreadyBorrowedException;
import com.cs.lms.exception.BookAlreadyReturnedException;
import com.cs.lms.service.LibraryService;
import com.cs.lms.service.UserService;
import com.cs.lms.uimodel.ResponseDataModel;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
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
public class BookIssueControllerTest {

    @Autowired
    BookIssueController bookIssueController;

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
    public void test_borrowBook(){
        var borrowBookResponse = bookIssueController.borrowBook(8888888888l, "ISBN-10000001", 5);
        var borrowBook = (BookIssue)borrowBookResponse.getBody().getData();
        Assertions.assertNull(borrowBook.getReturnedDate());
    }

    @Test
    @Order(2)
    public void test_borrowBook_Exception(){
        Assertions.assertThrows(BookAlreadyBorrowedException.class, () -> {
            libraryService.issueBookToUser(8888888888l, "ISBN-10000001", 5);
        });
    }

    @Test
    @Order(3)
    public void test_returnBook(){
        var borrowBookResponse = bookIssueController.returnBook(8888888888l, "ISBN-10000001");
        var borrowBook = (BookIssue)borrowBookResponse.getBody().getData();
        Assertions.assertNotNull(borrowBook.getReturnedDate());
    }

    @Test
    @Order(4)
    public void test_returnBook_Exception(){
        Assertions.assertThrows(BookAlreadyReturnedException.class, () -> {
            libraryService.returnBookToLibrary(8888888888l, "ISBN-10000001");
        });
    }
}
