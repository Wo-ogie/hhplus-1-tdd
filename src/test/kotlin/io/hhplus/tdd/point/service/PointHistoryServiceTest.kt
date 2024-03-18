package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.TransactionType
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PointHistoryServiceTest {

    @InjectMocks
    private lateinit var sut: PointHistoryService

    @Mock
    private lateinit var pointHistoryTable: PointHistoryTable

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
    }

    private fun createPointHistory(id: Long, userId: Long): PointHistory {
        return PointHistory(id, userId, TransactionType.CHARGE, 1_000, 12345)
    }
}