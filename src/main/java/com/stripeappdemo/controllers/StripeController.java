package com.stripeappdemo.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Order;
import com.stripe.model.Subscription;
import com.stripeappdemo.models.CartItem;
import com.stripeappdemo.models.Product;
import com.stripeappdemo.repository.CartItemRepository;
import com.stripeappdemo.repository.ProductRepository;
import com.stripeappdemo.repository.ShoppingCartRepository;
import com.stripeappdemo.repository.UserRepository;

@Controller
@RequestMapping("/stripe")
public class StripeController {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private ShoppingCartRepository shoppingCartRepository;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/")
	public String stripe(Model model) {
		List<Product> productList = (List<Product>) productRepository.findAll();
		List<CartItem> cartItemList = (List<CartItem>) cartItemRepository.findAll();

		BigDecimal total = new BigDecimal(0);

		for (CartItem item : cartItemList) {
			total = total.add(item.getSubTotal());
		}

		model.addAttribute("productList", productList);
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("total", total.abs());

		return "stripe-cart";
	}

	@RequestMapping("/addToCart")
	public String addToCart(@RequestParam Long id) {
		Product product = productRepository.findOne(id);

		CartItem cartItem = new CartItem();
		cartItem.setProduct(product);
		cartItem.setQty(1);
		cartItem.setSubTotal(new BigDecimal(product.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
		cartItemRepository.save(cartItem);

		return "redirect:/stripe/";
	}

	@RequestMapping("/remove")
	public String remove(@RequestParam Long id) {
		cartItemRepository.delete(id);

		return "redirect:/stripe/";
	}

	@RequestMapping(value = "/updateCart", method = RequestMethod.POST)
	public String updateCart(HttpServletRequest request) {
		Long id = Long.parseLong(request.getParameter("id"));
		int qty = Integer.parseInt(request.getParameter("qty"));

		CartItem cartItem = cartItemRepository.findOne(id);
		cartItem.setQty(qty);
		cartItem.setSubTotal(
				new BigDecimal(cartItem.getProduct().getPrice() * qty).setScale(2, BigDecimal.ROUND_HALF_UP));

		cartItemRepository.save(cartItem);

		return "redirect:/stripe/";
	}

	@RequestMapping(value = "/checkoutPay", method = RequestMethod.POST)
	public String checkoutPay(HttpServletRequest request, Model model) throws AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		// Set your secret key: remember to change this to your live secret key
		// in production
		// See your keys here: https://dashboard.stripe.com/account/apikeys
		Stripe.apiKey = "sk_test_p5VUQTAeJjAbqQb6qZJBQDqu";

		// Token is created using Stripe.js or Checkout!
		// Get the payment token submitted by the form:
		String token = request.getParameter("stripeToken");

		// Charge the user's card:
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("amount", 100000);
		params.put("currency", "usd");
		params.put("description", "Stripe App Demo Charge");
		params.put("source", token);

		Charge charge = Charge.create(params);

		model.addAttribute("checkoutPaySuccess", true);

		return "forward:/stripe/";
	}

	@RequestMapping(value = "/elementsPay", method = RequestMethod.POST)
	public String elementsPay(HttpServletRequest request, Model model) throws AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		// Set your secret key: remember to change this to your live secret key
		// in production
		// See your keys here: https://dashboard.stripe.com/account/apikeys
		Stripe.apiKey = "sk_test_p5VUQTAeJjAbqQb6qZJBQDqu";

		// Token is created using Stripe.js or Checkout!
		// Get the payment token submitted by the form:
		String token = request.getParameter("stripeToken");

		// Create a Customer:
		Map<String, Object> customerParams = new HashMap<String, Object>();
		customerParams.put("email", "testcustomer@example.com");
		customerParams.put("source", token);
		Customer customer = Customer.create(customerParams);

		// Charge the Customer instead of the card:
		Map<String, Object> chargeParams = new HashMap<String, Object>();
		chargeParams.put("amount", 1000);
		chargeParams.put("currency", "usd");
		chargeParams.put("customer", customer.getId());
		Charge charge = Charge.create(chargeParams);

		// YOUR CODE: Save the customer ID and other info in a database for
		// later.

		// YOUR CODE (LATER): When it's time to charge the customer again,
		// retrieve the customer ID.
		// Map<String, Object> chargeParams = new HashMap<String, Object>();
		// chargeParams.put("amount", 1500); // $15.00 this time
		// chargeParams.put("currency", "usd");
		// chargeParams.put("customer", customerId);
		// Charge charge = Charge.create(chargeParams);

		model.addAttribute("elementsPaySuccess", true);
		return "forward:/stripe/";
	}

	@RequestMapping(value = "/paymentOrderPay", method = RequestMethod.POST)
	public String paymentOrderPay(Model model) throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		// Set your secret key: remember to change this to your live secret key
		// in production
		// See your keys here: https://dashboard.stripe.com/account/apikeys
		Stripe.apiKey = "sk_test_p5VUQTAeJjAbqQb6qZJBQDqu";

		Map<String, Object> orderParams = new HashMap<String, Object>();
		orderParams.put("currency", "usd");
		orderParams.put("email", "jenny@example.com");
		List<Object> itemsParams = new LinkedList<Object>();
		Map<String, String> item1 = new HashMap<String, String>();
		item1.put("type", "sku");
		item1.put("parent", "sku_ARefVL8oP2AMQx");
		item1.put("quantity", "2");
		itemsParams.add(item1);
		orderParams.put("items", itemsParams);
		Map<String, Object> shippingParams = new HashMap<String, Object>();
		shippingParams.put("name", "Jenny Rosen");
		Map<String, Object> addressParams = new HashMap<String, Object>();
		addressParams.put("line1", "1234 Main Street");
		addressParams.put("city", "Anytown");
		addressParams.put("country", "US");
		addressParams.put("postal_code", "123456");
		shippingParams.put("address", addressParams);
		orderParams.put("shipping", shippingParams);

		Order order = Order.create(orderParams, null);

		model.addAttribute("paymentOrderCreated", true);

		return "forward:/stripe/";
	}

	@RequestMapping(value = "/subscribe", method = RequestMethod.POST)
	public String subscribe(Model model, @RequestParam double total) throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		// Set your secret key: remember to change this to your live secret key
		// in production
		// See your keys here: https://dashboard.stripe.com/account/apikeys
		Stripe.apiKey = "sk_test_p5VUQTAeJjAbqQb6qZJBQDqu";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("customer", "cus_AcVshbUgn7U45K");
		params.put("plan", "basic-monthly");

		Subscription subscription = Subscription.create(params);

		model.addAttribute("subscriptionSuccess", true);

		return "forward:/stripe/";
	}
	
	@RequestMapping(value = "/multiplanSubscribe", method = RequestMethod.POST)
	public String multiplanSubscribe(Model model) throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		// Set your secret key: remember to change this to your live secret key in production
		// See your keys here: https://dashboard.stripe.com/account/apikeys
		Stripe.apiKey = "sk_test_p5VUQTAeJjAbqQb6qZJBQDqu";

		Map<String, Object> itemA = new HashMap<String, Object>();
		itemA.put("plan", "basic-monthly");

		Map<String, Object> itemB = new HashMap<String, Object>();
		itemB.put("plan", "additional-license");
		itemB.put("quantity", 2);

		Map<String, Object> items = new HashMap<String, Object>();
		items.put("0", itemA);
		items.put("1", itemB);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("customer", "cus_AQT83NNBQ3h5gP");
		params.put("items", items);
		Subscription.create(params);
		
		model.addAttribute("multiplePlanSubscriptionSuccess", true);

		return "forward:/stripe/";
	}
	
	@RequestMapping(value = "/discountSubscribe", method = RequestMethod.POST)
	public String discountSubscribe(Model model) throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		// Set your secret key: remember to change this to your live secret key in production
		// See your keys here: https://dashboard.stripe.com/account/apikeys
		Stripe.apiKey = "sk_test_p5VUQTAeJjAbqQb6qZJBQDqu";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("customer", "cus_AQu8qP0Yy4MgXn");
		params.put("plan", "pro-monthly");
		params.put("coupon", "free-period");

		Subscription subscription = Subscription.create(params);
		
		model.addAttribute("discountedSubscriptionSuccess", true);

		return "forward:/stripe/";
	}
	
	@RequestMapping(value = "/trialSubscribe", method = RequestMethod.POST)
	public String trialSubscribe(Model model) throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		// Set your secret key: remember to change this to your live secret key in production
		// See your keys here: https://dashboard.stripe.com/account/apikeys
		Stripe.apiKey = "sk_test_p5VUQTAeJjAbqQb6qZJBQDqu";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("customer", "cus_ARYQClbrDAPOJ3");
		params.put("plan", "basic-monthly");
		params.put("trial_period_days", 10);

		Subscription subscription = Subscription.create(params);
		
		model.addAttribute("trialSubscriptionSuccess", true);

		return "forward:/stripe/";
	}
}
