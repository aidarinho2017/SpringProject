package net.codejava.cart;

import net.codejava.order.Order;
import net.codejava.order.OrderRepository;
import net.codejava.product.Product;
import net.codejava.product.ProductRepository;
import net.codejava.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartApi {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public ResponseEntity<?> viewCart() {
        User currentUser = getCurrentUser();

        // Проверяем наличие корзины, если нет — создаём её
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseGet(() -> cartRepository.save(new Cart(currentUser)));

        // Получаем список товаров в корзине
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        return ResponseEntity.ok(cartItems);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody @Valid CartItemRequest cartItemRequest) {
        User currentUser = getCurrentUser();

        // Ищем товар по ID
        Product product = productRepository.findById(cartItemRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Создаём корзину, если её нет
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseGet(() -> cartRepository.save(new Cart(currentUser)));

        // Проверяем, есть ли товар в корзине
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseGet(() -> new CartItem(cart, product, 0));

        // Обновляем количество товара
        cartItem.setQuantity(cartItem.getQuantity() + cartItemRequest.getQuantity());
        cartItemRepository.save(cartItem);

        return ResponseEntity.ok("Product added to cart successfully.");
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout() {
        User currentUser = getCurrentUser();

        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        Order order = new Order(currentUser, cart);
        orderRepository.save(order);

        cartItemRepository.deleteAllByCart(cart);

        return ResponseEntity.ok("Order placed successfully.");
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
