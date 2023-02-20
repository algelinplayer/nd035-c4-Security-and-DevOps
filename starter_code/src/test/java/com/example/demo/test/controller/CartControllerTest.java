package com.example.demo.test.controller;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepository  = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {

        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

    }

    @Test
    public void addItemToCartHappyPath() {
      User user = TestUtils.createUser();

      Item item = TestUtils.createItem();

      Cart cart = TestUtils.createCart();
      user.setCart(cart);

      ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
      modifyCartRequest.setUsername(user.getUsername());
      modifyCartRequest.setItemId(item.getId());
      modifyCartRequest.setQuantity(1);

      when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
      when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

      ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
      assertNotNull(response);
      assertEquals(BigDecimal.valueOf(9.99), response.getBody().getTotal());
    }

    @Test
    public  void  addToCartForInvalidUser(){
      ModifyCartRequest request = TestUtils.createModifyCartRequestForInvalidUser();
      ResponseEntity<Cart> cart = cartController.addTocart(request);
      assertNotNull(cart);
      assertEquals(404, cart.getStatusCodeValue());
    }

    @Test
    public  void  addCartWithInvalidItemId(){
      ModifyCartRequest request = TestUtils.createModifyCartRequestForInvalidItem();
      ResponseEntity<Cart> cart = cartController.addTocart(request);
      assertNotNull(cart);
    }

    @Test
    public void addToCartAndRemoveFromCart() {
      when(userRepository.findByUsername("usernamefortesting")).thenReturn(Optional.of(TestUtils.createUser()));
      when(itemRepository.findById(1L)).thenReturn(Optional.of(TestUtils.createItem()));

      ModifyCartRequest request = TestUtils.createModifyCartRequest();
      ResponseEntity<Cart> cart = cartController.addTocart(request);
      assertNotNull(cart);
      assertEquals("usernamefortesting", cart.getBody().getUser().getUsername());
      List<Item> items = cart.getBody().getItems();
      assertTrue(!items.isEmpty());

      ResponseEntity<Cart> c= cartController.removeFromcart(request);
      assertNotNull(c);
    }

}
