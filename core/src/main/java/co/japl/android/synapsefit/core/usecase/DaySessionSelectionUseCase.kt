package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.ActiveWorkoutSessionState
import co.japl.android.synapsefit.core.domain.model.WorkoutSessionItem
import co.japl.android.synapsefit.core.port.secondary.ActiveSessionRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DaySessionSelectionUseCase( : IDaySessionSelectionUseCase
    private val getTodayRoutineUseCase: GetTodayRoutineUseCase,
    private val activeSessionRepositoryPort: ActiveSessionRepositoryPort? = null,
    private val getActiveWorkoutSessionUseCase: GetActiveWorkoutSessionUseCase? = null,
), IDaySessionSelectionUseCase : IDaySessionSelectionUseCase {
    override fun getTodayRoutines(): Flow<List<WorkoutSessionItem>> {
        return getTodayRoutineUseCase.invoke()
    }

    override fun getActiveSessionState(): Flow<ActiveWorkoutSessionState> {
        return getActiveWorkoutSessionUseCase?.invoke()
            ?: activeSessionRepositoryPort?.getActiveSessionState()
            ?: flowOf(ActiveWorkoutSessionState())
    }
}
