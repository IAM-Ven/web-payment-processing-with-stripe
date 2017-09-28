package com.stripeappdemo.repository;

import org.springframework.data.repository.CrudRepository;

import com.stripeappdemo.models.User;

public interface UserRepository extends CrudRepository<User, Long> {

}
