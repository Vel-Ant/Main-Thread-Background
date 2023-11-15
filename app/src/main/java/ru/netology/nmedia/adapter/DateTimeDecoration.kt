package ru.netology.nmedia.adapter

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.dto.DateSeparator
import ru.netology.nmedia.dto.getText

class DateTimeDecoration(
    private val offset: Int,
    private val textSize: Float
) : RecyclerView.ItemDecoration() {
    private val paint = Paint().apply {
        textSize = this@DateTimeDecoration.textSize
    }
    private val rect = Rect()
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in
        0 until parent.childCount) {
            val view = parent[i]

            val separator = view.tag as? DateSeparator ?: continue

            val text = separator.getText(view.context)

            paint.getTextBounds(text, 0, text.length, rect)

            c.drawText(text, view.width / 2F - rect.centerX(), view.y - offset / 2 - rect.exactCenterY(), paint)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val separator = view.tag as? DateSeparator

        if (separator != null) {
            outRect.top += offset
        } else {
            super.getItemOffsets(outRect, view, parent, state)
        }
    }
}