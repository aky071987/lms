package com.cs.lms.controller;

import com.cs.lms.entity.BookIssue;
import com.cs.lms.exception.BookAlreadyBorrowedException;
import com.cs.lms.exception.BookAlreadyReturnedException;
import com.cs.lms.exception.NoBookAvailableForBorrowException;
import com.cs.lms.service.LibraryService;
import com.cs.lms.uimodel.ResponseDataModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/borrow")
@Api(value = "Book Borrow")
public class BookIssueController {

    private LibraryService libraryService;
    public BookIssueController(LibraryService libraryService){
        this.libraryService = libraryService;
    }

    @GetMapping(value = "/issue/{phoneNumber}/{isbn}/{numberOfDays}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "issue book(isbn) to user(phoneNumber)")
    public ResponseEntity<ResponseDataModel> borrowBook(@PathVariable Long phoneNumber, @PathVariable String isbn, @PathVariable int numberOfDays){
        ResponseEntity<ResponseDataModel> response = null;
        try {
            BookIssue result =  libraryService.issueBookToUser(phoneNumber, isbn, numberOfDays);
            response = new ResponseEntity<>(ResponseDataModel.withResponse(result, null), HttpStatus.OK);
        } catch (BookAlreadyBorrowedException | NoBookAvailableForBorrowException e) {
            response = new ResponseEntity<>(ResponseDataModel.withResponse(null, e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @GetMapping(value = "/return/{phoneNumber}/{isbn}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "return book(isbn) issued to user(phoneNumber)")
    public ResponseEntity<ResponseDataModel> returnBook(@PathVariable Long phoneNumber, @PathVariable String isbn){
        ResponseEntity<ResponseDataModel> response = null;
        try {
            BookIssue result =  libraryService.returnBookToLibrary(phoneNumber, isbn);
            response = new ResponseEntity<>(ResponseDataModel.withResponse(result, null), HttpStatus.OK);
        } catch (BookAlreadyReturnedException e) {
            response = new ResponseEntity<>(ResponseDataModel.withResponse(null, e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @GetMapping(value = "/total-borrowed-books", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "get total borrowed books")
    public ResponseEntity<ResponseDataModel> numberOfBorrowedBooks(){
        var result =  libraryService.numberOfBorrowedBooks();
        return new ResponseEntity<>(ResponseDataModel.withResponse(result, null), HttpStatus.OK);
    }


    @GetMapping(value = "/borrowed-books/{phoneNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "get total borrowed books by user (phoneNumber)")
    public ResponseEntity<ResponseDataModel> numberOfBorrowedBooks(@PathVariable Long phoneNumber){
        var result =  libraryService.numberOfBorrowedBooksByUser(phoneNumber);
        return new ResponseEntity<>(ResponseDataModel.withResponse(result, null), HttpStatus.OK);
    }
}
