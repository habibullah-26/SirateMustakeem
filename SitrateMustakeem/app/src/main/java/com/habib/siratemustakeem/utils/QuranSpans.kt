package com.habib.siratemustakeem.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan

/**
 * Draws the traditional small circular "end of ayah" seal with the ayah
 * number (in Arabic-Indic digits) centered inside it — the way printed
 * Mushafs render it. Drawn on canvas rather than relying on a font's
 * U+06DD ligature support, which varies a lot between fonts/devices.
 */
class AyahMarkerSpan(
    private val numberText: String,
    private val circleColor: Int,
    private val textColor: Int
) : ReplacementSpan() {

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val numberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private fun diameter(paint: Paint): Float = paint.textSize * 1.15f

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val d = diameter(paint)
        if (fm != null) {
            val original = paint.fontMetricsInt
            fm.ascent = original.ascent
            fm.descent = original.descent
            fm.top = original.top
            fm.bottom = original.bottom
        }
        return (d + paint.textSize * 0.35f).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val d = diameter(paint)
        val radius = d / 2f
        val centerX = x + paint.textSize * 0.18f + radius
        val centerY = (top + bottom) / 2.5f

        circlePaint.color = circleColor
        canvas.drawCircle(centerX, centerY, radius, circlePaint)

        numberPaint.color = textColor
        numberPaint.textSize = paint.textSize * 0.42f
        val numberMetrics = numberPaint.fontMetrics
        val textY = centerY - (numberMetrics.ascent + numberMetrics.descent) / 2f
        canvas.drawText(numberText, centerX, textY, numberPaint)
    }
}

/**
 * Draws the traditional Indo-Pak Ruku box: a small square outline containing
 * "ع" with the Ruku number beneath it, marking the start of a new Ruku
 * within the continuous Mushaf text.
 */
class RukuMarkerSpan(
    private val rukuNumberText: String,
    private val boxColor: Int
) : ReplacementSpan() {

    private val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private fun boxSize(paint: Paint): Float = paint.textSize * 1.25f

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val size = boxSize(paint)
        if (fm != null) {
            val original = paint.fontMetricsInt
            fm.ascent = original.ascent
            fm.descent = original.descent
            fm.top = original.top
            fm.bottom = original.bottom
        }
        return (size + paint.textSize * 0.5f).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val size = boxSize(paint)
        val left = x + paint.textSize * 0.25f
        val centerY = (top + bottom) / 2f
        val rect = RectF(left, centerY - size / 2f, left + size, centerY + size / 2f)

        boxPaint.color = boxColor
        boxPaint.strokeWidth = paint.textSize * 0.06f
        canvas.drawRoundRect(rect, size * 0.15f, size * 0.15f, boxPaint)

        textPaint.color = boxColor
        textPaint.textSize = size * 0.42f
        val ainMetrics = textPaint.fontMetrics
        canvas.drawText("ع", rect.centerX(), rect.centerY() - size * 0.14f - (ainMetrics.ascent + ainMetrics.descent) / 2f, textPaint)

        textPaint.textSize = size * 0.28f
        canvas.drawText(rukuNumberText, rect.centerX(), rect.centerY() + size * 0.32f, textPaint)
    }
}
