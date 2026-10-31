package pos.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Sale {
    private final String id;
    private final Date date = new Date();
    private final List<SalesLineItem> lineItems = new ArrayList<SalesLineItem>();
    private boolean complete;
    private Payment payment;

    public Sale(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    /** 创建者：销售包含明细，因此由本对象创建 SalesLineItem。 */
    public SalesLineItem makeLineItem(ProductDescription product, int quantity) {
        if (complete) {
            throw new IllegalStateException("Sale already completed");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        product.decreaseStock(quantity);
        for (SalesLineItem existing : lineItems) {
            if (existing.getProduct().getItemId().equals(product.getItemId())) {
                existing.addQuantity(quantity);
                return existing;
            }
        }
        SalesLineItem item = new SalesLineItem(product, quantity);
        lineItems.add(item);
        return item;
    }

    public void becomeComplete() {
        this.complete = true;
    }

    public boolean isComplete() {
        return complete;
    }

    public void makePayment(Payment payment) {
        this.payment = payment;
        becomeComplete();
    }

    public Payment getPayment() {
        return payment;
    }

    public List<SalesLineItem> getLineItems() {
        return Collections.unmodifiableList(lineItems);
    }

    /** 信息专家：销售知道全部明细，因此由本对象计算合计。 */
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (SalesLineItem item : lineItems) {
            total = total.add(item.getSubtotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getBalance() {
        if (payment == null) {
            return getTotal().negate();
        }
        return payment.getAmount().subtract(getTotal()).setScale(2, RoundingMode.HALF_UP);
    }

    public String formatReceipt() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sb.append("======= MiniMart POS Receipt =======\n");
        sb.append("Sale ID : ").append(id).append('\n');
        sb.append("Date    : ").append(fmt.format(date)).append('\n');
        sb.append("------------------------------------\n");
        for (SalesLineItem item : lineItems) {
            sb.append(String.format("%-16s x%-3d %8.2f%n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getSubtotal()));
        }
        sb.append("------------------------------------\n");
        sb.append(String.format("TOTAL              %8.2f%n", getTotal()));
        if (payment != null) {
            sb.append(String.format("PAID (%s)%n", payment.getMethod()));
            sb.append(String.format("CASH               %8.2f%n", payment.getAmount()));
            sb.append(String.format("CHANGE             %8.2f%n", getBalance()));
        }
        sb.append("Thank you. Please come again.\n");
        return sb.toString();
    }
}
