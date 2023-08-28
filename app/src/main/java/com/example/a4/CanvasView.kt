package com.example.a4starter

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.graphics.PathMeasure

data class Point(var x: Float, var y: Float)

class CanvasView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): View(context, attrs, defStyleAttr) {
    private enum class State { DOWN, MOVE, UP, TEST }
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val canvasBackgroundColour = Color.rgb(201, 227, 171)
    private var state = State.UP
    private val pointList = ArrayList<Point>()

    val pointListSize: Int
        get() = pointList.size

    val gesturePath: Path
        get() = path


    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.strokeJoin = Paint.Join.ROUND
        setBackgroundColor(canvasBackgroundColour)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        when (state) {
            State.TEST -> drawTest(canvas)
            else -> drawPath(canvas)
        }
    }

    private fun drawTest(canvas: Canvas?) {
        pointList.forEach {
            canvas?.drawPoint(it.x, it.y, paint)
        }
    }

    private fun drawPath(canvas: Canvas?) {
        canvas?.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //bound check
        event?.x?.let { xPos ->
            if (xPos < 0f || xPos > width) {
                touchUp()
                return true
            }
        }

        event?.y?.let { yPos ->
            if (yPos < 0f || yPos > height) {
                touchUp()
                return true
            }
        }

        // Log.d("MotionEvent", "${event?.x},${event?.y}")
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> touchDown(event.x, event.y)
            MotionEvent.ACTION_MOVE -> touchMove(event.x, event.y)
            MotionEvent.ACTION_UP -> touchUp()
            else -> return false
        }
        return true
    }

    private fun touchUp() {
        state = State.UP
    }

    private fun touchDown(x: Float, y: Float) {
        resetCanvas()
        state = State.DOWN
        path.moveTo(x,y)
        path.lineTo(x+5,y)
        pointList.add(Point(x,y))
        invalidate()
    }

    private fun touchMove(x: Float, y: Float) {
        state = State.MOVE
        path.lineTo(x,y)
        pointList.add(Point(x,y))
        invalidate()
    }

    fun resetCanvas() {
        path.reset()
        pointList.clear()
        invalidate()
    }

    fun saveGesture(name: String, viewModel: SharedViewModel?) {
        val pathCopy = Path()
        pathCopy.set(path)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)

        viewModel?.addGesture(Gesture(name, pathCopy, bitmap))
        resetCanvas()
    }

    fun drawPointsTest(list: ArrayList<Point>) {
        pointList.clear()
        list.forEach {
            pointList.add(it)
        }
        state = State.TEST
        invalidate()
    }

    fun drawList(list: ArrayList<ArrayList<Point>>) {
        pointList.clear()
        list.forEach {
            it.forEach { p ->
                pointList.add(p)
            }
        }
        state = State.TEST
        invalidate()
    }

}