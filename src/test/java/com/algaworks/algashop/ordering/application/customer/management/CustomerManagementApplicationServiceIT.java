package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.algaworks.algashop.ordering.application.customer.query.CustomerOutput;
import com.algaworks.algashop.ordering.application.customer.query.CustomerQueryService;
import com.algaworks.algashop.ordering.domain.model.customer.*;
import com.algaworks.algashop.ordering.infrastructure.listener.customer.CustomerEventListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;


@SpringBootTest
@Transactional
class CustomerManagementApplicationServiceIT {

    @Autowired
    private CustomerManagementApplicationService customerManagementApplicationService;

    @MockitoSpyBean
    private CustomerEventListener customerEventListener;

    @MockitoSpyBean
    private CustomerNotificationApplicationService customerNotificationApplicationService;

    @Autowired
    private CustomerQueryService customerQueryService;

    @Test
    void shouldRegister() {
        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();

        UUID customerId = customerManagementApplicationService.create(input);
        Assertions.assertThat(customerId).isNotNull();

        CustomerOutput customerOutput = customerQueryService.findById(customerId);

        Assertions.assertThat(customerOutput)
                .extracting(
                        CustomerOutput::getId,
                        CustomerOutput::getFirstName,
                        CustomerOutput::getLastName,
                        CustomerOutput::getEmail,
                        CustomerOutput::getBirthDate
                ).containsExactly(
                        customerId,
                        "John",
                        "Doe",
                        "johndoe@email.com",
                        LocalDate.of(1991, 7, 5)
                );

        Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();

        Mockito.verify(customerEventListener)
                .listen(Mockito.any(CustomerRegisteredEvent.class));

        Mockito.verify(customerEventListener, Mockito.never())
                .listen(Mockito.any(CustomerArchivedEvent.class));

        Mockito.verify(customerNotificationApplicationService)
                .notifyNewRegistration(Mockito.any(CustomerNotificationApplicationService.NotifyNewRegistrationInput.class));
    }

    @Test
    void shouldUpdate() {
        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        CustomerUpdateInput updateInput = CustomerUpdateInputTestDataBuilder.aCustomerUpdate().build();

        UUID customerId = customerManagementApplicationService.create(input);
        Assertions.assertThat(customerId).isNotNull();

        customerManagementApplicationService.update(customerId, updateInput);

        CustomerOutput customerOutput = customerQueryService.findById(customerId);

        Assertions.assertThat(customerOutput)
                .extracting(
                        CustomerOutput::getId,
                        CustomerOutput::getFirstName,
                        CustomerOutput::getLastName,
                        CustomerOutput::getEmail,
                        CustomerOutput::getBirthDate
                ).containsExactly(
                        customerId,
                        "Matt",
                        "Damon",
                        "johndoe@email.com",
                        LocalDate.of(1991, 7, 5)
                );

        Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();
    }

    @Test
    void shouldArchiveCustomer() {
        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(input);
        Assertions.assertThat(customerId).isNotNull();

        customerManagementApplicationService.archive(customerId);

        CustomerOutput archivedCustomer = customerQueryService.findById(customerId);

        Assertions.assertThat(archivedCustomer)
                .isNotNull()
                .extracting(
                CustomerOutput::getFirstName,
                CustomerOutput::getLastName,
                CustomerOutput::getPhone,
                CustomerOutput::getDocument,
                CustomerOutput::getBirthDate,
                CustomerOutput::getPromotionNotificationsAllowed
        ).containsExactly(
                "Anonymous",
                "Anonymous",
                "000-000-0000",
                "000-000-0000",
                null,
                false
        );

        Assertions.assertThat(archivedCustomer.getEmail()).endsWith("@anonymous.com");
        Assertions.assertThat(archivedCustomer.getArchived()).isTrue();
        Assertions.assertThat(archivedCustomer.getArchivedAt()).isNotNull();

        Assertions.assertThat(archivedCustomer.getAddress()).isNotNull();
        Assertions.assertThat(archivedCustomer.getAddress().getNumber()).isNotNull().isEqualTo("Anonymized");
        Assertions.assertThat(archivedCustomer.getAddress().getComplement()).isNull();
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenArchivingNonExistingCustomer() {
        UUID nonExistingId = UUID.randomUUID();

        Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> customerManagementApplicationService.archive(nonExistingId));
    }

    @Test
    void shouldThrowCustomerArchivedExceptionWhenArchivingAlreadyArchivedCustomer() {
        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(input);
        Assertions.assertThat(customerId).isNotNull();

        customerManagementApplicationService.archive(customerId);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customerManagementApplicationService.archive(customerId));
    }

    @Test
    void shouldChangeEmailSuccessfully() {
        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(input);

        Assertions.assertThat(customerId).isNotNull();

        String newEmail = "newemail@email.com";

        customerManagementApplicationService.changeEmail(customerId, newEmail);
        CustomerOutput updatedCustomer = customerQueryService.findById(customerId);

        Assertions.assertThat(updatedCustomer.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenChangingEmailOfNonExistingCustomer() {
        UUID nonExistingId = UUID.randomUUID();
        String newEmail = "newemail@email.com";

        Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> customerManagementApplicationService.changeEmail(nonExistingId, newEmail));
    }

    @Test
    void shouldThrowCustomerArchivedExceptionWhenChangingEmailOfArchivedCustomer() {
        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(input);

        Assertions.assertThat(customerId).isNotNull();

        customerManagementApplicationService.archive(customerId);

        String newEmail = "newemail@email.com";

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerId, newEmail));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenChangingEmailToInvalidFormat() {
        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(input);

        Assertions.assertThat(customerId).isNotNull();

        String invalidEmail = "email-invalido";

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerId, invalidEmail));
    }

    @Test
    void shouldThrowCustomerEmailAlreadyExistsExceptionWhenChangingEmailToExistingEmail() {
        CustomerInput input1 = CustomerInputTestDataBuilder.aCustomer()
                .email("existing.email@email.com").build();
        UUID customerId1 = customerManagementApplicationService.create(input1);
        Assertions.assertThat(customerId1).isNotNull();
        CustomerInput input2 = CustomerInputTestDataBuilder.aCustomer()
                .email("existing.email2@email.com").build();
        UUID customerId2 = customerManagementApplicationService.create(input2);
        Assertions.assertThat(customerId2).isNotNull();
        Assertions.assertThatExceptionOfType(CustomerEmailIsInUseException.class)
                .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerId1, "existing.email2@email.com"));
    }

}