package com.evasilakop.portfolio.client;

import com.evasilakop.portfolio.model.Order;
import com.evasilakop.portfolio.soap.OrderSoapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

public class OrderClient {

	private static final Logger log = LoggerFactory.getLogger(OrderClient.class);

	public static void main(String[] args) {
		log.info("Client starting...");
		try {
			// The URL of the WSDL, which describes the running service.
			URL wsdlUrl = new URL("http://localhost:8080/order-service/orders?wsdl");
			QName serviceQName = new QName("https://soap.portfolio.evasilakop.com/", "OrderSoapServiceImplService");
			Service service = Service.create(wsdlUrl, serviceQName);
			// Create the service proxy. This is a local object that represents the remote service.
			OrderSoapService orderServiceProxy = service.getPort(OrderSoapService.class);

			log.info("Client: Calling getOrderById with ID '123'...");
			Order existingOrder = orderServiceProxy.getOrderById("123");
			log.info("Client Response: Found order! ID: {}, Product: {}, Status: {}",
					existingOrder.getId(), existingOrder.getProductName(), existingOrder.getStatus());

			log.info("---------------------------------");

			log.info("Client: Calling createOrder with product 'New Monitor'...");
			Order createdOrder = orderServiceProxy.createOrder("New Monitor");
			log.info("Client Response: Created new order with ID: {}", createdOrder.getId());

			log.info("Client: Verifying new order...");
			Order verifiedOrder = orderServiceProxy.getOrderById(createdOrder.getId());
			log.info("Client Response: Verified order status is: {}", verifiedOrder.getStatus());

		} catch (Exception e) {
			log.error("An error occurred while communicating with the SOAP service.", e);
		}
	}
}

