package com.stripeappdemo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stripe")
public class StripeController {

	@RequestMapping("/")
	public String stripe (Model model) {
		return "stripe-cart";
	}
}
