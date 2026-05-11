package com.algaworks.algashop.ordering.infrastructure.adapters.in.listener.customer;

import java.util.UUID;

public interface ForConfirmCustomerRegistration {
    void confirm(UUID customerId);
}
