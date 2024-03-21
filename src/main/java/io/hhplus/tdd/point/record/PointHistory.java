package io.hhplus.tdd.point.record;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
}
