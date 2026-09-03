package co.japl.android.synapsefit.services.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.japl.android.synapsefit.services.database.entity.MedicalRecommendationEntity
import co.japl.android.synapsefit.services.database.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = :id LIMIT 1")
    fun getUserProfile(id: String = "user_profile_default"): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(entity: UserProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMedicalRecommendation(entity: MedicalRecommendationEntity)

    @Query("SELECT * FROM tbl_medical_result ORDER BY create_dt DESC LIMIT 1")
    fun getLatestMedicalRecommendation(): Flow<MedicalRecommendationEntity?>
}
