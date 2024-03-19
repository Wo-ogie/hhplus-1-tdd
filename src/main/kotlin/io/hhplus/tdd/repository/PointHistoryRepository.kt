package io.hhplus.tdd.repository

import io.hhplus.tdd.point.domain.PointHistory

interface PointHistoryRepository {

    /**
     * User id에 해당하는 포인트 충전/이용 내역을 전부 조회한다.
     *
     * @param userId 포인트 충전/이용 내역을 조회하고자 하는 유저의 id
     * @return 조회된 포인트 충전/이용 내역 목록
     */
    fun findAllByUserId(userId: Long): List<PointHistory>
}