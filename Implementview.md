Especificación detallada de implementación para la creación de las 12 interfaces de usuario de SynapseFit en Jetpack Compose, siguiendo la arquitectura de componentes pasivos (Dumb UI) y el sistema de diseño Kinetic Pulse.
V1: DashboardView
 * Ruta de Navegación: dashboard
 * Módulo: :app (co.japl.android.synapsefit.app.ui.dashboard)
Estructura de Componentes Composables
 * DashboardScreen: Contenedor principal sin estado (State Hoisting).
 * SyncStatusHeader: Barra superior con foto de perfil, nombre de usuario e indicador de estado de sincronización.
 * LatestMeasurementCard: Tarjeta de resumen con la última métrica antropométrica registrada.
 * TodayWorkoutCard: Módulo destacado con la rutina prescrita para el día actual.
 * QuickActionsRow: Fila de accesos rápidos para registrar peso o iniciar sesión en vivo.
Modelo de Estado UI (DashboardUiState)
data class DashboardUiState(
    val userName: String = "",
    val profileImageUrl: String? = null,
    val latestWeightKg: Double? = null,
    val weightTrendDeltaKg: Double? = null,
    val todayWorkoutTitle: String? = null,
    val isSyncing: Boolean = false,
    val isLoading: Boolean = false
)

Eventos de Interfaz (DashboardUiEvents)
 * onStartWorkoutClick: (planId: String) -> Unit
 * onLogMeasurementClick: () -> Unit
 * onProfileClick: () -> Unit
V2: BodyMeasurementsView
 * Ruta de Navegación: measurements/entry
 * Módulo: :app (co.japl.android.synapsefit.app.ui.measurements)
Estructura de Componentes Composables
 * BodyMeasurementsScreen: Formulario de captura de medidas por zona anatómica.
 * AnatomicalInputField: Campo numérico estilizado con etiqueta de unidad (cm / kg).
 * SaveMeasurementButton: Botón principal de acción con indicador de carga.
Modelo de Estado UI (BodyMeasurementsUiState)
data class BodyMeasurementsUiState(
    val weightKg: String = "",
    val chestCm: String = "",
    val waistCm: String = "",
    val hipCm: String = "",
    val bicepLeftCm: String = "",
    val bicepRightCm: String = "",
    val thighLeftCm: String = "",
    val thighRightCm: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

Eventos de Interfaz (BodyMeasurementsUiEvents)
 * onWeightChange: (String) -> Unit
 * onZoneValueChange: (zone: AnatomicalZone, value: String) -> Unit
 * onSaveClick: () -> Unit
V3: MeasurementProgressGraphView
 * Ruta de Navegación: measurements/progress
 * Módulo: :app (co.japl.android.synapsefit.app.ui.measurements)
Estructura de Componentes Composables
 * MeasurementProgressGraphScreen: Vista analítica de tendencias.
 * MetricSelectorChipRow: Selector horizontal de zona corporal (Peso, Pecho, Cintura, etc.).
 * CanvasTrendGraph: Gráfica interactiva personalizada dibujada sobre Canvas de Compose con líneas de tendencia neón y puntos de datos táctiles.
Modelo de Estado UI (MeasurementProgressUiState)
data class MeasurementProgressUiState(
    val selectedMetric: AnatomicalZone = AnatomicalZone.WEIGHT,
    val timeRangeDays: Int = 30,
    val dataPoints: List<GraphDataPoint> = emptyList(),
    val averageValue: Double = 0.0,
    val isLoading: Boolean = false
)

V4: WorkoutPlansView
 * Ruta de Navegación: workout/plans
 * Módulo: :app (co.japl.android.synapsefit.app.ui.workout)
Estructura de Componentes Composables
 * WorkoutPlansScreen: Catálogo de rutinas creadas y guardadas.
 * ActivePlanCard: Tarjeta destacada del plan de entrenamiento activo.
 * ArchivedPlanItem: Tarjeta compacta para planes secundarios o históricos.
 * GeneratePlanFab: Botón flotante para invocar al Agente IA.
Modelo de Estado UI (WorkoutPlansUiState)
data class WorkoutPlansUiState(
    val activePlan: WorkoutPlanSummary? = null,
    val archivedPlans: List<WorkoutPlanSummary> = emptyList(),
    val isLoading: Boolean = false
)

V5: AICoachGeneratorView
 * Ruta de Navegación: workout/ai-generator
 * Módulo: :app (co.japl.android.synapsefit.app.ui.workout)
Estructura de Componentes Composables
 * AICoachGeneratorScreen: Interfaz para configurar parámetros del generador IA.
 * EnvironmentSelector: Selector de entorno (Peso Corporal, Mancuernas, Gimnasio de Cadena).
 * GymChainSearchInput: Campo de texto condicional para inspección web de inventario de sedes.
 * GenerateButton: Botón Neón de alta prioridad para desencadenar la generación.
Modelo de Estado UI (AICoachGeneratorUiState)
data class AICoachGeneratorUiState(
    val selectedEnvironment: TrainingEnvironment = TrainingEnvironment.BODYWEIGHT,
    val gymChainQuery: String = "",
    val promptContext: String = "",
    val isGenerating: Boolean = false,
    val generationError: String? = null
)

V6: WorkoutPlanDetailView
 * Ruta de Navegación: workout/detail/{planId}
 * Módulo: :app (co.japl.android.synapsefit.app.ui.workout)
Estructura de Componentes Composables
 * WorkoutPlanDetailScreen: Detalle de una rutina específica.
 * RoutineSummaryModule: Tarjeta con métricas de duración, total de ejercicios y tiempo promedio de descanso.
 * ExerciseListItem: Lista interactiva de ejercicios con miniaturas, badges de grupo muscular y chips de sets/reps.
Modelo de Estado UI (WorkoutPlanDetailUiState)
data class WorkoutPlanDetailUiState(
    val planId: String = "",
    val planTitle: String = "",
    val daySubtitle: String = "",
    val totalExercises: Int = 0,
    val exercises: List<ExerciseUiModel> = emptyList(),
    val isLoading: Boolean = false
)

V7: ActiveWorkoutSessionView
 * Ruta de Navegación: workout/active/{planId}
 * Módulo: :app (co.japl.android.synapsefit.app.ui.workout)
Estructura de Componentes Composables
 * ActiveWorkoutSessionScreen: Interfaz de ejecución en vivo.
 * WorkoutChronometer: Cronómetro de sesión con efectos Neón Glow.
 * HeartRateGauge: Monitor de pulso cardíaco animado (BPM).
 * RestTimerWidget: Temporizador de conteo regresivo entre series.
 * SetTrackingTable: Tabla de captura en tiempo real por serie (Peso, Reps, Check).
Modelo de Estado UI (ActiveWorkoutUiState)
data class ActiveWorkoutUiState(
    val elapsedTimeSeconds: Long = 0L,
    val restTimerSecondsRemaining: Int? = null,
    val heartRateBpm: Int? = null,
    val currentExerciseName: String = "",
    val sets: List<WorkoutSetUiModel> = emptyList(),
    val isSessionComplete: Boolean = false
)

V8: WorkoutHistoryView
 * Ruta de Navegación: workout/history
 * Módulo: :app (co.japl.android.synapsefit.app.ui.history)
Estructura de Componentes Composables
 * WorkoutHistoryScreen: Historial cronológico de actividades.
 * CalendarHistoryWidget: Calendario mensual con indicadores de días entrenados.
 * WeeklyStatsGrid: Rejilla de métricas semanales (Sesiones ejecutadas, volumen acumulado).
 * WorkoutSessionHistoryCard: Registro resumido de sesión con etiquetas de músculos trabajados.
Modelo de Estado UI (WorkoutHistoryUiState)
data class WorkoutHistoryUiState(
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val trainedDays: Set<LocalDate> = emptySet(),
    val weeklySessionsCount: Int = 0,
    val weeklyTotalHours: Double = 0.0,
    val recordedSessions: List<SessionHistoryUiModel> = emptyList()
)

V9: BackupSyncView
 * Ruta de Navegación: settings/backup
 * Módulo: :app (co.japl.android.synapsefit.app.ui.settings)
Estructura de Componentes Composables
 * BackupSyncScreen: Control de respaldos en la nube.
 * GoogleDriveAccountCard: Información de conexión con la carpeta privada AppData.
 * BackupMetadataCard: Resumen de peso del respaldo y visor de Hash SHA-256.
 * SyncSettingsToggles: Conmutadores para auto-sincronización y medios.
Modelo de Estado UI (BackupSyncUiState)
data class BackupSyncUiState(
    val connectedAccountEmail: String? = null,
    val isDriveConnected: Boolean = false,
    val lastBackupTimestamp: Long? = null,
    val integrityHashSha256: String = "",
    val isSyncing: Boolean = false
)

V10: LLMSettingsView
 * Ruta de Navegación: settings/llm
 * Módulo: :app (co.japl.android.synapsefit.app.ui.settings)
Estructura de Componentes Composables
 * LLMSettingsScreen: Gestión de credenciales IA.
 * SecurityBadge: Indicador de almacenamiento cifrado en KeyStore.
 * ProviderCard: Tarjeta por proveedor (Gemini, OpenAI, Anthropic) con controles para API Key enmascarada y selector de modelo.
Modelo de Estado UI (LlmSettingsUiState)
data class LlmSettingsUiState(
    val providers: List<LlmProviderUiModel> = emptyList(),
    val activeProviderId: String? = null,
    val isLoading: Boolean = false
)

V11: AboutDeveloperView
 * Ruta de Navegación: settings/about
 * Módulo: :app (co.japl.android.synapsefit.app.ui.settings)
Estructura de Componentes Composables
 * AboutDeveloperScreen: Información institucional de la aplicación.
 * AppVersionCard: Detalles del Build, número de versión y Package ID (co.japl.android.synapsefit).
 * ModuleArchitectureSummary: Visualizador técnico de la estructura multi-módulo.
Modelo de Estado UI (AboutDeveloperUiState)
data class AboutDeveloperUiState(
    val versionName: String = "1.0.0",
    val versionCode: Long = 1L,
    val applicationId: String = "co.japl.android.synapsefit"
)

V12: WearOSActiveSessionView
 * Ruta de Navegación: Native Wear OS Activity
 * Módulo: :wear (co.japl.android.synapsefit.wear.ui)
Estructura de Componentes Composables (Wear Compose)
 * WearActiveWorkoutScreen: Layout circular adaptado a pantallas táctiles redondas.
 * CurvedExerciseTitle: Texto curvado perimetral (CurvedText) con el nombre del ejercicio.
 * HeartRateMetric: Lectura central en vivo de BPM proveniente de Health Services.
 * RepCounterWidget: Control táctil de incremento/decremento de repeticiones.
Modelo de Estado UI (WearActiveWorkoutUiState)
data class WearActiveWorkoutUiState(
    val exerciseName: String = "",
    val currentHeartRateBpm: Int = 0,
    val currentReps: Int = 0,
    val isSyncedWithPhone: Boolean = true
)

