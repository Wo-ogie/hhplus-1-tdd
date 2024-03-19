package io.hhplus.tdd.point.service

import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.TransactionType
import io.hhplus.tdd.point.domain.UserPoint
import io.hhplus.tdd.repository.PointHistoryRepository
import io.hhplus.tdd.repository.UserPointRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PointServiceTest {

    @InjectMocks
    private lateinit var sut: PointService

    @Mock
    private lateinit var userPointRepository: UserPointRepository

    @Mock
    private lateinit var pointHistoryRepository: PointHistoryRepository

    @Test
    fun `주어진 id로 유저의 포인트를 조회하면 조회된 유저 포인트가 반환된다`() {
        // given
        val userPointId = 1L
        val expectedResult = createUserPoint(userPointId)
        given(userPointRepository.getById(userPointId)).willReturn(expectedResult)

        // when
        val actualResult = sut.getPointById(userPointId)

        // then
        then(userPointRepository).should().getById(userPointId)
        then(userPointRepository).shouldHaveNoMoreInteractions()
        then(pointHistoryRepository).shouldHaveNoInteractions()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `특정 유저의 포인트 id가 주어지고, 주어진 id에 해당하는 모든 포인트 내역을 조회하면, 조회된 포인트 목록이 반환된다`() {
        // given
        val userId = 1L
        val expectedResult = listOf(
            createPointHistory(2L, userId),
            createPointHistory(3L, userId),
        )
        given(pointHistoryRepository.findAllByUserId(userId)).willReturn(expectedResult)

        // when
        val actualResult = sut.findPointHistoriesByUserId(userId)

        // then
        assertIterableEquals(expectedResult, actualResult)
        then(pointHistoryRepository).should().findAllByUserId(userId)
        then(userPointRepository).shouldHaveNoInteractions()
        then(pointHistoryRepository).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `특정 유저의 포인트를 충전하면, 충전된 유저 포인트 정보가 반환된다`() {
        // given
        val userId = 1L
        val amount = 1_000L
        val previousUserPoint = createUserPoint(userId, 500L, 1_000_000L)
        val expectedResult = createUserPoint(
            id = userId,
            point = previousUserPoint.point + amount,
            updateMillis = 1_000_123L
        )
        given(userPointRepository.getById(userId)).willReturn(previousUserPoint)
        given(
            userPointRepository.saveOrUpdate(
                id = userId,
                point = previousUserPoint.point + amount
            )
        ).willReturn(expectedResult)
        given(
            pointHistoryRepository.save(
                userId = userId,
                amount = amount,
                transactionType = TransactionType.CHARGE,
                updateMillis = expectedResult.updateMillis
            )
        ).willReturn(createPointHistory(2L, userId, TransactionType.CHARGE))

        // when
        val actualResult = sut.chargePoint(userId, amount)

        // then
        then(userPointRepository).should().getById(userId)
        then(userPointRepository).should().saveOrUpdate(userId, previousUserPoint.point + amount)
        then(pointHistoryRepository).should().save(userId, amount, TransactionType.CHARGE, expectedResult.updateMillis)
        then(userPointRepository).shouldHaveNoMoreInteractions()
        then(pointHistoryRepository).shouldHaveNoMoreInteractions()
        assertThat(actualResult).isEqualTo(expectedResult)
        assertThat(actualResult.id).isEqualTo(previousUserPoint.id)
        assertThat(actualResult.point).isGreaterThan(previousUserPoint.point)
        assertThat(actualResult.updateMillis).isNotEqualTo(previousUserPoint.updateMillis)
    }

    @Test
    fun `특정 유저가 포인트를 사용하면, 사용한 액수만큼 포인트가 차감되고, 변경된 유저 포인트 정보가 반환된다`() {
        // given
        val userId = 1L
        val amount = 1_000L
        val previousUserPoint = createUserPoint(userId, 1500L, 1_000_000L)
        val expectedResult = createUserPoint(
            id = userId,
            point = previousUserPoint.point - amount,
            updateMillis = 1_000_123L
        )
        given(userPointRepository.getById(userId)).willReturn(previousUserPoint)
        given(userPointRepository.saveOrUpdate(userId, previousUserPoint.point - amount)).willReturn(expectedResult)
        given(pointHistoryRepository.save(userId, amount, TransactionType.USE, expectedResult.updateMillis))
            .willReturn(createPointHistory(2L, userId, TransactionType.USE))

        // when
        val actualResult = sut.usePoint(userId, amount)

        // then
        then(userPointRepository).should().getById(userId)
        then(userPointRepository).should().saveOrUpdate(userId, previousUserPoint.point - amount)
        then(pointHistoryRepository).should().save(userId, amount, TransactionType.USE, expectedResult.updateMillis)
        then(userPointRepository).shouldHaveNoMoreInteractions()
        then(pointHistoryRepository).shouldHaveNoMoreInteractions()
        assertThat(actualResult).isEqualTo(expectedResult)
        assertThat(actualResult.id).isEqualTo(previousUserPoint.id)
        assertThat(actualResult.point).isLessThan(previousUserPoint.point)
        assertThat(actualResult.updateMillis).isNotEqualTo(previousUserPoint.updateMillis)
    }

    @Test
    fun `보유한 포인트가 사용하려는 포인트보다 적을 때, 유저가 포인트를 사용하려고 하면, 예외가 발생한다`() {
        // given
        val userId = 1L
        val amount = 1_000L
        val previousUserPoint = createUserPoint(userId, 0L, 1_000_000L)
        given(userPointRepository.getById(userId)).willReturn(previousUserPoint)

        // when
        val throwable = catchThrowable { sut.usePoint(userId, amount) }

        // then
        then(userPointRepository).should().getById(userId)
        then(userPointRepository).shouldHaveNoMoreInteractions()
        then(pointHistoryRepository).shouldHaveNoInteractions()
        assertThat(throwable).isInstanceOf(IllegalArgumentException::class.java)
    }

    private fun createUserPoint(id: Long, point: Long, updateMillis: Long): UserPoint {
        return UserPoint(id = id, point = point, updateMillis = updateMillis)
    }

    private fun createUserPoint(id: Long): UserPoint {
        return UserPoint(id = id, point = 10L, updateMillis = 12345L)
    }

    private fun createPointHistory(id: Long, userId: Long, transactionType: TransactionType): PointHistory {
        return PointHistory(id, userId, transactionType, 1_000, 12345)
    }

    private fun createPointHistory(id: Long, userId: Long): PointHistory {
        return PointHistory(id, userId, TransactionType.CHARGE, 1_000, 12345)
    }
}