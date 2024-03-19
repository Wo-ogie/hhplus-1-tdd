package io.hhplus.tdd.repository

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.domain.UserPoint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserPointMemoryRepositoryTest {

    private val userPointTable: UserPointTable = mock()
    private val sut: UserPointRepository = UserPointMemoryRepository(userPointTable)

    @Test
    fun `주어진 id로 유저의 포인트를 조회하면 조회된 유저 포인트가 반환된다`() {
        // given
        val userPointId = 1L
        val expectedResult = createUserPoint(userPointId)
        given(userPointTable.selectById(userPointId)).willReturn(expectedResult)

        // when
        val actualResult = sut.getById(userPointId)

        // then
        then(userPointTable).should().selectById(userPointId)
        then(userPointTable).shouldHaveNoMoreInteractions()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `유저 포인트를 신규 저장하거나, 기존 유저 포인트를 갱신한다`() {
        // given
        val userPointId = 1L
        val amount = 1000L
        val expectedResult = createUserPoint(userPointId, amount)
        given(userPointTable.insertOrUpdate(userPointId, amount)).willReturn(expectedResult)

        // when
        val actualResult = sut.saveOrUpdate(userPointId, amount)

        // then
        then(userPointTable).should().insertOrUpdate(userPointId, amount)
        then(userPointTable).shouldHaveNoMoreInteractions()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    private fun createUserPoint(id: Long, point: Long): UserPoint {
        return UserPoint(id = id, point = point, updateMillis = 12345L)
    }

    private fun createUserPoint(id: Long): UserPoint {
        return UserPoint(id = id, point = 10L, updateMillis = 12345L)
    }
}