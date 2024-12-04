package net.codejava.order;

import net.codejava.product.Product;
import net.codejava.product.ProductRepository;
import net.codejava.user.User;
import net.codejava.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    public Order createOrder(int userId, int productId, int quantity) {
        // Fetch User and Product
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        // Create Order
        Order order = new Order();
        order.setUser(user);
        order.setProduct(product);
        order.setQuantity(quantity);

        // Save Order
        return orderRepository.save(order);
    }
}
