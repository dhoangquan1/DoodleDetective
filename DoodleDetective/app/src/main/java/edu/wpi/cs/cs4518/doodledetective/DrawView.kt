package edu.wpi.cs.cs4518.doodledetective

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.createBitmap
import java.util.LinkedList

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs){
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private val undoStack = LinkedList<Bitmap>()

    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 50f
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val path = Path()
    private val bitmapPaint = Paint(Paint.DITHER_FLAG)

    private var lastX = 0f
    private var lastY = 0f

    private var touchListener: ((Bitmap) -> Unit)? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (bitmap == null) {
            bitmap = createBitmap(w, h)
            canvas = Canvas(bitmap!!)
            canvas?.drawColor(Color.WHITE)  // Initialize with white color, no clearing on resize
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                val currentBitmap = bitmap
                if (currentBitmap != null) {
                    val config = currentBitmap.config ?: Bitmap.Config.ARGB_8888
                    val snapshot: Bitmap = currentBitmap.copy(config, true)
                    undoStack.addLast(snapshot)
                    if (undoStack.size > 10) {
                        undoStack.removeFirst()
                    }
                }
                path.moveTo(event.x, event.y)
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                path.quadTo(lastX, lastY, (event.x + lastX) / 2, (event.y + lastY) / 2)
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_UP -> {
                canvas?.drawPath(path, paint)
                path.reset()
                bitmap?.let {
                    val snapshot = it.copy(it.config ?: Bitmap.Config.ARGB_8888, false)
                    Log.d("DrawView", "Bitmap size: ${snapshot?.width}x${snapshot?.height}")
                    touchListener?.invoke(snapshot)
                }
            }
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, bitmapPaint)
        }
        canvas.drawPath(path, paint)
    }

    fun getBitmap(): Bitmap? = bitmap

    fun clear(){
        path.reset()
        bitmap?.eraseColor(Color.WHITE)
        undoStack.clear()
        invalidate()
    }

    fun revert() {
        if (undoStack.isNotEmpty()) {
            val previous = undoStack.removeLast()
            Log.d("Revert", "Revert Called")
            bitmap = previous
            canvas = Canvas(bitmap!!)
            invalidate()

        }
    }

    fun setActionUpListener(listener: (Bitmap) -> Unit){
        this.touchListener = listener
    }

    fun setBitmap(newBitmap: Bitmap) {
        bitmap = newBitmap
        canvas = Canvas(bitmap!!)
        invalidate()
    }

}