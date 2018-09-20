package io.training.week5.repo;

import io.training.week5.entity.OrderLineItems;
import io.training.week5.model.OrderLineDisplay;
import java.util.List;
import javax.validation.constraints.Null;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

public interface OrderLineItemsRepository extends JpaRepository<OrderLineItems, Long> {

//  @Nullable
//  List<OrderLineItems> findByOrdersId(long ordersId);

  @Nullable
  List<OrderLineItems> getOrderLineItemsByOrdersId(long ordersId);

  @Nullable
  OrderLineItems getOrderLineItemsById(long orderLineId);

  void deleteOrderLineItemsByOrdersIdAndId(long orderId, long orderLineId);

  void deleteOrderLineItemsByOrdersId(long ordersId);

  @Query(nativeQuery = true, name="retrieveOrderLineDisplay")
  List<OrderLineDisplay> retrieveOrderLineDisplay(long ordersId);

}
