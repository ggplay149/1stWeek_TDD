package io.hhplus.tdd.point.record;

import io.hhplus.tdd.Exception.PointErrorResults;
import io.hhplus.tdd.Exception.PointException;
import io.hhplus.tdd.point.database.PointHistoryTable;

import java.util.List;
import java.util.Optional;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
    public static List<PointHistory> selectHistories(long userid, PointHistoryTable pointHistoryTable){
        //조회 후 예외처리
        return Optional.ofNullable(pointHistoryTable.selectAllByUserId(userid))
                .orElseThrow(() -> new PointException(PointErrorResults.USER_ID_NOT_FOUND));
    }
}
