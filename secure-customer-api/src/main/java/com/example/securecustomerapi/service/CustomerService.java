package com.example.securecustomerapi.service;

import com.example.securecustomerapi.dto.CustomerRequestDTO;
import com.example.securecustomerapi.dto.CustomerResponseDTO;
import com.example.securecustomerapi.dto.CustomerUpdateDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomerService {
    
    // Exercise 6: Updated to return Page and accept pagination/sorting params
    Page<CustomerResponseDTO> getAllCustomers(int page, int size, String sortBy, String sortDir);

    List<CustomerResponseDTO> getAllCustomers();
    
    CustomerResponseDTO getCustomerById(Long id);
    
    CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO);
    
    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO requestDTO);

    // Exercise 7: Partial Update
    CustomerResponseDTO partialUpdateCustomer(Long id, CustomerUpdateDTO updateDTO);
    
    void deleteCustomer(Long id);
    
    List<CustomerResponseDTO> searchCustomers(String keyword);
    
    List<CustomerResponseDTO> getCustomersByStatus(String status);

    // Exercise 5.3: Advanced Search
    List<CustomerResponseDTO> advancedSearch(String name, String email, String status);
}