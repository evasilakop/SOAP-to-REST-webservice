package com.evasilakop.portfolio.soap;

import com.evasilakop.portfolio.model.Order;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface OrderSoapService {
	@WebMethod
	Order getOrderById(String orderId); // Changed to return a full Order object

	@WebMethod
	Order createOrder(String productName); // New method to create an order
}