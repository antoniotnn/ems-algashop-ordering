package com.algaworks.algashop.ordering;

import com.algaworks.algashop.ordering.domain.entity.Customer;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class CustomerTest {

    @Test
    void testingCustomer() {
        Customer customer = new Customer();
        customer.setId(null);
        customer.setFullName("Alex Silva");
        customer.setDocument(null);
        customer.setLoyaltyPoints(10);
    }
}
