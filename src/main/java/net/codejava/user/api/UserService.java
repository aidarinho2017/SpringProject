package net.codejava.user.api;

import javax.transaction.Transactional;

import net.codejava.cart.Cart;
import net.codejava.cart.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import net.codejava.user.User;
import net.codejava.user.UserRepository;

@Service
@Transactional
public class UserService {
	@Autowired private UserRepository repo;
	@Autowired private CartRepository cartRepo;
	@Autowired private PasswordEncoder passwordEncoder;
	
	public User save(User user) {
		String rawPassword = user.getPassword();
		String encodedPassword = passwordEncoder.encode(rawPassword);
		user.setPassword(encodedPassword);
		Cart cart = new Cart(user);
		cartRepo.save(cart);
		
		return repo.save(user);
	}
}
