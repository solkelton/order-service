package io.training.week5.controller;

import io.training.week5.entity.OrderLineItems;
import io.training.week5.entity.Orders;
import io.training.week5.model.OrderLineDisplay;
import io.training.week5.model.OrderNumber;
import io.training.week5.service.OrderLineItemService;
import io.training.week5.service.OrderService;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class OrdersController {

  private OrderService orderService;
  private OrderLineItemService orderLineItemService;

  public OrdersController(OrderService orderService,
      OrderLineItemService orderLineItemService) {
    this.orderService = orderService;
    this.orderLineItemService = orderLineItemService;
  }

  @GetMapping("/{orderId}")
  public Orders retrieveOrder(@PathVariable("orderId") long orderId) {
    return orderService.retrieveOrder(orderId);
  }

  @GetMapping("{orderId}/lines")
  public List<OrderLineItems> retrieveLines(@PathVariable("orderId") long orderId) {
    return orderLineItemService.retrieveOrderLineItems(orderId);
  }

  @GetMapping
  public List<Orders> retrieveAccountOrders(@RequestParam("accountId") long accountId) {
    return orderService.retrieveAccountOrders(accountId);
  }

  @GetMapping("{accountId}/orderNumber")
  public List<OrderNumber> retrieveOrderNumber(@PathVariable("accountId") long accountId) {
    return orderService.retrieveOrderNumber(accountId);
  }

  @GetMapping("lines/{shipmentId}")
  public List<OrderLineDisplay> retrieveOrderLineDisplay(@PathVariable("shipmentId") long productId) {
    return orderLineItemService.retrieveOrderLineDisplay(productId);
  }

  @PostMapping("{orderId}/lines")
  public OrderLineItems addLineToOrder(@PathVariable("orderId") long orderId, @RequestBody OrderLineItems newOrderLineItems) {
    return orderLineItemService.addOrderLineItem(orderId, newOrderLineItems);
  }

  @PostMapping
  public Orders addOrder(@RequestBody Orders order) {
    return orderService.addOrder(order);
  }

  @PutMapping("/{orderId}")
  public Orders updateOrder(@PathVariable("orderId") long orderId, @RequestBody Orders order) {
    return orderService.updateOrder(orderId, order);
  }

  @PutMapping("/{orderId}/lines/{orderLineId}")
  public OrderLineItems updateOrderLineItems(@PathVariable("orderId") long orderId, @PathVariable("orderLineId") long orderLineId,
      @RequestBody OrderLineItems orderLineItems) {
    return orderLineItemService.updateOrderLineItems(orderId, orderLineId, orderLineItems);
  }

  @Transactional
  @DeleteMapping("/{orderId}")
  public boolean removeOrder(@PathVariable("orderId") long orderId) {
    return orderService.removeOrder(orderId);
  }

  @Transactional
  @DeleteMapping("/{orderId}/lines/{orderLineId}")
  public boolean removeAnOrderLineItem(@PathVariable("orderId") long orderId, @PathVariable("orderLineId") long orderLineId) {
    return orderLineItemService.removeAnOrderLineItem(orderId, orderLineId);
  }

  @Transactional
  @DeleteMapping("/{orderId}/lines")
  public boolean removeOrderLineItems(@PathVariable("orderId") long orderId) {
    return orderLineItemService.removeOrderLineItems(orderId);
  }

}
