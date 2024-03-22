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
        return  UserPoint.select(id,userPointTable);
    }

    //충전
    public synchronized UserPoint chargePoint(Long id, Long chargePoint) {
        return UserPoint.charge(id,chargePoint,userPointTable,pointHistoryTable);
    }

    //사용
    public synchronized UserPoint usePoint(Long id, Long usePoint) {
        return UserPoint.use(id,usePoint,userPointTable,pointHistoryTable);
    }

    //History 조회
    public List<PointHistory> selectPointHistories(long userid) {
        return PointHistory.selectHistories(userid,pointHistoryTable);
    }
}
