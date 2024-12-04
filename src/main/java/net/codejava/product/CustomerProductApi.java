package net.codejava.product;

import java.util.List;

import javax.annotation.security.RolesAllowed;


import net.codejava.order.Order;
import net.codejava.order.OrderRequest;
import net.codejava.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/products")
public class CustomerProductApi {

    @Autowired
    private ProductRepository repo;

    @Autowired
    private OrderService orderService;

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

    @PutMapping("/order/{productId}")
    @RolesAllowed("ROLE_CUSTOMER")
    public ResponseEntity<Order> buyProduct(@PathVariable int productId, @RequestBody OrderRequest orderRequest) {
        try {

            int userId = getUserIdFromToken();
            System.out.println("User ID from token: " + userId);  // Log the userId

            // Check if the product exists
            Product product = repo.findById(productId).orElse(null);
            if (product == null) {
                System.out.println("Product not found: " + productId);  // Log if product doesn't exist
                return ResponseEntity.notFound().build();
            }

            // Create the order
            Order order = orderService.createOrder(userId, productId, orderRequest.getQuantity());
            System.out.println("Order created: " + order);  // Log the created order

            return ResponseEntity.ok(order);
        } catch (Exception e) {
            e.printStackTrace();  // Log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private int getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String subject = authentication.getName(); // Example: "customer1@example.com"
        System.out.println("Email: " + subject);   // Extract the email
        // maybe we should find userID through email, i dont know

        return 18;
    }
//    private String getUserIdFromToken() {
//
//    }
}
