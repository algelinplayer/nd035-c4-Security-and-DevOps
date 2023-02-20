package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/cart")
public class CartController {

  final Logger logger = LoggerFactory.getLogger(CartController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ItemRepository itemRepository;

	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
    Optional<Item> optionalItem = itemRepository.findById(request.getItemId());
    if (optionalUser.isPresent() && optionalItem.isPresent()) {
      User user = optionalUser.get();
      Cart cart = user.getCart();
      cart.setUser(user);
      IntStream.range(0, request.getQuantity()).forEach(i -> cart.addItem(optionalItem.get()));
      cartRepository.save(cart);
      logger.info("Item {} successfully added to {}'s cart.", optionalItem.get(), optionalUser.get().getUsername());
      return ResponseEntity.ok(cart);
    } else {
      logger.error("Item {} failed to be added to {}'s cart.", optionalItem.isPresent() ? optionalItem.get().getName() : "[NOT FOUND]", optionalUser.isPresent() ? optionalUser.get().getUsername() : "[NOT FOUND]");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
    Optional<Item> optionalItem = itemRepository.findById(request.getItemId());
    if (optionalUser.isPresent() && optionalItem.isPresent()) {
        Cart cart = optionalUser.get().getCart();
        IntStream.range(0, request.getQuantity()).forEach(i -> cart.removeItem(optionalItem.get()));
        cartRepository.save(cart);
        logger.info("Item {} successfully removed from {}'s cart.", optionalItem.get().getName(), optionalUser.get().getUsername());
        return ResponseEntity.ok(cart);
    } else {
      logger.error("Item {} failed to be removed to {}'s cart.", optionalItem.isPresent() ? optionalItem.get().getName() : "[NOT FOUND]", optionalUser.isPresent() ? optionalUser.get().getUsername() : "[NOT FOUND]");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

}
