package pos.domain;

import java.math.BigDecimal;

/** 一条退货明细。 */
public class ReturnLineItem {
    private final ProductDescription product;
    private final int quantity;

    public ReturnLineItem(ProductDescription product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public ProductDescription getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
