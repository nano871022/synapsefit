package co.japl.android.synapsefit.services.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: String = "user_profile_default",
    @ColumnInfo(name = "full_name")
    val fullName: String,
    @ColumnInfo(name = "birth_date")
    val birthDate: String,
    val gender: String,
    @ColumnInfo(name = "height_cm")
    val heightCm: Double,
    @ColumnInfo(name = "blood_type")
    val bloodType: String,
    @ColumnInfo(name = "medical_conditions")
    val medicalConditions: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)
