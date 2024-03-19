package io.hhplus.tdd.repository

import io.hhplus.tdd.point.domain.PointHistory

interface PointHistoryRepository {

    fun findAllByUserId(userId: Long): List<PointHistory>
}