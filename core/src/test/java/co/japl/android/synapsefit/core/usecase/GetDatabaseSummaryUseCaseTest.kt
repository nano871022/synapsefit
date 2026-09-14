package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.DatabaseMetadata
import co.japl.android.synapsefit.core.domain.model.TableSummary
import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetDatabaseSummaryUseCaseTest {
    private val databaseManagerPort: DatabaseManagerPort = mockk(relaxed = true)
    private lateinit var useCase: GetDatabaseSummaryUseCase

    @Before
    fun setUp() {
        useCase = GetDatabaseSummaryUseCase(databaseManagerPort)
    }

    @Test
    fun `execute returns database metadata when successful`() =
        runTest {
            val mockTables =
                listOf(
                    TableSummary("user_profile", 1L, 5, listOf("id", "full_name")),
                    TableSummary("workout_plans", 2L, 4, listOf("id", "title")),
                )
            val mockMetadata =
                DatabaseMetadata(
                    databaseVersion = 7,
                    fileSizeBytes = 102400L,
                    lastModifiedTimestamp = 1600000000000L,
                    tables = mockTables,
                )

            coEvery { databaseManagerPort.getDatabaseMetadata() } returns Result.success(mockMetadata)

            val result = useCase.execute()

            assertTrue(result.isSuccess)
            assertEquals(7, result.getOrNull()?.databaseVersion)
            assertEquals(2, result.getOrNull()?.tables?.size)
        }

    @Test
    fun `execute returns failure when port fails`() =
        runTest {
            val exception = RuntimeException("Database error")
            coEvery { databaseManagerPort.getDatabaseMetadata() } returns Result.failure(exception)

            val result = useCase.execute()

            assertTrue(result.isFailure)
            assertEquals("Database error", result.exceptionOrNull()?.message)
        }
}
