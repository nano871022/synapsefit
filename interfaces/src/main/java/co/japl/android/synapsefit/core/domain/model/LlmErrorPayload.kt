package co.japl.android.synapsefit.core.domain.model

data class LlmErrorPayload(
    val code: String,
    val status: String,
    val message: String,
)

fun parseLlmErrorResponse(
    rawMessage: String?,
    defaultCode: Int = 500,
): LlmErrorPayload {
    if (rawMessage.isNullOrBlank()) {
        return LlmErrorPayload(
            code = defaultCode.toString(),
            status = "ERROR",
            message = "Ocurrió un error inesperado al comunicarse con el servicio LLM.",
        )
    }

    val codeMatch = Regex(""""code"\s*:\s*"?([^",}]+)"?""").find(rawMessage)?.groupValues?.get(1)?.trim()
    val statusMatch = Regex(""""status"\s*:\s*"([^"]+)"""").find(rawMessage)?.groupValues?.get(1)?.trim()
    val messageMatch = Regex(""""message"\s*:\s*"([^"]+)"""").find(rawMessage)?.groupValues?.get(1)?.trim()

    val code = codeMatch ?: defaultCode.toString()
    val status = statusMatch ?: if (rawMessage.contains("501")) "NOT_IMPLEMENTED" else "HTTP_ERROR"
    val message = messageMatch ?: rawMessage.trim()

    return LlmErrorPayload(
        code = code,
        status = status,
        message = message,
    )
}
