package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataBaseHelper;
import data.DataHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import page.CreditPage;
import page.StartPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreditPageTests {
    StartPage startPage;
    CreditPage creditPage;

    @BeforeEach
    void shouldCleanDataBaseAndOpenWeb() {
        DataBaseHelper.cleanDataBase();
        startPage = open("http://localhost:8080", StartPage.class);
        creditPage = startPage.buyWithCredit();
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
    void shouldApproveFirstCard() {  /* Кредит по данным карты со статусом APPROVED при вводе валидных данных карты*/
        var cardNumber = DataHelper.getFirstCardNumber();
        var month = DataHelper.getValidMonth();
        var year = DataHelper.getValidYear();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getValidCvc();
        creditPage.fillOutFields(cardNumber, month, year, owner, cvc);
        creditPage.expectApprovalFromBank();
        var expected = DataHelper.getFirstCardExpectedStatus();
        var actual = DataBaseHelper.getStatusPaymentWithCredit();
        assertEquals(expected, actual);
    }

    @Test
    void shouldRejectSecondCard() { /*Кредит по данным карты со статусом DECLINED при вводе валидных данных карты*/
        var cardNumber = DataHelper.getSecondCardNumber();
        var month = DataHelper.getValidMonth();
        var year = DataHelper.getValidYear();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getValidCvc();
        creditPage.fillOutFields(cardNumber, month, year, owner, cvc);
        creditPage.expectRejectionFromBank();
        var expected = DataHelper.getSecondCardExpectedStatus();
        var actual = DataBaseHelper.getStatusPaymentWithCredit();
        assertEquals(expected, actual);
    }

    @Test
    void shouldRejectEmptyNumberCard() { /*Кредит по данным карты ghb отсутствии ввода номера карты*/
        var cardNumber = DataHelper.getEmptyValue();
        var month = DataHelper.getValidMonth();
        var year = DataHelper.getValidYear();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getValidCvc();
        creditPage.fillOutFields(cardNumber, month, year, owner, cvc);
        creditPage.waitNecessaryFillOutField();
    }

    @Test
    void checkingMonthOverLimit() { /*ПКредит по данным карты при вводе трех цифр в поле месяца*/
        var cardNumber = DataHelper.getFirstCardNumber();
        var month = DataHelper.getDurationOverLimit();
        var year = DataHelper.getValidYear();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getValidCvc();
        creditPage.fillOutFields(cardNumber, month, year, owner, cvc);
        creditPage.waitInvalidDuration();
    }

    @Test
    void shouldRejectPastMonth() {  /*Кредит по данным карты с истекшим сроком годности*/
        var cardNumber = DataHelper.getFirstCardNumber();
        var month = DataHelper.getPastMonth();
        var year = DataHelper.getThisYear();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getValidCvc();
        creditPage.fillOutFields(cardNumber, month, year, owner, cvc);
        creditPage.waitInvalidDuration();
    }

    @Test
    void shouldRejectZeroYear() { /*Кредит по данным карты при вводе нулевого года*/
        var cardNumber = DataHelper.getFirstCardNumber();
        var month = DataHelper.getValidMonth();
        var year = DataHelper.getZeroValue();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getValidCvc();
        creditPage.fillOutFields(cardNumber, month, year, owner, cvc);
        creditPage.waitInvalidFormat();
    }

    @Test
    void checkingOwnerRus() { /*Кредит по данным карты при вводе владельца на кириллице*/
        var cardNumber = DataHelper.getFirstCardNumber();
        var month = DataHelper.getValidMonth();
        var year = DataHelper.getValidYear();
        var owner = DataHelper.getInvalidOwnerRus();
        var cvc = DataHelper.getValidCvc();
        creditPage.fillOutFields(cardNumber, month, year, owner, cvc);
        creditPage.waitInvalidFormat();
    }

    @Test
    void shouldRejectZeroCvc() { /*Кредит по данным карты при вводе нулевого cvc*/
        var cardNumber = DataHelper.getFirstCardNumber();
        var month = DataHelper.getValidMonth();
        var year = DataHelper.getValidYear();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getZeroCvv();
        creditPage.fillOutFields(cardNumber, month, year, owner, cvc);
        creditPage.waitInvalidFormat();
    }

    @Test
    void shouldRejectEmptyCvc() { /*Кредит по данным карты при отсутствии ввода данных в поле cvc*/
        var cardNumber = DataHelper.getFirstCardNumber();
        var month = DataHelper.getValidMonth();
        var year = DataHelper.getValidYear();
        var owner = DataHelper.getValidOwner();
        var cvc = DataHelper.getEmptyValue();
        creditPage.fillOutFields(cardNumber, month, year, owner, cvc);
        creditPage.waitNecessaryFillOutField();
    }
}
