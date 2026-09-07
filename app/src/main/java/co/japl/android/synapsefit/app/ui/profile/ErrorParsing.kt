package co.japl.android.synapsefit.app.ui.profile

import co.japl.android.synapsefit.R

data class ParsedError(
    val code: String,
    val titleRes: Int,
    val originalMessage: String
)

fun parseHttpError(message: String?): ParsedError {
    val errorMsg = message ?: ""
    return when {
        errorMsg.contains("400") -> ParsedError("Error 400", R.string.http_error_400, errorMsg)
        errorMsg.contains("401") -> ParsedError("Error 401", R.string.http_error_401, errorMsg)
        errorMsg.contains("403") -> ParsedError("Error 403", R.string.http_error_403, errorMsg)
        errorMsg.contains("429") -> ParsedError("Error 429", R.string.http_error_429, errorMsg)
        errorMsg.contains("500") -> ParsedError("Error 500", R.string.http_error_500, errorMsg)
        errorMsg.contains("502") -> ParsedError("Error 502", R.string.http_error_502, errorMsg)
        errorMsg.contains("503") -> ParsedError("Error 503", R.string.http_error_503, errorMsg)
        else -> ParsedError("Error", R.string.http_error_unknown, errorMsg)
    }
}
