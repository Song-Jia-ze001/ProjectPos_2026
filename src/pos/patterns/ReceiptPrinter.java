package pos.patterns;

/** 领域层使用的小票打印目标接口。 */
public interface ReceiptPrinter {
    void print(String receiptText);

    String getLastOutput();
}
