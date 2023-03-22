package shop.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import shop.dto.product.ProductCreateDTO;
import shop.dto.product.ProductEditDTO;
import shop.dto.product.ProductItemDTO;
import shop.entities.CategoryEntity;
import shop.entities.ProductEntity;
import shop.entities.ProductImageEntity;
import shop.interfaces.ProductService;
import shop.mapper.ProductMapper;
import shop.repositories.ProductImageRepository;
import shop.repositories.ProductRepository;
import shop.storage.StorageService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final StorageService storageService;
    private final ProductMapper productMapper;

    @Override
    public ProductItemDTO create(ProductCreateDTO model) {
        var p = new ProductEntity();
        var cat = new CategoryEntity();
        cat.setId(model.getCategory_id());
        p.setName(model.getName());
        p.setDescription(model.getDescription());
        p.setPrice(model.getPrice());
        p.setDateCreated(new Date());
        p.setCategory(cat);
        p.setDelete(false);
        productRepository.save(p);
        int priority = 1;
        for (var img : model.getFiles()) {
            var file = storageService.saveMultipartFile(img);
            ProductImageEntity pi = new ProductImageEntity();
            pi.setName(file);
            pi.setDateCreated(new Date());
            pi.setPriority(priority);
            pi.setDelete(false);
            pi.setProduct(p);
            productImageRepository.save(pi);
            priority++;
        }
        return null;
    }

    @Override
    public List<ProductItemDTO> get() {
        var products = productRepository.findAll();
        var result = new ArrayList<ProductItemDTO>();
        for (var p : products) {
            var item = productMapper.ProductItemDTOByProduct(p);
            for (var img : p.getProductImages())
                item.getFiles().add(img.getName());
            result.add(item);
        }
        return result;
    }

    @Override
    public ProductItemDTO edit(int id, ProductEditDTO model) {
        var p = productRepository.findById(id);
        if (p.isPresent()) {
            var product = p.get();
            for (var name : model.getRemoveFiles()) {
                var pi = productImageRepository.findByName(name);
                if (pi != null) {
                    productImageRepository.delete(pi);
                    storageService.removeFile(name);
                }
            }
            var cat = new CategoryEntity();
            cat.setId(model.getCategory_id());
            product.setName(model.getName());
            product.setDescription(model.getDescription());
            product.setPrice(model.getPrice());
            product.setDateCreated(new Date());
            product.setCategory(cat);
            productRepository.save(product);
            var productImages = product.getProductImages();
            int priority = 1;
            for (var pi : productImages) {
                if (pi.getPriority() > priority)
                    priority = pi.getPriority();
            }
            priority++;
            for (var img : model.getFiles()) {
                var file = storageService.saveMultipartFile(img);
                ProductImageEntity pi = new ProductImageEntity();
                pi.setName(file);
                pi.setDateCreated(new Date());
                pi.setPriority(priority);
                pi.setDelete(false);
                pi.setProduct(product);
                productImageRepository.save(pi);
                priority++;
            }
        }

        return null;
    }

    @Override
    public ProductItemDTO getById(int id) {
        var productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            var product = productOptional.get();
            var data = productMapper.ProductItemDTOByProduct(product);
            for (var img : product.getProductImages())
                data.getFiles().add(img.getName());
            return data;
        }
        return null;
    }

    @Override
    public void delete(int id) {
        ProductEntity product = productRepository.findById(id).get();
        for(var img : product.getProductImages())
            storageService.removeFile(img.getName());;
        productRepository.deleteById(id);
    }
}