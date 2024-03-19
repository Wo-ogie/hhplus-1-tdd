package io.hhplus.tdd.point.api

import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.TransactionType
import io.hhplus.tdd.point.domain.UserPoint
import io.hhplus.tdd.point.service.PointService
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [PointController::class])
class PointControllerTest(
    @Autowired private val mvc: MockMvc,
) {

    @MockBean
    private lateinit var pointService: PointService

    @Test
    fun `주어진 id로 유저 포인트를 단건 조회하면 조회된 포인트가 응답된다`() {
        // given
        val userPointId = 1L
        val expectedResult = createUserPoint(userPointId)
        given(pointService.getPointById(userPointId)).willReturn(expectedResult)

        // when & then
        mvc.perform(get("/point/{id}", userPointId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(expectedResult.id))
        then(pointService).should().getPointById(userPointId)
        then(pointService).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `양수가 아닌 id가 주어지고, 주어진 id로 유저 포인트를 단건 조회하면, 예외가 발생한다`() {
        // given
        val userPointId = -1L

        // when & then
        mvc.perform(get("/point/{id}", userPointId))
            .andExpect(status().isInternalServerError)
        then(pointService).shouldHaveNoInteractions()
    }

    @Test
    fun `유저 포인트 id가 주어지고, 주어진 id에 해당하는 포인트 내역을 조회하면, 조회된 포인트 내역 목록이 응답된다`() {
        // given
        val userId = 1L
        val expectedResult = listOf(
            createPointHistory(2L, userId),
            createPointHistory(3L, userId),
        )
        given(pointService.findPointHistoriesByUserId(userId)).willReturn(expectedResult)

        // when & then
        mvc.perform(get("/point/{id}/histories", userId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<PointHistory>(expectedResult.size)))
            .andExpect(jsonPath("$.[0].id").value(expectedResult[0].id))
            .andExpect(jsonPath("$.[1].id").value(expectedResult[1].id))
        then(pointService).should().findPointHistoriesByUserId(userId)
        then(pointService).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `양수가_아닌_유저 포인트 id가 주어지고, 주어진 id에 해당하는 포인트 내역을 조회하면, 예외가 발생한다`() {
        // given
        val userId = -1L

        // when & then
        mvc.perform(get("/point/{id}/histories", userId))
            .andExpect(status().isInternalServerError)
        then(pointService).shouldHaveNoInteractions()
    }

    @Test
    fun `유저 포인트 id와 충전 금액이 주어지고, 포인트를 충전하면, 충전된 유저 포인트 정보를 응답한다`() {
        // given
        val userPointId = 1L
        val amount = 500L
        val expectedResult = createUserPoint(userPointId, 1000L + amount)
        given(pointService.chargePoint(userPointId, amount)).willReturn(expectedResult)

        // when & then
        mvc.perform(
            patch("/point/${userPointId}/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(amount.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(expectedResult.id))
            .andExpect(jsonPath("$.point").value(expectedResult.point))
            .andExpect(jsonPath("$.updateMillis").value(expectedResult.updateMillis))
        then(pointService).should().chargePoint(userPointId, amount)
        then(pointService).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `유저 포인트 id와 양수가 아닌 충전 금액이 주어지고, 포인트를 충전하면, 예외가 발생한다`() {
        // given
        val userPointId = 1L
        val amount = -500L

        // when & then
        mvc.perform(
            patch("/point/${userPointId}/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(amount.toString())
        ).andExpect(status().isInternalServerError)
        then(pointService).shouldHaveNoInteractions()
    }

    @Test
    fun `유저 포인트 id와 사용 금액이 주어지고, 포인트를 사용하면, 포인트가 차감된 유저 포인트 정보를 응답한다`() {
        // given
        val userPointId = 1L
        val amount = 500L
        val expectedResult = createUserPoint(userPointId, 1000L - amount)
        given(pointService.usePoint(userPointId, amount)).willReturn(expectedResult)

        // when & then
        mvc.perform(
            patch("/point/${userPointId}/use")
                .contentType(MediaType.APPLICATION_JSON)
                .content(amount.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(expectedResult.id))
            .andExpect(jsonPath("$.point").value(expectedResult.point))
            .andExpect(jsonPath("$.updateMillis").value(expectedResult.updateMillis))
        then(pointService).should().usePoint(userPointId, amount)
        then(pointService).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `유저 포인트 id와 양수가 아닌 사용 금액이 주어지고, 포인트를 사용하면, 예외가 발생한다`() {
        // given
        val userPointId = 1L
        val amount = -500L

        // when & then
        mvc.perform(
            patch("/point/${userPointId}/use")
                .contentType(MediaType.APPLICATION_JSON)
                .content(amount.toString())
        ).andExpect(status().isInternalServerError)
        then(pointService).shouldHaveNoInteractions()
    }

    private fun createUserPoint(id: Long, point: Long): UserPoint {
        return UserPoint(id = id, point = point, updateMillis = 12345L)
    }

    private fun createUserPoint(id: Long): UserPoint {
        return UserPoint(id = id, point = 10L, updateMillis = 12345L)
    }

    private fun createPointHistory(id: Long, userId: Long): PointHistory {
        return PointHistory(id, userId, TransactionType.CHARGE, 1_000, 12345)
    }
}