package io.training.week5.service;

import io.training.week5.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@FeignClient(name="product-service")
public interface ProductService {

  @RequestMapping(method= RequestMethod.GET, value="/products/{id}")
  public Product retrieveProduct(@PathVariable("id") long id);

}
