package com.example.productmanagement.service;

import com.example.productmanagement.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    // Basic CRUD
    List<Product> getAllProducts();
    List<Product> getAllProducts(Sort sort); // Sorting support
    Page<Product> getAllProducts(Pageable pageable); // Pagination support
    
    Optional<Product> getProductById(Long id);
    Product saveProduct(Product product);
    void deleteProduct(Long id);
    
    // Search
    List<Product> searchProducts(String keyword);
    Page<Product> searchProducts(String keyword, Pageable pageable);
    Page<Product> advancedSearch(String name, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // Categories
    List<String> getAllCategories();
    List<Product> getProductsByCategory(String category);
    
    // Statistics
    long getCountByCategory(String category);
    BigDecimal getTotalValue();
    BigDecimal getAveragePrice();
    List<Product> getLowStockProducts(int threshold);
    List<Product> getRecentProducts();
}