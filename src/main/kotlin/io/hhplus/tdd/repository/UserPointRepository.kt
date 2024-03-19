package io.hhplus.tdd.repository

import io.hhplus.tdd.point.domain.UserPoint

interface UserPointRepository {

    fun getById(id: Long): UserPoint
}