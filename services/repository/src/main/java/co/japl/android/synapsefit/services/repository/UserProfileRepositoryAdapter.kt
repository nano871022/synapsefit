package co.japl.android.synapsefit.services.repository

import co.japl.android.synapsefit.core.domain.model.UserProfile
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort
import co.japl.android.synapsefit.services.database.dao.UserProfileDao
import co.japl.android.synapsefit.services.database.mapper.toDomain
import co.japl.android.synapsefit.services.database.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserProfileRepositoryAdapter(
    private val userProfileDao: UserProfileDao,
) : UserProfileRepositoryPort {
    override fun getUserProfile(): Flow<UserProfile?> {
        return userProfileDao.getUserProfile().map { it?.toDomain() }
    }

    override suspend fun saveUserProfile(userProfile: UserProfile) {
        userProfileDao.saveUserProfile(userProfile.toEntity())
    }
}
