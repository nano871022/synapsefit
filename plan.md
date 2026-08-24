# Plan de Ejecución Técnico para SynapseFit (co.japl.android.synapsefit)

## Fase 1: Cimientos del Proyecto, Repositorio y CI/CD
 * Crear la estructura multi-módulo en Gradle (:app, :core, :services, :ui, :util, :wear) utilizando Gradle Version Catalogs (libs.versions.toml).
 * Integrar la documentación técnica y las habilidades en el repositorio (README.md, SDD.md y la carpeta .github/skills/).
 * Configurar el pipeline automatizado en .github/workflows/deploy.yml para verificación de código (ktlint, detekt), ejecución de pruebas unitarias, firmado de artefactos y despliegue automático del bundle .aab en Google Play Console.
 * 
## Fase 2: Capa de Dominio Pura (:core)
 * Implementar las entidades inmutables de dominio (BodyMeasurement, WorkoutPlan, Exercise, WorkoutLog, LlmConfig).
 * Definir las interfaces de los puertos secundarios (BodyMeasurementRepositoryPort, WorkoutPlanRepositoryPort, LlmClientPort, DriveSyncPort).
 * Escribir los casos de uso (SaveBodyMeasurementUseCase, GenerateWorkoutPlanUseCase, RecordWorkoutSessionUseCase, PerformDriveSyncUseCase).
 * Desarrollar pruebas unitarias aisladas en JVM con JUnit 5, MockK y Turbine.
 * 
## Fase 3: Infraestructura, Persistencia y Adaptadores (:services)
 * Configurar la base de datos Room (synapsefit_database.db) con las 5 tablas SQL y campos de auditoría temporal (created_at, updated_at).
 * Implementar los DAOs y mappers bidireccionales (Entity \leftrightarrow Domain Model).
 * Desarrollar GoogleDriveAppDataAdapter para respaldos cifrados en DriveScopes.DRIVE_APPDATA con validación de checksum SHA-256.
 * Construir MultiLlmClientAdapter (Gemini, OpenAI, Anthropic) integrando EncryptedSharedPreferences backed por Android KeyStore (AES256_GCM).
   
## Fase 4: Sistema de Diseño "Kinetic Pulse" y Utilidades (:ui & :util)
 * Declarar la paleta de colores (#101416 fondo, #00F5FF neón), tipografías (Hanken Grotesk, Inter, JetBrains Mono) y componentes atómicos en :ui.
 * Crear utilidades transversales en :util (formateadores de fecha/hora, calculador criptográfico SHA-256, Math helpers).
   
## Fase 5: Capa de Presentación Mobile (:app)
 * Implementar el orquestador de navegación AppNavigator y configurar AppNavHost con las 11 rutas principales.
 * Construir los ViewModels basados en StateFlow<UiState> que consumen los casos de uso de :core.
 * Desarrollar las 11 pantallas Compose pasivas (DashboardView, BodyMeasurementsView, ActiveWorkoutSessionView, LLMSettingsView, etc.).
 * Aplicar soporte responsive mediante WindowSizeClass para teléfonos, plegables (Samsung Z Fold 4) y tabletas.
   
## Fase 6: Companion App Wear OS (:wear)
 * Configurar el módulo standalone :wear bajo el namespace co.japl.android.synapsefit.wear.
 * Desarrollar la UI pasiva circular (384x384px) con soporte para CurvedText y contadores de repeticiones.
 * Integrar Health Services API para captura de pulso cardíaco en tiempo real y WearableListenerService para sincronización diferida vía Bluetooth.
   
## Fase 7: QA, Hardening y Lanzamiento
 * Ejecutar la suite completa de pruebas de integración (./gradlew testDebugUnitTest).
 * Validar el cumplimiento de guardas arquitectónicas con ktlintCheck y detekt.
 * Generar el paquete firmado final apk desde git hub actions para q sea descargado para realizar pruebas en dispositivos.
 * genera  github actions pára ejecutar las pruebas y el lint del proyecto para q se pueda descargar y verificar
 * genera github actions para ejecutar actualizacion de version(ejecucion manual), actualiza version del proyecto ademas de crear el tag en github.
