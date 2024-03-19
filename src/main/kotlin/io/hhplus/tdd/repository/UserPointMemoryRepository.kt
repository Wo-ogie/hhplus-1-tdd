package io.hhplus.tdd.repository

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.domain.UserPoint
import org.springframework.stereotype.Repository

@Repository
class UserPointMemoryRepository(
    private val userPointTable: UserPointTable
) : UserPointRepository {

    override fun getById(id: Long): UserPoint {
        return userPointTable.selectById(id)
    }

    override fun saveOrUpdate(id: Long, point: Long): UserPoint {
        TODO("Not yet implemented")
    }
}