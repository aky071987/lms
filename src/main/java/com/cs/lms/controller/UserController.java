package com.cs.lms.controller;

import com.cs.lms.entity.Book;
import com.cs.lms.entity.User;
import com.cs.lms.service.LibraryService;
import com.cs.lms.service.UserService;
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
@RequestMapping("/api/user")
@Api(value = "User")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Get a all users available in library")
    public ResponseEntity<ResponseDataModel> getAllUsers(){
        LOG.debug("fetching all available users");
        var result = userService.findAllUsers();
        return new ResponseEntity<>(ResponseDataModel.withResponse(result, null), HttpStatus.OK);
    }

    @GetMapping(value="/phoneNumber/{phone}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiOperation(value = "Get a user using its phone number (only 10 digit numbers, no country code)")
    public ResponseEntity<ResponseDataModel> getUserByPhoneNumber(@PathVariable Long phone) {
        LOG.debug("fetching user with phone number {} ",phone);
        HttpStatus httpStatus;
        try {
            var result = userService.searchByPhoneNumber(phone);
            httpStatus = HttpStatus.OK;
            return new ResponseEntity<>(ResponseDataModel.withResponse(result, null), httpStatus);
        } catch (Exception ex){
            LOG.error("exception while searching user with phone {}", phone, ex);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(ResponseDataModel.withResponse(null, ex), httpStatus);
        }
    }

    @RequestMapping(value = "/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Add a user")
    public ResponseEntity<ResponseDataModel> addUser(@Valid @RequestBody User user) {
        var bookCreated = userService.createUser(user);
        return new ResponseEntity<>(ResponseDataModel.withResponse(bookCreated, null), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/update/{phoneNumber}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update a book for a given phone number (only 10 digit numbers, no country code)")
    public ResponseEntity<ResponseDataModel> updateUser(@PathVariable Long phoneNumber, @RequestBody User user) {
        HttpStatus httpStatus;
        if(userService.searchByPhoneNumber(phoneNumber) != null){
            user = userService.updateUser(user);
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.NOT_FOUND;
        }
        LOG.info("user record with phone number updated successfully {} ",phoneNumber);
        return new ResponseEntity<>(ResponseDataModel.withResponse(user, null), httpStatus);
    }

    @RequestMapping(value = "/delete/{phoneNumber}",
            method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete a user using phone number (only 10 digits of phone number, no country code)")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Long phoneNumber) {
        HttpStatus httpStatus;
        Boolean res = false;
        if(userService.deleteUser(phoneNumber)){
            httpStatus = HttpStatus.OK;
            res = true;
        } else {
            httpStatus = HttpStatus.NOT_FOUND;
        }
        LOG.info("user record with phone number delete status :  {} ",res);
        return new ResponseEntity<>(res, httpStatus);
    }

}
