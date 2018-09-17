package io.training.week5.controller;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.training.week5.entity.OrderLineItems;
import io.training.week5.entity.Orders;
import io.training.week5.model.Account;
import io.training.week5.model.Address;
import io.training.week5.model.OrderLineDisplay;
import io.training.week5.model.Product;
import io.training.week5.model.ShipmentDisplay;
import io.training.week5.service.OrderLineItemService;
import io.training.week5.service.OrderService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = OrdersController.class)
@AutoConfigureMockMvc(secure=false)
public class OrdersControllerTests {

  @Autowired private MockMvc mockMvc;
  @MockBean private OrderService orderService;
  @MockBean private OrderLineItemService orderLineItemService;

  private Orders order;
  private List<OrderLineItems> orderLineItemsList;
  private List<OrderLineDisplay> orderLineDisplayList;

  @Before
  public void setUpOrders() {
    orderLineItemsList = createTestingOrderLineList();
    orderLineDisplayList = createTestingOrderDisplayList();
    order = createTestingOrder();
  }

  @Test
  public void testRetrieveOrder_ValidInput_ShouldReturnFoundOrderEntry() throws Exception {
    when(orderService.retrieveOrder(1)).thenReturn(order);

    mockMvc.perform(get("/orders/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$.*", hasSize(5)))
        .andExpect(jsonPath("$.orderNumber", Matchers.is(123)))
        .andExpect(jsonPath("$.orderLineItemsList.[0].quantity", Matchers.is(5)))
        .andExpect(jsonPath("$.orderLineItemsList.[0].product.name", Matchers.is("product1")));

    verify(orderService, times(1)).retrieveOrder(1);
    verifyNoMoreInteractions(orderService);
  }

  @Test
  public void testRetrieveOrderLineItems_ValidInput_ShouldReturnArrayOfEntry() throws Exception {
    when(orderLineItemService.retrieveOrderLineItems(1)).thenReturn(orderLineItemsList);

    mockMvc.perform(get("/orders/{id}/lines",1))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$",hasSize(1)))
        .andExpect(jsonPath("$[0].shipment.shippedDate", Matchers.anything()))
        .andExpect(jsonPath("$[0].product.name", Matchers.is("product1")));

    verify(orderLineItemService, times(1)).retrieveOrderLineItems(1);
    verifyNoMoreInteractions(orderLineItemService);
  }

  @Test
  public void testRetrieveAccountOrders_ValidInput_ShouldReturnArrayOfEntry() throws Exception {
    List<Orders> ordersList = new ArrayList<Orders>() {{ add(order); }};
    when(orderService.retrieveAccountOrders(1)).thenReturn(ordersList);

    mockMvc.perform(get("/orders")
        .param("accountId","1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$[0].*", hasSize(5)))
        .andExpect(jsonPath("$[0].orderLineItemsList.[0].quantity", Matchers.is(5)))
        .andExpect(jsonPath("$[0].orderLineItemsList.[0].product.name", Matchers.is("product1")))
        .andExpect(jsonPath("$[0].orderLineItemsList.[0].shipment", Matchers.anything()));

    verify(orderService, times(1)).retrieveAccountOrders(1);
    verifyNoMoreInteractions(orderService);
  }

  @Test
  public void testRetrieveOrderNumber_ValidInput_ShouldReturnOrderNumber() throws Exception {
    long orderNumber = 123;
    when(orderService.retrieveOrderNumber(1)).thenReturn(orderNumber);

    mockMvc.perform(get("/orders/{accountId}/orderNumber", 1))
        .andExpect(status().isOk());

    verify(orderService, times(1)).retrieveOrderNumber(1);
    verifyNoMoreInteractions(orderService);
  }

  @Test
  public void testRetrieveOrderLineDisplay_ValidInput_ShouldReturnArrayOfEntry() throws Exception {
    when(orderLineItemService.retrieveOrderLineDisplay(1)).thenReturn(orderLineDisplayList);

    mockMvc.perform(get("/orders/lines/{shipmentId}", 1)
        .param("shipmentId","1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$[0].*", hasSize(3)))
        .andExpect(jsonPath("$[0].shipment.shippedDate", Matchers.anything()))
        .andExpect(jsonPath("$[0].productName", Matchers.is("product")));

    verify(orderLineItemService, times(1)).retrieveOrderLineDisplay(1);
    verifyNoMoreInteractions(orderLineItemService);
  }

  private Orders createTestingOrder() {
    Orders order = new Orders();
    order.setOrderLineItemsList(orderLineItemsList);
    order.setAddress(new Address("18","E. Elm","Chicago","IL", "60611", "U.S"));
    order.setAccountId(1);
    order.setAddressId(1);
    order.setId(1);
    order.setOrderDate(LocalDateTime.now());
    order.setOrderNumber(123);
    order.setTotalPrice(20.0);
    return order;
  }

  private List<OrderLineItems> createTestingOrderLineList() {
    OrderLineItems orderLineItems = new OrderLineItems(20.0, 1L, 5, 1L);
    orderLineItems.setShipment(new ShipmentDisplay(LocalDateTime.now(), LocalDateTime.now()));
    orderLineItems.setProduct(new Product("product1", "description", "product/image",10.0));
    List<OrderLineItems> orderLineItemsList = new ArrayList<OrderLineItems>(){{
      add(orderLineItems);
    }};
    return orderLineItemsList;
  }

  private List<OrderLineDisplay> createTestingOrderDisplayList() {
    OrderLineDisplay orderLineDisplay = new OrderLineDisplay("product", 5);
    orderLineDisplay.setShipment(new ShipmentDisplay(LocalDateTime.now(), LocalDateTime.now()));
    List<OrderLineDisplay> orderLineDisplayList = new ArrayList<OrderLineDisplay>() {{
      add(orderLineDisplay);
    }};
    return orderLineDisplayList;
  }

}
