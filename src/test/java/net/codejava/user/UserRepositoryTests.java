package net.codejava.user;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void testCreateMultipleUsersWithRoles() {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		// Create users with encoded passwords
		User adminUser = new User("admin@example.com", passwordEncoder.encode("admin123"));
		User editorUser = new User("editor@example.com", passwordEncoder.encode("editor123"));
		User customerUser1 = new User("customer1@example.com", passwordEncoder.encode("customer123"));
		User customerUser2 = new User("customer2@example.com", passwordEncoder.encode("customer456"));

		// Assign roles to users
		adminUser.addRole(new Role(35)); // Assuming ROLE_ADMIN has ID 1
		editorUser.addRole(new Role(36)); // Assuming ROLE_EDITOR has ID 2
		customerUser1.addRole(new Role(37)); // Assuming ROLE_CUSTOMER has ID 3
		customerUser2.addRole(new Role(37)); // Assign ROLE_CUSTOMER to another user

		// Save users in the database
		userRepository.saveAll(List.of(adminUser, editorUser, customerUser1, customerUser2));

		// Assertions for roles


	}
}