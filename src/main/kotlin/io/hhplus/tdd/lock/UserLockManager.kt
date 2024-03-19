package io.hhplus.tdd.lock

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

/**
 * <p>단일 유저에 대한 lock을 관리하는 class.
 * <p>특정 유저에 대한 lock을 취득하고 해제함으로써 해당 유저의 동작들을 동기화 할 수 있다.
 */

object UserLockManager {

    private val userLocks = ConcurrentHashMap<Long, ReentrantLock>()

    fun <T> withLock(userId: Long, action: () -> T): T {
        val userLock = getUserLock(userId)
        userLock.lock()
        try {
            return action()
        } finally {
            userLock.unlock()
        }
    }

    private fun getUserLock(userId: Long): ReentrantLock {
        return userLocks.computeIfAbsent(userId) { ReentrantLock() }
    }
}