package pos.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import pos.patterns.PaymentFactory;
import pos.patterns.ReceiptPrinterAdapter;

/** 商店根门面：加载商品目录并创建收银机。 */
public class Store {
    private final ProductCatalog catalog = new ProductCatalog();
    private final Register register;

    public Store(File catalogFile) {
        loadCatalog(catalogFile);
        this.register = new Register(catalog, new PaymentFactory(), new ReceiptPrinterAdapter());
    }

    private void loadCatalog(File catalogFile) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(catalogFile), "UTF-8"));
            try {
                String line = reader.readLine(); // header
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(",");
                    catalog.add(new ProductDescription(
                            parts[0].trim(),
                            parts[1].trim(),
                            new BigDecimal(parts[2].trim()),
                            Integer.parseInt(parts[3].trim())));
                }
            } finally {
                reader.close();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load catalog: " + catalogFile, e);
        }
    }

    public Register getRegister() {
        return register;
    }

    public ProductCatalog getCatalog() {
        return catalog;
    }
}
