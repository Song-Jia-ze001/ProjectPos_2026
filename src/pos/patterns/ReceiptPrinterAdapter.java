package pos.patterns;

/**
 * 适配器（GoF）：把内存/界面中的小票输出，适配成收银机所依赖的稳定接口 ReceiptPrinter。
 */
public class ReceiptPrinterAdapter implements ReceiptPrinter {
    private String lastOutput = "";

    public void print(String receiptText) {
        this.lastOutput = receiptText;
        System.out.println(receiptText);
    }

    public String getLastOutput() {
        return lastOutput;
    }
}
