package co.japl.android.synapsefit.services.llm

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

data class GeminiRequest(
    val contents: List<Content>,
)

data class Content(
    val parts: List<Part>,
)

data class Part(
    val text: String,
)

data class GeminiResponse(
    val candidates: List<Candidate>,
)

data class Candidate(
    val content: Content,
)

data class GeminiModelListResponse(
    val models: List<GeminiModel>,
)

data class GeminiModel(
    val name: String,
    val displayName: String,
    val description: String,
)

interface GeminiApi {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @retrofit2.http.Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest,
    ): GeminiResponse

    @retrofit2.http.GET("v1beta/models")
    suspend fun listModels(
        @Query("key") apiKey: String,
    ): GeminiModelListResponse
}
