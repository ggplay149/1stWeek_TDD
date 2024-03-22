package io.hhplus.tdd.point.record;

import io.hhplus.tdd.Exception.PointErrorResults;
import io.hhplus.tdd.Exception.PointException;
import io.hhplus.tdd.point.database.PointHistoryTable;
import io.hhplus.tdd.point.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public static UserPoint select(long id, UserPointTable userPointTable) {
        //조회후 예외처리
        return Optional.ofNullable(userPointTable.selectById(id))
                .orElseThrow(() -> new PointException(PointErrorResults.ID_NOT_FOUND));
    }

    public static UserPoint charge(long id, Long chargePoint, UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        //아이디 조회
        Optional<UserPoint> userPoint = Optional.ofNullable(userPointTable.selectById(id));
        //없으면 신규 or 있으면 기존 포인트 + 충전 포인트
        Long amount = userPoint.isEmpty() ? chargePoint : userPoint.get().point()+chargePoint;
        //히스토리 기록
        pointHistoryTable.insert(id,chargePoint, TransactionType.CHARGE,System.currentTimeMillis());
        //포인트 업데이트
        return userPointTable.insertOrUpdate(id,amount);
    }

    public static UserPoint use(long id, Long usePoint, UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        //아이디 조회
        UserPoint userPoint = Optional.ofNullable(UserPoint.select(id,userPointTable))
                .orElseThrow(()->new PointException(PointErrorResults.ID_NOT_FOUND));
        //차감계산
        Long amount = userPoint.point-usePoint;
        //0미만이면 예외처리
        if(amount<0) throw new PointException(PointErrorResults.INSUFFICIENT_BALANCE);
        //히스토리 기록
        pointHistoryTable.insert(id,usePoint, TransactionType.USE,System.currentTimeMillis());
        //포인트 업데이트
        return userPointTable.insertOrUpdate(id,amount);
    }
}
