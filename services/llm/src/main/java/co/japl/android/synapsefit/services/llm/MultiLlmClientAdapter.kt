package co.japl.android.synapsefit.services.llm

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.domain.model.LlmProvider
import co.japl.android.synapsefit.core.domain.model.TrainingEnvironment
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

private const val TIMEOUT_SECONDS = 300L

@Suppress("TooGenericExceptionCaught", "TooManyFunctions", "SwallowedException")
class MultiLlmClientAdapter : LlmClientPort {
    private val client =
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()

    private val retrofit =
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val geminiApi = retrofit.create(GeminiApi::class.java)

    override suspend fun generateWorkoutPlan(
        promptContext: String,
        environment: TrainingEnvironment,
        gymChainQuery: String?,
        config: LlmConfig,
    ): Result<Pair<WorkoutPlan, List<Exercise>>> {
        return try {
            require(config.apiKeyEncrypted.isNotBlank()) { "Encrypted API Key is missing" }

            if (config.provider == LlmProvider.GEMINI) {
                generateWithGemini(promptContext, environment, gymChainQuery, config)
            } else {
                createMockPlan(promptContext, environment, config)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createMockPlan(
        promptContext: String,
        environment: TrainingEnvironment,
        config: LlmConfig,
    ): Result<Pair<WorkoutPlan, List<Exercise>>> {
        val now = System.currentTimeMillis()
        val planId = UUID.randomUUID().toString()
        val environmentName = environment.name.lowercase().replace('_', ' ')
        val title = "Mock Plan (${config.provider.name}): $environmentName"
        val workoutPlan =
            WorkoutPlan(
                id = planId,
                title = title,
                goalDescription = "Generated for context: $promptContext",
                isActive = true,
                generatedByLlm = true,
                createdAt = now,
                updatedAt = now,
            )
        return Result.success(Pair(workoutPlan, emptyList()))
    }

    private suspend fun generateWithGemini(
        promptContext: String,
        environment: TrainingEnvironment,
        gymChainQuery: String?,
        config: LlmConfig,
    ): Result<Pair<WorkoutPlan, List<Exercise>>> {
        val environmentName = environment.name.lowercase().replace('_', ' ')
        val prompt = buildGeminiPrompt(environmentName, gymChainQuery, promptContext)

        val response =
            geminiApi.generateContent(
                model = config.modelName,
                apiKey = config.apiKeyEncrypted.trim(),
                request = GeminiRequest(contents = listOf(Content(parts = listOf(Part(text = prompt))))),
            )

        val textResponse = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
        val cleanedJson = extractJson(textResponse)

        return parseGeminiResponse(cleanedJson, promptContext, environmentName)
    }

    private fun buildGeminiPrompt(
        environmentName: String,
        gymChainQuery: String?,
        promptContext: String,
    ): String {
        val gymSuffix = if (!gymChainQuery.isNullOrBlankCheck()) " in $gymChainQuery" else ""
        return """
            Genera un plan de entrenamiento dividido por días de la semana para $environmentName$gymSuffix.
            Contexto del usuario (objetivo, medidas antropométricas e historial): $promptContext
            Organiza los ejercicios especificados por día (ej. 'Día 1 - Pecho y Tríceps', 'Día 2 - Espalda y Bíceps', etc.) e incluye instrucciones claras de ejecución.
            Responde en formato JSON plano con esta estructura:
            {
              "title": "Nombre del plan",
              "goal": "Descripción del objetivo y división por días",
              "exercises": [
                {
                  "name": "[Día 1] Nombre ejercicio con indicación de ejecución",
                  "muscleGroup": "Grupo muscular",
                  "sets": 3,
                  "reps": "10-12",
                  "rest": 60
                }
              ]
            }
            """.trimIndent()
    }

    private fun extractJson(textResponse: String): String {
        val jsonStart = textResponse.indexOf('{')
        val jsonEnd = textResponse.lastIndexOf('}')
        return if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            textResponse.substring(jsonStart, jsonEnd + 1)
        } else {
            textResponse.trim()
        }
    }

    private fun parseGeminiResponse(
        cleanedJson: String,
        promptContext: String,
        environmentName: String,
    ): Result<Pair<WorkoutPlan, List<Exercise>>> {
        val now = System.currentTimeMillis()
        val planId = UUID.randomUUID().toString()

        return try {
            val jsonObject = JsonParser.parseString(cleanedJson).asJsonObject
            val title = jsonObject.get("title")?.asString ?: "Gemini: $environmentName"
            val goal = jsonObject.get("goal")?.asString ?: promptContext

            val workoutPlan =
                WorkoutPlan(
                    id = planId,
                    title = title,
                    goalDescription = goal,
                    isActive = true,
                    generatedByLlm = true,
                    createdAt = now,
                    updatedAt = now,
                )

            val exercises = parseExercises(jsonObject, planId, now)
            check(exercises.isNotEmpty()) { "No se encontraron ejercicios en el JSON" }

            Result.success(Pair(workoutPlan, exercises))
        } catch (e: Exception) {
            println("LLM Parsing Error: ${e.message} | JSON: $cleanedJson")
            Result.success(createFallbackPlan(planId, environmentName, now))
        }
    }

    private fun parseExercises(
        jsonObject: JsonObject,
        planId: String,
        now: Long,
    ): List<Exercise> {
        val exerciseArray = jsonObject.getAsJsonArray("exercises") ?: return emptyList()
        return exerciseArray.map { element ->
            val exObj = element.asJsonObject
            Exercise(
                id = UUID.randomUUID().toString(),
                planId = planId,
                name = exObj.get("name")?.asString ?: "Ejercicio sin nombre",
                muscleGroup = exObj.get("muscleGroup")?.asString ?: "VARIOUS",
                targetSets = exObj.get("sets")?.asInt ?: 3,
                targetReps = exObj.get("reps")?.asString ?: "10",
                restSeconds = exObj.get("rest")?.asInt ?: 60,
                createdAt = now,
                updatedAt = now,
            )
        }
    }

    private fun createFallbackPlan(
        planId: String,
        environmentName: String,
        now: Long,
    ): Pair<WorkoutPlan, List<Exercise>> {
        val workoutPlan =
            WorkoutPlan(
                id = planId,
                title = "Gemini: $environmentName (Error Formato)",
                goalDescription =
                    "La IA respondió pero no pudimos procesar los ejercicios. " +
                        "Intenta de nuevo con un prompt más específico.",
                isActive = true,
                generatedByLlm = true,
                createdAt = now,
                updatedAt = now,
            )
        val exercises =
            listOf(
                Exercise(
                    id = UUID.randomUUID().toString(),
                    planId = planId,
                    name = "Error en formato de respuesta",
                    muscleGroup = "SISTEMA",
                    targetSets = 0,
                    targetReps = "0",
                    restSeconds = 0,
                    createdAt = now,
                    updatedAt = now,
                ),
            )
        return Pair(workoutPlan, exercises)
    }

    override suspend fun fetchAvailableModels(
        provider: LlmProvider,
        apiKey: String,
    ): Result<List<String>> {
        return try {
            require(apiKey.isNotBlank()) { "API Key is missing" }

            if (provider == LlmProvider.GEMINI) {
                val response = geminiApi.listModels(apiKey.trim())
                val modelNames = response.models.map { it.name.removePrefix("models/") }
                Result.success(modelNames)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchExerciseMedia(
        exerciseName: String,
        config: LlmConfig,
    ): Result<Pair<String, String>> {
        return try {
            if (config.provider == LlmProvider.GEMINI && config.apiKeyEncrypted.isNotBlank()) {
                val prompt =
                    """
                    Proporciona un enlace de video de YouTube relevante y una URL de imagen para realizar el ejercicio '$exerciseName'.
                    Responde un JSON plano con la siguiente estructura exactas sin ningún otro texto:
                    {
                      "videoUrl": "https://www.youtube.com/results?search_query=...",
                      "imageUrl": "https://images.unsplash.com/photo-1517838277536-f5f99be501cd"
                    }
                    """.trimIndent()

                val response =
                    geminiApi.generateContent(
                        model = config.modelName,
                        apiKey = config.apiKeyEncrypted.trim(),
                        request = GeminiRequest(contents = listOf(Content(parts = listOf(Part(text = prompt))))),
                    )

                val textResponse = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                val cleanedJson = extractJson(textResponse)
                val jsonObject = JsonParser.parseString(cleanedJson).asJsonObject

                val queryFormatted = exerciseName.replace(" ", "+")
                val defaultVideo = "https://www.youtube.com/results?search_query=$queryFormatted"
                val defaultImage = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd"

                val videoUrl = jsonObject.get("videoUrl")?.asString ?: defaultVideo
                val imageUrl = jsonObject.get("imageUrl")?.asString ?: defaultImage
                Result.success(Pair(videoUrl, imageUrl))
            } else {
                val queryFormatted = exerciseName.replace(" ", "+")
                val videoUrl = "https://www.youtube.com/results?search_query=$queryFormatted"
                val imageUrl = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd"
                Result.success(Pair(videoUrl, imageUrl))
            }
        } catch (e: Exception) {
            val queryFormatted = exerciseName.replace(" ", "+")
            val videoUrl = "https://www.youtube.com/results?search_query=$queryFormatted"
            val imageUrl = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd"
            Result.success(Pair(videoUrl, imageUrl))
        }
    }

    private fun String?.isNullOrBlankCheck(): Boolean = this == null || this.trim().isEmpty()
}
