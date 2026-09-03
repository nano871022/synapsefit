package co.japl.android.synapsefit.services.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_medical_result")
data class MedicalRecommendationEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "profile_code")
    val profileCode: String = "PRIMARY_USER",
    val result: String,
    @ColumnInfo(name = "create_dt")
    val createdAt: Long,
)
