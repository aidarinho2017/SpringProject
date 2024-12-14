package net.codejava.user.api;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import net.codejava.user.User;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Duration;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
public class UserApi {

	@Autowired
	private UserService service;

	// Map to store rate-limiting buckets for each user (or IP address)
	private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

	// Method to get or create a bucket for a given user key
	private Bucket getBucket(String key) {
		return buckets.computeIfAbsent(key, k -> Bucket.builder()
				.addLimit(Bandwidth.classic(1, Refill.intervally(1, Duration.ofSeconds(10))))
				.build());
	}

	@PostMapping("/users")
	public ResponseEntity<?> createUser(@RequestBody @Valid User user, HttpServletRequest request) {
		// Use IP address or a user-specific identifier as the key
		String userKey = request.getRemoteAddr();

		Bucket bucket = getBucket(userKey);

		// Check if the rate limit is exceeded
		if (bucket.tryConsume(1)) {
			User createdUser = service.save(user);
			URI uri = URI.create("/users/" + createdUser.getId());

			UserDTO userDto = new UserDTO(createdUser.getId(), createdUser.getEmail());
			return ResponseEntity.created(uri).body(userDto);
		} else {
			// Rate limit exceeded
			return ResponseEntity.status(429) // HTTP 429 Too Many Requests
					.body("Rate limit exceeded. Please try again in 10 seconds.");
		}
	}
	@GetMapping("/users")
	public ResponseEntity<List<UserDTO>> getUsers() {
		List<User> users = service.findAll(); // Assuming the service has a method to find all users
		List<UserDTO> userDTOs = users.stream()
				.map(user -> new UserDTO(user.getId(), user.getEmail()))
				.collect(Collectors.toList());

		return ResponseEntity.ok(userDTOs);
	}
}

