@file:Suppress("MaxLineLength")

package co.japl.android.synapsefit.app.controller.settings

import co.japl.android.synapsefit.core.domain.model.DatabaseMetadata
import co.japl.android.synapsefit.core.domain.model.TableSummary
import co.japl.android.synapsefit.core.usecase.GetDatabaseSummaryUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DatabaseExplorerViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val getDatabaseSummaryUseCase: GetDatabaseSummaryUseCase = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDatabaseSummary emits Success state when usecase succeeds`() =
        runTest {
            val mockMetadata =
                DatabaseMetadata(
                    databaseVersion = 7,
                    fileSizeBytes = 50000L,
                    lastModifiedTimestamp = System.currentTimeMillis(),
                    tables = listOf(TableSummary("user_profile", 1L, 4, listOf("id"))),
                )

            coEvery { getDatabaseSummaryUseCase.execute() } returns Result.success(mockMetadata)

            val viewModel = DatabaseExplorerViewModel(getDatabaseSummaryUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is DatabaseExplorerUiState.Success)
            val successState = state as DatabaseExplorerUiState.Success
            assertEquals(7, successState.metadata.databaseVersion)
            assertEquals(1, successState.metadata.tables.size)
        }

    @Test
    fun `loadDatabaseSummary emits Error state when usecase fails`() =
        runTest {
            coEvery { getDatabaseSummaryUseCase.execute() } returns
                Result.failure(
                    RuntimeException("Unable to read database"),
                )

            val viewModel = DatabaseExplorerViewModel(getDatabaseSummaryUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is DatabaseExplorerUiState.Error)
            val errorState = state as DatabaseExplorerUiState.Error
            assertEquals("Unable to read database", errorState.message)
        }
}
