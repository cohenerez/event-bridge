package com.erez.loans.bridge.auth;


public interface SessionProvider {

    SalesforceSession current();


    void invalidate();
}
