package io.hhplus.tdd.repository

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.TransactionType
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PointHistoryMemoryRepositoryTest {

    private val pointHistoryTable: PointHistoryTable = mock()

    private val sut: PointHistoryRepository = PointHistoryMemoryRepository(pointHistoryTable)

    @Test
    fun `특정 유저의 포인트 id가 주어지고, 주어진 id에 해당하는 모든 포인트 내역을 조회하면, 조회된 포인트 목록이 반환된다`() {
        // given
        val userId = 1L
        val expectedResult = listOf(createPointHistory(2L, userId), createPointHistory(3L, userId))
        given(pointHistoryTable.selectAllByUserId(userId)).willReturn(expectedResult)

        // when
        val actualResult = sut.findAllByUserId(userId)

        // then
        assertIterableEquals(expectedResult, actualResult)
    }

    private fun createPointHistory(id: Long, userId: Long): PointHistory {
        return PointHistory(
            id = id,
            userId = userId,
            type = TransactionType.CHARGE,
            amount = 1_000,
            timeMillis = 12345
        )
    }
}