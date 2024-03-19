package io.hhplus.tdd.repository

import io.hhplus.tdd.point.domain.UserPoint

interface UserPointRepository {

    /**
     * Id로 user point 정보를 단건 조회한다.
     *
     * @param id 조회하고자 하는 point의 id
     * @return 조회된 user point 정보
     */
    fun getById(id: Long): UserPoint
}