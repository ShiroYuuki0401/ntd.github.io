package com.example.productmanagement.controller;

import com.example.productmanagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    private final ProductService productService;

    @Autowired
    public DashboardController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public String showDashboard(Model model) {
        // High-level stats
        model.addAttribute("totalValue", productService.getTotalValue());
        model.addAttribute("avgPrice", productService.getAveragePrice());
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        
        // Low Stock (Threshold < 10)
        model.addAttribute("lowStockProducts", productService.getLowStockProducts(10));
        
        // Recent Products
        model.addAttribute("recentProducts", productService.getRecentProducts());
        
        // Category Breakdown
        List<String> categories = productService.getAllCategories();
        Map<String, Long> categoryStats = new HashMap<>();
        for (String cat : categories) {
            categoryStats.put(cat, productService.getCountByCategory(cat));
        }
        model.addAttribute("categoryStats", categoryStats);
        
        return "dashboard";
    }
}