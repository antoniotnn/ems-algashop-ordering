package com.algaworks.algashop.ordering.application.customer.notification;

import java.util.UUID;

public interface CustomerNotificationApplicationService {
    void notifyNewRegistration(UUID customerId);
}
