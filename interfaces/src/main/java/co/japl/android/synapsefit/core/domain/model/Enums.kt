package co.japl.android.synapsefit.core.domain.model

enum class SourceDevice {
    MOBILE,
    WEAR_OS,
}

enum class LlmProvider {
    GEMINI,
    OPENAI,
    ANTHROPIC,
}

enum class AnatomicalZone {
    WEIGHT,
    CHEST,
    WAIST,
    HIP,
    BICEP_LEFT,
    BICEP_RIGHT,
    THIGH_LEFT,
    THIGH_RIGHT,
}

enum class TrainingEnvironment {
    BODYWEIGHT,
    DUMBBELLS,
    CHAIN_GYM,
}

enum class TrainingLocation {
    HOME,
    GYM,
}

enum class EquipmentPreference {
    DUMBBELLS,
    CALISTHENICS,
    MACHINES,
    NO_PREFERENCE,
}
