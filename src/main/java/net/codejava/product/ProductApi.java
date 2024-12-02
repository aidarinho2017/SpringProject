package net.codejava.product;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
@RestController
@RequestMapping("/products")
public class ProductApi {

	@Autowired
	private ProductRepository repo;

	// CREATE: Add a new product (ROLE_EDITOR)
	@PostMapping
	@RolesAllowed({"ROLE_EDITOR", "ROLE_ADMIN"})
	public ResponseEntity<Product> create(@RequestBody @Valid Product product) {
		Product savedProduct = repo.save(product);
		URI productURI = URI.create("/products/" + savedProduct.getId());
		return ResponseEntity.created(productURI).body(savedProduct);
	}

		@GetMapping
		@RolesAllowed({"ROLE_CUSTOMER", "ROLE_EDITOR", "ROLE_ADMIN"})
		public List<Product> list() {
			List<Product> products = repo.findAll();
			System.out.println("Products retrieved: " + products.size());
			return products;
		}





	// READ: Get a single product by ID (ROLE_ADMIN)
	@GetMapping("/{id}")
	@RolesAllowed({"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_EDITOR"})
	public ResponseEntity<Product> getById(@PathVariable Integer id) {
		return repo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
}
