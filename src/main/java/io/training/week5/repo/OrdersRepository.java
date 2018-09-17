package io.training.week5.repo;

import io.training.week5.entity.Orders;
import io.training.week5.model.OrderNumber;
import java.math.BigInteger;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

  List<Orders> getOrdersByAccountIdOrderByOrderDate(long accountId);

  @Query(value = "select order_number as orderNumber from Orders where account_id=?1", nativeQuery = true)
  List<BigInteger> retrieveOrderNumber(long accountId);




//  @Query(nativeQuery=true, name="retrieveAccountOrders")
//  List<Orders> retrieveAccountOrders(long accountId);
}
