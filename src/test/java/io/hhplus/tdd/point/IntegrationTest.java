package io.hhplus.tdd.point;

import io.hhplus.tdd.point.database.UserPointTable;
import io.hhplus.tdd.point.record.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class IntegrationTest {

    @Autowired
    PointService pointService;

    @Autowired
    UserPointTable userPointTable;


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
