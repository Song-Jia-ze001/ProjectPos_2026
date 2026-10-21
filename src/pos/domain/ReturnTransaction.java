package pos.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/** 一笔退货交易：包含退货明细并计算应退金额。 */
public class ReturnTransaction {
    private final String id;
    private final Date date = new Date();
    private final List<ReturnLineItem> lineItems = new ArrayList<ReturnLineItem>();
    private boolean complete;
    private BigDecimal refundAmount = BigDecimal.ZERO;

    public ReturnTransaction(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public ReturnLineItem makeLineItem(ProductDescription product, int quantity) {
        if (complete) {
            throw new IllegalStateException("Return already completed");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        product.increaseStock(quantity);
        ReturnLineItem item = new ReturnLineItem(product, quantity);
        lineItems.add(item);
        return item;
    }

    public void complete() {
        this.complete = true;
        this.refundAmount = getTotal();
    }

    public boolean isComplete() {
        return complete;
    }

    public List<ReturnLineItem> getLineItems() {
        return Collections.unmodifiableList(lineItems);
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ReturnLineItem item : lineItems) {
            total = total.add(item.getSubtotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public String formatReceipt() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sb.append("======= MiniMart POS Return =======\n");
        sb.append("Return ID : ").append(id).append('\n');
        sb.append("Date      : ").append(fmt.format(date)).append('\n');
        sb.append("------------------------------------\n");
        for (ReturnLineItem item : lineItems) {
            sb.append(String.format("%-16s x%-3d %8.2f%n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getSubtotal()));
        }
        sb.append("------------------------------------\n");
        sb.append(String.format("REFUND             %8.2f%n", getTotal()));
        sb.append("Please keep this return slip.\n");
        return sb.toString();
    }
}
