package co.japl.android.synapsefit.app.ui.measurements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.AnatomicalZone
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import co.japl.android.synapsefit.navigation.AppNavigator
import co.japl.android.synapsefit.ui.components.GraphDataPoint
import co.japl.android.synapsefit.util.DateTimeUtils
import co.japl.android.synapsefit.util.MathUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MeasurementProgressUiState(
    val selectedMetric: AnatomicalZone = AnatomicalZone.WEIGHT,
    val timeRangeDays: Int = 30,
    val dataPoints: List<GraphDataPoint> = emptyList(),
    val averageValue: Double = 0.0,
    val isLoading: Boolean = false,
)

class MeasurementProgressViewModel(
    private val bodyMeasurementRepositoryPort: BodyMeasurementRepositoryPort? = null,
    private val appNavigator: AppNavigator? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MeasurementProgressUiState())
    val uiState: StateFlow<MeasurementProgressUiState> = _uiState.asStateFlow()

    private var graphDataJob: kotlinx.coroutines.Job? = null

    init {
        loadGraphData()
    }

    fun onMetricSelected(metric: AnatomicalZone) {
        _uiState.update { it.copy(selectedMetric = metric) }
        loadGraphData()
    }

    fun onTimeRangeSelected(days: Int) {
        _uiState.update { it.copy(timeRangeDays = days) }
        loadGraphData()
    }

    fun loadGraphData() {
        graphDataJob?.cancel()
        graphDataJob =
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                appNavigator?.setLoading(true)

                bodyMeasurementRepositoryPort?.getMeasurementsHistory()?.collect { measurements ->
                    val selectedZone = _uiState.value.selectedMetric
                    val timeRangeDays = _uiState.value.timeRangeDays
                    val now = System.currentTimeMillis()
                    val startTime =
                        if (timeRangeDays > 0) {
                            now - (timeRangeDays.toLong() * 24 * 60 * 60 * 1000)
                        } else {
                            0L
                        }

                    val filteredMeasurements =
                        measurements
                            .filter { it.createdAt >= startTime }
                            .sortedBy { it.createdAt }

                    val points =
                        filteredMeasurements.mapNotNull { m ->
                            val valForZone =
                                when (selectedZone) {
                                    AnatomicalZone.WEIGHT -> m.weightKg
                                    AnatomicalZone.CHEST -> m.chestCm
                                    AnatomicalZone.WAIST -> m.waistCm
                                    AnatomicalZone.HIP -> m.hipCm
                                    AnatomicalZone.BICEP_LEFT -> m.bicepLeftCm
                                    AnatomicalZone.BICEP_RIGHT -> m.bicepRightCm
                                    AnatomicalZone.THIGH_LEFT -> m.thighLeftCm
                                    AnatomicalZone.THIGH_RIGHT -> m.thighRightCm
                                }
                            valForZone?.let { v ->
                                GraphDataPoint(
                                    xLabel = DateTimeUtils.formatEpoch(m.createdAt, "MM-dd"),
                                    value = v.toFloat(),
                                )
                            }
                        }

                    val values = points.map { it.value.toDouble() }
                    val avg = MathUtils.calculateAverage(values)

                    _uiState.update {
                        it.copy(
                            dataPoints = points,
                            averageValue = avg,
                            isLoading = false,
                        )
                    }
                    appNavigator?.setLoading(false)
                }
            }
    }
}
