package io.hhplus.tdd.point.record;

import io.hhplus.tdd.point.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
    public static UserPoint select(long id) {return new UserPointTable().selectById(id); }
}
