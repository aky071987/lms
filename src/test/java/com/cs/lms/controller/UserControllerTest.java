package com.cs.lms.controller;


import com.cs.lms.constant.LmsConstant;
import com.cs.lms.entity.User;
import com.cs.lms.exception.UserNotFoundException;
import com.cs.lms.service.UserService;
import com.cs.lms.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserControllerTest {

    @Autowired
    UserService userService;
    @Autowired
    UserController userController;

    @BeforeAll
    public void setupDataBaseWithTestData(){
        //create users
        User users[] = {
                new User("ajay", "8888888888", Util.generateLmsUserId(), LmsConstant.ROLE_PATRON, true),
                new User("james", "7777777777", Util.generateLmsUserId(), LmsConstant.ROLE_PATRON, true),
        };
        Arrays.asList(users).stream().forEach(user -> {
            userService.createUser(user);
        });
    }

    @Test
    @Order(1)
    public void test_fetchUsersCount(){
        Object data = userController.getAllUsers().getBody().getData();
        Assertions.assertTrue(data instanceof List);
        List dataList = (List)data;
        Assertions.assertTrue(dataList.size() == 2);
    }

    @Test
    @Order(2)
    public void test_createNewUser(){
        var user = new User("martin", "6666666666", Util.generateLmsUserId(), LmsConstant.ROLE_PATRON, true);
        var createdBook = userController.addUser(user);
        Assertions.assertEquals(createdBook.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(((User)createdBook.getBody().getData()).getUserLmsId(), Util.currentLmsUserId());
    }

    @Test
    @Order(3)
    public void test_createNewUser_Exception(){
        var user = new User("martin", "6666666666", Util.generateLmsUserId(),LmsConstant.ROLE_PATRON, true);
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            userController.addUser(user);
        });
    }

    @Test
    @Order(4)
    public void test_updateUser() throws JsonProcessingException {
        var userByPhoneNumber = userController.getUserByPhoneNumber(8888888888l);
        var user = (User)userByPhoneNumber.getBody().getData();
        Assertions.assertEquals(user.getName(), "ajay");
        user.setPhoneNumber("55555555");
        var updateResponse = userController.updateUser(8888888888l, user);
        var updatedUser = (User)updateResponse.getBody().getData();
        Assertions.assertEquals(updatedUser.getPhoneNumber(), "55555555", "phone number should be 55555555 after updating the user");
    }

    @Test
    @Order(5)
    public void test_deleteBook() throws JsonProcessingException {
        Assertions.assertTrue(userController.deleteUser(7777777777l).getBody());
    }

    @Test
    @Order(6)
    public void test_deleteBook_Exception(){
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.deleteUser(7777777777l);
        });
    }
}
