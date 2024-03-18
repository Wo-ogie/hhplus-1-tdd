package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.domain.PointHistory
import org.springframework.stereotype.Service

@Service
class PointHistoryService(
    private val pointHistoryTable: PointHistoryTable
) {
    fun findHistoriesByUserId(userId: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(userId)
    }
}