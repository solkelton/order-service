package io.training.week5.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.training.week5.clients.AccountClient;
import io.training.week5.clients.AddressClient;
import io.training.week5.clients.ShipmentClient;
import io.training.week5.model.Account;
import io.training.week5.model.Address;
import io.training.week5.entity.OrderLineItems;
import io.training.week5.entity.Orders;
import io.training.week5.model.OrderNumber;
import io.training.week5.model.ShipmentDisplay;
import io.training.week5.repo.OrdersRepository;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  private OrdersRepository ordersRepository;
  private AccountClient accountClient;
  private AddressClient addressClient;
  private OrderLineItemService orderLineItemService;
  private ShipmentClient shipmentClient;

  public OrderService(OrdersRepository ordersRepository,
      AccountClient accountClient, AddressClient addressClient,
      OrderLineItemService orderLineItemService,
      ShipmentClient shipmentClient) {
    this.ordersRepository = ordersRepository;
    this.accountClient = accountClient;
    this.addressClient = addressClient;
    this.orderLineItemService = orderLineItemService;
    this.shipmentClient = shipmentClient;
  }

  public Orders retrieveOrder(long id) {
    Orders order = ordersRepository.getOrdersById(id);

    if(validateOrder(order)) {
      logger.info("Valid Order Found at retrieveOrder Function");
      order.setAddress(retrieveAddress(order.getAccountId(), order.getAddressId()));
      order.setOrderLineItemsList(retrieveOrderLine(order.getId()));
      order.setShipments(retrieveShipmentDisplayList(order.getOrderLineItemsList()));
      order.calculateTotalPrice();
      return order;
    }
    logger.debug("Invalid Order Id at retrieveOrder Function");
    logger.debug("Order Id: {}", id);
    return new Orders();
  }

  @HystrixCommand(fallbackMethod = "retrieveAccountOrdersFallBack")
  public List<Orders> retrieveAccountOrders(long accountId) {
    List<Orders> ordersList = ordersRepository.getOrdersByAccountIdOrderByOrderDate(accountId);
    if(ordersList.size() > 0) {
      logger.info("Valid Orders List Found at retrieveAccountOrders");
      for(Orders order: ordersList) {
        order.setAddress(retrieveAddress(order.getAccountId(), order.getAddressId()));
        order.setOrderLineItemsList(retrieveOrderLine(order.getId()));
        order.calculateTotalPrice();
      }
      return ordersList;
    }
    logger.debug("Invalid Order Id Or No Orders Associated with Account Id");
    logger.debug("AccountId: {}", accountId);
    return ordersList;
  }

  public List<Orders> retrieveAccountOrdersFallBack(long accountId) {
    List<Orders> ordersList = ordersRepository.getOrdersByAccountIdOrderByOrderDate(accountId);
    if(ordersList.size() > 0) {
      logger.info("Valid Orders List Found at retrieveAccountOrdersFallBack");
      for(Orders order : ordersList) {
        order.setOrderLineItemsList(retrieveOrderLine(order.getId()));
        order.calculateTotalPrice();
      }
      return ordersList;
    }
    logger.debug("Invalid Order Id Or No Orders Associated with Account Id");
    logger.debug("AccountId: {}", accountId);
    return ordersList;
  }

  public List<OrderNumber> retrieveOrderNumber(long accountId) {
    List<BigInteger> longList = ordersRepository.retrieveOrderNumber(accountId);
    List<OrderNumber> orderNumberList = new ArrayList<>();
    for(BigInteger orderNum : longList) {
      orderNumberList.add(new OrderNumber(orderNum));
    }
    return orderNumberList;
  }

  public Orders addOrder(Orders order) {
   if(validateOrder(order)) {
     logger.info("Valid Order Added");
    return ordersRepository.save(order);
   }
   logger.debug("Invalid Order Attempted to Add");
   logger.debug("Order: {}", order.toString());
   return new Orders();
  }

  public Orders updateOrder(long id, Orders updatedOrder) {
    Orders originalOrder = retrieveOrder(id);
    if(validateOrder(originalOrder)) {
      logger.info("Valid Order Updated");
      Orders newOrder = update(originalOrder, updatedOrder);
      ordersRepository.save(newOrder);
      return newOrder;
    }
    logger.debug("Invalid Order Attempted to Update at Order Id {}",id);
    logger.debug("Order: {}", updatedOrder.toString());
    return new Orders();
  }

  public boolean removeOrder(long id) {
    Orders order = retrieveOrder(id);
    if(validateOrder(order)) {
      logger.info("Valid Order Removed");
      ordersRepository.deleteOrdersById(id);
      return true;
    }
    logger.debug("Invalid Order Attempted to Remove at Order Id {}", id);
    return false;
  }

  protected boolean validateOrder(Orders order) {
    if(order == null) return false;
    if(order.getAccountId() == 0) return false;
    if(order.getAddressId() == 0) return false;
    if(order.getOrderDate() == null) return false;
    if(order.getOrderNumber() == 0) return false;
    return true;
  }

  private Orders update(Orders original, Orders updated) {
    Orders newOrders = new Orders();
    newOrders.setId(original.getId());

    if(updated == null) return original;

    if(updated.getAccountId() == 0) newOrders.setAccountId(original.getAccountId());
    else newOrders.setAccountId(updated.getAccountId());

    if(updated.getAddressId() == 0) newOrders.setAddressId(original.getAddressId());
    else newOrders.setAddressId(updated.getAddressId());

    if(updated.getOrderDate() == null) newOrders.setOrderDate(original.getOrderDate());
    else newOrders.setOrderDate(updated.getOrderDate());

    if(updated.getOrderNumber() == 0) newOrders.setOrderNumber(original.getOrderNumber());
    else newOrders.setOrderNumber(updated.getOrderNumber());

    return newOrders;
  }

  private List<ShipmentDisplay> retrieveShipmentDisplayList(List<OrderLineItems> orderLineItemsList) {
    return new ArrayList<ShipmentDisplay>(){{
      for(OrderLineItems orderLineItems : orderLineItemsList) {
        add(retrieveShipment(orderLineItems.getShipmentId()));
      }
    }};
  }

  private Address retrieveAddress(long accountId, long addressId) { return addressClient
      .retrieveAddress(accountId, addressId); }
  private Account retrieveAccount(long accountId) { return accountClient.retrieveAccount(accountId); }
  private List<OrderLineItems> retrieveOrderLine(long orderId) { return orderLineItemService.retrieveOrderLineItems(orderId); }
  private ShipmentDisplay retrieveShipment(long shipmentId) {return shipmentClient.retrieveShipmentDates(shipmentId); }

}
