package io.hhplus.tdd.repository

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.TransactionType
import org.springframework.stereotype.Repository

@Repository
class PointHistoryMemoryRepository(
    private val pointHistoryTable: PointHistoryTable
) : PointHistoryRepository {

    override fun findAllByUserId(userId: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(userId)
    }

    override fun save(
        userId: Long,
        amount: Long,
        transactionType: TransactionType,
        updateMillis: Long
    ): PointHistory {
        return pointHistoryTable.insert(
            id = userId,
            amount = amount,
            transactionType = transactionType,
            updateMillis = updateMillis
        )
    }
}