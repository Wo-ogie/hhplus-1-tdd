package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.domain.UserPoint
import org.springframework.stereotype.Service

@Service
class UserPointService(
        private val userPointTable: UserPointTable,
) {
    // TODO: 기능 구현
    fun getPointById(id: Long): UserPoint = UserPoint(0, 0, 0)
}