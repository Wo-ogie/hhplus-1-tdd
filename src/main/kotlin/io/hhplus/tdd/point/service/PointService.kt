package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.UserPoint
import org.springframework.stereotype.Service

@Service
class PointService(
    private val userPointTable: UserPointTable,
    private val pointHistoryTable: PointHistoryTable
) {
    fun getPointById(id: Long): UserPoint {
        return userPointTable.selectById(id)
    }

    fun findHistoriesByUserId(userId: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(userId)
    }
}