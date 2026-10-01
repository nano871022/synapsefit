package co.japl.android.synapsefit.core.domain.model

data class TableSummary(
    val tableName: String,
    val recordCount: Long,
    val columnCount: Int,
    val columns: List<String> = emptyList(),
)

data class DatabaseMetadata(
    val databaseVersion: Int,
    val fileSizeBytes: Long,
    val lastModifiedTimestamp: Long,
    val tables: List<TableSummary>,
)
