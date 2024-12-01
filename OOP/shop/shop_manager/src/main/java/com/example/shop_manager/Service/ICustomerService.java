package com.example.shop_manager.Service;

import com.example.shop_manager.Entity.Customer;

import java.util.List;

public interface ICustomerService<Customer> {
	void addCustomer(Customer customer);
	void updateCustomer(Customer customer);
	void deleteCustomer(String id);
	List<Customer> loadAllCustomers();
	List<Customer> searchCustomerById(String id);
}
