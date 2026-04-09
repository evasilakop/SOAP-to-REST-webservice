package com.evasilakop.portfolio.soap;

import com.evasilakop.portfolio.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@WebService(endpointInterface = "com.evasilakop.portfolio.soap.OrderSoapService")
public class OrderSoapServiceImpl implements OrderSoapService {

	private static final Logger log = LoggerFactory.getLogger(OrderSoapServiceImpl.class);
	private static final Map<String, Order> orderRepository = new ConcurrentHashMap<>();

	// To pre-load dummy data when the class is loaded by the server
	static {
		Order sampleOrder = new Order("123", "Legacy Laptop", "SHIPPED");
		orderRepository.put(sampleOrder.getId(), sampleOrder);
		log.info("SOAP Service Initialized. Pre-loaded 1 sample order.");
	}

	@Override
	public Order getOrderById(String orderId) {
		log.info("SOAP Service: Processing getOrderById for ID: {}", orderId);
		return orderRepository.get(orderId);
	}

	@Override
	public Order createOrder(String productName) {
		log.info("SOAP Service: Processing createOrder for product: {}", productName);
		String newId = UUID.randomUUID().toString().substring(0, 8); // Shorter ID for readability
		Order newOrder = new Order(newId, productName, "PENDING");
		orderRepository.put(newId, newOrder);
		log.info("Successfully created new order with ID: {}", newId);
		return newOrder;
	}
}
