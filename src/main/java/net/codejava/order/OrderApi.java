package net.codejava.order;

import net.codejava.rabbitmq.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;

import net.codejava.product.Product;
import net.codejava.product.ProductRepository;
import net.codejava.user.User;

import java.util.Arrays;

@RestController
@RequestMapping("/customer/orders")
public class OrderApi {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private MessageSender messageSender;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody @Valid OrderRequest orderRequest) {

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        Product product = productRepo.findById(orderRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));


        Order order = new Order(currentUser, product, orderRequest.getQuantity());
        Order savedOrder = orderRepo.save(order);


        send(order.toString());

        return ResponseEntity.ok(savedOrder);
    }

    public ResponseEntity<?> send(String message) {
        if(message.isBlank()){
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }
        messageSender.send(message);
        return new ResponseEntity<>(HttpStatus.valueOf(200));
    }
}
