package net.codejava.product;
import java.net.URI;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/editor/products")
public class EditorProductApi {

    @Autowired
    private ProductRepository repo;

    // CREATE: Add a new product
    @PostMapping
    @RolesAllowed("ROLE_EDITOR")
    public ResponseEntity<Product> create(@RequestBody @Valid Product product) {
        Product savedProduct = repo.save(product);
        URI productURI = URI.create("/products/" + savedProduct.getId());
        return ResponseEntity.created(productURI).body(savedProduct);
    }

    // READ: Get all products
    @GetMapping
    @RolesAllowed("ROLE_EDITOR")
    public List<Product> list() {
        return repo.findAll();
    }
}

