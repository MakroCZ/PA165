package cz.muni.fi.pa165.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is base implementation of {@link CurrencyConvertor}.
 *
 * @author petr.adamek@embedit.cz
 */
public class CurrencyConvertorImpl implements CurrencyConvertor {

    private final ExchangeRateTable exchangeRateTable;
    private final Logger logger = LoggerFactory.getLogger(CurrencyConvertorImpl.class);

    public CurrencyConvertorImpl(ExchangeRateTable exchangeRateTable) {
        this.exchangeRateTable = exchangeRateTable;
    }

    @Override
    public BigDecimal convert(Currency sourceCurrency, Currency targetCurrency, BigDecimal sourceAmount) {
        logger.trace("Calling convert method with args {}, {}, {}", sourceCurrency, targetCurrency, sourceAmount);
        if (sourceCurrency == null) {
            throw new IllegalArgumentException("Source currency can´t be null");
        }
        if (targetCurrency == null) {
            throw new IllegalArgumentException("Target currency can´t be null");
        }
        if (sourceAmount == null) {
            throw new IllegalArgumentException("Source amount can´t be null");
        }
        
        try {
            BigDecimal exchangeRate = exchangeRateTable.getExchangeRate(sourceCurrency, targetCurrency);
            if (exchangeRate == null) {
                logger.warn("Convert fail due missing exchange rate");
                throw new UnknownExchangeRateException("Unknown exchange rate");
            }
            return exchangeRate.multiply(sourceAmount).setScale(2, RoundingMode.HALF_EVEN);
        } catch (ExternalServiceFailureException ex) {
            logger.error("Convert fail due problem when looking for exchange rate");
            throw new UnknownExchangeRateException("Problem when looking for exchange rate", ex);
        }
    }
}