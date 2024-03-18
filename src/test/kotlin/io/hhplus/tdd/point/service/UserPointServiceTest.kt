package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.domain.UserPoint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserPointServiceTest {

    @InjectMocks
    private lateinit var sut: UserPointService

    @Mock
    private lateinit var userPointTable: UserPointTable

    @Test
    fun `주어진 id로 유저의 포인트를 조회하면 조회된 유저 포인트가 반환된다`() {
        // given
        val userPointId = 1L
        val expectedResult = createUserPoint(userPointId)
        given(userPointTable.selectById(userPointId)).willReturn(expectedResult)

        // when
        val actualResult = sut.getPointById(userPointId)

        // then
        then(userPointTable).should().selectById(userPointId)
        then(userPointTable).shouldHaveNoMoreInteractions()
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    private fun createUserPoint(id: Long): UserPoint {
        return UserPoint(id = id, point = 10L, updateMillis = 12345L)
    }
}