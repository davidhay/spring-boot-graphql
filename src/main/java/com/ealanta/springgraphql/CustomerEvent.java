package com.ealanta.springgraphql;

public record CustomerEvent(Customer customer, CustomerEventType eventType) {

}
