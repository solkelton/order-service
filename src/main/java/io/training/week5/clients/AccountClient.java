package io.training.week5.clients;
import io.training.week5.model.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@FeignClient(name="account-service")
public interface AccountClient {

  @RequestMapping(method= RequestMethod.GET, value="/{id}")
  public Account retrieveAccount(@PathVariable("id") long id);
}