package co.japl.android.synapsefit.services.llm

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.domain.model.LlmProvider
import co.japl.android.synapsefit.core.domain.model.TrainingEnvironment
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

@Suppress("TooGenericExceptionCaught")
class MultiLlmClientAdapter : LlmClientPort {
    private val client =
        OkHttpClient.Builder()
            .connectTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
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
            if (config.apiKeyEncrypted.isBlank()) {
                return Result.failure(IllegalArgumentException("Encrypted API Key is missing"))
            }

            if (config.provider == LlmProvider.GEMINI) {
                return generateWithGemini(promptContext, environment, gymChainQuery, config)
            }

            // Fallback for others (mock for now)
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
            Result.success(Pair(workoutPlan, emptyList()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun generateWithGemini(
        promptContext: String,
        environment: TrainingEnvironment,
        gymChainQuery: String?,
        config: LlmConfig,
    ): Result<Pair<WorkoutPlan, List<Exercise>>> {
        val environmentName = environment.name.lowercase().replace('_', ' ')
        val gymSuffix = if (!gymChainQuery.isNullOrBlankCheck()) " in $gymChainQuery" else ""
        val prompt =
            """
            Genera un plan de entrenamiento para $environmentName$gymSuffix.
            Contexto: $promptContext
            Responde en formato JSON plano con esta estructura:
            {
              "title": "Nombre del plan",
              "goal": "Descripción del objetivo",
              "exercises": [
                {
                  "name": "Nombre ejercicio",
                  "muscleGroup": "Grupo muscular",
                  "sets": 3,
                  "reps": "10-12",
                  "rest": 60
                }
              ]
            }
            """.trimIndent()

        val response =
            geminiApi.generateContent(
                model = config.modelName,
                apiKey = config.apiKeyEncrypted.trim(),
                request = GeminiRequest(contents = listOf(Content(parts = listOf(Part(text = prompt))))),
            )

        val textResponse = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""

        // Extract JSON from textResponse (handles Markdown and extra text)
        val jsonStart = textResponse.indexOf('{')
        val jsonEnd = textResponse.lastIndexOf('}')
        val cleanedJson =
            if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                textResponse.substring(jsonStart, jsonEnd + 1)
            } else {
                textResponse.trim()
            }

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

            val exerciseArray = jsonObject.getAsJsonArray("exercises")
            val exercises =
                exerciseArray.map { element ->
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

            if (exercises.isEmpty()) {
                throw IllegalStateException("No se encontraron ejercicios en el JSON")
            }

            Result.success(Pair(workoutPlan, exercises))
        } catch (e: Exception) {
            println("LLM Parsing Error: ${e.message} | JSON: $cleanedJson")
            // Fallback to basic plan if parsing fails
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
            Result.success(Pair(workoutPlan, exercises))
        }
    }

    override suspend fun fetchAvailableModels(
        provider: LlmProvider,
        apiKey: String,
    ): Result<List<String>> {
        return try {
            if (apiKey.isBlank()) {
                return Result.failure(IllegalArgumentException("API Key is missing"))
            }

            if (provider == LlmProvider.GEMINI) {
                val response = geminiApi.listModels(apiKey.trim())
                // Filter to only include models that support generateContent
                val modelNames = response.models.map { it.name.removePrefix("models/") }
                Result.success(modelNames)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun String?.isNullOrBlankCheck(): Boolean = this == null || this.trim().isEmpty()
}
