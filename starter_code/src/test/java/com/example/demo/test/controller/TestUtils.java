package com.example.demo.test.controller;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.ModifyCartRequest;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestUtils {

    public static void injectObjects(Object target, String fieldName, Object toInject) throws NoSuchFieldException, IllegalAccessException {
        boolean wasPrivate = false;
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            if (!f.isAccessible()){
                f.setAccessible(true);
                wasPrivate = true;
            }
            f.set(target, toInject);
            if (wasPrivate){
                f.setAccessible(false);
            }
        } catch (Exception ex){
          throw ex;
        }
    }

    static Cart createCart(){
        Cart cart = new Cart();
        cart.setId(1L);
        return cart;
    }

    static User createUser(){
      User user= new User();
      user.setUsername("usernamefortesting");
      user.setPassword("Str0ngPa@@");
      Cart cart = new Cart();
      cart.setId(1L);
      cart.setUser(user);
      user.setCart( cart);
      return user;
    }

    static Item createItem() {
      Item item = new Item();
      item.setId(1L);
      item.setName("itemname");
      item.setPrice(BigDecimal.valueOf(9.99));
      item.setDescription("itemdescription");

      return item;
    }

    static List<Item> createItems() {
      Item item1 = new Item();
      item1.setId(1L);
      item1.setName("itemname1");
      item1.setPrice(BigDecimal.valueOf(1.00));
      item1.setDescription("itemdescription1");

      Item item2 = new Item();
      item2.setId(2L);
      item2.setName("itemname2");
      item2.setPrice(BigDecimal.valueOf(2.00));
      item2.setDescription("itemdescription2");

      return Arrays.asList(new Item[]{item2, item2});
    }

    static ModifyCartRequest createModifyCartRequest() {
      ModifyCartRequest request = new ModifyCartRequest();
      request.setItemId(1L);
      request.setUsername("usernamefortesting");
      request.setQuantity(1);
      return request;
    }

    static ModifyCartRequest createModifyCartRequestForInvalidUser() {
      ModifyCartRequest request = new ModifyCartRequest();
      request.setItemId(1L);
      request.setUsername("invalidUser");
      request.setQuantity(1);
      return request;
    }

    static ModifyCartRequest createModifyCartRequestForInvalidItem() {
      ModifyCartRequest request = new ModifyCartRequest();
      request.setItemId(0L);
      request.setUsername("usernamefortesting");
      request.setQuantity(1);
      return request;
    }

    static User createUserForOrder(){

      Item item = createItem();

      List<Item> items = Collections.singletonList(item);

      User user = createUser();

      Cart cart = createCart();
      cart.setUser(user);
      cart.setItems(items);
      cart.setTotal(item.getPrice());
      user.setCart(cart);

      return user;
    }

}
