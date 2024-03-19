package io.hhplus.tdd.point.api

import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.UserPoint
import io.hhplus.tdd.point.service.PointService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/point")
class PointController(
    private val pointService: PointService,
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("{id}")
    fun point(
        @PathVariable id: Long,
    ): UserPoint {
        require(id > 0) { "Id는 0보다 커야 합니다. id=${id}" }
        return pointService.getPointById(id)
    }

    @GetMapping("{id}/histories")
    fun history(
        @PathVariable id: Long,
    ): List<PointHistory> {
        require(id > 0) { "Id는 0보다 커야 합니다. id=${id}" }
        return pointService.findPointHistoriesByUserId(id)
    }

    @PatchMapping("{id}/charge")
    fun charge(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): UserPoint {
        require(amount > 0) { "충전 금액은 0보다 커야 합니다. amount=${amount}" }
        return pointService.chargePoint(userId = id, amount = amount)
    }

    @PatchMapping("{id}/use")
    fun use(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): UserPoint {
        require(amount > 0) { "사용 포인트는 0보다 커야 합니다. amount=$amount" }
        return pointService.usePoint(userId = id, amount = amount)
    }
}