package com.evasilakop.portfolio.soap;

import com.evasilakop.portfolio.orders.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of the OrderPortType SOAP service.
 * This class handles the core order lifecycle logic using a thread-safe in-memory repository.
 */
@WebService(
		endpointInterface = "com.evasilakop.portfolio.orders.OrderPortType",
		serviceName = "OrderService",
		portName = "OrderPort",
		targetNamespace = "http://portfolio.evasilakop.com/orders",
		wsdlLocation = "wsdl/orders.wsdl"
)
public class OrderSoapServiceImpl implements OrderPortType {

	private static final Logger log = LoggerFactory.getLogger(OrderSoapServiceImpl.class);

	/**
	 * In-memory storage for orders, simulating a legacy database.
	 */
	private static final Map<String, Order> repository = new ConcurrentHashMap<>();

	static {
		// 1. A Shipped order (Cannot be cancelled)
		Order o1 = new Order();
		o1.setId("123");
		o1.setCustomerId("user-001");
		o1.setProductName("Legacy Laptop");
		o1.setStatus("SHIPPED");
		repository.put(o1.getId(), o1);

		// 2. A New order (Can be cancelled or updated)
		Order o2 = new Order();
		o2.setId("456");
		o2.setCustomerId("user-001");
		o2.setProductName("Mechanical Keyboard");
		o2.setStatus("CREATED");
		repository.put(o2.getId(), o2);

		// 3. A Delivered order for a different user
		Order o3 = new Order();
		o3.setId("789");
		o3.setCustomerId("user-002");
		o3.setProductName("Ergonomic Mouse");
		o3.setStatus("DELIVERED");
		repository.put(o3.getId(), o3);

		// 4. A Pending order for user-001
		Order o4 = new Order();
		o4.setId("999");
		o4.setCustomerId("user-001");
		o4.setProductName("UltraWide Monitor");
		o4.setStatus("PENDING");
		repository.put(o4.getId(), o4);

		// 5. An already Cancelled order
		Order o5 = new Order();
		o5.setId("000");
		o5.setCustomerId("user-003");
		o5.setProductName("Broken Headset");
		o5.setStatus("CANCELLED");
		repository.put(o5.getId(), o5);

		log.info("SOAP Service Initialized. Pre-loaded {} sample orders across multiple customers.", repository.size());
	}

	/**
	 * Creates a new order in the system with an initial status of 'CREATED'.
	 *
	 * @param parameters Contains the productName and customerId for the new order.
	 * @return CreateOrderResponse containing the newly created Order object with its generated ID.
	 */
	@Override
	public CreateOrderResponse createOrder(CreateOrderRequest parameters) {
		log.info("SOAP: Creating order for customer {}", parameters.getCustomerId());
		Order order = new Order();
		order.setId(UUID.randomUUID().toString().substring(0, 8));
		order.setCustomerId(parameters.getCustomerId());
		order.setProductName(parameters.getProductName());
		order.setStatus("CREATED");

		repository.put(order.getId(), order);

		CreateOrderResponse response = new CreateOrderResponse();
		response.setOrder(order);
		return response;
	}

	/**
	 * Retrieves the details of a specific order by its unique identifier.
	 *
	 * @param parameters Contains the orderId to search for.
	 * @return GetOrderResponse containing the Order if found, or null if no match exists.
	 */
	@Override
	public GetOrderResponse getOrderById(GetOrderRequest parameters) {
		log.info("SOAP: Fetching order {}", parameters.getOrderId());
		Order order = repository.get(parameters.getOrderId());

		GetOrderResponse response = new GetOrderResponse();
		response.setOrder(order);
		return response;
	}

	/**
	 * Attempts to cancel an existing order.
	 * Cancellation is only permitted if the order has not yet been 'SHIPPED' or 'DELIVERED'.
	 *
	 * @param parameters Contains the orderId of the order to be cancelled.
	 * @return CancelOrderResponse with success set to true if cancelled, false otherwise.
	 */
	@Override
	public CancelOrderResponse cancelOrder(CancelOrderRequest parameters) {
		log.info("SOAP: Cancelling order {}", parameters.getOrderId());
		Order order = repository.get(parameters.getOrderId());
		CancelOrderResponse response = new CancelOrderResponse();

		if (order != null && !"SHIPPED".equals(order.getStatus()) && !"DELIVERED".equals(order.getStatus())) {
			order.setStatus("CANCELLED");
			response.setSuccess(true);
			log.info("Order {} successfully cancelled.", parameters.getOrderId());
		} else {
			response.setSuccess(false);
			log.warn("Cancellation rejected for order {}. Current status: {}",
					parameters.getOrderId(), (order != null ? order.getStatus() : "NOT_FOUND"));
		}
		return response;
	}

	/**
	 * Updates the delivery/product details for an order.
	 * In this implementation, updates are appended to the product name field.
	 *
	 * @param parameters Contains the orderId and the newDetails string to append.
	 * @return UpdateDeliveryResponse containing the updated Order object.
	 */
	@Override
	public UpdateDeliveryResponse updateDeliveryDetails(UpdateDeliveryRequest parameters) {
		log.info("SOAP: Updating delivery for order {}", parameters.getOrderId());
		Order order = repository.get(parameters.getOrderId());
		if (order != null) {
			order.setProductName(order.getProductName() + " [Updated: " + parameters.getNewDetails() + "]");
		}

		UpdateDeliveryResponse response = new UpdateDeliveryResponse();
		response.setOrder(order);
		return response;
	}

	/**
	 * Retrieves all orders associated with a specific customer ID.
	 *
	 * @param parameters Contains the customerId to filter by.
	 * @return ListOrdersResponse containing a list of orders (empty list if none found).
	 */
	@Override
	public ListOrdersResponse getOrdersByCustomer(ListOrdersRequest parameters) {
		log.info("SOAP: Listing orders for customer {}", parameters.getCustomerId());
		ListOrdersResponse response = new ListOrdersResponse();

		response.getOrders().addAll(
				repository.values().stream()
						.filter(o -> parameters.getCustomerId().equals(o.getCustomerId()))
						.collect(Collectors.toList())
		);
		return response;
	}

	/**
	 * Performs an internal system update of an order's status.
	 * This method bypasses standard user business rules to allow lifecycle progression.
	 *
	 * @param parameters Contains the orderId and the newStatus (e.g., 'SHIPPED', 'DELIVERED').
	 * @return UpdateStatusResponse containing the updated Order object.
	 */
	@Override
	public UpdateStatusResponse updateOrderStatus(UpdateStatusRequest parameters) {
		log.info("SOAP: Internal status update for order {} to {}", parameters.getOrderId(), parameters.getNewStatus());
		Order order = repository.get(parameters.getOrderId());
		if (order != null) {
			order.setStatus(parameters.getNewStatus());
		}

		UpdateStatusResponse response = new UpdateStatusResponse();
		response.setOrder(order);
		return response;
	}
}