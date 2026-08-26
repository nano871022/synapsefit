@file:Suppress("MagicNumber", "FunctionNaming", "MatchingDeclarationName", "LongParameterList")

package co.japl.android.synapsefit.ui.components

import android.graphics.Paint
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.japl.android.synapsefit.util.MathUtils

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
    axisTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    gridLineColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
) {
    val axisTextPx = 10.sp
    Canvas(
        modifier =
            modifier
                .fillMaxWidth()
                .height(220.dp),
    ) {
        if (dataPoints.isEmpty()) return@Canvas

        val leftPadding = 48.dp.toPx()
        val bottomPadding = 36.dp.toPx()
        val topPadding = 16.dp.toPx()
        val rightPadding = 24.dp.toPx()

        val width = size.width - leftPadding - rightPadding
        val height = size.height - topPadding - bottomPadding

        val minY = dataPoints.minOf { it.value }
        val maxY = dataPoints.maxOf { it.value }
        val rangeY = if (maxY - minY == 0f) 1f else maxY - minY

        val textPaint =
            Paint().apply {
                color = axisTextColor.toArgb()
                textSize = axisTextPx.toPx()
                isAntiAlias = true
            }

        // Draw Y-axis grid lines and labels (3 steps)
        val ySteps = 3
        for (i in 0..ySteps) {
            val fraction = i.toFloat() / ySteps
            val yPos = topPadding + height * (1f - fraction)
            val valueAtGrid = minY + rangeY * fraction

            // Grid line
            drawLine(
                color = gridLineColor,
                start = Offset(leftPadding, yPos),
                end = Offset(size.width - rightPadding, yPos),
                strokeWidth = 1.dp.toPx(),
            )

            // Y-axis label text
            val formattedValue = "${MathUtils.roundToDecimals(valueAtGrid.toDouble(), 1)}"
            drawContext.canvas.nativeCanvas.drawText(
                formattedValue,
                12.dp.toPx(),
                yPos + 4.dp.toPx(),
                textPaint,
            )
        }

        val points =
            dataPoints.mapIndexed { index, point ->
                val x = leftPadding + (index.toFloat() / (dataPoints.size - 1).coerceAtLeast(1)) * width
                val y = topPadding + height * (1f - ((point.value - minY) / rangeY))
                Offset(x, y)
            }

        // Draw X-axis labels (dates)
        val step = (dataPoints.size / 5).coerceAtLeast(1)
        dataPoints.forEachIndexed { index, point ->
            if (index % step == 0 || index == dataPoints.lastIndex) {
                val xPos = points[index].x
                drawContext.canvas.nativeCanvas.drawText(
                    point.xLabel,
                    xPos - 16.dp.toPx(),
                    size.height - 8.dp.toPx(),
                    textPaint,
                )
            }
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
