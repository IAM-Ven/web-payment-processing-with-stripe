package com.stripeappdemo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.stripeappdemo.models.CartItem;
import com.stripeappdemo.models.Product;
import com.stripeappdemo.repository.CartItemRepository;
import com.stripeappdemo.repository.ProductRepository;

@Controller
@RequestMapping("/stripe")
public class StripeController {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CartItemRepository cartItemRepository;

	@RequestMapping("/")
	public String stripe (Model model) {
		List<Product> productList = (List<Product>) productRepository.findAll();
		List<CartItem> cartItemList = (List<CartItem>) cartItemRepository.findAll();
		
		model.addAttribute("productList", productList);
		model.addAttribute("cartItemList", cartItemList);
		
		return "stripe-cart";
	}
}
