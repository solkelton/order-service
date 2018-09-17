package io.training.week5.service;

import io.training.week5.entity.OrderLineItems;
import io.training.week5.model.OrderLineDisplay;
import io.training.week5.repo.OrderLineItemsRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderLineItemService {

  private OrderLineItemsRepository orderLineItemsRepository;
  @Autowired private ProductService productService;
  @Autowired private ShipmentService shipmentService;

  public OrderLineItemService(
      OrderLineItemsRepository orderLineItemsRepository) {
    this.orderLineItemsRepository = orderLineItemsRepository;
  }
  public List<OrderLineItems> retrieveOrderLineItems(long orderId) {
    List<OrderLineItems> orderLineItemsList = orderLineItemsRepository.findByOrdersId(orderId);
    for (OrderLineItems orderLine : orderLineItemsList) {
      orderLine.setProduct(productService.retrieveProduct(orderLine.getProductId()));
      orderLine.setShipment(shipmentService.retrieveShipmentDates(orderLine.getShipmentId()));
      orderLine.calculateTotalPrice();
    }
    return orderLineItemsList;
  }

  public List<OrderLineDisplay> retrieveOrderLineDisplay(long orderId) {
    return orderLineItemsRepository.retrieveOrderLineDisplay(orderId);
  }

  public void addOrderLineItem(long orderId, OrderLineItems orderLineItems) {
    orderLineItems.setOrdersId(orderId);
    orderLineItemsRepository.save(orderLineItems);
  }

}
