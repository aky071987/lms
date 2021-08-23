package com.cs.lms.service;

import com.cs.lms.entity.BookIssue;
import com.cs.lms.entity.User;
import com.cs.lms.exception.*;
import com.cs.lms.entity.Book;
import com.cs.lms.repository.BookIssueRepository;
import com.cs.lms.repository.BookRepository;
import com.cs.lms.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;


@Service
public class LibraryService {
    private static final Logger LOG = LoggerFactory.getLogger(LibraryService.class);
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private BookIssueRepository bookIssueRepository;

    public LibraryService(BookRepository bookRepository, UserRepository userRepository, BookIssueRepository bookIssueRepository){
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookIssueRepository = bookIssueRepository;
    }

    public long getAllAvailableBooks(){
        return bookRepository.count();
    }

    public long getAvailableBooks(Book book){
        return bookRepository.count();
    }

    //@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Book> searchBooks(String keyword, boolean exact) {
        if (StringUtils.hasLength(keyword)) {
            if(exact)
                return bookRepository.searchExact(keyword);
            return bookRepository.search(keyword);
        }
        return Collections.emptyList();
    }

    public Integer totalBooksAvailable(){
        return bookRepository.totalBooksAvailable();
    }

    public Integer totalBooksForIsbn(String isbn){
        return bookRepository.totalBooksForIsbn(isbn);
    }

    public Integer totalBooksForTitle(String title){
        return bookRepository.totalBooksForTitle(title);
    }

    public List<Book> searchByTitle(String title, boolean exact) {
        if (StringUtils.hasLength(title)) {
            if(exact)
                return bookRepository.searchByTitleExact(title);
            return bookRepository.searchByTitle(title);
        }
        return Collections.emptyList();
    }

    public List<Book> searchByIsbn(String isbn, boolean exact) {
        if (StringUtils.hasLength(isbn)) {
            if(exact) {
                var book = bookRepository.searchByIsbnExact(isbn);
                if(Objects.nonNull(book))
                    return Arrays.asList(book);
                else
                    return Collections.emptyList();
            }
            return bookRepository.searchByIsbn(isbn);
        }
        return null;
    }

    @Transactional
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Book book) {
        var bk = bookRepository.findById(book.getId());
        if(bk.isPresent()){
           return bookRepository.save(book);
        }else{
            LOG.error("Book not found with ID -> {}", book.getId());
            throw new BookNotFoundException(String.format("Book not found with ID %s", book.getId()));
        }
    }

    @Transactional
    public boolean deleteBook(String isbn) {
        final Book book = bookRepository.searchByIsbnExact(isbn);
        if(book != null){
            bookRepository.deleteById(book.getId());
            return true;
        }else{
            LOG.error("Book not found with isbn -> {}", isbn);
            throw new BookNotFoundException(String.format("Book not found with isbn %s", isbn));
        }

    }

    @Transactional
    public void issueBook(BookIssue bookIssue) {
        bookIssueRepository.save(bookIssue);
    }

    @Transactional
    public void returnBook(BookIssue bookIssue) {
        bookIssue.setReturnedDate(new Date());
        bookIssueRepository.save(bookIssue);
    }

    public Integer totalBooksForTitleAndIsbn(String title, String isbn) {
        return bookRepository.totalBooksForTitleAndIsbn(title,isbn);
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public Integer numberOfBorrowedBooks(){
        return bookIssueRepository.findNumberOfBorrowedBooks();
    }

    public Integer numberOfBorrowedBooksByUser(Long phoneNumber){
        var user = userRepository.searchUserByPhoneNumber(String.valueOf(phoneNumber));
        if(user == null){
            LOG.error("User not found with phone -> {}", phoneNumber);
            throw new UserNotFoundException(String.format("User not found with phone %d", phoneNumber));
        }
        return bookIssueRepository.findNumberOfBorrowedBooksByUser(user.getId());
    }

    @Transactional
    public BookIssue issueBookToUser(Long phoneNumber, String isbn, int period) throws BookAlreadyBorrowedException, NoBookAvailableForBorrowException{
        var user = userRepository.searchUserByPhoneNumber(String.valueOf(phoneNumber));
        if(user == null){
            LOG.error("User not found with phone -> {}", phoneNumber);
            throw new UserNotFoundException(String.format("User not found with phone %d", phoneNumber));
        }
        var book = bookRepository.searchByIsbnExact(isbn);
        if(book == null){
            LOG.error("Book not found with isbn -> {}", isbn);
            throw new BookNotFoundException(String.format("Book not found with isbn %s", isbn));
        }
        if(book.getQuantity() < 1 ){
            throw new NoBookAvailableForBorrowException("book with isbn " + book.getIsbn() + " is currently not available to borrow.");
        }
        var userBooks = bookIssueRepository.findBorrowedBookForUser(user.getPhoneNumber());
        var userBookIssue = userBooks.stream().filter(bi -> bi.getBook().getId() == book.getId() && bi.getReturnedDate() == null).findAny();
        if(userBookIssue.isPresent()){
            throw new BookAlreadyBorrowedException("book with isbn " + book.getIsbn() + " is already issued to user " + user.getName());
        }else{
            BookIssue bookIssue = new BookIssue(book, user, period);
            book.setQuantity(book.getQuantity()-1);
            bookRepository.save(book);
            return bookIssueRepository.save(bookIssue);
        }
    }

    @Transactional
    public BookIssue returnBookToLibrary(Long phoneNumber, String isbn) throws BookAlreadyReturnedException{
        var user = userRepository.searchUserByPhoneNumber(String.valueOf(phoneNumber));
        if(user == null){
            LOG.error("User not found with phone -> {}", phoneNumber);
            throw new UserNotFoundException(String.format("User not found with phone %d", phoneNumber));
        }
        var book = bookRepository.searchByIsbnExact(isbn);
        if(book == null){
            LOG.error("Book not found with isbn -> {}", isbn);
            throw new BookNotFoundException(String.format("Book not found with isbn %s", isbn));
        }
        var userBooks = bookIssueRepository.findBorrowedBookForUser(user.getPhoneNumber());
        var userBookIssue =userBooks.stream().filter(bi -> bi.getBook().getId() == book.getId() && bi.getReturnedDate() == null).findAny();
        if(userBookIssue.isPresent()){
            var bi = userBookIssue.get();
            bi.setReturnedDate(new Date());
            book.setQuantity(book.getQuantity()+1);
            bookRepository.save(book);
            return bookIssueRepository.save(bi);
        }else{
            throw new BookAlreadyReturnedException("book with isbn " + book.getIsbn() + " is already returned by the user " + user.getName());
        }
    }
}
