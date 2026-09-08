@file:Suppress(
    "MaxLineLength",
    "TooManyFunctions",
    "LongParameterList",
    "LongMethod",
    "CyclomaticComplexMethod",
    "TooGenericExceptionCaught",
    "ReturnCount",
    "MagicNumber",
)

package co.japl.android.synapsefit.app.controller.workout

import android.content.Context
import android.content.SharedPreferences

data class RestoredSessionState(
    val uiState: ActiveWorkoutUiState,
    val sessionStartTimestamp: Long,
    val exerciseTimeSpent: Map<String, Long>,
    val exerciseCompletedSets: Map<String, Int>,
    val exerciseMaxWeight: Map<String, Double>,
)

object WorkoutSessionStateManager {
    private const val PREF_NAME = "synapsefit_active_workout_pref"
    private const val KEY_HAS_ACTIVE_SESSION = "has_active_session"
    private const val KEY_PLAN_ID = "plan_id"
    private const val KEY_PLAN_TITLE = "plan_title"
    private const val KEY_SESSION_START_TIMESTAMP = "session_start_timestamp"
    private const val KEY_CURRENT_EXERCISE_INDEX = "current_exercise_index"
    private const val KEY_CURRENT_EXERCISE_ID = "current_exercise_id"
    private const val KEY_CURRENT_EXERCISE_NAME = "current_exercise_name"
    private const val KEY_CURRENT_SET_INDEX = "current_set_index"
    private const val KEY_TOTAL_SETS = "total_sets"
    private const val KEY_TARGET_REPS = "target_reps"
    private const val KEY_CURRENT_SET_WEIGHT = "current_set_weight"
    private const val KEY_CURRENT_SET_REPS = "current_set_reps"
    private const val KEY_EXERCISES_JSON = "exercises_json"
    private const val KEY_TIME_SPENT_JSON = "time_spent_json"
    private const val KEY_COMPLETED_SETS_JSON = "completed_sets_json"
    private const val KEY_MAX_WEIGHT_JSON = "max_weight_json"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun hasActiveSession(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_HAS_ACTIVE_SESSION, false)
    }

    fun saveSession(
        context: Context,
        uiState: ActiveWorkoutUiState,
        sessionStartTimestamp: Long,
        exerciseTimeSpent: Map<String, Long>,
        exerciseCompletedSets: Map<String, Int>,
        exerciseMaxWeight: Map<String, Double>,
    ) {
        if (uiState.planId.isBlank() || uiState.isSessionComplete) {
            clearSession(context)
            return
        }

        val exercisesJson = serializeExercises(uiState.exercises)
        val timeSpentJson = serializeMap(exerciseTimeSpent)
        val completedSetsJson = serializeMap(exerciseCompletedSets)
        val maxWeightJson = serializeMap(exerciseMaxWeight)

        getPrefs(context).edit().apply {
            putBoolean(KEY_HAS_ACTIVE_SESSION, true)
            putString(KEY_PLAN_ID, uiState.planId)
            putString(KEY_PLAN_TITLE, uiState.planTitle)
            putLong(KEY_SESSION_START_TIMESTAMP, sessionStartTimestamp)
            putInt(KEY_CURRENT_EXERCISE_INDEX, uiState.currentExerciseIndex)
            putString(KEY_CURRENT_EXERCISE_ID, uiState.currentExerciseId)
            putString(KEY_CURRENT_EXERCISE_NAME, uiState.currentExerciseName)
            putInt(KEY_CURRENT_SET_INDEX, uiState.currentSetIndex)
            putInt(KEY_TOTAL_SETS, uiState.totalSetsForCurrentExercise)
            putString(KEY_TARGET_REPS, uiState.targetRepsForCurrentSet)
            putString(KEY_CURRENT_SET_WEIGHT, uiState.currentSetWeightKg)
            putString(KEY_CURRENT_SET_REPS, uiState.currentSetReps)
            putString(KEY_EXERCISES_JSON, exercisesJson)
            putString(KEY_TIME_SPENT_JSON, timeSpentJson)
            putString(KEY_COMPLETED_SETS_JSON, completedSetsJson)
            putString(KEY_MAX_WEIGHT_JSON, maxWeightJson)
            uiState.cooldownTargetTimestamp?.let { putLong(KEY_COOLDOWN_TARGET_TIMESTAMP, it) } ?: remove(KEY_COOLDOWN_TARGET_TIMESTAMP)
            apply()
        }
    }

    fun loadSession(context: Context): RestoredSessionState? {
        val prefs = getPrefs(context)
        if (!prefs.getBoolean(KEY_HAS_ACTIVE_SESSION, false)) {
            return null
        }

        val planId = prefs.getString(KEY_PLAN_ID, "") ?: ""
        if (planId.isBlank()) return null

        val planTitle = prefs.getString(KEY_PLAN_TITLE, "") ?: ""
        val sessionStartTimestamp = prefs.getLong(KEY_SESSION_START_TIMESTAMP, System.currentTimeMillis())
        val currentExerciseIndex = prefs.getInt(KEY_CURRENT_EXERCISE_INDEX, 0)
        val currentExerciseId = prefs.getString(KEY_CURRENT_EXERCISE_ID, "") ?: ""
        val currentExerciseName = prefs.getString(KEY_CURRENT_EXERCISE_NAME, "") ?: ""
        val currentSetIndex = prefs.getInt(KEY_CURRENT_SET_INDEX, 1)
        val totalSets = prefs.getInt(KEY_TOTAL_SETS, 3)
        val targetReps = prefs.getString(KEY_TARGET_REPS, "10") ?: "10"
        val currentSetWeight = prefs.getString(KEY_CURRENT_SET_WEIGHT, "") ?: ""
        val currentSetReps = prefs.getString(KEY_CURRENT_SET_REPS, "") ?: ""

        val exercisesList = parseExercises(prefs.getString(KEY_EXERCISES_JSON, "[]") ?: "[]")
        val timeSpentMap = parseLongMap(prefs.getString(KEY_TIME_SPENT_JSON, "{}") ?: "{}")
        val completedSetsMap = parseIntMap(prefs.getString(KEY_COMPLETED_SETS_JSON, "{}") ?: "{}")
        val maxWeightMap = parseDoubleMap(prefs.getString(KEY_MAX_WEIGHT_JSON, "{}") ?: "{}")

        val cooldownTargetTs = if (prefs.contains(KEY_COOLDOWN_TARGET_TIMESTAMP)) prefs.getLong(KEY_COOLDOWN_TARGET_TIMESTAMP, 0L) else null

        val uiState =
            ActiveWorkoutUiState(
                planId = planId,
                planTitle = planTitle,
                currentExerciseIndex = currentExerciseIndex,
                currentExerciseId = currentExerciseId,
                currentExerciseName = currentExerciseName,
                exercises = exercisesList,
                currentSetIndex = currentSetIndex,
                totalSetsForCurrentExercise = totalSets,
                targetRepsForCurrentSet = targetReps,
                currentSetWeightKg = currentSetWeight,
                currentSetReps = currentSetReps,
                cooldownTargetTimestamp = cooldownTargetTs,
            )

        return RestoredSessionState(
            uiState = uiState,
            sessionStartTimestamp = sessionStartTimestamp,
            exerciseTimeSpent = timeSpentMap,
            exerciseCompletedSets = completedSetsMap,
            exerciseMaxWeight = maxWeightMap,
        )
    }

    private fun serializeExercises(list: List<ExerciseUiModel>): String {
        return list.joinToString(prefix = "[", postfix = "]") { ex ->
            "{\"id\":\"${escapeJson(ex.id)}\",\"name\":\"${escapeJson(ex.name)}\",\"muscleGroup\":\"${
                escapeJson(ex.muscleGroup)
            }\",\"targetSets\":${ex.targetSets},\"targetReps\":\"${escapeJson(
                ex.targetReps,
            )}\",\"restSeconds\":${ex.restSeconds},\"day\":${ex.day},\"guideVideoUrl\":\"${
                escapeJson(ex.guideVideoUrl ?: "")
            }\",\"guideImageUrl\":\"${escapeJson(ex.guideImageUrl ?: "")}\"}"
        }
    }

    private fun serializeMap(map: Map<String, Any>): String {
        return map.entries.joinToString(prefix = "{", postfix = "}") { (k, v) ->
            "\"${escapeJson(k)}\":$v"
        }
    }

    private fun parseExercises(json: String): List<ExerciseUiModel> {
        if (json.isBlank() || json == "[]") return emptyList()
        val result = mutableListOf<ExerciseUiModel>()
        val items = json.removePrefix("[").removeSuffix("]").split("},{")
        for (item in items) {
            if (item.isBlank()) continue
            val clean = item.removePrefix("{").removeSuffix("}")
            val id = extractJsonValue(clean, "id")
            val name = extractJsonValue(clean, "name")
            val muscleGroup = extractJsonValue(clean, "muscleGroup")
            val targetSets = extractJsonValue(clean, "targetSets").toIntOrNull() ?: 3
            val targetReps = extractJsonValue(clean, "targetReps").ifBlank { "10" }
            val restSeconds = extractJsonValue(clean, "restSeconds").toIntOrNull() ?: 60
            val day = extractJsonValue(clean, "day").toIntOrNull() ?: 1
            val video = extractJsonValue(clean, "guideVideoUrl").ifBlank { null }
            val image = extractJsonValue(clean, "guideImageUrl").ifBlank { null }
            if (id.isNotBlank() || name.isNotBlank()) {
                result.add(
                    ExerciseUiModel(
                        id = id,
                        name = name,
                        muscleGroup = muscleGroup,
                        targetSets = targetSets,
                        targetReps = targetReps,
                        restSeconds = restSeconds,
                        day = day,
                        guideVideoUrl = video,
                        guideImageUrl = image,
                    ),
                )
            }
        }
        return result
    }

    private fun parseLongMap(json: String): Map<String, Long> {
        if (json.isBlank() || json == "{}") return emptyMap()
        val map = mutableMapOf<String, Long>()
        val pairs = json.removePrefix("{").removeSuffix("}").split(",")
        for (pair in pairs) {
            val parts = pair.split(":")
            if (parts.size == 2) {
                val key = parts[0].trim().removeSurrounding("\"")
                val valLong = parts[1].trim().toLongOrNull()
                if (key.isNotBlank() && valLong != null) {
                    map[key] = valLong
                }
            }
        }
        return map
    }

    private fun parseIntMap(json: String): Map<String, Int> {
        if (json.isBlank() || json == "{}") return emptyMap()
        val map = mutableMapOf<String, Int>()
        val pairs = json.removePrefix("{").removeSuffix("}").split(",")
        for (pair in pairs) {
            val parts = pair.split(":")
            if (parts.size == 2) {
                val key = parts[0].trim().removeSurrounding("\"")
                val valInt = parts[1].trim().toIntOrNull()
                if (key.isNotBlank() && valInt != null) {
                    map[key] = valInt
                }
            }
        }
        return map
    }

    private fun parseDoubleMap(json: String): Map<String, Double> {
        if (json.isBlank() || json == "{}") return emptyMap()
        val map = mutableMapOf<String, Double>()
        val pairs = json.removePrefix("{").removeSuffix("}").split(",")
        for (pair in pairs) {
            val parts = pair.split(":")
            if (parts.size == 2) {
                val key = parts[0].trim().removeSurrounding("\"")
                val valDbl = parts[1].trim().toDoubleOrNull()
                if (key.isNotBlank() && valDbl != null) {
                    map[key] = valDbl
                }
            }
        }
        return map
    }

    private fun extractJsonValue(
        json: String,
        key: String,
    ): String {
        val pattern = "\"$key\":\\s*\"?([^\",}]+)\"?".toRegex()
        val match = pattern.find(json)
        return match?.groupValues?.get(1)?.trim() ?: ""
    }

    private fun escapeJson(value: String): String {
        return value.replace("\"", "\\\"")
    }

    fun clearSession(context: Context) {
        getPrefs(context).edit().apply {
            putBoolean(KEY_HAS_ACTIVE_SESSION, false)
            remove(KEY_PLAN_ID)
            remove(KEY_PLAN_TITLE)
            remove(KEY_SESSION_START_TIMESTAMP)
            remove(KEY_CURRENT_EXERCISE_INDEX)
            remove(KEY_CURRENT_EXERCISE_ID)
            remove(KEY_CURRENT_EXERCISE_NAME)
            remove(KEY_CURRENT_SET_INDEX)
            remove(KEY_TOTAL_SETS)
            remove(KEY_TARGET_REPS)
            remove(KEY_CURRENT_SET_WEIGHT)
            remove(KEY_CURRENT_SET_REPS)
            remove(KEY_EXERCISES_JSON)
            remove(KEY_TIME_SPENT_JSON)
            remove(KEY_COMPLETED_SETS_JSON)
            remove(KEY_MAX_WEIGHT_JSON)
            remove(KEY_COOLDOWN_TARGET_TIMESTAMP)
            apply()
        }
    }
}
