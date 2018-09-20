package io.training.week5.service;

import io.training.week5.entity.OrderLineItems;
import io.training.week5.entity.Orders;
import io.training.week5.model.OrderLineDisplay;
import io.training.week5.repo.OrderLineItemsRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderLineItemService {

  private OrderLineItemsRepository orderLineItemsRepository;
  private OrderService orderService;
  @Autowired private ProductService productService;
  @Autowired private ShipmentService shipmentService;

  public OrderLineItemService(
      OrderLineItemsRepository orderLineItemsRepository, OrderService orderService) {
    this.orderLineItemsRepository = orderLineItemsRepository;
    this.orderService = orderService;
  }
  public List<OrderLineItems> retrieveOrderLineItems(long orderId) {
    List<OrderLineItems> orderLineItemsList = orderLineItemsRepository.getOrderLineItemsByOrdersId(orderId);

    if(validatedOrderLineItemsList(orderLineItemsList)) {
      for(OrderLineItems orderLine : orderLineItemsList) { orderLine = setValues(orderLine); }
      return orderLineItemsList;
    }
    return new ArrayList<OrderLineItems>() {{ add(new OrderLineItems()); }};
  }

  public List<OrderLineDisplay> retrieveOrderLineDisplay(long orderId) {
    return orderLineItemsRepository.retrieveOrderLineDisplay(orderId);
  }

  public OrderLineItems addOrderLineItem(long orderId, OrderLineItems orderLineItems) {
    Orders order = orderService.retrieveOrder(orderId);
    orderLineItems.setOrdersId(orderId);

    if(orderService.validateOrder(order) && validateOrderLineItems(orderLineItems)) {
      return orderLineItemsRepository.save(orderLineItems);
    }
    return new OrderLineItems();
  }

  public OrderLineItems updateOrderLineItems(long orderId, long orderLineId, OrderLineItems updatedOrderLineItems) {
    Orders order = orderService.retrieveOrder(orderId);
    OrderLineItems originalOrderLineItems = retrieveAnOrderLineItem(orderLineId);

    if(orderService.validateOrder(order) && validateOrderLineItems(originalOrderLineItems)) {
      OrderLineItems newOrderLineItems = update(originalOrderLineItems, updatedOrderLineItems);
      orderLineItemsRepository.save(newOrderLineItems);
      return newOrderLineItems;
    }
    return new OrderLineItems();
  }

  public boolean removeAnOrderLineItem(long orderId, long orderLineId) {
    OrderLineItems orderLineItems = retrieveAnOrderLineItem(orderLineId);
    if(validateOrderLineItems(orderLineItems)) {
      orderLineItemsRepository.deleteOrderLineItemsByOrdersIdAndId(orderId, orderLineId);
      return true;
    }
    return false;
  }

  public boolean removeOrderLineItems(long orderId) {
    List<OrderLineItems> orderLineItemsList = retrieveOrderLineItems(orderId);
    if(validatedOrderLineItemsList(orderLineItemsList)) {
      orderLineItemsRepository.deleteOrderLineItemsByOrdersId(orderId);
      return true;
    }
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
    orderLineItems.setProduct(productService.retrieveProduct(orderLineItems.getProductId()));
    orderLineItems.setShipment(shipmentService.retrieveShipmentDates(orderLineItems.getShipmentId()));
    orderLineItems.calculateTotalPrice();
    return orderLineItems;
  }

  private OrderLineItems retrieveAnOrderLineItem(long orderLineId) {
    return orderLineItemsRepository.getOrderLineItemsById(orderLineId);
  }
}
