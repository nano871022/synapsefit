package co.japl.android.synapsefit.services.repository

import app.cash.turbine.test
import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.services.database.dao.BodyMeasurementDao
import co.japl.android.synapsefit.services.database.dao.WorkoutLogDao
import co.japl.android.synapsefit.services.database.entity.BodyMeasurementEntity
import co.japl.android.synapsefit.services.database.entity.WorkoutLogEntity
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RepositoryAdaptersTest {
    private val bodyMeasurementDao: BodyMeasurementDao = mockk(relaxed = true)
    private val adapter = BodyMeasurementRepositoryAdapter(bodyMeasurementDao)

    @Test
    fun `getLatestMeasurement maps entity to domain`() =
        runTest {
            val now = System.currentTimeMillis()
            val entity =
                BodyMeasurementEntity(
                    id = "bm1",
                    weightKg = 70.0,
                    createdAt = now,
                    updatedAt = now,
                )
            every { bodyMeasurementDao.getLatestMeasurement() } returns flowOf(entity)

            adapter.getLatestMeasurement().test {
                val item = awaitItem()
                assertEquals("bm1", item?.id)
                assertEquals(70.0, item?.weightKg ?: 0.0, 0.01)
                awaitComplete()
            }
        }

    @Test
    fun `saveMeasurement delegates to dao`() =
        runTest {
            val now = System.currentTimeMillis()
            val domain =
                BodyMeasurement(
                    id = "bm1",
                    weightKg = 70.0,
                    createdAt = now,
                    updatedAt = now,
                )

            adapter.saveMeasurement(domain)

            coVerify { bodyMeasurementDao.insertMeasurement(any()) }
        }

    @Test
    fun `getAllLogs in WorkoutLogRepositoryAdapter maps entities to domain`() =
        runTest {
            val dao = mockk<WorkoutLogDao>(relaxed = true)
            val logAdapter = WorkoutLogRepositoryAdapter(dao)
            val now = System.currentTimeMillis()
            val entities =
                listOf(
                    WorkoutLogEntity(
                        id = "log1",
                        exerciseId = "ex1",
                        repsCompleted = 10,
                        weightLiftedKg = 80.0,
                        heartRateBpm = 130,
                        sourceDevice = "MOBILE",
                        timestamp = now,
                        createdAt = now,
                        updatedAt = now,
                    ),
                )
            every { dao.getAllLogs() } returns flowOf(entities)

            logAdapter.getAllLogs().test {
                val list = awaitItem()
                assertEquals(1, list.size)
                assertEquals("log1", list.first().id)
                assertEquals("ex1", list.first().exerciseId)
                awaitComplete()
            }
        }
}
