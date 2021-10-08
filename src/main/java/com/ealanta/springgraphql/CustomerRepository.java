package com.ealanta.springgraphql;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface  CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
  Flux<Customer> findByName(String name);
}
