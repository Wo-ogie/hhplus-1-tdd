package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.domain.UserPoint
import io.hhplus.tdd.point.repository.PointHistoryRepository
import io.hhplus.tdd.point.repository.UserPointMemoryRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

/**
 * PointService의 동시성 제어(실행 순서 보장) 테스트를 수행하는 test class
 */
class PointServiceSyncTest {

    /**
     * 포인트 사용/충전이 각각 얼마나 시간이 걸리던 상관 없이 다음 순서대로 실행되어야 한다.
     * 1. 1000 포인트 사용 (보유 포인트가 없으니 에러가 발생해야 함)
     * 2. 2000 포인트 충전
     */
    @Test
    fun `짧은 시간 동안 여러 번의 포인트 충전 및 사용이 일어나도 실행 순서가 보장된다`() {
        // given
        val userPointTable = UserPointTable()
        val pointRepositoryStubWithLazyLoading = object : UserPointMemoryRepository(userPointTable) {
            override fun getById(id: Long): UserPoint {
                Thread.sleep(500L)  // 포인트 사용이 시작하기 전, 포인트 충전 로직이 동작하도록 하기 위해 대기
                return userPointTable.selectById(id)
            }

            override fun saveOrUpdate(id: Long, point: Long): UserPoint {
                return userPointTable.insertOrUpdate(id, point)
            }
        }
        val pointRepositoryStub = object : UserPointMemoryRepository(userPointTable) {
            override fun getById(id: Long): UserPoint {
                return userPointTable.selectById(id)
            }

            override fun saveOrUpdate(id: Long, point: Long): UserPoint {
                return userPointTable.insertOrUpdate(id, point)
            }
        }
        val pointHistoryRepository: PointHistoryRepository = mock()
        val pointServiceWithLazyLoading = PointService(pointRepositoryStubWithLazyLoading, pointHistoryRepository)
        val pointService = PointService(pointRepositoryStub, pointHistoryRepository)
        val userId = 1L

        // when & then
        val useThread = Thread {
            // 잔액이 부족하여 에러가 발생하는지 검증
            assertThatThrownBy {
                pointServiceWithLazyLoading.usePoint(userId, 1000L)
            }.isInstanceOf(IllegalArgumentException::class.java)
        }
        val chargeThread = Thread {
            pointService.chargePoint(userId, 2000L)
        }

        useThread.start()
        Thread.sleep(100)   // 포인트 사용이 우선적으로 동작함을 보장하기 위해 100ms delay
        chargeThread.start()

        useThread.join()
        chargeThread.join()

        // 충전/사용 이후 남은 포인트 조회 (사용되지 않았어야 함)
        val userPoint = pointService.getPointById(userId)
        assertThat(userPoint.point).isEqualTo(2000L)
    }
}