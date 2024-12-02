package net.codejava.product;
import java.net.URI;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/products")
public class CustomerProductApi {

    @Autowired
    private ProductRepository repo;

    // READ: Get all products
    @GetMapping
    @RolesAllowed("ROLE_CUSTOMER")
    public List<Product> list() {
        return repo.findAll();
    }

    // READ: Get a single product by ID
    @GetMapping("/{id}")
    @RolesAllowed("ROLE_CUSTOMER")
    public ResponseEntity<Product> getById(@PathVariable Integer id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
