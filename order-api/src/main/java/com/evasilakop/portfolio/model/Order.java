package com.evasilakop.portfolio.model;

public class Order {
	private String id;
	private String productName;
	private String status;

	// JAX-WS requires a no-arg constructor
	public Order() {}

	public Order(String id, String productName, String status) {
		this.id = id;
		this.productName = productName;
		this.status = status;
	}

	public String getId() { return id; }

	public void setId(String id) { this.id = id; }

	public String getProductName() { return productName; }

	public void setProductName(String productName) { this.productName = productName; }

	public String getStatus() { return status; }

	public void setStatus(String status) { this.status = status; }
}
