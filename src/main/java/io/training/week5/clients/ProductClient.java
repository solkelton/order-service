package io.training.week5.clients;

import io.training.week5.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@FeignClient(name="product-service")
public interface ProductClient {

  @RequestMapping(method= RequestMethod.GET, value="/{id}")
  public Product retrieveProduct(@PathVariable("id") long id);

}
