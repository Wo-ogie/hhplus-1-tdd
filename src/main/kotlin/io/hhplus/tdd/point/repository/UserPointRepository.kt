package io.hhplus.tdd.point.repository

import io.hhplus.tdd.point.domain.UserPoint

interface UserPointRepository {

    /**
     * Id로 user point 정보를 단건 조회한다.
     *
     * @param id 조회하고자 하는 point의 id
     * @return 조회된 user point 정보
     */
    fun getById(id: Long): UserPoint

    /**
     * 유저 포인트를 저장하거나, 기존 유저 포인트를 업데이트 한다.
     *
     * @param id user point id
     * @param point 저장하거나 갱신할 포인트
     * @return 저장/갱신된 user point 정보
     */
    fun saveOrUpdate(id: Long, point: Long): UserPoint
}