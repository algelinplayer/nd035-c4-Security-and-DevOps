package com.example.demo.test.controller;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

        private static ItemController itemController;

        private static ItemRepository itemRepository = mock(ItemRepository.class);

        @BeforeClass
        public static void setUp() throws NoSuchFieldException, IllegalAccessException {
            itemController = new ItemController();
            TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
        }

        @Test
        public void findItemByNameHappyPath() {

          when(itemRepository.findByName("itemname")).thenReturn(Collections.singletonList(TestUtils.createItem()));

          ResponseEntity<List<Item>> response = itemController.getItemsByName("itemname");

          assertNotNull(response);
          assertEquals(200, response.getStatusCodeValue());

          List<Item> items = response.getBody();
          assertNotNull(items);
          assertEquals(TestUtils.createItem(), items.stream().findFirst().get());

        }

        @Test
        public void findAllItems() {

          when(itemRepository.findAll()).thenReturn(TestUtils.createItems());

          ResponseEntity<List<Item>> response = itemController.getItems();
          assertNotNull(response);
          assertEquals(200, response.getStatusCodeValue());

          List<Item> items = response.getBody();
          assertNotNull(items);
          assertTrue(!items.isEmpty());
          assertEquals(2, items.size());
        }

        @Test
        public void findItemById() {

          when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(TestUtils.createItem()));

          ResponseEntity<Item> response = itemController.getItemById(1L);

          assertNotNull(response);
          assertEquals(200, response.getStatusCodeValue());
          assertEquals(TestUtils.createItem(), response.getBody());

        }

}
