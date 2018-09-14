package io.training.week5.service;

import com.netflix.discovery.converters.Auto;
import io.training.week5.model.Account;
import io.training.week5.model.Address;
import io.training.week5.entity.OrderLineItems;
import io.training.week5.entity.Orders;
import io.training.week5.model.Shipment;
import io.training.week5.repo.OrdersRepository;
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
    Orders order = new Orders();
    Optional<Orders> query = ordersRepository.findById(id);
    if(query.isPresent()) {
      order = query.get();
      order.setAddress(retrieveAddress(order.getAccountId(), order.getAddressId()));
      order.setOrderLineItemsList(retrieveOrderLine(order.getId()));
      order.calculateTotalPrice();
    }
    return order;
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

  public long retrieveOrderNumber(long accountId) { return ordersRepository.retrieveOrderNumber(accountId);}

  private Address retrieveAddress(long accountId, long addressId) { return addressService.retrieveAddress(accountId, addressId); }
  private Account retrieveAccount(long accountId) { return accountService.retrieveAccount(accountId); }
  private List<OrderLineItems> retrieveOrderLine(long orderId) { return orderLineItemService.retrieveOrderLineItems(orderId); }

}
