@file:Suppress("MaxLineLength")

package co.japl.android.synapsefit.services.wear

import co.japl.android.synapsefit.core.domain.model.LiveSyncEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WearableStateMirrorAdapterTest {
    @Test
    fun testSerializeAndDeserializeStartSession() {
        val event = LiveSyncEvent.StartSession(planId = "p1", day = 2, sessionStartTimestamp = 1000L)
        val json = WearableStateMirrorAdapter.serializeEvent(event)
        val deserialized = WearableStateMirrorAdapter.deserializeEvent(json)

        assertNotNull(deserialized)
        assertTrue(deserialized is LiveSyncEvent.StartSession)
        val startSession = deserialized as LiveSyncEvent.StartSession
        assertEquals("p1", startSession.planId)
        assertEquals(2, startSession.day)
        assertEquals(1000L, startSession.sessionStartTimestamp)
    }

    @Test
    fun testSerializeAndDeserializeSelectExercise() {
        val event = LiveSyncEvent.SelectExercise(exerciseId = "ex1", exerciseName = "Pushups")
        val json = WearableStateMirrorAdapter.serializeEvent(event)
        val deserialized = WearableStateMirrorAdapter.deserializeEvent(json)

        assertNotNull(deserialized)
        assertTrue(deserialized is LiveSyncEvent.SelectExercise)
        val selectEx = deserialized as LiveSyncEvent.SelectExercise
        assertEquals("ex1", selectEx.exerciseId)
        assertEquals("Pushups", selectEx.exerciseName)
    }

    @Test
    fun testSerializeAndDeserializeCompleteSetWithCooldown() {
        val event =
            LiveSyncEvent.CompleteSet(
                exerciseId = "ex1",
                setIndex = 2,
                reps = 10,
                weightKg = 40.5,
                targetTimestamp = 123456789L,
                cooldownDurationSeconds = 45,
            )
        val json = WearableStateMirrorAdapter.serializeEvent(event)
        val deserialized = WearableStateMirrorAdapter.deserializeEvent(json)

        assertNotNull(deserialized)
        assertTrue(deserialized is LiveSyncEvent.CompleteSet)
        val completeSet = deserialized as LiveSyncEvent.CompleteSet
        assertEquals("ex1", completeSet.exerciseId)
        assertEquals(2, completeSet.setIndex)
        assertEquals(10, completeSet.reps)
        assertEquals(40.5, completeSet.weightKg, 0.001)
        assertEquals(123456789L, completeSet.targetTimestamp)
        assertEquals(45, completeSet.cooldownDurationSeconds)
    }

    @Test
    fun testSerializeAndDeserializeSkipExercise() {
        val event = LiveSyncEvent.SkipExercise(exerciseId = "ex2")
        val json = WearableStateMirrorAdapter.serializeEvent(event)
        val deserialized = WearableStateMirrorAdapter.deserializeEvent(json)

        assertNotNull(deserialized)
        assertTrue(deserialized is LiveSyncEvent.SkipExercise)
        val skip = deserialized as LiveSyncEvent.SkipExercise
        assertEquals("ex2", skip.exerciseId)
    }

    @Test
    fun testSerializeAndDeserializeFinishSession() {
        val event = LiveSyncEvent.FinishSession(planId = "p1", totalDurationSeconds = 1200L)
        val json = WearableStateMirrorAdapter.serializeEvent(event)
        val deserialized = WearableStateMirrorAdapter.deserializeEvent(json)

        assertNotNull(deserialized)
        assertTrue(deserialized is LiveSyncEvent.FinishSession)
        val finish = deserialized as LiveSyncEvent.FinishSession
        assertEquals("p1", finish.planId)
        assertEquals(1200L, finish.totalDurationSeconds)
    }

    @Test
    fun testSendEventEmitsToSharedFlow() =
        runTest {
            val adapter = WearableStateMirrorAdapter(coroutineScope = backgroundScope)
            val event = LiveSyncEvent.StartSession("p1", 1, 100L)

            adapter.sendEvent(event)

            val emitted = adapter.liveSyncEvents.first()
            assertEquals(event, emitted)
        }

    @Test
    fun testOnEventReceivedEmitsToSharedFlow() =
        runTest {
            val adapter = WearableStateMirrorAdapter(coroutineScope = backgroundScope)
            val event = LiveSyncEvent.SkipExercise("ex_test")

            adapter.onEventReceived(event)

            val emitted = adapter.liveSyncEvents.first()
            assertEquals(event, emitted)
        }

    @Test
    fun testConnectionStateChanged() {
        val adapter = WearableStateMirrorAdapter()
        assertTrue(adapter.isConnected.value)

        adapter.onConnectionStateChanged(false)
        assertEquals(false, adapter.isConnected.value)

        adapter.onConnectionStateChanged(true)
        assertEquals(true, adapter.isConnected.value)
    }
}
