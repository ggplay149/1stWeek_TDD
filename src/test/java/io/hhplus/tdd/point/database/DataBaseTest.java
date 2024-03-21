package io.hhplus.tdd.point.database;
import io.hhplus.tdd.point.record.PointHistory;
import io.hhplus.tdd.point.record.TransactionType;
import io.hhplus.tdd.point.record.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class DataBaseTest {

    @Mock //userPointTable 가짜 객체생성
    UserPointTable userPointTable;

    @Mock //pointHistoryTable 가짜 객체생성
    PointHistoryTable pointHistoryTable;

    @InjectMocks //DataBaseTest mock 주입
    DataBaseTest dataBaseTest;

    /***************************
     * Point 조회
     ***************************/

    // 작성이유 : userPointTable을 통해 포인트 조회
    @Test
    @DisplayName("포인트 조회")
    public void selectPoint(){
        //given
        final UserPoint selected = new UserPoint(1L,100,System.currentTimeMillis());
        doReturn(selected).when(userPointTable).selectById(1L);
        //when
        UserPoint result = userPointTable.selectById(1L);
        //then
        assertThat(result.id()).isEqualTo(selected.id());
    }

    /***************************
     * Point 충전
     ***************************/

    // 작성이유 : userPointTable을 통해 포인트 충전
    @Test
    @DisplayName("포인트 충전")
    public void chargePoint(){
        //given
        final UserPoint charged = new UserPoint(1L,100,System.currentTimeMillis());
        doReturn(charged).when(userPointTable).insertOrUpdate(charged.id(), charged.point());
        //when
        UserPoint addUserPoint = userPointTable.insertOrUpdate(charged.id(),charged.point());
        //then
        assertThat(charged.point()).isEqualTo(addUserPoint.point());
    }

    /***************************
     * Point 사용
     * 동일한 insertOrUpdate를 사용하기 때문에 따로 테스트 작성하지 않는다.
     ***************************/


    /***************************
     * Point 내역 조회
     ***************************/

    // 작성 이유 : PointHistoryTable을 통해 포인트 내역 조회
    @Test
    @DisplayName("포인트 내역 조회")
    public void selectPointHistories(){
        //given
        final PointHistory pointHistory1 = new PointHistory(1L,1000L, 100L,TransactionType.CHARGE,System.currentTimeMillis());
        final PointHistory pointHistory2 = new PointHistory(1L,1000L, 200L,TransactionType.USE,System.currentTimeMillis());
        final PointHistory pointHistory3 = new PointHistory(1L,1000L, 300L,TransactionType.CHARGE,System.currentTimeMillis());

        List<PointHistory> expected = new ArrayList<>();
        expected.add(pointHistory1);
        expected.add(pointHistory2);
        expected.add(pointHistory3);

        doReturn(expected).when(pointHistoryTable).selectAllByUserId(1000L);

        //when
        int count = pointHistoryTable.selectAllByUserId(1000L).size();

        //then
        assertThat(count).isEqualTo(3);
    }
}
