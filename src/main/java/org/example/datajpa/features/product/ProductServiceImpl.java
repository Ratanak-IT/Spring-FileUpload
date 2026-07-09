package org.example.datajpa.features.product;

import lombok.RequiredArgsConstructor;
import org.example.datajpa.features.category.Category;
import org.example.datajpa.features.category.CategoryRepository;
import org.example.datajpa.features.product.dto.ProductRequest;
import org.example.datajpa.features.product.dto.UpdateProductRequest;
import org.example.datajpa.features.util.GenerateUtil;
import org.example.datajpa.specification.dto.RequestDto;
import org.example.datajpa.features.product.dto.ProductResponse;
import org.example.datajpa.specification.FilterSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;
        private final FilterSpecification<Product> filterSpecification;
        private final ProductMapper productMapper;

    @Override
    public Page<ProductResponse> filterAll(RequestDto requestDto, Pageable pageable) {
        Specification<Product> productSpecification = filterSpecification.getSearchSpecification(requestDto.getSearchRequestDtoList(), requestDto.getGlobalOperation());
        return productRepository.findAll(productSpecification, pageable)
                .map(productMapper::toProductResponse);
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {

        if (productRepository.existsByName(productRequest.name())){
            throw  new ResponseStatusException(HttpStatus.CONFLICT, "Product already exists");
        }

        Category category = categoryRepository.findById(productRequest.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        Product product = productMapper.toProduct(productRequest);
        product.setCategory(category);
        product.setCode(GenerateUtil.generateProductCode()); // ITE-3RD-****
        product.setSlug(GenerateUtil.generateSlug(product.getName()));

        Product savedProduct = productRepository.save(product);

        return productMapper.toProductResponse(savedProduct);
    }

    @Override
    public Page<ProductResponse> getProducts(int page, int size) {
    Sort sort = Sort.by(Sort.Direction.DESC,"id");
    Pageable pages = PageRequest.of(page, size, sort);
    return productRepository.findAll(pages)
            .map(productMapper::toProductResponse);
    }

    @Override
    public ProductResponse updateProduct(Integer id, UpdateProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid category id"
            );
        }

        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setThumbnail(productRequest.thumbnail());
        product.setUnitPrice(productRequest.unitPrice());
        product.setQty(productRequest.qty());
        product.setIsAvailable(productRequest.isAvailable());
        Product savedProduct = productRepository.save(product);

        return productMapper.toProductResponse(savedProduct);

    }

    @Override
    public void deleteProduct(Integer id) {

        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid product id"
            );
        }
        Product exit = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        productRepository.delete(exit);
    }

    @Override
    public void softDeleteProduct(Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid product id"
            );
        }
       Product exit = productRepository.findById(id)
                       .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        exit.setIsDelete(true);
        productRepository.save(exit);
    }
}
