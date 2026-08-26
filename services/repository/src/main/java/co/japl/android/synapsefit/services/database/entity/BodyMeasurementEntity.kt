package co.japl.android.synapsefit.services.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_measurements")
data class BodyMeasurementEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "weight_kg")
    val weightKg: Double,
    @ColumnInfo(name = "chest_cm")
    val chestCm: Double? = null,
    @ColumnInfo(name = "waist_cm")
    val waistCm: Double? = null,
    @ColumnInfo(name = "hip_cm")
    val hipCm: Double? = null,
    @ColumnInfo(name = "bicep_left_cm")
    val bicepLeftCm: Double? = null,
    @ColumnInfo(name = "bicep_right_cm")
    val bicepRightCm: Double? = null,
    @ColumnInfo(name = "thigh_left_cm")
    val thighLeftCm: Double? = null,
    @ColumnInfo(name = "thigh_right_cm")
    val thighRightCm: Double? = null,
    val notes: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)
