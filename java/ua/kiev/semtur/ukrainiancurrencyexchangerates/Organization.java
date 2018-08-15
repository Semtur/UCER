package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SemTur on 11.10.2017.
 */

public class Organization implements Serializable {
    private String mId;
    private int mOrgType;
    private String mName;
    private String mRegion;
    private String mCity;
    private String mAddress;
    private String mPhone;
    private String mLink;
    private ArrayList<Currency> mCurrencyList;

    public Organization() {
        mCurrencyList = new ArrayList<>();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public int getOrgType() {
        return mOrgType;
    }

    public void setOrgType(int orgType) {
        mOrgType = orgType;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getRegion() {
        return mRegion;
    }

    public void setRegion(String region) {
        mRegion = region;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public ArrayList<Currency> getCurrencyList() {
        return mCurrencyList;
    }

    public Currency getCurrency(String currencyCode) {
        for (Currency c : mCurrencyList) {
            if (c == null) {
                continue;
            }
            String code = c.getCode();
            if (code != null && code.equals(currencyCode)) {
                return c;
            }
        }
        return null;
    }

    public void addCurrency(Currency currency) {
        mCurrencyList.add(currency);
    }
}
