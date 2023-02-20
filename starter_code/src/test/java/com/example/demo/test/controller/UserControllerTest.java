package com.example.demo.test.controller;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private static UserController userController;

    private static BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    private static UserRepository userRepository = mock(UserRepository.class);

    private static CartRepository cartRepository = mock(CartRepository.class);

    @BeforeClass
    public static void setUp() throws NoSuchFieldException, IllegalAccessException {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void createUserHappyPath() {
      when(bCryptPasswordEncoder.encode("Str0ngPa$$")).thenReturn("encodedPazz");

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("usernamefortesting");
        createUserRequest.setPassword("Str0ngPa$$");
        createUserRequest.setConfirmPassword("Str0ngPa$$");

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();

        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("usernamefortesting", user.getUsername());
        assertEquals("encodedPazz", user.getPassword());
    }

    @Test
    public void createWeakUserPassword(){
      CreateUserRequest r = new CreateUserRequest();
      r.setUsername("usernamefortesting");
      r.setPassword("aaaa");
      r.setConfirmPassword("aaaa");

      ResponseEntity<User> response = userController.createUser(r);

      assertNotNull(response);
      assertEquals(400, response.getStatusCodeValue());
      assertNull(response.getBody());
    }

    @Test
    public  void  findUserByName(){
      when(userRepository.findByUsername("usernamefortesting")).thenReturn(Optional.of(TestUtils.createUser()));
        Optional<User> userOptional = userRepository.findByUsername("usernamefortesting");

        assertTrue(userOptional.isPresent());
        assertEquals("usernamefortesting",userOptional.get().getUsername());
    }

    @Test
    public void findUserById(){
      when(userRepository.findById(1L)).thenReturn(Optional.of(TestUtils.createUser()));
        ResponseEntity<User> userResponse = userController.findById(1L);

        assertNotNull(userResponse);
        assertEquals(200, userResponse.getStatusCodeValue());

        User userEntity = userResponse.getBody();
        Assert.assertNotNull(userEntity);
        Assert.assertEquals("usernamefortesting", userEntity.getUsername());
    }

    @Test
    public  void  noUserFound(){
      when(userRepository.findByUsername("usernamefortesting")).thenReturn(Optional.of(TestUtils.createUser()));
        ResponseEntity<User> userResponse = userController.findByUserName("usernamenotfound");

        Assert.assertNotNull(userResponse);
        Assert.assertEquals(404,userResponse.getStatusCodeValue());
    }

}
