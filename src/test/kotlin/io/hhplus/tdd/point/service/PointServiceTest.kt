package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.TransactionType
import io.hhplus.tdd.point.domain.UserPoint
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PointServiceTest {

    @InjectMocks
    private lateinit var sut: PointService

    @Mock
    private lateinit var userPointTable: UserPointTable

    @Mock
    private lateinit var pointHistoryTable: PointHistoryTable

    @Test
    fun `주어진 id로 유저의 포인트를 조회하면 조회된 유저 포인트가 반환된다`() {
        // given
        val userPointId = 1L
        val expectedResult = createUserPoint(userPointId)
        given(userPointTable.selectById(userPointId)).willReturn(expectedResult)

        // when
        val actualResult = sut.getPointById(userPointId)

        // then
        then(userPointTable).should().selectById(userPointId)
        then(userPointTable).shouldHaveNoMoreInteractions()
        then(pointHistoryTable).shouldHaveNoInteractions()
        Assertions.assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `특정 유저의 포인트 id가 주어지고, 주어진 id에 해당하는 모든 포인트 내역을 조회하면, 조회된 포인트 목록이 반환된다`() {
        // given
        val userId = 1L
        val expectedResult = listOf(
            createPointHistory(2L, userId),
            createPointHistory(3L, userId),
        )
        given(pointHistoryTable.selectAllByUserId(userId)).willReturn(expectedResult)

        // when
        val actualResult = sut.findHistoriesByUserId(userId)

        // then
        assertIterableEquals(expectedResult, actualResult)
        then(pointHistoryTable).should().selectAllByUserId(userId)
        then(userPointTable).shouldHaveNoInteractions()
        then(pointHistoryTable).shouldHaveNoMoreInteractions()
    }


    private fun createUserPoint(id: Long): UserPoint {
        return UserPoint(id = id, point = 10L, updateMillis = 12345L)
    }

    private fun createPointHistory(id: Long, userId: Long): PointHistory {
        return PointHistory(id, userId, TransactionType.CHARGE, 1_000, 12345)
    }
}