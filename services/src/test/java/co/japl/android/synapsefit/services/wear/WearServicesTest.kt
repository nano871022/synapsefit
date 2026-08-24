package co.japl.android.synapsefit.services.wear

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WearServicesTest {

    @Test
    fun testWearHeartRateSensorAdapter() {
        val adapter = WearHeartRateSensorAdapter()
        assertFalse(adapter.isMonitoring.value)

        adapter.startHeartRateMonitoring()
        assertTrue(adapter.isMonitoring.value)

        adapter.onHeartRateSensorChanged(135)
        assertEquals(135, adapter.heartRateBpm.value)

        adapter.stopHeartRateMonitoring()
        assertFalse(adapter.isMonitoring.value)
    }

    @Test
    fun testWearableSyncAdapter() {
        val adapter = WearableSyncAdapter()
        assertTrue(adapter.isPhoneConnected.value)

        adapter.queueDataForDeferredSync("ex-101", 12, 140)
        assertEquals(1, adapter.pendingSyncDataCount.value)

        adapter.onConnectionStateChanged(false)
        assertFalse(adapter.isPhoneConnected.value)

        adapter.flushSyncQueue()
        assertEquals(1, adapter.pendingSyncDataCount.value)

        adapter.onConnectionStateChanged(true)
        adapter.flushSyncQueue()
        assertEquals(0, adapter.pendingSyncDataCount.value)
    }
}
