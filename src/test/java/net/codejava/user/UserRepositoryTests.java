package net.codejava.user;

import static org.assertj.core.api.Assertions.assertThat;

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

	@Autowired
	private RoleRepository roleRepository;

	@Test
	public void testCreateUsersWithRoles() {
		// Создаем роли, если они отсутствуют
		Role adminRole = roleRepository.findByName("ROLE_ADMIN")
				.orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
		Role editorRole = roleRepository.findByName("ROLE_EDITOR")
				.orElseGet(() -> roleRepository.save(new Role("ROLE_EDITOR")));
		Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
				.orElseGet(() -> roleRepository.save(new Role("ROLE_CUSTOMER")));

		// Создаем пользователей
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		User adminUser = new User("admin@example.com", passwordEncoder.encode("admin123"));
		User editorUser = new User("editor@example.com", passwordEncoder.encode("editor123"));
		User customerUser = new User("customer@example.com", passwordEncoder.encode("customer123"));

		// Назначаем роли пользователям
		adminUser.addRole(adminRole);
		editorUser.addRole(editorRole);
		customerUser.addRole(customerRole);

		// Сохраняем пользователей в базе данных
		userRepository.saveAll(List.of(adminUser, editorUser, customerUser));

		// Проверяем сохранение
		List<User> users = userRepository.findAll();
		assertThat(users).hasSizeGreaterThanOrEqualTo(3);

		// Проверяем, что роли назначены
		User savedAdmin = userRepository.findByEmail("admin@example.com").orElseThrow();
		assertThat(savedAdmin.getRoles()).extracting(Role::getName).containsExactly("ROLE_ADMIN");

		User savedEditor = userRepository.findByEmail("editor@example.com").orElseThrow();
		assertThat(savedEditor.getRoles()).extracting(Role::getName).containsExactly("ROLE_EDITOR");

		User savedCustomer = userRepository.findByEmail("customer@example.com").orElseThrow();
		assertThat(savedCustomer.getRoles()).extracting(Role::getName).containsExactly("ROLE_CUSTOMER");
	}
}
