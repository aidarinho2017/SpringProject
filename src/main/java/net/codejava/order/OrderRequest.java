package net.codejava.order;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class OrderRequest {
    @NotNull
    private Integer productId;

    @NotNull
    @Min(1)
    private int quantity;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
