package io.hhplus.tdd.point.record;

import io.hhplus.tdd.Exception.PointErrorResults;
import io.hhplus.tdd.Exception.PointException;
import io.hhplus.tdd.point.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
    public static Long use(long point, long usePoint) {
        if(point < usePoint) throw new PointException(PointErrorResults.INSUFFICIENT_BALANCE);
        return point - usePoint;
    }
    public static Long charge(Optional<UserPoint> userPoint, Long chargePoint) {
        Long amount = userPoint.isEmpty() ? chargePoint : userPoint.get().point()+chargePoint;
        return amount;
    }
}
