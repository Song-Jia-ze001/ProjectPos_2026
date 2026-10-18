package pos.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** 信息专家：按商品编号查找商品描述。 */
public class ProductCatalog {
    private final Map<String, ProductDescription> descriptions = new LinkedHashMap<String, ProductDescription>();

    public void add(ProductDescription description) {
        descriptions.put(description.getItemId(), description);
    }

    public ProductDescription getProductDescription(String itemId) {
        return descriptions.get(itemId);
    }

    public Collection<ProductDescription> getAll() {
        return Collections.unmodifiableCollection(descriptions.values());
    }
}
