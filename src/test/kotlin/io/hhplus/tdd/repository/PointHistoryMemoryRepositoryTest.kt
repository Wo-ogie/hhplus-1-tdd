package io.hhplus.tdd.repository

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.TransactionType
import io.hhplus.tdd.point.repository.PointHistoryMemoryRepository
import io.hhplus.tdd.point.repository.PointHistoryRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
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

    @MethodSource("savePointHistoryTestDataSet")
    @ParameterizedTest
    fun `포인트 충전,이용 내역을 저장한다`(
        userId: Long,
        amount: Long,
        transactionType: TransactionType,
        updateMillis: Long
    ) {
        // given
        val expectedResult = createPointHistory(10L, userId, amount, transactionType, updateMillis)
        given(pointHistoryTable.insert(userId, amount, transactionType, updateMillis)).willReturn(expectedResult)

        // when
        val actualResult = sut.save(
            userId = userId,
            amount = amount,
            transactionType = transactionType,
            updateMillis = updateMillis
        )

        // then
        then(pointHistoryTable).should().insert(userId, amount, transactionType, updateMillis)
        then(pointHistoryTable).shouldHaveNoMoreInteractions()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    companion object {
        @JvmStatic
        fun savePointHistoryTestDataSet() = listOf(
            Arguments.of(1L, 1000L, TransactionType.CHARGE, 12345L),
            Arguments.of(2L, 500L, TransactionType.USE, 1_000_000L)
        )
    }

    private fun createPointHistory(
        id: Long,
        userId: Long,
        amount: Long,
        transactionType: TransactionType,
        updateMillis: Long
    ): PointHistory {
        return PointHistory(
            id = id,
            userId = userId,
            amount = amount,
            type = transactionType,
            timeMillis = updateMillis
        )
    }

    private fun createPointHistory(id: Long, userId: Long): PointHistory {
        return PointHistory(
            id = id,
            userId = userId,
            amount = 1_000,
            type = TransactionType.CHARGE,
            timeMillis = 12345
        )
    }
}