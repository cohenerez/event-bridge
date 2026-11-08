package com.erez.loans.bridge.kafka;

import java.math.BigDecimal;


public record LoanStatusEvent(
        String eventUuid,
        String loanId,
        String oldStatus,
        String newStatus,
        BigDecimal amount,
        String applicantEmail,
        String occurredAt,
        String replayId) {
}
