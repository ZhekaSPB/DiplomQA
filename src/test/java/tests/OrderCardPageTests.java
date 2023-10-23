package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataBaseHelper;
import data.DataHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import page.OrderCardPage;
import page.StartPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderCardPageTests {
    StartPage startPage;
    OrderCardPage orderCardPage;

    @BeforeEach
    void shouldOpenWeb() {
        DataBaseHelper.cleanDataBase();
        startPage = open("http://localhost:8080", StartPage.class);
        orderCardPage = startPage.buyWithoutCredit();
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    void shouldApproveFirstCard() {  /*Оплата картой со статусом APPROVED, с вводом корректных данных банковской карты*/
        var cardNumber = DataHelper.getFirstCardNumber();
        var month = DataHelper.getValidMonth();
        var year = DataHelper.getValidYear();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getValidCvc();
        orderCardPage.fillOutFields(cardNumber, month, year, owner, cvc);
        orderCardPage.expectApprovalFromBank();
        var expected = DataHelper.getFirstCardExpectedStatus();
        var actual = DataBaseHelper.getStatusPaymentWithoutCredit();
        assertEquals(expected, actual);

    }

    @Test
    void shouldRejectSecondCard() { /*Оплата картой со статусом DECLINED, с вводом корректных данных банковской карты*/
        var cardNumber = DataHelper.getSecondCardNumber();
        var month = DataHelper.getValidMonth();
        var year = DataHelper.getValidYear();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getValidCvc();
        orderCardPage.fillOutFields(cardNumber, month, year, owner, cvc);
        orderCardPage.expectRejectionFromBank();
        var expected = DataHelper.getSecondCardExpectedStatus();
        var actual = DataBaseHelper.getStatusPaymentWithoutCredit();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void checkingCardNumberRequestedData() { /*Оплата картой с вводом некорректного номера карты*/
        var cardNumber = DataHelper.getCardNumberNotExisting();
        var month = DataHelper.getValidMonth();
        var year = DataHelper.getValidYear();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getValidCvc();
        orderCardPage.fillOutFields(cardNumber, month, year, owner, cvc);
        orderCardPage.expectRejectionFromBank();
    }

    @Test
    void checkingIncompleteData() { /*Оплата картой с вводом неполных данных*/
        var cardNumber = DataHelper.getCardNumberIncomplete();
        var month = DataHelper.getIncompleteMonth();
        var year = DataHelper.getIncompleteYear();
        var owner = DataHelper.getIncompleteOwner();
        var cvc = DataHelper.getIncompleteCvc();
        orderCardPage.fillOutFields(cardNumber, month, year, owner, cvc);
        orderCardPage.waitNecessaryFillOutField();
    }

    @Test
    void shouldRejectInvalidMonth() { /*ПОплата картой при вводе невалидного месяца*/
        var cardNumber = DataHelper.getFirstCardNumber();
        var month = DataHelper.getInvalidMonth();
        var year = DataHelper.getValidYear();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getValidCvc();
        orderCardPage.fillOutFields(cardNumber, month, year, owner, cvc);
        orderCardPage.waitInvalidDuration();
    }

    @Test
    void shouldRejectPastMonth() {  /*Оплата картой с вводом в поле «месяц» не корректных данных – значение предыдущего месяца*/
        var cardNumber = DataHelper.getFirstCardNumber();
        var month = DataHelper.getPastMonth();
        var year = DataHelper.getThisYear();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getValidCvc();
        orderCardPage.fillOutFields(cardNumber, month, year, owner, cvc);
        orderCardPage.waitInvalidYear();
    }

    @Test
    void shouldRejectInvalidYear() { /*Оплата картой с вводом в поле «год» не корректных данных – значение предыдущего года*/
        var cardNumber = DataHelper.getFirstCardNumber();
        var month = DataHelper.getValidMonth();
        var year = DataHelper.getInvalidYear();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getValidCvc();
        orderCardPage.fillOutFields(cardNumber, month, year, owner, cvc);
        orderCardPage.waitInvalidYear();
    }

    @Test
    void checkingOwnerRus() { /*Оплата картой при вводе владельца на кириллице*/
        var cardNumber = DataHelper.getFirstCardNumber();
        var month = DataHelper.getValidMonth();
        var year = DataHelper.getValidYear();
        var owner = DataHelper.getInvalidOwnerRus();
        var cvc = DataHelper.getValidCvc();
        orderCardPage.fillOutFields(cardNumber, month, year, owner, cvc);
        orderCardPage.waitInvalidFormat();
    }

    @Test
    void shouldRejectZeroCvc() { /*Оплата картой при нулевом значении поля cvc*/
        var cardNumber = DataHelper.getFirstCardNumber();
        var month = DataHelper.getValidMonth();
        var year = DataHelper.getValidYear();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getZeroCvv();
        orderCardPage.fillOutFields(cardNumber, month, year, owner, cvc);
        orderCardPage.waitInvalidFormat();
    }

    @Test
    void checkingCVVWithText() { /*Оплата картой при вводе текста в поле cvc*/
        var cardNumber = DataHelper.getFirstCardNumber();
        var month = DataHelper.getValidMonth();
        var year = DataHelper.getValidYear();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getValueText();
        orderCardPage.fillOutFields(cardNumber, month, year, owner, cvc);
        orderCardPage.waitInvalidFormat();
    }

}
