package shop.interfaces;

import shop.dto.product.ProductCreateDTO;
import shop.dto.product.ProductItemDTO;

import java.util.List;

public interface ProductService {
    ProductItemDTO create(ProductCreateDTO model);
    List<ProductItemDTO> get();
}
