package com.example.a4starter

import android.graphics.Bitmap
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.Float.Companion.POSITIVE_INFINITY
import kotlin.math.*

data class Gesture(val name: String,
                   var path: Path,
                   val bitMap: Bitmap)

class SharedViewModel : ViewModel() {
    private val gestures = ArrayList<Gesture>()
    private val gesturesLiveData = MutableLiveData<List<Gesture>>()

    val gestureList: LiveData<List<Gesture>>
        get() = gesturesLiveData

    val gestureListSize: Int
        get() = gestures.size


    fun addGesture(gesture: Gesture) {
        var indexToRemove = -1
        for (i in 0 until gestures.size) {
            if(gestures[i].name  == gesture.name) {
                indexToRemove = i
            }
        }

        if (indexToRemove >= 0) {
            gestures.removeAt(indexToRemove)
        }

        gestures.add(gesture)
        gesturesLiveData.value = gestures
    }

    fun deleteGesture(gesture: Gesture?) {
        if (null != gesture) {
            gestures.remove(gesture)
            gesturesLiveData.value = gestures
        }
    }

    private fun transformPointsFromPath(gesturePath: Path): ArrayList<Point> {
        val gesturePointList = resamplePath(gesturePath)
        val gestureCentroid = getGestureCentroid(gesturePointList)

        assert(gesturePointList.isNotEmpty())
        val angleToRotateCounterclockwise = getRotationAngle(gestureCentroid, gesturePointList.first())
        rotatePoints(gesturePointList, angleToRotateCounterclockwise, gestureCentroid)

        val resizeScale = getResizeScale(gestureCentroid, gesturePointList.first())
        scaleAndTranslatePoints(gesturePointList, resizeScale, gestureCentroid)

        return gesturePointList
    }

    fun findBestMatches(gesturePath: Path): ArrayList<Gesture> {
        val input = transformPointsFromPath(gesturePath)

        var min1 = POSITIVE_INFINITY
        var min2 = POSITIVE_INFINITY
        var min3 = POSITIVE_INFINITY
        var match1: Gesture? = null
        var match2: Gesture? = null
        var match3: Gesture? = null

        gestures.forEach { g ->
            val toCompare = transformPointsFromPath(g.path)
            val err = calculateErr(input, toCompare)

            if (err < min1) {
                min3 = min2
                min2 = min1
                min1 = err
                match3 = match2
                match2 = match1
                match1 = g
            } else if (err < min2) {
                min3 = min2
                min2 = err
                match3 = match2
                match2 = g
            } else if (err < min3) {
                min3 = err
                match3 = g
            }
        }

        val bestMatches = ArrayList<Gesture>()
        match1?.let { bestMatches.add(it) }
        match2?.let { bestMatches.add(it) }
        match3?.let { bestMatches.add(it) }
        return bestMatches
    }

    private fun calculateErr(input: ArrayList<Point>, toCompare: ArrayList<Point>): Float {
        assert(input.size == toCompare.size)

        var d = 0f

        for (i in 0 until input.size) {
            d += sqrt((input[i].x - toCompare[i].x).pow(2f) + (input[i].y - toCompare[i].y).pow(2f))
        }

        return d/input.size
    }

    private fun scaleAndTranslatePoints(gesturePointList: ArrayList<Point>, resizeScale: Float, center: Point) {
        gesturePointList.forEach { p ->
            p.x -= center.x
            p.y -= center.y

            p.x *= resizeScale
            p.y *= resizeScale
        }
    }

    private fun getResizeScale(p1: Point, p2: Point): Float {
        //val dis = sqrt((p2.x - p1.x).pow(2f) + (p2.y - p1.y).pow(2f))
        return 100f/abs(p2.x - p1.x)
    }

    private fun rotatePoints(gesturePointList: ArrayList<Point>, angle: Float, center: Point) {
        gesturePointList.forEach { p ->
            val x = p.x
            val y = p.y

            p.x = cos(angle) * (x - center.x) + sin(angle) * (y - center.y) + center.x
            p.y = sin(angle) * (x - center.x) - cos(angle) * (y - center.y) + center.y
        }
    }

    private fun getRotationAngle(p1: Point, p2: Point): Float {
        val y = -p2.y + p1.y
        val x = p2.x - p1.x

        return (2*PI).toFloat() - atan2(y,x)
    }

    private fun getGestureCentroid(gesturePointList: ArrayList<Point>): Point {
        var xAvg = 0f
        var yAvg = 0f
        val numPoints = gesturePointList.size

        gesturePointList.forEach { p ->
            xAvg += p.x
            yAvg += p.y
        }

        return Point(xAvg/numPoints, yAvg/numPoints)
    }

    private fun resamplePath(path: Path): ArrayList<Point>  {
        val list = ArrayList<Point>()
        val pm = PathMeasure(path, false)
        val length = pm.length
        var distance = 0f
        val speed = length / 128
        var counter = 0
        val aCoordinates = FloatArray(2)

        while (distance < length && counter < 128) {

            pm.getPosTan(distance, aCoordinates, null)
            list.add(Point(aCoordinates[0],aCoordinates[1]))
            counter++
            distance += speed
        }

        return list
    }

    private fun radToDeg(radians: Float): Float {
        return (radians*180)/PI.toFloat()
    }


    private fun testPoints(gesturePointList: ArrayList<Point>, angle: Float, center: Point) {

        gesturePointList.forEach { p ->
            val x = p.x
            val y = p.y

            p.x = cos(angle) * (x - center.x) + sin(angle) * (y - center.y) + center.x
            p.y = sin(angle) * (x - center.x) - cos(angle) * (y - center.y) + center.y

            p.x -= center.x
            p.y -= center.y
        }
    }

    fun printNameTest() {
        gestures.forEach { gesture ->
            Log.d("Gestures", gesture.name)
        }
    }

    fun findBestMatchesTest(gesturePath: Path): ArrayList<ArrayList<Point>> {
        val input = transformPointsFromPath(gesturePath)

        var min1 = POSITIVE_INFINITY
        var min2 = POSITIVE_INFINITY
        var min3 = POSITIVE_INFINITY
        var match1: Gesture? = null
        var match2: Gesture? = null
        var match3: Gesture? = null

        gestures.forEach { g ->
            val toCompare = transformPointsFromPath(g.path)
            val err = calculateErr(input, toCompare)

            if (err < min1) {
                min3 = min2
                min2 = min1
                min1 = err
                match3 = match2
                match2 = match1
                match1 = g
            } else if (err < min2) {
                min3 = min2
                min2 = err
                match3 = match2
                match2 = g
            } else if (err < min3) {
                min3 = err
                match3 = g
            }
        }

        // Test Code
        val list = ArrayList<ArrayList<Point>>()
        list.add(input)
        match1?.let { list.add(transformPointsFromPath(it.path)) }
        match2?.let { list.add(transformPointsFromPath(it.path)) }
        return list
    }
}