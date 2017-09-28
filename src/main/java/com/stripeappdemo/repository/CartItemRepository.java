package com.stripeappdemo.repository;

import org.springframework.data.repository.CrudRepository;

import com.stripeappdemo.models.CartItem;

public interface CartItemRepository extends CrudRepository<CartItem, Long>{

}
