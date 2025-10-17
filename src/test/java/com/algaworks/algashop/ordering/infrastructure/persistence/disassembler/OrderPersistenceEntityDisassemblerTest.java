package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderPersistenceEntityDisassemblerTest {

    private OrderPersistenceEntityDisassembler disassembler = new OrderPersistenceEntityDisassembler();

    @Test
    void shouldConvertFromPersistence() {
        OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();
        Order domainEntity = disassembler.toDomainEntity(persistenceEntity);
        Assertions.assertThat(domainEntity).satisfies(
                s -> Assertions.assertThat(s.id()).isEqualTo(new OrderId(persistenceEntity.getId())),
                s -> Assertions.assertThat(s.customerId()).isEqualTo(new CustomerId(persistenceEntity.getCustomerId())),
                s -> Assertions.assertThat(s.totalAmount()).isEqualTo(new Money(persistenceEntity.getTotalAmount())),
                s -> Assertions.assertThat(s.totalItems()).isEqualTo(new Quantity(persistenceEntity.getTotalItems())),
                s -> Assertions.assertThat(s.placedAt()).isEqualTo(persistenceEntity.getPlacedAt()),
                s -> Assertions.assertThat(s.paidAt()).isEqualTo(persistenceEntity.getPaidAt()),
                s -> Assertions.assertThat(s.canceledAt()).isEqualTo(persistenceEntity.getCanceledAt()),
                s -> Assertions.assertThat(s.readyAt()).isEqualTo(persistenceEntity.getReadyAt()),
                s -> Assertions.assertThat(s.status()).isEqualTo(OrderStatus.valueOf(persistenceEntity.getStatus())),
                s -> Assertions.assertThat(s.paymentMethod()).isEqualTo(PaymentMethod.valueOf(persistenceEntity.getPaymentMethod()))
        );
    }

}