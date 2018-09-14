package io.training.week5.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.training.week5.entity.OrderLineItems;
import java.time.LocalDateTime;
import java.util.List;

public class Shipment {

  @JsonIgnore
  private Account account;
  @JsonIgnore
  private Address address;
  @JsonIgnore
  private List<OrderLineItems> orderLineItemsList;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime shippedDate;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime deliveryDate;

  public Shipment(Account account, Address address,
      List<OrderLineItems> orderLineItemsList, LocalDateTime shippedDate,
      LocalDateTime deliveryDate) {
    this.account = account;
    this.address = address;
    this.orderLineItemsList = orderLineItemsList;
    this.shippedDate = shippedDate;
    this.deliveryDate = deliveryDate;
  }

  public Shipment() {}

  public Shipment(LocalDateTime shippedDate, LocalDateTime deliveryDate) {
    this.shippedDate = shippedDate;
    this.deliveryDate = deliveryDate;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public List<OrderLineItems> getOrderLineItemsList() {
    return orderLineItemsList;
  }

  public void setOrderLineItemsList(List<OrderLineItems> orderLineItemsList) {
    this.orderLineItemsList = orderLineItemsList;
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
}
