package io.hhplus.tdd.point;


import io.hhplus.tdd.Exception.ApiControllerAdvice;
import io.hhplus.tdd.point.record.PointHistory;
import io.hhplus.tdd.point.record.TransactionType;
import io.hhplus.tdd.point.record.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PointControllerTest {

    @Mock
    PointService pointService;
    @InjectMocks
    PointController pointController;

    MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(pointController)
                .setControllerAdvice(new ApiControllerAdvice())
                .build();
    }

    /***************************
     * Point 조회
     ***************************/

    //작성 이유 : PointService를 통해 조회 실패 확인
    @Test
    @DisplayName("포인트 조회 실패")
    public void select_Fail_NoId() throws Exception {
        //given
        final String url = "/point/1";
        UserPoint userPoint = new UserPoint(1L,1000L,System.currentTimeMillis());
        doReturn(null).when(pointService).selectPoint(1L);
        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions.andExpect(status().isInternalServerError());
        //resultActions.andExpect(status().is(200));
    }

    //작성 이유 : PointService를 통해 조회 성공 확인
    @Test
    @DisplayName("포인트 조회 성공")
    public void selectSuccess() throws Exception {
        //given
        final String url = "/point/1";
        UserPoint userPoint = new UserPoint(1L,1000L,System.currentTimeMillis());
        doReturn(userPoint).when(pointService).selectPoint(1L);
        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(status().is(200));
    }

    /***************************
     * Point 충전
     ***************************/
    
    //작성 이유 : pointService를 통해 충천 성공 확인
    @Test
    @DisplayName("포인트 충전 성공")
    public void Charge_Success() throws Exception {
        //given
        final String url = "/point/1/charge";
        final String json = "1000"; //amount

        UserPoint expectedUserPoint = new UserPoint(1L, 200L, System.currentTimeMillis());
        doReturn(expectedUserPoint).when(pointService).chargePoint(1L, 1000L);

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch(url)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions.andExpect(status().isOk());
    }

    /***************************
     * Point 사용
     ***************************/

    //작성 이유 : pointService를 통해 사용 성공 확인
    @Test
    @DisplayName("포인트 사용 성공")
    public void use_Success() throws Exception {
        //given
        final String url = "/point/1/use";
        final String json = "500"; //amount

        UserPoint expectedUserPoint = new UserPoint(1L, 500L, System.currentTimeMillis());
        doReturn(expectedUserPoint).when(pointService).usePoint(1L, 500L);

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch(url)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions.andExpect(status().isOk());
    }

    /***************************
     * Point 내역 조회 성공
     ***************************/

    //작성 이유 : pointService를 통해 내역 조회 성공 확인
    @Test
    @DisplayName("포인트 내역 조회 성공")
    public void histories_Success() throws Exception {
        //given
        final String url = "/point/1000/histories";

        final PointHistory pointHistory1 = new PointHistory(1L,1000L, 100L, TransactionType.CHARGE,System.currentTimeMillis());
        List<PointHistory> expected = new ArrayList<>();
        expected.add(pointHistory1);

         doReturn(expected).when(pointService).selectPointHistories(1000L);

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions.andExpect(status().isOk());
    }
}
