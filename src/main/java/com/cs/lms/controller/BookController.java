package com.cs.lms.controller;

import com.cs.lms.entity.Book;
import com.cs.lms.service.LibraryService;
import com.cs.lms.uimodel.BookUiModel;
import com.cs.lms.uimodel.ResponseDataModel;
import com.cs.lms.util.Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/book")
@Api(value = "Book")
public class BookController {
    private static final Logger LOG = LoggerFactory.getLogger(BookController.class);
    private LibraryService libraryService;

    public BookController(LibraryService libraryService){
        this.libraryService = libraryService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Get a all books available in library")
    public ResponseEntity<ResponseDataModel> getAllBooks(){
        LOG.debug("fetching all available books");
        var result = libraryService.findAllBooks();
        return new ResponseEntity<>(ResponseDataModel.withResponse(result, null), HttpStatus.OK);
    }

    @GetMapping(value = "/total-book-quantity", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Get a total quantity of books available in library(which are not borrowed) for given book")
    public ResponseEntity<ResponseDataModel> getAllBooksQuantity(){
        var result = libraryService.totalBooksAvailable();
        LOG.debug("total quantity of books available in library is {}", result);
        return new ResponseEntity<>(ResponseDataModel.withResponse(result, null), HttpStatus.OK);
    }

    @PostMapping(value = "/book-quantity", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Get a quantity of books available for given book based on " +
            "either isbn or title or both. (which are not borrowed)")
    public ResponseEntity<ResponseDataModel> getAllBooksFor(@RequestBody BookUiModel.TitleIsbn titleIsbn){
        LOG.debug("find books request is {}", titleIsbn);
        Integer result = null;
        if(StringUtils.hasLength(titleIsbn.getTitle()) && StringUtils.hasLength(titleIsbn.getIsbn())){
            result = libraryService.totalBooksForTitleAndIsbn(titleIsbn.getTitle(), titleIsbn.getIsbn());
        } else if(!StringUtils.hasLength(titleIsbn.getTitle()) && StringUtils.hasLength(titleIsbn.getIsbn())){
            result = libraryService.totalBooksForIsbn(titleIsbn.getIsbn());
        } else if(StringUtils.hasLength(titleIsbn.getTitle()) && !StringUtils.hasLength(titleIsbn.getIsbn())){
            result = libraryService.totalBooksForTitle(titleIsbn.getTitle());
        }
        if (result == null){
            result = 0;
        }
        LOG.debug("total quantity of books available in library for requested param {} is {}", titleIsbn , result);
        return new ResponseEntity<>(ResponseDataModel.withResponse(result, null), HttpStatus.OK);
    }

    @PostMapping(value="/isbn",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiOperation(value = "Get a book using its ISBN")
    public ResponseEntity<ResponseDataModel> getBookByIsbn(@RequestBody BookUiModel.Isbn bookRequest) {
        HttpStatus httpStatus;
        try {
            var result = libraryService.searchByIsbn(bookRequest.getIsbn(), Util.toBoolean(bookRequest.getExact()));
            httpStatus = HttpStatus.OK;
            return new ResponseEntity<>(ResponseDataModel.withResponse(result, null), httpStatus);
        } catch (Exception ex){
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(ResponseDataModel.withResponse(null, ex), httpStatus);
        }
    }

    @RequestMapping(value = "/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Add a book")
    public ResponseEntity<ResponseDataModel> addBook(@Valid @RequestBody Book book) {
        var bookCreated = libraryService.createBook(book);
        return new ResponseEntity<>(ResponseDataModel.withResponse(bookCreated, null), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/update/{isbn}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update a book")
    public ResponseEntity<ResponseDataModel> updateBook(@PathVariable String isbn, @RequestBody Book book) {
        HttpStatus httpStatus;
        var bk =libraryService.searchByIsbn(isbn, true);
        if(!bk.isEmpty()){
            book.setIsbn(isbn);
            book.setId(bk.get(0).getId());
            book = libraryService.updateBook(book);
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.NOT_FOUND;
        }
        LOG.info("book record with isbn updated successfully {} ",isbn);
        return new ResponseEntity<>(ResponseDataModel.withResponse(book, null), httpStatus);
    }

    @RequestMapping(value = "/delete/{isbn}",
            method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete a book")
    public ResponseEntity<Boolean> deleteBook(@PathVariable String isbn) {
        HttpStatus httpStatus;
        Boolean res = false;
        if(libraryService.deleteBook(isbn)){
            httpStatus = HttpStatus.OK;
            res= true;
        } else {
            httpStatus = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(res, httpStatus);
    }

    @RequestMapping(value="search/{keyword}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiOperation(value = "Get a book using keyword (title or isbn or description), this search is case insensitive")
    public ResponseEntity<ResponseDataModel> getBooksByKeyword(@PathVariable String keyword) {
        var books = libraryService.searchBooks(keyword,false);
        return new ResponseEntity<>(ResponseDataModel.withResponse(books, null), HttpStatus.OK);
    }
}
