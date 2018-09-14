package io.training.week5.controller;

import io.training.week5.entity.OrderLineItems;
import io.training.week5.entity.Orders;
import io.training.week5.model.OrderLineDisplay;
import io.training.week5.service.OrderLineItemService;
import io.training.week5.service.OrderService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrdersController {

  private OrderService orderService;
  private OrderLineItemService orderLineItemService;

  public OrdersController(OrderService orderService,
      OrderLineItemService orderLineItemService) {
    this.orderService = orderService;
    this.orderLineItemService = orderLineItemService;
  }

  @GetMapping("/{id}")
  public Orders retrieveOrder(@PathVariable("id") long id) {
    return orderService.retrieveOrder(id);
  }

  @GetMapping("{id}/lines")
  public List<OrderLineItems> retrieveLines(@PathVariable("id") long id) {
    return orderLineItemService.retrieveOrderLineItems(id);
  }

  @GetMapping
  public List<Orders> retrieveAccountOrders(@RequestParam("accountId") long accountId) {
    return orderService.retrieveAccountOrders(accountId);
  }

  @GetMapping("{accountId}/orderNumber")
  public long retrieveOrderNumber(@PathVariable("accountId") long accountId) {
    return orderService.retrieveOrderNumber(accountId);
  }

  @GetMapping("lines/{shipmentId}")
  public List<OrderLineDisplay> retrieveOrderLineDisplay(@PathVariable("shipmentId") long productId) {
    return orderLineItemService.retrieveOrderLineDisplay(productId);
  }
}
