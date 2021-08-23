package com.cs.lms;

import com.cs.lms.constant.LmsConstant;
import com.cs.lms.entity.Author;
import com.cs.lms.entity.Book;
import com.cs.lms.entity.BookIssue;
import com.cs.lms.entity.User;
import com.cs.lms.service.LibraryService;
import com.cs.lms.service.UserService;
import com.cs.lms.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 */

@Component
@Profile(value = {"dev", "default", "local"})
public class LmsStartupListener implements ApplicationRunner {

    @Autowired
    private LibraryService libraryService;
    @Autowired
    private UserService userService;
    /**
     * This is a callback hook to create dummy data for
     * development purpose
     * Called by spring framework once application bootstrap,
     * Create dummy users and books after application startup.
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
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
        var book = new Book("ISBN-1000001", "Java Stream Programing", 70, 4,"A comprehensive book to master java stream features.");
        book.addAuthors(new Author("Ajay", "Java programmer"));
        books.add(book);

        book = new Book("ISBN-1000002", "Java Spring Boot", 220, 3,"A comprehensive guide for Spring boot programmers");
        book.addAuthors(new Author("James", "java programmer"));
        book.addAuthors(new Author("Martin", "java developer"));
        books.add(book);

        books.forEach((b)->{
            libraryService.createBook(b);
        });

        libraryService.issueBook(new BookIssue(books.get(0), users[0], 4));
    }
}
