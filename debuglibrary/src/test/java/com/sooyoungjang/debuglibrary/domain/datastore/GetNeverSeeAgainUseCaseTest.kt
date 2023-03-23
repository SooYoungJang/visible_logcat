package com.sooyoungjang.debuglibrary.domain.datastore

import com.appmattus.kotlinfixture.kotlinFixture
import com.sooyoungjang.debuglibrary.domain.datastore.usecase.GetNeverSeeAgainUseCase
import com.sooyoungjang.debuglibrary.util.di.MainCoroutineRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalStdlibApi
@ExtendWith(MainCoroutineRule::class)
class GetNeverSeeAgainUseCaseTest {

    val fixture = kotlinFixture()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var useCase: GetNeverSeeAgainUseCase
    private val dataStoreRepository: DataStoreRepository = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        useCase = GetNeverSeeAgainUseCase(dataStoreRepository)
    }

    @Test
    fun `요청하면 repository 값을 그대로 리턴 한다`() {
        //given
        val excepted = fixture<Boolean>()
        every { dataStoreRepository.getNeverSeeAgain() } returns excepted

        //when
        val result = useCase.exec(Unit)

        //then
        verify { dataStoreRepository.getNeverSeeAgain() }
        assertEquals(excepted, result)
    }

}