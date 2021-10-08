package com.ealanta.springgraphql;

import java.time.Duration;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class CustomerGraphqlController {

  private final CustomerRepository repo;

  public CustomerGraphqlController(CustomerRepository repo) {
    this.repo = repo;
  }

  @SchemaMapping(typeName = "Query", field = "customers")
  public Flux<Customer> customers() {
    return repo.findAll();
  }

  @SchemaMapping(typeName = "Query", field = "customersByName")
  public Flux<Customer> customersByName(@Argument("name") String name) {
    return repo.findByName(name);
  }

  @MutationMapping
  public Mono<Customer> addCustomer(@Argument String name) {
    Customer c = new Customer(null,name);
    return repo.save(c);
  }

  @SubscriptionMapping
  Flux<CustomerEvent> customerEvents(@Argument Integer customerId){
    Function<Customer, Flux<CustomerEvent>>  customerToStream = (customer) -> {
      var stream = Stream.generate(() -> new CustomerEvent(customer, Math.random() > .5 ? CustomerEventType.DELETED :CustomerEventType.UPDATED));
      return Flux.fromStream(stream);
    };
    return repo.findById(customerId)
        .flatMapMany(customerToStream)
        .delayElements(Duration.ofSeconds(1))
        .take(10);
  }

}
