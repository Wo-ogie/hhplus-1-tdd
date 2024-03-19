package io.hhplus.tdd.point.service

import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.TransactionType
import io.hhplus.tdd.point.domain.UserPoint
import io.hhplus.tdd.repository.PointHistoryRepository
import io.hhplus.tdd.repository.UserPointRepository
import org.springframework.stereotype.Service

@Service
class PointService(
    private val userPointRepository: UserPointRepository,
    private val pointHistoryRepository: PointHistoryRepository
) {

    /**
     * Id로 user point 정보를 단건 조회한다.
     *
     * @param id 조회하고자 하는 point의 id
     * @return 조회된 user point 정보
     */
    fun getPointById(id: Long): UserPoint {
        return userPointRepository.getById(id)
    }

    /**
     * User id에 해당하는 포인트 충전/이용 내역을 전부 조회한다.
     *
     * @param userId 포인트 충전/이용 내역을 조회하고자 하는 유저의 id
     * @return 조회된 포인트 충전/이용 내역 목록
     */
    fun findPointHistoriesByUserId(userId: Long): List<PointHistory> {
        return pointHistoryRepository.findAllByUserId(userId)
    }

    /**
     * 포인트를 충전한다.
     *
     * @param userId 포인트를 충전할 user의 id
     * @param amount 충전할 포인트 액수
     * @return 충전된 상태의 user point 정보
     */
    fun chargePoint(userId: Long, amount: Long): UserPoint {
        val userPoint = getPointById(userId)
        val chargedUserPoint = userPointRepository.saveOrUpdate(
            id = userId,
            point = userPoint.point + amount
        )
        pointHistoryRepository.save(
            userId = userId,
            amount = amount,
            transactionType = TransactionType.CHARGE,
            updateMillis = chargedUserPoint.updateMillis
        )
        return chargedUserPoint
    }
}