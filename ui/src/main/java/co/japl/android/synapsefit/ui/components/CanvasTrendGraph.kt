@file:Suppress("MagicNumber", "FunctionNaming", "MatchingDeclarationName")

package co.japl.android.synapsefit.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

data class GraphDataPoint(
    val xLabel: String,
    val value: Float,
)

@Composable
fun CanvasTrendGraph(
    dataPoints: List<GraphDataPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    pointColor: Color = MaterialTheme.colorScheme.primary,
) {
    Canvas(
        modifier =
            modifier
                .fillMaxWidth()
                .height(200.dp),
    ) {
        if (dataPoints.isEmpty()) return@Canvas

        val padding = 32.dp.toPx()
        val width = size.width - (padding * 2)
        val height = size.height - (padding * 2)

        val minY = dataPoints.minOf { it.value }
        val maxY = dataPoints.maxOf { it.value }
        val rangeY = if (maxY - minY == 0f) 1f else maxY - minY

        val points =
            dataPoints.mapIndexed { index, point ->
                val x = padding + (index.toFloat() / (dataPoints.size - 1).coerceAtLeast(1)) * width
                val y = size.height - padding - ((point.value - minY) / rangeY) * height
                Offset(x, y)
            }

        val path =
            Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }
            }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx()),
        )

        points.forEach { point ->
            drawCircle(
                color = pointColor,
                radius = 5.dp.toPx(),
                center = point,
            )
        }
    }
}
