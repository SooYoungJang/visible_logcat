package com.sooyoungjang.debuglibrary.domain.datastore

import com.appmattus.kotlinfixture.kotlinFixture
import com.sooyoungjang.debuglibrary.domain.datastore.usecase.WriteDataStoreUseCase
import com.sooyoungjang.debuglibrary.util.di.MainCoroutineRule
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(MainCoroutineRule::class)
class WriteDataStoreUseCaseTest {

    val fixture = kotlinFixture()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var useCase: WriteDataStoreUseCase
    private val dataStoreRepository: DataStoreRepository = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        useCase = WriteDataStoreUseCase(dataStoreRepository)
    }

    @Test
    fun `요청하면 repository write 함수를 실행 한다`() = runTest {
        //given
        val excepted = fixture<Boolean>()

        //when
        useCase.run(excepted)

        //then

        coVerify { dataStoreRepository.writeNeverSeeAgain(excepted) }
    }
}