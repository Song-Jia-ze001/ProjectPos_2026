package pos.patterns;

import java.math.BigDecimal;

import pos.domain.Payment;

/**
 * 简单工厂（Larman 第 26 章）：把付款对象的创建隔离出来，
 * 让收银机保持内聚，并便于以后增加新的付款方式。
 */
public class PaymentFactory {
    public Payment createCashPayment(BigDecimal cashTendered) {
        return new Payment(cashTendered, "CASH");
    }
}
