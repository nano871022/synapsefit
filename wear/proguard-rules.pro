# SynapseFit ProGuard Rules for Wear Module

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep entities and DAOs
-keep class co.japl.android.synapsefit.services.repository.entities.** { *; }
-keep interface co.japl.android.synapsefit.services.repository.dao.** { *; }

# Domain models
-keep class co.japl.android.synapsefit.core.domain.model.** { *; }

# Gson & Serialization
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.google.gson.** { *; }

# Kotlin Coroutines
-keepclassmembers class * {
    kotlinx.coroutines.Requirement *;
}

# Play Services Wearable
-keep class com.google.android.gms.wearable.** { *; }
