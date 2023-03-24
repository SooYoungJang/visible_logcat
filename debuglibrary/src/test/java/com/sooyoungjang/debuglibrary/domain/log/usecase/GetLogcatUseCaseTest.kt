package com.sooyoungjang.debuglibrary.domain.log.usecase

import com.appmattus.kotlinfixture.kotlinFixture
import com.sooyoungjang.debuglibrary.domain.log.LogRepository
import com.sooyoungjang.debuglibrary.domain.log.model.LogLevel
import com.sooyoungjang.debuglibrary.domain.log.model.LogModel
import com.sooyoungjang.debuglibrary.util.di.MainCoroutineRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(MainCoroutineRule::class)
class GetLogcatUseCaseTest {

    val fixture = kotlinFixture()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var useCase: GetLogcatUseCase
    private val logRepository:LogRepository = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        useCase = GetLogcatUseCase(logRepository)
    }

    @Test
    fun `필터링 키워드 값을 param에 담아 호출하면 해당 값과 contains 된 값만  방출 한다`() = runTest {
        //gien
        val param = GetLogcatUseCase.Params("test")
        val filteredData = listOf(
            LogModel("test occurred", LogLevel.D),
            LogModel("Another error occurred", LogLevel.W)
        )
        val excepted = flowOf(filteredData)
        every { logRepository.getLogcatData(param.searchKeyWord) } returns excepted

        //when
        val result = useCase.invoke(param)

        //then
        assertEquals(excepted, result)
    }

}