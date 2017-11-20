package ru.nsu.fit.endpoint.service.manager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.slf4j.Logger;
import ru.nsu.fit.endpoint.service.database.DBService;
import ru.nsu.fit.endpoint.service.database.data.Customer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.internal.verification.VerificationModeFactory.times;

public class CustomerManagerTest {
    private DBService dbService;
    private Logger logger;
    private CustomerManager customerManager;

    private Customer customerBeforeCreateMethod;
    private Customer customerAfterCreateMethod;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void before() {
        // create stubs for the test's class
        dbService = Mockito.mock(DBService.class);
        logger = Mockito.mock(Logger.class);

        customerBeforeCreateMethod = new Customer()
                .setId(null)
                .setFirstName("John")
                .setLastName("Wick")
                .setLogin("john_wick@gmail.com")
                .setPass("Baba_Jaga")
                .setBalance(0);
        customerAfterCreateMethod = customerBeforeCreateMethod.clone();
        customerAfterCreateMethod.setId(UUID.randomUUID());

        Mockito.when(dbService.createCustomer(customerBeforeCreateMethod)).thenReturn(customerAfterCreateMethod);

        // create the test's class
        customerManager = new CustomerManager(dbService, logger);
    }

    @Test
    public void testCreateNewCustomer() {
        // Вызываем метод, который хотим протестировать
        Customer customer = customerManager.createCustomer(customerBeforeCreateMethod);

        // Проверяем результат выполенния метода
        Assert.assertEquals(customer.getId(), customerAfterCreateMethod.getId());

        // Проверяем, что метод мока базы данных был вызван 1 раз
        Mockito.verify(dbService, times(1)).createCustomer(Mockito.any(Customer.class));
        //Assert.assertEquals(1, Mockito.mockingDetails(dbService).getInvocations().size());
    }

    @Test
    public void testCreateCustomerWithNullArgument() {
        try {
            customerManager.createCustomer(null);
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("Argument 'customerData' is null.", ex.getMessage());
        }
    }

    @Test
    public void testCreateCustomerWithEasyPassword() {
        try {
            customerBeforeCreateMethod.setPass("123qwe");
            customerManager.createCustomer(customerBeforeCreateMethod);

            //TODO: fail();
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("Password is easy.", ex.getMessage());
        }
    }

    @Test
    public void testCreateCustomerWithUpperCaseLastName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Last name is not a valid name");

        customerBeforeCreateMethod.setLastName("ANTONOV");
        customerManager.createCustomer(customerBeforeCreateMethod);
    }

    @Test
    public void testCreateCustomerWithNumberInLastName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Last name is not a valid name");

        customerBeforeCreateMethod.setLastName("An1onov");
        customerManager.createCustomer(customerBeforeCreateMethod);
    }

    @Test
    public void testCreateCustomerWithLowercaseLastName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Last name is not a valid name");

        customerBeforeCreateMethod.setLastName("antonov");
        customerManager.createCustomer(customerBeforeCreateMethod);
    }

    @Test
    public void testCreateCustomerWithLongLastName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Last name's length must be in range from 2 to 12");

        customerBeforeCreateMethod.setLastName("Antononononov");
        customerManager.createCustomer(customerBeforeCreateMethod);
    }

    @Test
    public void testCreateCustomerWithShortLastName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Last name's length must be in range from 2 to 12");

        customerBeforeCreateMethod.setLastName("A");
        customerManager.createCustomer(customerBeforeCreateMethod);
    }

    @Test
    public void testCreateCustomerWithNonEmailLogin() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Login must be a valid e-mail address");

        customerBeforeCreateMethod.setLogin("anton");
        customerManager.createCustomer(customerBeforeCreateMethod);
    }

    @Test
    public void testCreateCustomerWithExistingLogin() {

        Customer customer = customerBeforeCreateMethod;
        customer.setLogin("anton@anton.ru");

        Mockito.when(dbService.getCustomers()).thenReturn(Collections.singletonList(customer));

        customerBeforeCreateMethod.setLogin("anton@anton.ru");

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Customer already exists");

        customerManager.createCustomer(customerBeforeCreateMethod);
    }

    @Test
    public void testCreateCustomerWithShortPassword() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(
                "Password's length should be more or equal 6 symbols and less or equal 12 symbols.");

        customerBeforeCreateMethod.setPass("k2jd5");
        customerManager.createCustomer(customerBeforeCreateMethod);
    }
}
