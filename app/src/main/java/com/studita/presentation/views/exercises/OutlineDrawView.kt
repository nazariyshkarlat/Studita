package com.studita.presentation.views.exercises

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.graphics.get
import com.sdsmdg.harjot.vectormaster.VectorMasterDrawable
import com.studita.utils.ImageUtils.getBitmap
import com.studita.utils.ImageUtils.getOrNull
import com.studita.utils.ImageUtils.lastXIndex
import com.studita.utils.ImageUtils.lastYIndex
import com.studita.utils.ImageUtils.toBooleanArray
import com.studita.utils.ThemeUtils
import com.studita.utils.dp
import kotlin.math.*

class OutlineDrawView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){

    private var drawCompletedCallback: DrawCompletedCallback? = null

    private val drawColor = ThemeUtils.getAccentColor(context)
    private val eraseColor = ThemeUtils.getPrimaryColor(context)

    private var drawingLineWidth: Int = 0
    private val outsideTouchAreaWidth get() = drawingLineWidth

    private val drawingLineScaleFactor = 2

    private val leftPos get() = (measuredWidth/2-bmpToErase.width/2).toFloat()
    private val topPos get() = (measuredHeight/2-bmpToErase.height/2).toFloat()

    private var lastPoint: Point? = null

    private var isInitialized = false

    private var drawingCompleted = false

    private var needsDownAction = true

    private lateinit var bmpBack: Bitmap
    private lateinit var bmpToErase: Bitmap
    private lateinit var bmpToEraseInitial: Bitmap
    private lateinit var skeletonArray: Array<Array<Boolean>>

    private val points = arrayListOf<Pair<Boolean, Point>>()
    private val pPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val pPaint1 = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    fun init(drawCompletedCallback: DrawCompletedCallback, @DrawableRes vectorShapeRes: Int){
        this.drawCompletedCallback = drawCompletedCallback

        with(VectorMasterDrawable(this.context, vectorShapeRes)) {
            bmpBack = apply {
                getPathModelByName("line").strokeColor = drawColor
            }.getBitmap()

            bmpToErase = run {
                getPathModelByName("line").strokeColor = eraseColor
                drawingLineWidth =
                    getPathModelByName("line").strokeWidth.dp * drawingLineScaleFactor
                getBitmap()
            }

            bmpToEraseInitial = bmpToErase.copy(Bitmap.Config.ARGB_8888, true)

            skeletonArray = apply {
                getPathModelByName("line").strokeWidth = 0F
            }.getBitmap().toBooleanArray()
        }

        isInitialized = true
        drawingCompleted = false
        lastPoint = null
        points.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if(isInitialized) {
            canvas.drawBitmap(
                bmpBack,
                leftPos,
                topPos,
                null
            )
            canvas.drawBitmap(
                bmpToErase,
                leftPos,
                topPos,
                null
            )
            points.forEach {
                if(it.first)
                    canvas.drawCircle(getViewXCoordinate(it.second.x), getViewYCoordinate(it.second.y), 2F.dp.toFloat(), pPaint)
                else
                    canvas.drawCircle(getViewXCoordinate(it.second.x), getViewYCoordinate(it.second.y), 2F.dp.toFloat(), pPaint1)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(isInitialized && !drawingCompleted &&
            !(event.action != MotionEvent.ACTION_DOWN && needsDownAction)) {
            if (inBitmapBounds(event.x, event.y)) {
                getBitmapXCoordinate(event.x).let { x ->
                    getBitmapYCoordinate(event.y).let { y ->
                        inOutlineBounds(x, y).let {isInBoundsPlusPoint->
                            if (isInBoundsPlusPoint.first) {
                                if(event.action == MotionEvent.ACTION_DOWN)
                                    needsDownAction = false
                                getCenteredPoint(isInBoundsPlusPoint.second.x, isInBoundsPlusPoint.second.y).let {
                                    points.add(true to Point(it.x, it.y))
                                    drawPixels(it.x, it.y)
                                    lastPoint?.let { lastPoint ->
                                        var currentDistance = drawingLineWidth / drawingLineScaleFactor
                                        var needsBreak = false
                                        while (currentDistance < dist(lastPoint.x, lastPoint.y, x, y)) {
                                            linePointAtDist(
                                                lastPoint.x,
                                                lastPoint.y,
                                                x,
                                                y,
                                                currentDistance
                                            ).let { pointInBetween ->
                                                nearestPointInRadius(
                                                    1,
                                                    pointInBetween.x,
                                                    pointInBetween.y
                                                )?.let {pointInOutline->
                                                    getCenteredPoint(
                                                        pointInOutline.x,
                                                        pointInOutline.y
                                                    ).let { centeredPoint ->
                                                        points.add(false to Point(centeredPoint.x, centeredPoint.y))
                                                        drawPixels(
                                                            centeredPoint.x,
                                                            centeredPoint.y
                                                        )
                                                    }
                                                } ?: run {
                                                    needsBreak = true
                                                }
                                            }
                                            if(needsBreak){
                                                reset()
                                                break
                                            }
                                            currentDistance += drawingLineWidth / drawingLineScaleFactor
                                        }
                                    }

                                    invalidate()
                                }

                                if (event.action == ACTION_MOVE) {
                                    lastPoint?.let{
                                        it.x = x
                                        it.y = y
                                    } ?: run{
                                        lastPoint = Point(x, y)
                                    }
                                }else
                                    clearLastPoint()

                                if(event.action == ACTION_UP)
                                    reset()

                            }else
                                reset()
                        }
                    }
                }
            }else
                reset()
            return true
        }else {
            return false
        }
    }

    private fun getBitmapXCoordinate(viewX: Float): Int = (viewX - leftPos).toInt()

    private fun getBitmapYCoordinate(viewY: Float): Int = (viewY - topPos).toInt()

    private fun getViewXCoordinate(x: Int): Float = leftPos+x

    private fun getViewYCoordinate(y: Int): Float = topPos+y

    private fun inOutlineBounds(xPoint: Int, yPoint: Int): Pair<Boolean, Point> {
        val clickPoint =  bmpBack.getOrNull(xPoint, yPoint)
        return if(clickPoint == null || clickPoint == Color.TRANSPARENT){
            nearestPointInRadius(startRadius = 1, xPoint, yPoint)?.let {
                return true to Point(it.x, it.y)
            }
            return false to Point(xPoint, yPoint)
        }else
            true to Point(xPoint, yPoint)
    }


    private fun nearestPointInRadius(startRadius: Int, xPoint: Int, yPoint: Int): Point?{
        var radius = startRadius
        while (radius < drawingLineWidth/2){
            for(i in 0..3600 step (acos(1F-1F/radius)*10).toInt()){
                val angle = i/10
                val x1 = (radius*cos(angle.toFloat())).toInt()
                val y1 = (radius*sin(angle.toFloat())).toInt()
                val point = bmpBack.getOrNull(xPoint+x1, yPoint+y1)
                if (point != null && point != Color.TRANSPARENT)
                    return Point(xPoint+x1, yPoint+y1)
            }
            radius++
        }
        return null
    }

    private fun inBitmapBounds(viewX: Float, viewY: Float) = getBitmapXCoordinate(viewX) > (-outsideTouchAreaWidth) &&
            getBitmapXCoordinate(viewX) < (bmpBack.width + outsideTouchAreaWidth) &&
            getBitmapYCoordinate(viewY) > (-outsideTouchAreaWidth) &&
            getBitmapYCoordinate(viewY) < (bmpBack.height + outsideTouchAreaWidth)

    private fun drawPixels(xPoint: Int, yPoint: Int) {
        ((yPoint-drawingLineWidth/drawingLineScaleFactor).coerceAtLeast(0)..(yPoint+drawingLineWidth/drawingLineScaleFactor).coerceAtMost(
            bmpBack.lastYIndex
        )).forEach{ y->
            ((xPoint-drawingLineWidth/drawingLineScaleFactor).coerceAtLeast(0)..(xPoint+drawingLineWidth/drawingLineScaleFactor).coerceAtMost(
                bmpBack.lastXIndex
            )).forEach{ x->
                val dx = x - xPoint
                val dy = y - yPoint
                val isInCircle = dx*dx + dy*dy < (drawingLineWidth/drawingLineScaleFactor)*(drawingLineWidth/drawingLineScaleFactor)
                if (isInCircle)
                    bmpToErase.setPixel(x, y, Color.TRANSPARENT)
            }
        }
       checkDrawCompleted()
    }

    private fun checkDrawCompleted(){
        skeletonArray.forEachIndexed {
                y, rows ->
            rows.forEachIndexed { x, value ->
                if(value && bmpToErase[x, y] != Color.TRANSPARENT){
                    return@checkDrawCompleted
                }
            }
        }
        drawingCompleted = true
        bmpToErase.eraseColor(Color.TRANSPARENT)
        drawCompletedCallback?.onDrawCompleted()
    }

    private fun getCenteredPoint(xPoint: Int, yPoint: Int, secondCycle: Boolean = false): Point{
        var currentIndex = yPoint
        while (bmpBack.getOrNull(xPoint, currentIndex - 1) != Color.TRANSPARENT){
            if(currentIndex > 0) currentIndex-- else break
        }
        val topY = currentIndex
        currentIndex = yPoint
        while (bmpBack.getOrNull(xPoint, currentIndex + 1) != Color.TRANSPARENT){
            if(currentIndex < bmpBack.height) currentIndex++ else break
        }
        val bottomY = currentIndex
        currentIndex = xPoint
        while (bmpBack.getOrNull(currentIndex-  1, yPoint) != Color.TRANSPARENT){
            if(currentIndex > 0) currentIndex-- else break
        }
        val leftX = currentIndex
        currentIndex = xPoint
        while (bmpBack.getOrNull(currentIndex + 1, yPoint) != Color.TRANSPARENT){
            if(currentIndex < bmpBack.width) currentIndex++ else break
        }
        val rightX = currentIndex

        val leftXDist = abs(xPoint-leftX)
        val rightXDist = abs(rightX-xPoint)
        val topYDist = abs(yPoint-topY)
        val bottomYDist = abs(yPoint-bottomY)

        println("leftXDist ${leftXDist} rightXDist ${rightXDist} topYDist ${topYDist} bottomYDist ${bottomYDist}")
        val xDist = rightXDist + leftXDist
        val yDist = bottomYDist + topYDist

        val nearestPointX = if(leftXDist < rightXDist) leftX else rightX
        val nearestPointY = if(topYDist< bottomYDist) topY else bottomY

        val distFromNearest = drawingLineWidth/(drawingLineScaleFactor*2)

        val centerX = if(nearestPointX == leftX) leftX+distFromNearest else rightX-distFromNearest
        val centerY = if(nearestPointY == topY) topY+distFromNearest else bottomY-distFromNearest

        println("nearestX ${xDist}, nearestY ${yDist} ddasd ${drawingLineWidth/10F}")
        return when {
            (abs(xDist-yDist) < (drawingLineWidth/5F) && !secondCycle) -> getCenteredPoint(centerX, yPoint, true)
            (xDist < yDist) -> Point(centerX, yPoint)
            else -> Point(xPoint, centerY)
        }
    }

    private fun dist(x0: Int, y0: Int, x1: Int, y1: Int) = sqrt(((x1-x0)*(x1-x0) + (y1-y0)*(y1-y0)).toDouble()).toInt()

    private fun linePointAtDist(x0: Int, y0: Int, x1: Int, y1: Int, dist: Int): Point = Point().apply {
        sqrt(((x1-x0)*(x1-x0)+(y1-y0)*(y1-y0)).toDouble()).let {vectorMagnitude->
            x = x0 + (dist * ((x1 - x0) / vectorMagnitude)).toInt()
            y = y0 + (dist * ((y1 - y0) / vectorMagnitude)).toInt()
        }
    }

    private fun clearLastPoint(){
        lastPoint = null
    }

    private fun reloadEraseLayer(){
        bmpToErase = bmpToEraseInitial.copy(Bitmap.Config.ARGB_8888, true)
    }

    private fun reset(){
        reloadEraseLayer()
        clearLastPoint()
        needsDownAction = true
        invalidate()
    }

    interface DrawCompletedCallback{
        fun onDrawCompleted()
    }

}