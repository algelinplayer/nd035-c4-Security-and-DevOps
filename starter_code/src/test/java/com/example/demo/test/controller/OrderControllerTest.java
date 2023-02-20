package com.example.demo.test.controller;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private static OrderController orderController;
    private static OrderRepository orderRepository = mock(OrderRepository.class);
    private static UserRepository userRepository = mock(UserRepository.class);

    @BeforeClass
    public static void setUp() throws NoSuchFieldException, IllegalAccessException {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
    }

    @Test
    public void submitOrderHappyPath() {
      when(userRepository.findByUsername("usernamefortesting")).thenReturn(Optional.of(TestUtils.createUserForOrder()));
      ResponseEntity<UserOrder> response = orderController.submit("usernamefortesting");
      assertNotNull(response);
      assertEquals(200, response.getStatusCodeValue());
      assertNotNull(response.getBody());
    }

    @Test
    public void submitOrderInvalidUser() {
        ResponseEntity<UserOrder> response = orderController.submit("invaliduser");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUser() {
      when(userRepository.findByUsername("usernamefortesting")).thenReturn(Optional.of(TestUtils.createUser()));

      ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("usernamefortesting");
      assertNotNull(ordersForUser);
      assertEquals(200, ordersForUser.getStatusCodeValue());
      List<UserOrder> orders = ordersForUser.getBody();
      assertNotNull(orders);
    }

}
