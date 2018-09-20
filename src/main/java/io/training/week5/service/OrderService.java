package io.training.week5.service;

import io.training.week5.model.Account;
import io.training.week5.model.Address;
import io.training.week5.entity.OrderLineItems;
import io.training.week5.entity.Orders;
import io.training.week5.model.OrderNumber;
import io.training.week5.repo.OrdersRepository;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  private OrdersRepository ordersRepository;
  @Autowired private AccountService accountService;
  @Autowired private AddressService addressService;
  @Autowired private OrderLineItemService orderLineItemService;
  @Autowired private ShipmentService shipmentService;

  public OrderService(OrdersRepository ordersRepository) {
    this.ordersRepository = ordersRepository;
  }

  public Orders retrieveOrder(long id) {
    Orders order = ordersRepository.getOrdersById(id);

    if(validateOrder(order)) {
      order.setAddress(retrieveAddress(order.getAccountId(), order.getAddressId()));
      order.setOrderLineItemsList(retrieveOrderLine(order.getId()));
      order.calculateTotalPrice();
      return order;
    }
    return new Orders();
  }

  public List<Orders> retrieveAccountOrders(long accountId) {
    List<Orders> ordersList = ordersRepository.getOrdersByAccountIdOrderByOrderDate(accountId);
    if(ordersList.size() > 0) {
      for(Orders order: ordersList) {
        order.setAddress(retrieveAddress(order.getAccountId(), order.getAddressId()));
        order.setOrderLineItemsList(retrieveOrderLine(order.getId()));
//        order.setShipment(shipmentService.retrieveShipmentDates(orderLine.getShipmentId()));
        order.calculateTotalPrice();
      }
    }
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
    return ordersRepository.save(order);
   }
   return new Orders();
  }

  public Orders updateOrder(long id, Orders updatedOrder) {
    Orders originalOrder = retrieveOrder(id);
    if(validateOrder(originalOrder)) {
      Orders newOrder = update(originalOrder, updatedOrder);
      ordersRepository.save(newOrder);
      return newOrder;
    }
    return new Orders();
  }

  public boolean removeOrder(long id) {
    Orders order = retrieveOrder(id);
    if(validateOrder(order)) {
      ordersRepository.deleteOrdersById(id);
      return true;
    }
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

  private Address retrieveAddress(long accountId, long addressId) { return addressService.retrieveAddress(accountId, addressId); }
  private Account retrieveAccount(long accountId) { return accountService.retrieveAccount(accountId); }
  private List<OrderLineItems> retrieveOrderLine(long orderId) { return orderLineItemService.retrieveOrderLineItems(orderId); }

}
