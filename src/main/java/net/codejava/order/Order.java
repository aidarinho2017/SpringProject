package net.codejava.order;


import net.codejava.product.Product;
import net.codejava.user.User;

import javax.persistence.*;


@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id") // Foreign key referencing users table
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id") // Foreign key referencing products table
    private Product product;



    private int quantity;

    // Getters and setters
    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}


