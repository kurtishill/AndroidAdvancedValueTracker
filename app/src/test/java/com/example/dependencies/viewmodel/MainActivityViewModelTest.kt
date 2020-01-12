package com.example.dependencies.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.dependencies.api.SendCodeResponse
import com.example.dependencies.injection.Injector
import com.example.dependencies.provider.CodeProvider
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class MainActivityViewModelTest {
    private val CODE = 1
    private val VALUE = 10
    private val FAILURE_MESSAGE = "Failure"

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Mock lateinit var codeProvider: CodeProvider

    @InjectMocks var SUT = MainActivityViewModel()

    private var testSingle: Single<SendCodeResponse>? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Before
    fun setupRxSchedulers() {
        val immediate = object : Scheduler() {
            override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
                return super.scheduleDirect(run, 0, unit)
            }

            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() })
            }
        }

        RxJavaPlugins.setInitIoSchedulerHandler { immediate }
        RxJavaPlugins.setInitSingleSchedulerHandler { immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }
    }

    @Test
    fun postNumber_codeSent_valueAndSuccessReturned() {
        // Arrange
        val code = CODE
        val sendCodeResponse = SendCodeResponse("", true, VALUE)
        testSingle = Single.just(sendCodeResponse)

        // Act
        Mockito.`when`(codeProvider.postNumber(code)).thenReturn(testSingle)
        SUT.postNumber(code)

        // Assert
        assertNull(SUT.success.value)
        assertEquals(VALUE, SUT.number.value)
        assertEquals(false, SUT.loading.value)
    }

    @Test
    fun postNumber_codeSent_failureReturned() {
        // Arrange
        val code = CODE
        val sendCodeResponse = SendCodeResponse(FAILURE_MESSAGE, false, 0)
        testSingle = Single.just(sendCodeResponse)

        // Act
        Mockito.`when`(codeProvider.postNumber(code)).thenReturn(testSingle)
        SUT.postNumber(code)

        // Assert
        assertNull(SUT.success.value)
        assertEquals(false, SUT.loading.value)
        assertEquals(FAILURE_MESSAGE, SUT.error.value)
    }

    @Test
    fun postNumber_codeSent_exceptionThrown() {
        // Arrange
        val code = CODE
        testSingle = Single.error(Throwable())

        // Act
        Mockito.`when`(codeProvider.postNumber(code)).thenReturn(testSingle)
        SUT.postNumber(code)

        // Assert
        assertEquals(false, SUT.loading.value)
        assertNull(SUT.number.value)
    }
}