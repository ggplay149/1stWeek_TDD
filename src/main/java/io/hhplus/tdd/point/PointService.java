package io.hhplus.tdd.point;

import io.hhplus.tdd.Exception.PointErrorResults;
import io.hhplus.tdd.Exception.PointException;
import io.hhplus.tdd.point.database.PointHistoryTable;
import io.hhplus.tdd.point.database.UserPointTable;
import io.hhplus.tdd.point.record.PointHistory;
import io.hhplus.tdd.point.record.UserPoint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    //조회
    public UserPoint selectPoint(long id) {
        UserPoint userPoint = Optional.ofNullable(userPointTable.selectById(id))
        .orElseThrow(() -> new PointException(PointErrorResults.ID_NOT_FOUND));
        return userPoint;
    }

    //충전
    public synchronized UserPoint chargePoint(Long id, Long chargePoint) {
        Optional<UserPoint> optionalUserPoint = Optional.ofNullable(userPointTable.selectById(id));
        //기존 포인트 + 충전 포인트
        Long amount = !optionalUserPoint.isPresent() ? chargePoint : chargePoint + optionalUserPoint.get().point();
        //insertOrUpdate
        return userPointTable.insertOrUpdate(id, amount);
    }

    //사용
    public synchronized UserPoint usePoint(Long id, Long usePoint) {

        Optional<UserPoint> optionalUserPoint = Optional.ofNullable(userPointTable.selectById(id));
        UserPoint userPoint = optionalUserPoint.orElseThrow(() -> new PointException(PointErrorResults.ID_NOT_FOUND));
        if (userPoint.point() - usePoint >= 0) {
            return userPointTable.insertOrUpdate(id, userPoint.point() - usePoint);
        }else {
            throw new PointException(PointErrorResults.INSUFFICIENT_BALANCE);
        }

    }


    //History 조회
    public List<PointHistory> selectPointHistories(long userid) {
        Optional<List<PointHistory>> optionalList = Optional.ofNullable(pointHistoryTable.selectAllByUserId(userid));
        List<PointHistory> pointHistories = optionalList.orElseThrow(() -> new PointException(PointErrorResults.USER_ID_NOT_FOUND));
        return pointHistories;
    }
}
