package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.PointHistoryTable
import org.springframework.stereotype.Service

@Service
class PointHistoryService(
    private val pointHistoryTable: PointHistoryTable
) {
}