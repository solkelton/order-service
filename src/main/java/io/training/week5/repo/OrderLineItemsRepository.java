package io.training.week5.repo;

import io.training.week5.entity.OrderLineItems;
import io.training.week5.model.OrderLineDisplay;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderLineItemsRepository extends JpaRepository<OrderLineItems, Long> {

  List<OrderLineItems> findByOrdersId(long ordersId);

  @Query(nativeQuery = true, name="retrieveOrderLineDisplay")
  List<OrderLineDisplay> retrieveOrderLineDisplay(long ordersId);

}
