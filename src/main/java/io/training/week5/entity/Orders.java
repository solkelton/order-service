package io.training.week5.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.training.week5.model.Account;
import io.training.week5.model.Address;
import io.training.week5.model.Shipment;
import io.training.week5.model.ShipmentDisplay;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
//@SqlResultSetMapping(
//    name="orderMapping",
//    classes = @ConstructorResult(
//        targetClass = Orders.class,
//        columns={
////            @ColumnResult(name="id", type=Long.class),
//            @ColumnResult(name="orderDate", type=Long.class),
//            @ColumnResult(name="orderNumber", type=Long.class),
//            @ColumnResult(name="accountId", type=Long.class),
//            @ColumnResult(name="addressId", type=Long.class),
//        }))
//@NamedNativeQuery(
//    name="retrieveAccountOrders",
//    query="select order_date as orderDate, order_number as orderNumber, "
//        + "account_id as accountId, address_id as addressId "
//        + "from orders "
//        + "where account_id=?1",
//    resultSetMapping = "orderMapping"
//)
public class Orders {
  @Id
  @GeneratedValue
//  @JsonIgnore
  private long id;
//  @JoinColumn(name="accountId")
  private long accountId;
  private long orderNumber;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime orderDate;
//  @JoinColumn(name="addressId")
//  @JsonIgnore
  private long addressId;
  @OneToMany(fetch= FetchType.LAZY, cascade = CascadeType.ALL)
  private List<OrderLineItems> orderLineItemsList;
  @Transient
  private double totalPrice;
//  @Transient
//  private Account account;
  @Transient
  private Address address;

//  public Orders(long accountId, long orderNumber, LocalDateTime orderDate, long addressId,
//      List<OrderLineItems> orderLineItemsList, double totalPrice) {
//    this.accountId = accountId;
//    this.orderNumber = orderNumber;
//    this.orderDate = orderDate;
//    this.addressId = addressId;
//    this.orderLineItemsList = orderLineItemsList;
//    this.totalPrice = totalPrice;
//  }
//
  public Orders(LocalDateTime orderDate, long orderNumber, long accountId, long addressId) {
    this.orderDate = orderDate;
    this.orderNumber = orderNumber;
    this.accountId = accountId;
    this.addressId = addressId;
  }

  public Orders() {}

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @JsonIgnore
  public long getAccountId() {
    return accountId;
  }

  @JsonProperty
  public void setAccountId(long accountId) {
    this.accountId = accountId;
  }

  public long getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(long orderNumber) {
    this.orderNumber = orderNumber;
  }

  public LocalDateTime getOrderDate() { return orderDate; }

  public void setOrderDate(LocalDateTime orderDate) {
    this.orderDate = orderDate;
  }

  @JsonIgnore
  public long getAddressId() {
    return addressId;
  }

  @JsonProperty
  public void setAddressId(long addressId) { this.addressId = addressId; }

  public List<OrderLineItems> getOrderLineItemsList() {
    return orderLineItemsList;
  }

  public void setOrderLineItemsList(List<OrderLineItems> orderLineItemsList) {
    this.orderLineItemsList = orderLineItemsList;
  }
  public double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(double totalPrice) {
    this.totalPrice = totalPrice;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public void calculateTotalPrice() {
    totalPrice = 0;
    for(OrderLineItems orderLine: orderLineItemsList) {
      totalPrice += orderLine.getTotalPrice();
    }
  }


//  public Account getAccount() {
//    return account;
//  }
//
//  public void setAccount(Account account) {
//    this.account = account;
//  }

}
