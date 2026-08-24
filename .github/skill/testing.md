# Skill: Testing Strategy & Execution

> **File Location:** `.github/skills/testing.md`  
> **Target Scope:** Unit, Integration, Architecture, and UI testing across `:core`, `:services`, `:app`, and `:wear` modules in `co.japl.android.synapsefit`.

---

## 1. Testing Pyramid & Module Scope

Testing in SynapseFit follows strict layer isolation to guarantee high execution speed and reliable code quality across all modules.

```text
                     / \
                    /   \     UI & Wear Tests (Compose Test Rule / Screenshot)
                   / UI  \    [Modules: :app, :wear]
                  /-------\
                 /  Integ  \  Database & API Integration Tests (Room In-Memory, MockWebServer)
                /  Services \ [Module: :services]
               /-------------\
              /   Unit Tests  \ Pure Domain & Use Case Tests (JUnit5 + MockK + Turbine)
             /      :core      \ [Module: :core]
            /-------------------\

2. Testing Guidelines by Layer
A. :core Unit Tests (Pure Kotlin)
 * Frameworks: JUnit 5, MockK, Turbine, Kotlinx Coroutines Test.
 * Rules:
   * Zero Android Dependencies: Tests must run on the local JVM in milliseconds without Android/Robolectric runners.
   * Secondary Ports Mocking: Mock all repository/client interfaces using MockK.
   * Flow Verification: Validate all Flow emissions using Turbine.
B. :services Integration Tests (Adapters & Infrastructure)
 * Frameworks: Room In-Memory DB, MockWebServer, EncryptedSharedPreferences Test Rules.
 * Rules:
   * Verify DAOs using an in-memory SQLite database instance (Room.inMemoryDatabaseBuilder).
   * Verify Mappers bidirectional conversion (ToEntity and ToDomain) against nullability and edge cases.
   * Ensure all primary entities enforce created_at and updated_at timestamps.
C. :app & :wear ViewModel & UI Tests
 * Frameworks: Jetpack Compose Testing, UnconfinedTestDispatcher.
 * Rules:
   * ViewModel Testing: Test StateFlow emissions by executing ViewModels against fake or mocked Use Cases from :core.
   * Passive UI Testing: Verify Compose components via createComposeRule() to confirm state rendering and event lambda emissions.
3. Standard Test Implementation Examples
Example 1: Domain UseCase Test (:core)
// File: :core/src/test/java/co/japl/android/synapsefit/core/usecase/SaveBodyMeasurementUseCaseTest.kt
package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SaveBodyMeasurementUseCaseTest {

    private val repositoryPort: BodyMeasurementRepositoryPort = mockk(relaxed = true)
    private lateinit var useCase: SaveBodyMeasurementUseCase

    @BeforeEach
    fun setUp() {
        useCase = SaveBodyMeasurementUseCase(repositoryPort)
    }

    @Test
    fun `when weight is zero or negative, return failure result`() = runTest {
        val result = useCase(weightKg = -5.0, waistCm = 80.0)
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repositoryPort.saveMeasurement(any()) }
    }

    @Test
    fun `when parameters are valid, invoke repository and return success`() = runTest {
        coEvery { repositoryPort.saveMeasurement(any()) } returns Unit

        val result = useCase(weightKg = 75.5, waistCm = 82.0)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { 
            repositoryPort.saveMeasurement(match { 
                it.weightKg == 75.5 && it.waistCm == 82.0 
            }) 
        }
    }
}

Example 2: Room DAO Integration Test (:services)
// File: :services/src/test/java/co/japl/android/synapsefit/services/dao/BodyMeasurementDaoTest.kt
package co.japl.android.synapsefit.services.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import co.japl.android.synapsefit.services.db.SynapseFitDatabase
import co.japl.android.synapsefit.services.entity.BodyMeasurementEntity
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BodyMeasurementDaoTest {

    private lateinit var database: SynapseFitDatabase
    private lateinit var dao: BodyMeasurementDao

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SynapseFitDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.bodyMeasurementDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetLatestMeasurement() = runTest {
        val now = System.currentTimeMillis()
        val entity = BodyMeasurementEntity(
            id = "m1",
            weightKg = 74.2,
            chestCm = 100.0,
            waistCm = 82.0,
            hipCm = 95.0,
            bicepLeftCm = 35.0,
            bicepRightCm = 35.5,
            thighLeftCm = 58.0,
            thighRightCm = 58.0,
            notes = "Morning log",
            createdAt = now,
            updatedAt = now
        )

        dao.insert(entity)

        dao.getLatestEntity().test {
            val item = awaitItem()
            assertEquals("m1", item?.id)
            assertEquals(74.2, item?.weightKg ?: 0.0, 0.01)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

Example 3: ViewModel Test with Turbine (:app)
// File: :app/src/test/java/co/japl/android/synapsefit/app/ui/dashboard/DashboardViewModelTest.kt
package co.japl.android.synapsefit.app.ui.dashboard

import app.cash.turbine.test
import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val repositoryPort: BodyMeasurementRepositoryPort = mockk()
    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { repositoryPort.getLatestMeasurement() } returns flowOf(
            BodyMeasurement("1", 75.0, 80.0, 1000L, 1000L, 1000L)
        )
        viewModel = DashboardViewModel(repositoryPort)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads latest measurement into UiState`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(75.0, state.latestWeightKg ?: 0.0, 0.01)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

4. Verification Execution Commands
Run these local Gradle commands prior to submitting commits:
# Run all unit tests across all modules
./gradlew testDebugUnitTest

# Run pure unit tests for domain logic (:core)
./gradlew :core:test

# Run database integration tests (:services)
./gradlew :services:testDebugUnitTest

# Run static analysis and lint checks
./gradlew ktlintCheck detekt


