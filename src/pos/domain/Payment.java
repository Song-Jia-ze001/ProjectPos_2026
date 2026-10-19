package pos.domain;

import java.math.BigDecimal;

/** 一笔付款，当前迭代仅支持现金。 */
public class Payment {
    private final BigDecimal amount;
    private final String method;

    public Payment(BigDecimal amount, String method) {
        this.amount = amount;
        this.method = method;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }
}
