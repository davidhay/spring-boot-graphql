package com.ealanta.springgraphql;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class CustomerController {

  private final CustomerRepository repo;

  public CustomerController(
      CustomerRepository repo) {
    this.repo = repo;
  }

  @RequestMapping("/customers")
  Flux<Customer> customers(){
    return repo.findAll();
  }

}
