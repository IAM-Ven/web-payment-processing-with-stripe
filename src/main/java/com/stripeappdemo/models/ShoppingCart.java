package com.stripeappdemo.models;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class ShoppingCart {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private BigDecimal GrandTotal;
	
	
	@OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	private List<CartItem> cartItemList;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public BigDecimal getGrandTotal() {
		return GrandTotal;
	}


	public void setGrandTotal(BigDecimal grandTotal) {
		GrandTotal = grandTotal;
	}


	public List<CartItem> getCartItemList() {
		return cartItemList;
	}


	public void setCartItemList(List<CartItem> cartItemList) {
		this.cartItemList = cartItemList;
	}
	
	
	
}
