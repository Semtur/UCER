package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SemTur on 08.10.2017.
 */

public class CERData {
    private static CERData sCERData;
    private ConcurrentHashMap<String, Organization> mOrganizations;
    private ConcurrentHashMap<String, String> mRegions;
    private ConcurrentHashMap<String, String> mCities;
    private String mDate;

    private CERData() {
        mOrganizations = new ConcurrentHashMap<>();
        mRegions = new ConcurrentHashMap<>();
        mCities = new ConcurrentHashMap<>();
    }

    public static CERData getInstance() {
        if (sCERData == null) {
            sCERData = new CERData();
        }
        return sCERData;
    }

    public Organization getOrganization(String orgId) {
        return mOrganizations.get(orgId);
    }

    public void addOrganization(String key, Organization org) {
        mOrganizations.put(key, org);
    }

    public ArrayList<Organization> getOrganizations(String currencyCode) {
        ArrayList<Organization> organizations = new ArrayList<>();
        for (Organization organization : mOrganizations.values()) {
            for (Currency currency : organization.getCurrencyList()) {
                if (currency != null && currency.getCode().equals(currencyCode)) {
                    if(organization.getId().equals("nbu")) {
                        organizations.add(0, organization);
                    } else {
                        organizations.add(organization);
                    }
                    break;
                }
            }
        }
        return organizations;
    }

    public ArrayList<Organization> getOrganizations(String currencyCode, String optimalBankName, String summaryBankName) {
        ArrayList<Organization> organizations = getOrganizations(currencyCode);
        addOptimalAndSummaryCourses(organizations, optimalBankName, summaryBankName, currencyCode);
        return organizations;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = new StringBuilder()
                .append(date.substring(8, 10))
                .append('.')
                .append(date.substring(5, 7))
                .append('.')
                .append(date.substring(0, 4))
                .append(' ')
                .append(date.substring(11, 16))
                .toString();
    }

    public void sort(ArrayList<Organization> orgs, String currencyCode, String sortBy, String sortDirection, Class callerFragmentClass) {
        int startPosition = 0;
        if (callerFragmentClass.equals(CERFragment.class)) {
            startPosition = 3;
        } else if (callerFragmentClass.equals(CalcFragment.class)) {
            startPosition = 1;
        }
        switch (sortBy) {
            case "org_name":
                if (sortDirection.equals("az")) {
                    for (int i = startPosition; i < orgs.size(); i++) {
                        for (int j = i + 1; j < orgs.size(); j++) {
                            if (orgs.get(i).getName().compareTo(orgs.get(j).getName()) > 0) {
                                Organization temp = orgs.get(i);
                                orgs.set(i, orgs.get(j));
                                orgs.set(j, temp);
                            }
                        }
                    }
                } else {
                    for (int i = startPosition; i < orgs.size(); i++) {
                        for (int j = i + 1; j < orgs.size(); j++) {
                            if (orgs.get(i).getName().compareTo(orgs.get(j).getName()) < 0) {
                                Organization temp = orgs.get(i);
                                orgs.set(i, orgs.get(j));
                                orgs.set(j, temp);
                            }
                        }
                    }
                }
                break;
            case OperationType.sBuy:
                if (sortDirection.equals("az")) {
                    for (int i = startPosition; i < orgs.size(); i++) {
                        for (int j = i + 1; j < orgs.size(); j++) {
                            double iCurrencyBuy = orgs.get(i).getCurrency(currencyCode).getBuy();
                            double jCurrencyBuy = orgs.get(j).getCurrency(currencyCode).getBuy();
                            if (iCurrencyBuy > jCurrencyBuy) {
                                Organization temp = orgs.get(i);
                                orgs.set(i, orgs.get(j));
                                orgs.set(j, temp);
                            }
                        }
                    }
                } else {
                    for (int i = startPosition; i < orgs.size(); i++) {
                        for (int j = i + 1; j < orgs.size(); j++) {
                            double iCurrencyBuy = orgs.get(i).getCurrency(currencyCode).getBuy();
                            double jCurrencyBuy = orgs.get(j).getCurrency(currencyCode).getBuy();
                            if (iCurrencyBuy < jCurrencyBuy) {
                                Organization temp = orgs.get(i);
                                orgs.set(i, orgs.get(j));
                                orgs.set(j, temp);
                            }
                        }
                    }
                }
                break;
            case OperationType.sSale:
                if (sortDirection.equals("az")) {
                    for (int i = startPosition; i < orgs.size(); i++) {
                        for (int j = i + 1; j < orgs.size(); j++) {
                            double iCurrencySale = orgs.get(i).getCurrency(currencyCode).getSale();
                            double jCurrencySale = orgs.get(j).getCurrency(currencyCode).getSale();
                            if (iCurrencySale > jCurrencySale) {
                                Organization temp = orgs.get(i);
                                orgs.set(i, orgs.get(j));
                                orgs.set(j, temp);
                            }
                        }
                    }
                } else {
                    for (int i = startPosition; i < orgs.size(); i++) {
                        for (int j = i + 1; j < orgs.size(); j++) {
                            double iCurrencySale = orgs.get(i).getCurrency(currencyCode).getSale();
                            double jCurrencySale = orgs.get(j).getCurrency(currencyCode).getSale();
                            if (iCurrencySale < jCurrencySale) {
                                Organization temp = orgs.get(i);
                                orgs.set(i, orgs.get(j));
                                orgs.set(j, temp);
                            }
                        }
                    }
                }
                break;
        }
    }

    public boolean isMaxBuyValue(double buyValue, String currencyCode) {
        ArrayList<Organization> organizations = getOrganizations(currencyCode);
        for (Organization org : organizations) {
            if(org.getId().equals("nbu")) {
                continue;
            }
            Currency c = org.getCurrency(currencyCode);
            if (c.getBuy() > buyValue) {
                return false;
            }
        }
        return true;
    }

    public boolean isMinSaleValue(double saleValue, String currencyCode) {
        ArrayList<Organization> organizations = getOrganizations(currencyCode);
        for (Organization org : organizations) {
            if(org.getId().equals("nbu")) {
                continue;
            }
            Currency c = org.getCurrency(currencyCode);
            if (c.getSale() < saleValue) {
                return false;
            }
        }
        return true;
    }

    public void addRegion(String key, String region) {
        mRegions.put(key, region);
    }

    public String getRegion(String key) {
        return mRegions.get(key);
    }

    public void addCity(String key, String city) {
        mCities.put(key, city);
    }

    public String getCity(String key) {
        return mCities.get(key);
    }

    private void addOptimalAndSummaryCourses(ArrayList<Organization> orgs, String optimalOrgName, String summaryOrgName, String currencyCode) {
        Organization orgOptimal = new Organization();
        Organization orgSummary = new Organization();
        Currency cOptimal = new Currency(currencyCode);
        Currency cSummary = new Currency(currencyCode);
        orgOptimal.setName(optimalOrgName);
        orgSummary.setName(summaryOrgName);
        double optimalBuy = orgs.get(1).getCurrency(currencyCode).getBuy();
        double optimalSale  = orgs.get(1).getCurrency(currencyCode).getSale();
        double summaryBuy = optimalBuy;
        double summarySale = optimalSale;
        for (int i = 2; i < orgs.size(); i++){
            Currency c = orgs.get(i).getCurrency(currencyCode);
            double buy = c.getBuy();
            double sale = c.getSale();
            if (optimalBuy < buy) {
                optimalBuy = buy;
            }
            if (optimalSale > sale) {
                optimalSale = sale;
            }
            summaryBuy += buy;
            summarySale += sale;
        }
        cOptimal.setBuy(optimalBuy);
        cOptimal.setSale(optimalSale);
        orgOptimal.addCurrency(cOptimal);
        summaryBuy = summaryBuy / (orgs.size() - 1);
        summarySale = summarySale / (orgs.size() - 1);
        cSummary.setBuy(summaryBuy);
        cSummary.setSale(summarySale);
        orgSummary.addCurrency(cSummary);
        orgs.add(1, orgOptimal);
        orgs.add(2, orgSummary);
    }
}
