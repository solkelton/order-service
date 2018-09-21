package io.training.week5.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.training.week5.clients.ProductClient;
import io.training.week5.clients.ShipmentClient;
import io.training.week5.entity.OrderLineItems;
import io.training.week5.entity.Orders;
import io.training.week5.model.OrderLineDisplay;
import io.training.week5.repo.OrderLineItemsRepository;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderLineItemService {

  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private OrderService orderService;

  private OrderLineItemsRepository orderLineItemsRepository;
  private ProductClient productClient;
  private ShipmentClient shipmentClient;

  public OrderLineItemService(
      OrderLineItemsRepository orderLineItemsRepository, ProductClient productClient,
      ShipmentClient shipmentClient) {
    this.orderLineItemsRepository = orderLineItemsRepository;
    this.productClient = productClient;
    this.shipmentClient = shipmentClient;
  }

  @HystrixCommand(fallbackMethod = "retrieveOrderLineItemsProductFallBack")
  public List<OrderLineItems> retrieveOrderLineItems(long orderId) {
    List<OrderLineItems> orderLineItemsList = orderLineItemsRepository.getOrderLineItemsByOrdersId(orderId);
    if(validatedOrderLineItemsList(orderLineItemsList)) {
      logger.info("Valid OrderLineItems List Found at retrieveOrderLineItems Function");
      for(OrderLineItems orderLine : orderLineItemsList) { orderLine = setValues(orderLine); }
      return orderLineItemsList;
    }
    logger.debug("Invalid Order Id at retrieveOrderLineItems Function");
    logger.debug("Order Id: {}", orderId);
    return new ArrayList<OrderLineItems>();
  }

  @HystrixCommand(fallbackMethod = "retrieveOrderLineItemsShipmentFallBack")
  public List<OrderLineItems> retrieveOrderLineItemsProductFallBack(long orderId) {
    logger.debug("ProductClient or ShipmentClient is down");
    List<OrderLineItems> orderLineItemsList = orderLineItemsRepository.getOrderLineItemsByOrdersId(orderId);
    if(validatedOrderLineItemsList(orderLineItemsList)) {
      logger.info("Valid OrderLineItems List Found at retrieveOrderLineItemsProductFallBack Function");
      for(OrderLineItems orderLine : orderLineItemsList) {
        orderLine.setProduct(productClient.retrieveProduct(orderLine.getProductId()));
        orderLine.calculateTotalPrice();
      }
      return orderLineItemsList;
    }
    logger.debug("Invalid Order Id at retrieveOrderLineItemsProductFallBack Function");
    logger.debug("Order Id: {}", orderId);
    return new ArrayList<OrderLineItems>();
  }

  @HystrixCommand(fallbackMethod = "retrieveOrderLineItemsProductShipmentFallBack")
  public List<OrderLineItems> retrieveOrderLineItemsShipmentFallBack(long orderId) {
    logger.debug("Product Client is down");
    List<OrderLineItems> orderLineItemsList = orderLineItemsRepository.getOrderLineItemsByOrdersId(orderId);
    if(validatedOrderLineItemsList(orderLineItemsList)) {
      logger.info("Valid OrderLineItems List Found at retrieveOrderLineItemsShipmentFallBack Function");
      for(OrderLineItems orderLine : orderLineItemsList) {
        orderLine.setShipment(shipmentClient.retrieveShipmentDates(orderLine.getShipmentId()));
      }
      return orderLineItemsList;
    }
    logger.debug("Invalid Order Id at retrieveOrderLineItemsShipmentFallBack Function");
    logger.debug("Order Id: {}", orderId);
    return new ArrayList<OrderLineItems>();
  }

  public List<OrderLineItems> retrieveOrderLineItemsProductShipmentFallBack(long orderId) {
    logger.debug("Product and Shipment Clients are down");
    List<OrderLineItems> orderLineItemsList = orderLineItemsRepository.getOrderLineItemsByOrdersId(orderId);
    if(validatedOrderLineItemsList(orderLineItemsList)) {
      logger.info("Valid OrderLineItems List Found at retrieveOrderLineItemsProductShipmentFallBack Function");
      return orderLineItemsList;
    }
    logger.debug("Invalid Order Id at retrieveOrderLineItemsProductShipmentFallBack Function");
    logger.debug("Order Id: {}", orderId);
    return new ArrayList<OrderLineItems>();
  }

  public List<OrderLineDisplay> retrieveOrderLineDisplay(long orderId) {
    return orderLineItemsRepository.retrieveOrderLineDisplay(orderId);
  }

  public OrderLineItems addOrderLineItem(long orderId, OrderLineItems orderLineItems) {
    Orders order = orderService.retrieveOrder(orderId);
    orderLineItems.setOrdersId(orderId);

    if(orderService.validateOrder(order) && validateOrderLineItems(orderLineItems)) {
      logger.info("Valid OrderLineItem Added");
      return orderLineItemsRepository.save(orderLineItems);
    }
    logger.debug("Invalid OrderLineItem Attempted to Add at Order Id: {}", orderId);
    logger.debug("Order Line Item: {}", orderLineItems.toString());
    return new OrderLineItems();
  }

  public OrderLineItems updateOrderLineItems(long orderId, long orderLineId, OrderLineItems updatedOrderLineItems) {
    Orders order = orderService.retrieveOrder(orderId);
    OrderLineItems originalOrderLineItems = retrieveAnOrderLineItem(orderLineId);

    if(orderService.validateOrder(order) && validateOrderLineItems(originalOrderLineItems)) {
      logger.info("Valid OrderLineItem Updated");
      OrderLineItems newOrderLineItems = update(originalOrderLineItems, updatedOrderLineItems);
      orderLineItemsRepository.save(newOrderLineItems);
      return newOrderLineItems;
    }
    logger.debug("Invalid OrderLineItem Attempted to Update at Order Id: {}, OrderLineId {}", orderId, orderLineId);
    logger.debug("Order Line Item: {}", updatedOrderLineItems.toString());
    return new OrderLineItems();
  }

  public boolean removeAnOrderLineItem(long orderId, long orderLineId) {
    OrderLineItems orderLineItems = retrieveAnOrderLineItem(orderLineId);
    if(validateOrderLineItems(orderLineItems)) {
      logger.info("Valid OrderLineItem Removed");
      orderLineItemsRepository.deleteOrderLineItemsByOrdersIdAndId(orderId, orderLineId);
      return true;
    }
    logger.debug("Invalid OrderLineItem Attempted to Remove at Order Id: {}, OrderLineId {}", orderId, orderLineId);
    return false;
  }

  public boolean removeOrderLineItems(long orderId) {
    List<OrderLineItems> orderLineItemsList = retrieveOrderLineItems(orderId);
    if(validatedOrderLineItemsList(orderLineItemsList)) {
      logger.info("Valid OrderLineItem Removed from Account");
      orderLineItemsRepository.deleteOrderLineItemsByOrdersId(orderId);
      return true;
    }
    logger.debug("Invalid OrderLineItem Attempted to Remove at Order Id: {}", orderId);
    return false;
  }

  private boolean validateOrderLineItems(OrderLineItems orderLineItems) {
    if(orderLineItems == null) return false;
    if(orderLineItems.getPrice() == 0) return false;
    if(orderLineItems.getProductId() == 0) return false;
    if(orderLineItems.getQuantity() == 0) return false;
    if(orderLineItems.getShipmentId() == 0) return false;
    if(orderLineItems.getOrdersId() == 0) return false;
    return true;
  }

  private boolean validatedOrderLineItemsList(List<OrderLineItems> orderLineItemsList) {
    for(OrderLineItems orderLineItems : orderLineItemsList) {
      if(!validateOrderLineItems(orderLineItems)) return false;
    }
    return true;
  }

  private OrderLineItems update(OrderLineItems original, OrderLineItems updated) {
    OrderLineItems newOrderLineItems = new OrderLineItems();
    newOrderLineItems.setId(original.getId());

    if(updated == null) return original;

    if(updated.getPrice() == 0) newOrderLineItems.setPrice(original.getPrice());
    else newOrderLineItems.setPrice(updated.getPrice());

    if(updated.getProductId() == 0) newOrderLineItems.setProductId(original.getProductId());
    else newOrderLineItems.setProductId(updated.getProductId());

    if(updated.getQuantity() == 0) newOrderLineItems.setQuantity(original.getQuantity());
    else newOrderLineItems.setQuantity(updated.getQuantity());

    if(updated.getShipmentId() == 0) newOrderLineItems.setShipmentId(original.getShipmentId());
    else newOrderLineItems.setShipmentId(updated.getShipmentId());

    if(updated.getOrdersId() == 0) newOrderLineItems.setOrdersId(original.getOrdersId());
    else newOrderLineItems.setOrdersId(updated.getOrdersId());

    return newOrderLineItems;
  }

  private OrderLineItems setValues(OrderLineItems orderLineItems) {
    orderLineItems.setProduct(productClient.retrieveProduct(orderLineItems.getProductId()));
//    orderLineItems.setShipment(shipmentClient.retrieveShipmentDates(orderLineItems.getShipmentId()));
    orderLineItems.calculateTotalPrice();
    return orderLineItems;
  }

  private OrderLineItems retrieveAnOrderLineItem(long orderLineId) {
    return orderLineItemsRepository.getOrderLineItemsById(orderLineId);
  }
}
