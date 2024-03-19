package io.hhplus.tdd.point.service

import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.UserPoint
import io.hhplus.tdd.repository.PointHistoryRepository
import io.hhplus.tdd.repository.UserPointRepository
import org.springframework.stereotype.Service

@Service
class PointService(
    private val userPointRepository: UserPointRepository,
    private val pointHistoryRepository: PointHistoryRepository
) {
    fun getPointById(id: Long): UserPoint {
        return userPointRepository.getById(id)
    }

    fun findPointHistoriesByUserId(userId: Long): List<PointHistory> {
        return pointHistoryRepository.findAllByUserId(userId)
    }
}