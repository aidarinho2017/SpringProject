package net.codejava.product;

import java.net.URI;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

	// READ: Get all products (ROLE_CUSTOMER, ROLE_EDITOR)
	@GetMapping
	@RolesAllowed({"ROLE_CUSTOMER", "ROLE_EDITOR"})
	public List<Product> list() {
		return repo.findAll();
	}

	// READ: Get a single product by ID (ROLE_ADMIN)
	@GetMapping("/{id}")
	@RolesAllowed("ROLE_ADMIN")
	public ResponseEntity<Product> getById(@PathVariable Integer id) {
		return repo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	// UPDATE: Update a product by ID (ROLE_ADMIN)
	@PutMapping("/{id}")
	@RolesAllowed("ROLE_ADMIN")
	public ResponseEntity<Product> update(@PathVariable Integer id, @RequestBody @Valid Product updatedProduct) {
		return repo.findById(id)
				.map(product -> {
					product.setName(updatedProduct.getName());
					product.setPrice(updatedProduct.getPrice());
					repo.save(product);
					return ResponseEntity.ok(product);
				})
				.orElse(ResponseEntity.notFound().build());
	}

	// DELETE: Remove a product by ID (ROLE_ADMIN)
	@DeleteMapping("/{id}")
	@RolesAllowed("ROLE_ADMIN")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		if (repo.existsById(id)) {
			repo.deleteById(id);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}
}
