package pos.test;

import java.io.File;
import java.math.BigDecimal;

import pos.domain.Register;
import pos.domain.Sale;
import pos.domain.Store;

/** 不依赖 JUnit 的轻量 TDD 风格校验。 */
public class SaleTest {
    public static void main(String[] args) {
        Store store = new Store(new File("data/catalog.csv"));
        Register register = store.getRegister();
        register.makeNewSale();
        register.enterItem("1001", 2); // 7.00
        register.enterItem("1008", 1); // 2.00
        Sale sale = register.getCurrentSale();
        assertEqual("9.00", sale.getTotal().toPlainString());
        register.endSale();
        register.makePayment(new BigDecimal("20.00"));
        assertEqual("11.00", sale.getBalance().toPlainString());
        System.out.println("SaleTest passed.");
    }

    private static void assertEqual(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("expected " + expected + " but was " + actual);
        }
    }
}
