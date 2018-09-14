package io.training.week5.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.training.week5.model.OrderLineDisplay;
import io.training.week5.model.Product;
import io.training.week5.model.Shipment;
import io.training.week5.model.ShipmentDisplay;
import javax.persistence.CascadeType;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Transient;

@Entity
@SqlResultSetMapping(
    name="orderLineMapping",
    classes=@ConstructorResult(
        targetClass = OrderLineDisplay.class,
        columns={
            @ColumnResult(name="productName", type=String.class),
            @ColumnResult(name="quantity", type=Integer.class),
        }))
@NamedNativeQuery(
    name="retrieveOrderLineDisplay",
    query="select q1.quantity, q2.name as productName "
        + "from order_line_items q1, product q2 "
        + "where q1.shipment_id=1 and q1.product_id in (select q2.id from product where q1.product_id=q2.id)",
    resultSetMapping = "orderLineMapping"
)
public class OrderLineItems {
  @Id
  @GeneratedValue
  @JsonIgnore
  private long id;
  @JoinColumn(name="productId")
  @JsonIgnore
  private long productId;
  private int quantity;
  private double price;
  @Transient
  private double totalPrice;
  @JoinColumn(name="shipmentId")
  @JsonIgnore
  private long shipmentId;
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name="ordersId")
  @JsonIgnore
  private Orders orders;

  @Transient
  private Product product;

  @Transient
  private ShipmentDisplay shipment;


  public OrderLineItems(long productId, int quantity, double price,
      long shipmentId) {
    this.productId = productId;
    this.quantity = quantity;
    this.price = price;
    this.totalPrice = 0;
    this.shipmentId = shipmentId;
    this.orders = null;
  }

  public OrderLineItems(double price, long productId, int quantity, long shipmentId) {
    this.price = price;
    this.productId = productId;
    this.quantity = quantity;
    this.shipmentId = shipmentId;
  }

  public OrderLineItems() {}

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getProductId() {
    return productId;
  }

  public void setProductId(long productId) {
    this.productId = productId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(double totalPrice) {
    this.totalPrice = totalPrice;
  }

  public long getShipmentId() {
    return shipmentId;
  }

  public void setShipmentId(long shipmentId) {
    this.shipmentId = shipmentId;
  }

  public Orders getOrders() {
    return orders;
  }

  public void setOrders(Orders orders) {
    this.orders = orders;
  }

  public ShipmentDisplay getShipment() {
    return shipment;
  }

  public void setShipment(ShipmentDisplay shipment) {
    this.shipment = shipment;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public void calculateTotalPrice() {
    totalPrice = price * quantity;
  }
}
