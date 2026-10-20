package pos.domain;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import pos.patterns.PaymentFactory;
import pos.patterns.ReceiptPrinter;

/**
 * 门面控制器（GRASP）：界面之后第一个接收系统操作的对象，
 * 包括 makeNewSale、enterItem、endSale、makePayment、
 * makeNewReturn、enterReturnItem、completeReturn。
 */
public class Register {
    private final ProductCatalog catalog;
    private final PaymentFactory paymentFactory;
    private final ReceiptPrinter receiptPrinter;
    private Sale currentSale;
    private ReturnTransaction currentReturn;
    private final List<Sale> completedSales = new ArrayList<Sale>();
    private final List<ReturnTransaction> completedReturns = new ArrayList<ReturnTransaction>();
    private int saleSeq;
    private int returnSeq;

    public Register(ProductCatalog catalog, PaymentFactory paymentFactory, ReceiptPrinter receiptPrinter) {
        this.catalog = catalog;
        this.paymentFactory = paymentFactory;
        this.receiptPrinter = receiptPrinter;
    }

    public void makeNewSale() {
        currentReturn = null;
        saleSeq++;
        String id = "S" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "-" + saleSeq;
        currentSale = new Sale(id);
    }

    public SalesLineItem enterItem(String itemId, int quantity) {
        if (currentSale == null) {
            makeNewSale();
        }
        ProductDescription desc = catalog.getProductDescription(itemId);
        if (desc == null) {
            throw new IllegalArgumentException("Unknown item id: " + itemId);
        }
        return currentSale.makeLineItem(desc, quantity);
    }

    public void endSale() {
        if (currentSale == null || currentSale.getLineItems().isEmpty()) {
            throw new IllegalStateException("No items in current sale");
        }
    }

    public Payment makePayment(BigDecimal cashTendered) {
        if (currentSale == null) {
            throw new IllegalStateException("No current sale");
        }
        if (cashTendered.compareTo(currentSale.getTotal()) < 0) {
            throw new IllegalArgumentException("Cash tendered is less than total");
        }
        Payment payment = paymentFactory.createCashPayment(cashTendered);
        currentSale.makePayment(payment);
        completedSales.add(currentSale);
        receiptPrinter.print(currentSale.formatReceipt());
        return payment;
    }

    public void makeNewReturn() {
        currentSale = null;
        returnSeq++;
        String id = "R" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "-" + returnSeq;
        currentReturn = new ReturnTransaction(id);
    }

    public ReturnLineItem enterReturnItem(String itemId, int quantity) {
        if (currentReturn == null) {
            makeNewReturn();
        }
        ProductDescription desc = catalog.getProductDescription(itemId);
        if (desc == null) {
            throw new IllegalArgumentException("Unknown item id: " + itemId);
        }
        return currentReturn.makeLineItem(desc, quantity);
    }

    public BigDecimal completeReturn() {
        if (currentReturn == null || currentReturn.getLineItems().isEmpty()) {
            throw new IllegalStateException("No items in current return");
        }
        currentReturn.complete();
        completedReturns.add(currentReturn);
        receiptPrinter.print(currentReturn.formatReceipt());
        return currentReturn.getRefundAmount();
    }

    public Sale getCurrentSale() {
        return currentSale;
    }

    public ReturnTransaction getCurrentReturn() {
        return currentReturn;
    }

    public ProductCatalog getCatalog() {
        return catalog;
    }

    public List<Sale> getCompletedSales() {
        return Collections.unmodifiableList(completedSales);
    }

    public String lastReceipt() {
        return receiptPrinter.getLastOutput();
    }
}
