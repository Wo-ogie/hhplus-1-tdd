package io.hhplus.tdd.point.repository

import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.TransactionType

interface PointHistoryRepository {

    /**
     * User id에 해당하는 포인트 충전/이용 내역을 전부 조회한다.
     *
     * @param userId 포인트 충전/이용 내역을 조회하고자 하는 유저의 id
     * @return 조회된 포인트 충전/이용 내역 목록
     */
    fun findAllByUserId(userId: Long): List<PointHistory>

    /**
     * 포인트 충전/이용 내역을 저장한다.
     *
     * @param userId user point id
     * @param amount 충전/이용한 포인트
     * @param transactionType 거래 종류 (충전 / 이용)
     * @param updateMillis 수정 시각
     */
    fun save(
        userId: Long,
        amount: Long,
        transactionType: TransactionType,
        updateMillis: Long
    ): PointHistory
}