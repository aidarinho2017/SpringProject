package net.codejava.user;

import net.codejava.product.Product;
import net.codejava.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import javax.persistence.criteria.Predicate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repo;

    @Test
    public void testFilterProducts() {
        Product product1 = new Product("Laptop", 1200.00f);
        Product product2 = new Product("Smartphone", 800.00f);
        Product product3 = new Product("Tablet", 300.00f);

        repo.saveAll(List.of(product1, product2, product3));

        List<Product> filteredProducts = repo.findAll((root, query, criteriaBuilder) -> {
            Predicate predicate = (Predicate) criteriaBuilder.like(root.get("name"), "%Smart%");
            return (javax.persistence.criteria.Predicate) predicate;
        });

        assertThat(filteredProducts).hasSize(1);
        assertThat(filteredProducts.get(0).getName()).isEqualTo("Smartphone");
    }
}
