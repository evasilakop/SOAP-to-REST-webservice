package com.evasilakop.portfolio.client;

import com.evasilakop.portfolio.orders.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.Scanner;

/**
 * An interactive command-line client to test the CXF SOAP web service.
 * Allows users to call service operations.
 */
public class OrderClient {

	private static final Logger log = LoggerFactory.getLogger(OrderClient.class);

	public static void main(String[] args) {
		log.info("Client starting...");
		try {
			URL wsdlUrl = new URL("http://localhost:8080/order-webservice/services/orders?wsdl");
			QName serviceQName = new QName("http://portfolio.evasilakop.com/orders", "OrderService");
			Service service = Service.create(wsdlUrl, serviceQName);
			OrderPortType orderServiceProxy = service.getPort(OrderPortType.class);
			log.info("Service proxy created successfully. Client is ready.");
			runCommandLoop(orderServiceProxy);
		} catch (Exception e) {
			log.error("A fatal error occurred during client startup.", e);
		}
		log.info("Client shutting down.");
	}

	/**
	 * Runs the main interactive loop, reading and processing user commands.
	 * @param proxy The SOAP service proxy.
	 */
	private static void runCommandLoop(OrderPortType proxy) {
		Scanner scanner = new Scanner(System.in);
		printHelp();

		while (true) {
			System.out.print("> ");
			String line = scanner.nextLine().trim();
			if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
				break;
			}
			if (line.isEmpty()) {
				continue;
			}

			String[] parts = line.split("\\s+");
			String command = parts[0].toLowerCase();

			try {
				switch (command) {
					case "get":
						getOrder(proxy, parts[1]);
						break;
					case "create":
						// Product name must be one word, or in quotes
						createOrder(proxy, parts[1], parts[2]);
						break;
					case "cancel":
						cancelOrder(proxy, parts[1]);
						break;
					case "list":
						listOrders(proxy, parts[1]);
						break;
					case "status":
						updateStatus(proxy, parts[1], parts[2]);
						break;
					case "help":
						printHelp();
						break;
					default:
						log.warn("Unknown command: '{}'. Type 'help' for available commands.", command);
						break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				log.error("Missing arguments for command '{}'. Type 'help' for usage.", command);
			} catch (Exception e) {
				log.error("An error occurred while executing command '{}': {}", command, e.getMessage());
			}
		}
	}

	private static void printHelp() {
		System.out.println("\nAvailable Commands:");
		System.out.println("  get <orderId>                     - Fetches an order by its ID.");
		System.out.println("  create <productName> <customerId> - Creates a new order. Product name must be one word or in quotes.");
		System.out.println("  cancel <orderId>                  - Cancels an order if possible.");
		System.out.println("  list <customerId>                 - Lists all orders for a customer.");
		System.out.println("  status <orderId> <NEW_STATUS>     - (Internal) Updates an order's status.");
		System.out.println("  help                              - Shows this help message.");
		System.out.println("  exit | quit                       - Shuts down the client.\n");
	}

	private static void getOrder(OrderPortType proxy, String orderId) {
		GetOrderRequest request = new GetOrderRequest();
		request.setOrderId(orderId);
		GetOrderResponse response = proxy.getOrderById(request);
		Order order = response.getOrder();
		if (order != null) {
			log.info("Response -> Order ID: {}, Product: {}, Customer: {}, Status: {}",
					order.getId(), order.getProductName(), order.getCustomerId(), order.getStatus());
		} else {
			log.warn("Response -> Order not found.");
		}
	}

	private static void createOrder(OrderPortType proxy, String productName, String customerId) {
		CreateOrderRequest request = new CreateOrderRequest();
		request.setProductName(productName);
		request.setCustomerId(customerId);
		CreateOrderResponse response = proxy.createOrder(request);
		log.info("Response -> Created order with ID: {}", response.getOrder().getId());
	}

	private static void cancelOrder(OrderPortType proxy, String orderId) {
		CancelOrderRequest request = new CancelOrderRequest();
		request.setOrderId(orderId);
		CancelOrderResponse response = proxy.cancelOrder(request);
		log.info("Response -> Cancellation success: {}", response.isSuccess());
	}

	private static void listOrders(OrderPortType proxy, String customerId) {
		ListOrdersRequest request = new ListOrdersRequest();
		request.setCustomerId(customerId);
		ListOrdersResponse response = proxy.getOrdersByCustomer(request);
		log.info("Response -> Found {} orders for customer {}:", response.getOrders().size(), customerId);
		response.getOrders().forEach(order -> log.info("  - Order ID: {}, Status: {}", order.getId(), order.getStatus()));
	}

	private static void updateStatus(OrderPortType proxy, String orderId, String newStatus) {
		UpdateStatusRequest request = new UpdateStatusRequest();
		request.setOrderId(orderId);
		request.setNewStatus(newStatus.toUpperCase());
		UpdateStatusResponse response = proxy.updateOrderStatus(request);
		log.info("Response -> Order {} status is now: {}", response.getOrder().getId(), response.getOrder().getStatus());
	}
}
