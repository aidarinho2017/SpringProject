package net.codejava.cart;

import net.codejava.product.Product;
import net.codejava.product.ProductRepository;
import net.codejava.user.User;
import net.codejava.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/cart")
public class CartApi {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add/{productId}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_EDITOR"})
    public ResponseEntity<String> addToCart(@PathVariable Integer productId, @RequestParam Integer userId) {
        // Fetch user and ensure they have a cart
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user"));

        // Fetch the product to add
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Add the product to the user's cart
        cart.addProduct(product);
        cartRepository.save(cart);

        return ResponseEntity.ok("Product added to cart successfully.");
    }
}
