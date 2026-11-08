package com.erez.loans.bridge.auth;

public record SalesforceSession(String accessToken, String instanceUrl, String tenantId) {
}
