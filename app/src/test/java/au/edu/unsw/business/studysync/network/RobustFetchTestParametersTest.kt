package au.edu.unsw.business.studysync.network

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.acra.ACRA
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import au.edu.unsw.business.studysync.constants.Constants.GROUP_AFFINE
import au.edu.unsw.business.studysync.workers.FetchTestParametersWorker

import org.junit.jupiter.api.Test
import java.lang.Exception

internal class RobustFetchTestParametersTest {

    private fun setFetchValues(testGroup: Int?, treatmentIntensity: Int?, treatmentLimit: Int?) {
        // Helper function to set values for mock objects for testing the fetch function

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        mockkStatic(ACRA::class)
        every { ACRA.errorReporter.handleSilentException(any()) } returns Unit

        mockkObject(SyncApi)
        coEvery { SyncApi.service.getTestParams(any(), any()) } returns
            ApiResponse("dummy message", GetTestParamsResponse(testGroup, treatmentIntensity, treatmentLimit))
        coEvery { SyncApi.service.getTestParams("invalidToken", any()) } returns
                ApiResponse("dummy message", null)

    }

    @Test
    fun `Successfully fetch data from api`() {
        val testGroup = 1
        val treatmentIntensity = 2
        val treatmentLimit = 3

        setFetchValues(testGroup, treatmentIntensity, treatmentLimit)

        runBlocking {
            val result = RobustFetchTestParameters.fetch("token", "subjectId")
            assertTrue(result.isSuccess)
            val (rTest, rIntensity, rLimit)  = result.getOrNull()!!

            assertEquals(testGroup, rTest)
            assertEquals(treatmentIntensity, rIntensity)
            assertEquals(treatmentLimit, rLimit)
        }

        // Check that the api was called
        coVerify { SyncApi.service.getTestParams("token", "subjectId") }
    }

    @Test
    fun `Failed to fetch data from api`() {
        setFetchValues(null, null, null)

        runBlocking {
            val result = RobustFetchTestParameters.fetch("invalidToken", "subjectId")
            assertFalse(result.isSuccess)
        }

        // Check that the error was reported
        coVerify { ACRA.errorReporter.handleSilentException(any()) }
    }

    @Test
    fun `Invalid testGroup supplied`() {
        setFetchValues(null, 2, 3)

        runBlocking {
            val result = RobustFetchTestParameters.fetch("token", "subjectId")
            assertFalse(result.isSuccess)
            result.exceptionOrNull()!!
        }
    }

    @Test
    fun `Subject is in affine test group but treatment intensity is null`() {
        setFetchValues(GROUP_AFFINE, null, 3)

        runBlocking {
            val result = RobustFetchTestParameters.fetch("token", "subjectId")
            assertFalse(result.isSuccess)
            result.exceptionOrNull()!!
        }
    }

    @Test
    fun `Subject is in affine test group but treatment limit is null`() {
        setFetchValues(GROUP_AFFINE, 2, null)

        runBlocking {
            val result = RobustFetchTestParameters.fetch("token", "subjectId")
            assertFalse(result.isSuccess)
            result.exceptionOrNull()!!
        }
    }

    private fun setupFetchOrScheduleTests() {
        // Helper function to set up the fetchOrScheduleRetry tests

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        mockkObject(RobustFetchTestParameters)
        coEvery { RobustFetchTestParameters.fetch(any(), any()) } returns Result.success(Triple(1, 2, 3))
        coEvery { RobustFetchTestParameters.fetch("invalidToken", any()) } returns Result.failure(Exception())

        mockkObject(FetchTestParametersWorker)
        coEvery { FetchTestParametersWorker.createRequest() } returns mockk(relaxed = true)

        mockkStatic(WorkManager::class)
        coEvery { WorkManager.getInstance(any()).enqueueUniqueWork(any(), any(), any<OneTimeWorkRequest>()) } returns mockk(relaxed = true)
    }

    @Test
    fun `FetchOrScheduleRetry should fetch data`() {
        setupFetchOrScheduleTests()

        val mockedContext = mockk<Context>(relaxed = true)

        runBlocking {
            val result =
                RobustFetchTestParameters.fetchOrScheduleRetry(mockedContext, "token", "subjectId")
            assertTrue(result.isSuccess)
            val (rTest, rIntensity, rLimit) = result.getOrNull()!!
            assertEquals(1, rTest)
            assertEquals(2, rIntensity)
            assertEquals(3, rLimit)
        }
    }

    @Test
    fun `FetchOrScheduleRetry should schedule a retry`() {
        setupFetchOrScheduleTests()

        val mockedContext = mockk<Context>(relaxed = true)

        runBlocking {
            val result = RobustFetchTestParameters.fetchOrScheduleRetry(mockedContext,"invalidToken", "subjectId")
            assertFalse(result.isSuccess)
            result.exceptionOrNull()!!
        }

        coVerify { FetchTestParametersWorker.createRequest() }
        coVerify { WorkManager.getInstance(any()).enqueueUniqueWork(any(), any(), any<OneTimeWorkRequest>()) }
    }

    @AfterEach
    fun afterTests() {
        unmockkAll()
    }
}
