package io.hhplus.tdd.point;

import io.hhplus.tdd.Exception.PointErrorResults;
import io.hhplus.tdd.Exception.PointException;
import io.hhplus.tdd.point.database.PointHistoryTable;
import io.hhplus.tdd.point.database.UserPointTable;
import io.hhplus.tdd.point.record.PointHistory;
import io.hhplus.tdd.point.record.TransactionType;
import io.hhplus.tdd.point.record.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    //조회
    public UserPoint selectPoint(long id) {

        UserPoint userPoint = Optional.ofNullable(userPointTable.selectById(id))
        .orElseThrow(() -> new PointException(PointErrorResults.ID_NOT_FOUND));

        return userPoint;
    }

    //충전
    public synchronized UserPoint chargePoint(Long id, Long chargePoint) {

        Optional<UserPoint> optionalUserPoint = Optional.ofNullable(userPointTable.selectById(id));

        pointHistoryTable.insert(id,chargePoint, TransactionType.CHARGE,System.currentTimeMillis());
        return userPointTable.insertOrUpdate(id, UserPoint.charge(optionalUserPoint,chargePoint));
    }

    //사용
    public synchronized UserPoint usePoint(Long id, Long usePoint) {

        UserPoint userPoint = Optional.ofNullable(userPointTable.selectById(id))
                .orElseThrow(() -> new PointException(PointErrorResults.ID_NOT_FOUND));

        pointHistoryTable.insert(id,usePoint,TransactionType.USE,System.currentTimeMillis());
        return userPointTable.insertOrUpdate(id, UserPoint.use(userPoint.point(),usePoint));
    }

    //History 조회
    public List<PointHistory> selectPointHistories(long userid) {
        List<PointHistory> pointHistories = Optional.ofNullable(pointHistoryTable.selectAllByUserId(userid))
                .orElseThrow(() -> new PointException(PointErrorResults.USER_ID_NOT_FOUND));
        return pointHistories;
    }
}
