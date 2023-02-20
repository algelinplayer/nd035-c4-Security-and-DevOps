package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

  final Logger logger =  LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}

	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		Optional<User> userOptional = userRepository.findByUsername(username);
		return userOptional.isPresent() ? ResponseEntity.ok(userOptional.get()) : ResponseEntity.notFound().build();
	}

	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

    ResponseEntity<User> responseForFailedPasswordCreation = createPassword(createUserRequest, user);
    if (responseForFailedPasswordCreation != null) return responseForFailedPasswordCreation;

    userRepository.save(user);

    logger.info("User created with success with username : {}", createUserRequest.getUsername());

		return ResponseEntity.ok(user);
	}

  private ResponseEntity<User> createPassword(CreateUserRequest createUserRequest, User user) {

    String password = createUserRequest.getPassword();

    ResponseEntity<User> passValidationResponse = validadePassword(createUserRequest, password);
    if (passValidationResponse != null) return passValidationResponse;

    user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

    return null;

  }

  private ResponseEntity<User> validadePassword(CreateUserRequest createUserRequest, String password) {
    ResponseEntity<User> passWeakResponse = isPasswordWeak(password, createUserRequest.getUsername());
    if (passWeakResponse != null) return passWeakResponse;

    ResponseEntity<User> noMatchPassResponse = passwordAndPasswordConfirmationDoesNotMatch(password, createUserRequest.getConfirmPassword(), createUserRequest.getUsername());
    if (noMatchPassResponse != null) return noMatchPassResponse;

    return null;
  }

  private ResponseEntity<User> passwordAndPasswordConfirmationDoesNotMatch(String password, String confirmPassword, String username) {
    if (password.equals(confirmPassword)) {
      return null;
    }
    String message = "Error creating user with username : " + username + " the informed password and password confirmation must match.";
    logger.error(message);
    return ResponseEntity.badRequest().build();
  }

  private ResponseEntity<User> isPasswordWeak(String password, String username) {
//    ^                 # start-of-string
//    (?=.*[0-9])       # a digit must occur at least once
//    (?=.*[a-z])       # a lower case letter must occur at least once
//    (?=.*[A-Z])       # an upper case letter must occur at least once
//    (?=.*[@#$%^&+=!])  # a special character must occur at least once
//    (?=\S+$)          # no whitespace allowed in the entire string
//    .{8,}             # anything, at least eight places though
//    $                 # end-of-string
    boolean isStrong = password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");
    boolean isWeak = !isStrong;
    if (isWeak){
      String message = "Error creating user with username : " + username + " the informed password has a weak pattern.";
      logger.error(message);
      return ResponseEntity.badRequest().build();
    }
    return null;
  }

}
