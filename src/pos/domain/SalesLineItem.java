package pos.domain;

import java.math.BigDecimal;

public class SalesLineItem {
    private final ProductDescription product;
    private int quantity;

    public SalesLineItem(ProductDescription product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public ProductDescription getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    /** 同一商品再次录入时，把数量叠上去。 */
    public void addQuantity(int qty) {
        this.quantity += qty;
    }

    /** 信息专家：本对象同时拥有单价和数量，因此负责计算小计。 */
    public BigDecimal getSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
