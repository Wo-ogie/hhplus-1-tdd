package io.hhplus.tdd.point.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CompletableFuture

/**
 * PointService의 동시성 제어(실행 순서 보장) 테스트를 수행하는 test class
 */
@SpringBootTest
class PointServiceConcurrencyTest(
    @Autowired private val pointService: PointService,
) {

    @Disabled
    @Test
    fun `동시에 포인트 사용과 충전 요청이 들어오면, 순차적으로 처리된다`() {
        // given
        val userId = 1L

        // when & then
        CompletableFuture.allOf(
            CompletableFuture.runAsync { pointService.usePoint(userId, 400) },
            CompletableFuture.runAsync { pointService.chargePoint(userId, 200) },
        ).exceptionally { e ->
            val exception = checkNotNull(e.cause as? java.lang.IllegalArgumentException)
            assertThat(exception).isInstanceOf(IllegalArgumentException::class.java)
            return@exceptionally null
        }.join()

        val result = pointService.getPointById(userId)
        assertThat(result.point).isEqualTo(200)
    }

    @Test
    fun `동시에 여러 건의 충전 요청이 들어와도, 순차적으로 포인트 충전을 처리한다`() {
        // given
        val userId = 1L

        // when
        CompletableFuture.allOf(
            CompletableFuture.runAsync { pointService.chargePoint(userId, 400) },
            CompletableFuture.runAsync { pointService.chargePoint(userId, 200) },
            CompletableFuture.runAsync { pointService.chargePoint(userId, 300) },
            CompletableFuture.runAsync { pointService.chargePoint(userId, 100) }
        ).join()

        // then
        val result = pointService.getPointById(userId)
        assertThat(result.point).isEqualTo(400 + 200 + 300 + 100)
    }
}