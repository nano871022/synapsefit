package co.japl.android.synapsefit.services.wear

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WearSyncAdapterTest {
    private lateinit var adapter: WearableSyncAdapter

    @Before
    fun setUp() {
        adapter = WearableSyncAdapter(null)
    }

    @Test
    fun testQueueDataAndConnectionState() {
        assertEquals(0, adapter.pendingSyncDataCount.value)
        assertTrue(adapter.isPhoneConnected.value)

        adapter.queueDataForDeferredSync("ex-1", 10, 120)
        assertEquals(1, adapter.pendingSyncDataCount.value)

        adapter.onConnectionStateChanged(false)
        assertFalse(adapter.isPhoneConnected.value)
    }

    @Test
    fun testFlushWhenDisconnected() {
        adapter.queueDataForDeferredSync("ex-1", 10, 120)
        adapter.onConnectionStateChanged(false)

        adapter.flushSyncQueue()

        // Should retain queue when disconnected
        assertEquals(1, adapter.pendingSyncDataCount.value)
    }
}
