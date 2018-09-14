package io.training.week5.service;

import io.training.week5.model.Shipment;
import io.training.week5.model.ShipmentDisplay;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@FeignClient(name="shipment-service")
public interface ShipmentService {

  @RequestMapping(method= RequestMethod.GET, value="shipments/{id}")
  public Shipment retrieveShipment(@PathVariable("id") long id);

  @RequestMapping(method= RequestMethod.GET, value="shipments/{id}/dates")
  public ShipmentDisplay retrieveShipmentDates(@PathVariable("id") long id);


}
