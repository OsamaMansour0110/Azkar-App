package com.learining.AzkarApp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.learining.AzkarApp.R

data class ChartData(
    val label: String,
    val value: Int,
    val color: Int
)

class FortuneBarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var chartData: List<ChartData> = emptyList()
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    private val barSpacing = 40f
    private val barHeight = 60f
    private val labelWidth = 400f
    private val valueTextSize = 36f
    private val labelTextSize = 32f

    init {
        textPaint.apply {
            color = ContextCompat.getColor(context, R.color.textPrimary)
            textSize = valueTextSize
            textAlign = Paint.Align.RIGHT
        }
        
        labelPaint.apply {
            color = ContextCompat.getColor(context, R.color.textPrimary)
            textSize = labelTextSize
            textAlign = Paint.Align.RIGHT
        }
    }

    fun setData(data: List<ChartData>) {
        chartData = data
        invalidate()
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = (chartData.size * (barHeight + barSpacing) + barSpacing).toInt()
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> minOf(desiredHeight, heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (chartData.isEmpty()) return

        val maxValue = chartData.maxOfOrNull { it.value } ?: 1
        val availableWidth = width - labelWidth - 120f // 120f for value text

        chartData.forEachIndexed { index, data ->
            val top = barSpacing + (index * (barHeight + barSpacing))
            
            // Draw label (zikr name) - right aligned
            val labelY = top + barHeight / 2 + labelTextSize / 3
            labelPaint.color = ContextCompat.getColor(context, R.color.textSecondary)
            canvas.drawText(
                data.label,
                labelWidth,
                labelY,
                labelPaint
            )

            // Calculate bar width based on value
            val barWidth = if (maxValue > 0) {
                (data.value.toFloat() / maxValue) * availableWidth
            } else 0f

            // Draw bar
            val barLeft = labelWidth + 20f
            val barRight = barLeft + barWidth
            val rect = RectF(barLeft, top, barRight, top + barHeight)
            
            barPaint.color = data.color
            canvas.drawRoundRect(rect, 16f, 16f, barPaint)

            // Draw value text at the end of the bar
            textPaint.color = ContextCompat.getColor(context, R.color.textPrimary)
            canvas.drawText(
                data.value.toString(),
                barRight + 80f,
                labelY,
                textPaint
            )
        }
    }
}
