package io.hhplus.tdd.repository

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.domain.PointHistory
import org.springframework.stereotype.Repository

@Repository
class PointHistoryMemoryRepository(
    private val pointHistoryTable: PointHistoryTable
) : PointHistoryRepository {

    override fun findAllByUserId(userId: Long): List<PointHistory> {
        TODO("Not yet implemented")
    }
}