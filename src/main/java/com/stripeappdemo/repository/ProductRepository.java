package com.stripeappdemo.repository;

import org.springframework.data.repository.CrudRepository;

import com.stripeappdemo.models.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {
	
	Product findByName(String name);

}
