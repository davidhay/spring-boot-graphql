package com.ealanta.springgraphql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerGraphqlControllerTest {

  static record Response(Map<String,List<Customer>> data) { }
  static record SingleResponse(Map<String,Customer> data) { }

  @Autowired
  WebTestClient client;

  Customer c1 = new Customer(1,"Tom");
  Customer c2 = new Customer(2,"Dick");
  Customer c3 = new Customer(3,"Harry");
  Customer c4 = new Customer(4, "Roger");
  Customer c5 = new Customer(5, "Brian");
  Customer c6 = new Customer(6, "Freddie");
  Customer c7 = new Customer(7, "John");

  @Autowired
  ObjectMapper mapper;

  @Test
  @Order(1)
  public void getBrian() throws JsonProcessingException {
    FluxExchangeResult<Response> result = client.post().uri("/graphql")
        .contentType(MediaType.APPLICATION_JSON).bodyValue("""
            {
              "query": "query{customersByName(name:\\"Brian\\"){id,name}}",
              "variables": null
            }
            """).exchange().returnResult(Response.class);
    Response response = result.getResponseBody().blockFirst();

    assertEquals(List.of(c5), response.data.get("customersByName"));
  }

  @Test
  @Order(2)
  public void getAllCustomers(){

    FluxExchangeResult<Response> result = client.post().uri("/graphql")
        .contentType(MediaType.APPLICATION_JSON).bodyValue("""
            {
              "query": "query{customers{id,name}}",
              "variables": null
            }
            """).exchange().returnResult(Response.class);

    Response response = result.getResponseBody().blockFirst();

    assertEquals(List.of(c1,c2,c3,c4,c5,c6,c7), response.data.get("customers"));
  }

  @Test
  @Order(3)
  public void addCustomer() {
    String newName = UUID.randomUUID().toString();

    FluxExchangeResult<SingleResponse> result = client.post().uri("/graphql")
        .contentType(MediaType.APPLICATION_JSON).bodyValue("""
            {
              "query": "mutation{addCustomer(name:\\"%s\\"){id name}}",
              "variables": null
            }
            """.formatted(newName)).exchange().returnResult(SingleResponse.class);

    SingleResponse response = result.getResponseBody().blockFirst();
    Customer added = response.data.get("addCustomer");
    assertEquals(8, added.id());
    assertEquals(newName, added.name());

  }

}
