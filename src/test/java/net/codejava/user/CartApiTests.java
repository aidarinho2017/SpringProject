package net.codejava.user;
import net.codejava.cart.Cart;
import net.codejava.cart.CartRepository;
import net.codejava.product.Product;
import net.codejava.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CartApiTests {

    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CartRepository cartRepository;

    @Test
    public void testAddProductToCart() {
        // Fetch an existing user
        Integer userId = 1; // Replace with a valid user ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Fetch user's cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user"));

        // Fetch an existing product
        Product product = productRepository.findById(5) // Replace with a valid product ID
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Add product to cart
        cart.addProduct(product);
        cartRepository.save(cart);

        // Assertions
        Cart updatedCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found after update"));

        assertThat(updatedCart.getProducts()).hasSize(1);
        assertThat(updatedCart.getProducts()).extracting("id").contains(product.getId());
    }
}
