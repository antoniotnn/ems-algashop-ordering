package com.algaworks.algashop.ordering.domain.entity;


import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void shouldGenerate() {
        Order draft = Order.draft(new CustomerId());
    }
}