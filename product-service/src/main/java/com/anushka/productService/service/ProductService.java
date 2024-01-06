package com.anushka.productService.service;

import com.anushka.productService.dto.ProductRequest;
import com.anushka.productService.dto.ProductResponse;
import com.anushka.productService.model.Product;
import com.anushka.productService.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor //create constructor automatically
@Slf4j //for log
public class ProductService {
    private final ProductRepository productRepository;


    public void createProduct(ProductRequest productRequest){
        Product product= Product.builder()//map Product service with product model
                        .name(productRequest.getName())
                        .description(productRequest.getDescription())
                        .price(productRequest.getPrice())
                .build();//to save this in database we need to access product repository

        productRepository.save(product);
        log.info("Product {} is saved",product.getId()); //place holder{} by Slf4j
    }

    public List<ProductResponse> getAllProducts() {
       List<Product> products= productRepository.findAll();
       //map product to product response

        return products.stream().map(this::mapToProductResponse).toList();

    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }


}
