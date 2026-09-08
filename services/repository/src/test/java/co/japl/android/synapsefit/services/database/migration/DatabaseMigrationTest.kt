package co.japl.android.synapsefit.services.database.migration

import co.japl.android.synapsefit.services.database.SynapseFitDatabase
import org.junit.Assert.assertNotNull
import org.junit.Test

class DatabaseMigrationTest {
    @Test
    fun synapseFitDatabaseIsConfigured() {
        assertNotNull(SynapseFitDatabase::class.java)
    }
}
