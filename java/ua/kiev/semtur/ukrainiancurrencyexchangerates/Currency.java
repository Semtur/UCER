package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import java.io.Serializable;

/**
 * Created by SemTur on 11.10.2017.
 */

public class Currency implements Serializable {
    private String mCode;
    private double mSale;
    private double mBuy;

    public Currency(String code) {
        mCode = code;
    }

    public Currency(String code, double sale, double buy) {
        mCode = code;
        mSale = sale;
        mBuy = buy;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public double getSale() {
        return mSale;
    }

    public void setSale(double sale) {
        mSale = sale;
    }

    public double getBuy() {
        return mBuy;
    }

    public void setBuy(double buy) {
        mBuy = buy;
    }
}
