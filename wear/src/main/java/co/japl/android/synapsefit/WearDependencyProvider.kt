package co.japl.android.synapsefit

import android.content.Context
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.IWearActiveWorkoutUseCase
import co.japl.android.synapsefit.core.usecase.IWearDaySelectionUseCase
import co.japl.android.synapsefit.core.usecase.IWearPostWorkoutSummaryUseCase
import co.japl.android.synapsefit.core.usecase.IWearSyncUseCase
import co.japl.android.synapsefit.core.usecase.WearActiveWorkoutUseCase
import co.japl.android.synapsefit.core.usecase.WearDaySelectionUseCase
import co.japl.android.synapsefit.core.usecase.WearPostWorkoutSummaryUseCase
import co.japl.android.synapsefit.core.usecase.WearSyncUseCase
import co.japl.android.synapsefit.services.repository.WorkoutLogRepositoryAdapter
import co.japl.android.synapsefit.services.repository.WorkoutPlanRepositoryAdapter

object WearDependencyProvider {
    private var context: Context? = null

    fun initialize(appContext: Context) {
        context = appContext
    }

    private val workoutPlanRepository: WorkoutPlanRepositoryPort by lazy {
        WorkoutPlanRepositoryAdapter(context!!)
    }

    private val workoutLogRepository: WorkoutLogRepositoryPort by lazy {
        WorkoutLogRepositoryAdapter(context!!)
    }

    val wearDaySelectionUseCase: IWearDaySelectionUseCase by lazy {
        WearDaySelectionUseCase(workoutPlanRepository)
    }

    val wearActiveWorkoutUseCase: IWearActiveWorkoutUseCase by lazy {
        WearActiveWorkoutUseCase(workoutPlanRepository, workoutLogRepository)
    }

    val wearPostWorkoutSummaryUseCase: IWearPostWorkoutSummaryUseCase by lazy {
        WearPostWorkoutSummaryUseCase(workoutLogRepository)
    }

    val wearSyncUseCase: IWearSyncUseCase by lazy {
        WearSyncUseCase(workoutLogRepository)
    }
}
