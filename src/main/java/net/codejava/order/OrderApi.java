package net.codejava.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import net.codejava.product.Product;
import net.codejava.product.ProductRepository;
import net.codejava.user.User;

@RestController
@RequestMapping("/customer/orders")
public class OrderApi {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private ProductRepository productRepo;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody @Valid OrderRequest orderRequest) {

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        Product product = productRepo.findById(orderRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));


        Order order = new Order(currentUser, product, orderRequest.getQuantity());
        Order savedOrder = orderRepo.save(order);

        return ResponseEntity.ok(savedOrder);
    }
}
