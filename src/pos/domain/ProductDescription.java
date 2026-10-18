package pos.domain;

import java.math.BigDecimal;

/**
 * 描述类（Larman 第 9 章）：商品目录数据，独立于当前库存中的实物实例而存在。
 */
public class ProductDescription {
    private final String itemId;
    private final String name;
    private final BigDecimal price;
    private int stock;

    public ProductDescription(String itemId, String name, BigDecimal price, int stock) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void decreaseStock(int qty) {
        if (qty > stock) {
            throw new IllegalStateException("Insufficient stock for item " + itemId);
        }
        stock -= qty;
    }

    public void increaseStock(int qty) {
        stock += qty;
    }
}
