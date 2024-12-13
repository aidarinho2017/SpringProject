package net.codejava.order;

import net.codejava.cart.Cart;
import net.codejava.cart.CartItem;
import net.codejava.cart.CartItemRepository;
import net.codejava.cart.CartRepository;
import net.codejava.rabbitmq.MessageSender;
import net.codejava.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/customer/orders")
public class OrderApi {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private MessageSender messageSender;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/checkout")
    @Transactional
    public ResponseEntity<?> placeOrder() {
        // Получаем текущего пользователя
        User currentUser = getCurrentUser();

        // Получаем корзину пользователя
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        // Получаем товары в корзине
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is empty. Cannot place an order.");
        }

        // Создаём заказ
        Order order = new Order(currentUser, cart);
        orderRepo.save(order);

        // Отправляем уведомление через RabbitMQ
        send("Order placed: " + order.getId() + " by user: " + currentUser.getId());

        // Очищаем корзину
        cartItemRepository.deleteAllByCart(cart);

        return ResponseEntity.ok("Order placed successfully.");
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public ResponseEntity<?> send(String message) {
        // Отключить отправку сообщений для тестирования
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
