package io.hhplus.tdd.point;

import io.hhplus.tdd.Exception.PointErrorResults;
import io.hhplus.tdd.Exception.PointException;
import io.hhplus.tdd.point.database.UserPointTable;
import io.hhplus.tdd.point.record.PointHistory;
import io.hhplus.tdd.point.record.TransactionType;
import io.hhplus.tdd.point.record.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class IntegrationTest {

    @Autowired
    PointService pointService;

    @Autowired
    UserPointTable userPointTable;

    /***************************
     * Point 통합 테스트 Test1
     * 1) 신규회원 1000포인트 충전 > 1000p
     * 2) 300 포인트 사용 > 700p
     * 3) 포인트 내역 조회 > CHARGE 1000, USE 700
     * 4) (10포인트 충전 + 5포인트 사용) 동시 10회 요청 > 750p
     * 5) 포인트 잔액 조회 > 750p
     * 6) 1000 포인트 사용 > 잔액부족 예외처리
     ***************************/

    @Test
    @DisplayName("Test1")
    void Test1() throws InterruptedException {

        // 1) 신규회원 1000포인트 충전 > 1000p
        pointService.chargePoint(1L, 1000L);
        // 2) 300 포인트 사용 > 700p
        pointService.usePoint(1L, 300L);
        // 3) 포인트 내역 조회 > CHARGE 1000, USE 700
        List<PointHistory> result = pointService.selectPointHistories(1L);
        assertThat(result.get(0).type()).isEqualTo(TransactionType.CHARGE);
        assertThat(result.get(1).type()).isEqualTo(TransactionType.USE);

        // 4) (10포인트 충전 + 5포인트 사용) 동시 10회 요청 > 750p
        int threadCount = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargePoint(1L, 10L);
                    pointService.usePoint(1L, 5L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        result = pointService.selectPointHistories(1L);
        assertThat(result.size()).isEqualTo(22);
        assertThat(result.get(2).type()).isEqualTo(TransactionType.CHARGE);

        // 5) 포인트 잔액 조회 > 650p
        assertThat(pointService.selectPoint(1L).point()).isEqualTo(750L);

        // 6) 1000 포인트 사용 > 잔액부족 예외처리
        //PointException pointException = use
        final PointException result3 = assertThrows(PointException.class,()->pointService.usePoint(1L,1000L));
        assertThat(result3.getPointErrorResults()).isEqualTo(PointErrorResults.INSUFFICIENT_BALANCE);

    }



    /***************************
     * Point 순차진행 동시성 테스트
     ***************************/

    @Test
    @DisplayName("1포인트 사용 30번 동시 요청")
    void syncTest1() throws InterruptedException {
        //given
        int threadCount = 30;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        userPointTable.insertOrUpdate(1L,30);
        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.usePoint(1L, 1L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        final UserPoint result = userPointTable.selectById(1L);
        assertThat(result.point()).isEqualTo(0L);
    }
}
