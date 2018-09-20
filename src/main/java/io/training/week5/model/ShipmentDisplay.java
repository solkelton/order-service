package io.training.week5.model;

import java.time.LocalDateTime;
import java.util.List;

public class ShipmentDisplay {

  private long orderNumber;
  private LocalDateTime shippedDate;
  private LocalDateTime deliveryDate;
  private List<OrderLineDisplay> orderLineItems;

//  public ShipmentDisplay(long orderNumber, long accountId, long addressId,
//      LocalDateTime shippedDate, LocalDateTime deliveryDate,
//      OrderLineDisplay orderLineItems) {
//    this.orderNumber = orderNumber;
//    this.shippedDate = shippedDate;
//    this.deliveryDate = deliveryDate;
//    this.orderLineItems = orderLineItems;
//  }

  public ShipmentDisplay(LocalDateTime shippedDate, LocalDateTime deliveryDate) {
    this.shippedDate = shippedDate;
    this.deliveryDate = deliveryDate;
  }

  public ShipmentDisplay() {}

  public long getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(long orderNumber) {
    this.orderNumber = orderNumber;
  }

  public LocalDateTime getShippedDate() {
    return shippedDate;
  }

  public void setShippedDate(LocalDateTime shippedDate) {
    this.shippedDate = shippedDate;
  }

  public LocalDateTime getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(LocalDateTime deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  public List<OrderLineDisplay> getOrderLineItems() {
    return orderLineItems;
  }

  public void setOrderLineItems(List<OrderLineDisplay> orderLineItems) {
    this.orderLineItems = orderLineItems;
  }
}
