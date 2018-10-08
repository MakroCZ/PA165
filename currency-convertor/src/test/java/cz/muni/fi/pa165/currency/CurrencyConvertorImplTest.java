package cz.muni.fi.pa165.currency;

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class CurrencyConvertorImplTest {

    
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency INR = Currency.getInstance("INR");
    
    private final ExchangeRateTable exchangeRateTable = mock(ExchangeRateTable.class);
    private final CurrencyConvertor currencyConvertor = new CurrencyConvertorImpl(exchangeRateTable);

    
    @Test
    public void testConvert() throws ExternalServiceFailureException {
        when(exchangeRateTable.getExchangeRate(USD, INR)).thenReturn(new BigDecimal("73.152"));
        
        
        assertThat(currencyConvertor.convert(USD, INR, new BigDecimal("1.50")))
                .isEqualTo(new BigDecimal("109.73"));
        
        when(exchangeRateTable.getExchangeRate(USD, INR)).thenReturn(new BigDecimal("0"));
        
        assertThat(currencyConvertor.convert(USD, INR, new BigDecimal("0.00")))
                .isEqualTo(new BigDecimal("0.00"));
        // Don't forget to test border values and proper rounding.
    }

    @Test
    public void testConvertWithNullSourceCurrency() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> currencyConvertor.convert(null, INR, BigDecimal.ONE));
    }

    @Test
    public void testConvertWithNullTargetCurrency() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> currencyConvertor.convert(USD, null, BigDecimal.ONE));
    }

    @Test
    public void testConvertWithNullSourceAmount() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> currencyConvertor.convert(USD, INR, null));
    }

    @Test
    public void testConvertWithUnknownCurrency() throws ExternalServiceFailureException {
        Currency unknown = Currency.getInstance("CZK");
        when(exchangeRateTable.getExchangeRate(unknown, INR)).thenReturn(null);
        when(exchangeRateTable.getExchangeRate(USD, unknown)).thenReturn(null);
        assertThatExceptionOfType(UnknownExchangeRateException.class)
                .isThrownBy(() -> currencyConvertor.convert(unknown, INR, BigDecimal.ZERO));
        assertThatExceptionOfType(UnknownExchangeRateException.class)
                .isThrownBy(() -> currencyConvertor.convert(USD, unknown, BigDecimal.ZERO));
        
    }

    @Test
    public void testConvertWithExternalServiceFailure() throws ExternalServiceFailureException {
        when(exchangeRateTable.getExchangeRate(USD, INR))
                .thenThrow(ExternalServiceFailureException.class);
        
        assertThatExceptionOfType(UnknownExchangeRateException.class)
                .isThrownBy(() -> currencyConvertor.convert(USD, INR, BigDecimal.ONE));
        
    }

}
