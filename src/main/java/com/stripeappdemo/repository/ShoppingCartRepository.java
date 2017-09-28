package com.stripeappdemo.repository;

import org.springframework.data.repository.CrudRepository;

import com.stripeappdemo.models.ShoppingCart;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Long>{

}
