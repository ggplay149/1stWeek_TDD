package io.hhplus.tdd.point;

import io.hhplus.tdd.Exception.PointErrorResults;
import io.hhplus.tdd.Exception.PointException;
import io.hhplus.tdd.point.database.PointHistoryTable;
import io.hhplus.tdd.point.database.UserPointTable;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock //userPointTable 가짜 객체생성
    UserPointTable userPointTable;

    @Mock
    PointHistoryTable pointHistoryTable;

    @InjectMocks  //DataBaseTest mock 주입
    PointService pointService;

    //기본 Userpoint 정보
    private final Long id = 1L;
    private final Long point = 1000L;

    /***************************
     * Point 조회
     ***************************/

    //작성 이유 :  userpointTable을 통해 아이디가 없는 경우 예외 처리
    @Test
    @DisplayName("포인트 조회 실패 : 없는 아이디")
    public void selectFail_NoID(){
        //given
        doReturn(null).when(userPointTable).selectById(id);
        //when
        final PointException result = assertThrows(PointException.class,()->pointService.selectPoint(id));
        //then
        assertThat(result.getPointErrorResults()).isEqualTo(PointErrorResults.ID_NOT_FOUND);
    }

    //작성 이유 : userpointTable을 통해 조회 성공 확인
    @Test
    @DisplayName("포인트 조회 성공")
    public void selectSuccess(){
        //given
        UserPoint userPoint = new UserPoint(id,point,System.currentTimeMillis());
        doReturn(userPoint).when(userPointTable).selectById(id);
        //when
        UserPoint result = pointService.selectPoint(id);
        //then
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.point()).isEqualTo(point);
    }

    /***************************
     * Point 충전
     ***************************/

    //작성 이유 :  userpointTable을 통해 아이디가 없는 경우 신규 아이디 충전
    @Test
    @DisplayName("포인트 충전 성공_신규아이디")
    public void Charge_Success_New_Id(){
        //given
        UserPoint newUserPoint = new UserPoint(id,700L,System.currentTimeMillis());
        doReturn(null).when(userPointTable).selectById(id);
        doReturn(newUserPoint).when(userPointTable).insertOrUpdate(id,newUserPoint.point());

        //when
        UserPoint result = pointService.chargePoint(id,700L);

        //then
        assertThat(result.point()).isEqualTo(700L);
    }

    //작성 이유 :  userpointTable을 통해 기존 아이디 충전 성공 확인
    @Test
    @DisplayName("포인트 충전 성공_기존아이디")
    public void Charge_Success_Exisiting_Id(){
        //given
        UserPoint ExistingUserPoint = new UserPoint(id,point, System.currentTimeMillis());
        UserPoint ResultUserPoint = new UserPoint(id,ExistingUserPoint.point()+500L,System.currentTimeMillis());

        doReturn(ExistingUserPoint).when(userPointTable).selectById(id);
        doReturn(ResultUserPoint).when(userPointTable).insertOrUpdate(id,ExistingUserPoint.point()+500L);

        //when
        UserPoint result = pointService.chargePoint(id,500L);

        //then
        assertThat(result.point()).isEqualTo(1500L);
    }

    /***************************
     * Point 사용
     ***************************/

    //작성 이유 :  userpointTable을 통해 잔액 부족 예외처리
    @Test
    @DisplayName("포인트 사용 실패_기존아이디 : 잔액부족")
    public void use_Fail_Exisiting_Id(){
        //given
        UserPoint ExistingUserPoint = new UserPoint(id,point, System.currentTimeMillis());
        doReturn(ExistingUserPoint).when(userPointTable).selectById(id);

        //when
        final PointException result = assertThrows(PointException.class,()->pointService.usePoint(id,1200L));

        //then
        assertThat(result.getPointErrorResults()).isEqualTo(PointErrorResults.INSUFFICIENT_BALANCE);

    }

    //작성 이유 :  userpointTable을 통해 사용 성공 확인
    @Test
    @DisplayName("포인트 사용 성공_기존아이디")
    public void use_Success_Exisiting_Id(){
        //given
        UserPoint ExistingUserPoint = new UserPoint(id,point, System.currentTimeMillis());
        UserPoint ResultUserPoint = new UserPoint(id,ExistingUserPoint.point()-300L,System.currentTimeMillis());

        doReturn(ExistingUserPoint).when(userPointTable).selectById(id);
        doReturn(ResultUserPoint).when(userPointTable).insertOrUpdate(id,ExistingUserPoint.point()-300L);

        //when
        UserPoint result = pointService.usePoint(id,300L);

        //then
        assertThat(result.point()).isEqualTo(700L);
    }

    /***************************
     * Point 내역 조회
     ***************************/

    // 작성 이유 : PointHistoryTable을 통해 유저아이디가 없는 경우 예외 처리
    @Test
    @DisplayName("포인트 내역 조회 실패 : 없는 userId")
    public void selectPointHistories_Fail_No_Id(){
        //given
        doReturn(null).when(pointHistoryTable).selectAllByUserId(1000L); //유저아이디로 검색

        //when
        PointException result = assertThrows(PointException.class, ()-> pointService.selectPointHistories(1000L));

        //then
        assertThat(result.getPointErrorResults()).isEqualTo(PointErrorResults.USER_ID_NOT_FOUND);
    }

    // 작성 이유 : PointHistoryTable을 통해 포인트 내역 조회 성공 확인
    @Test
    @DisplayName("포인트 내역 조회 성공")
    public void selectPointHistories_Success(){
        final PointHistory pointHistory1 = new PointHistory(1L,1000L, 100L, TransactionType.CHARGE,System.currentTimeMillis());
        final PointHistory pointHistory2 = new PointHistory(1L,1000L, 200L,TransactionType.USE,System.currentTimeMillis());
        final PointHistory pointHistory3 = new PointHistory(1L,1000L, 300L,TransactionType.CHARGE,System.currentTimeMillis());

        List<PointHistory> expected = new ArrayList<>();
        expected.add(pointHistory1);
        expected.add(pointHistory2);
        expected.add(pointHistory3);
        //given
        doReturn(expected).when(pointHistoryTable).selectAllByUserId(1000L); //유저아이디로 검색

        //when
        List<PointHistory> result = pointService.selectPointHistories(1000L);

        //then
        assertThat(result.size()).isEqualTo(3);
    }

}
