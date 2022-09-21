package com.nfa.emchip8

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class Screen() : Canvas() {

    private val width: Int = 64
    private val height: Int = 32
    private val scale: Double = 10.0
    private val pixels = Array(width) {IntArray(height)}
    private val gc: GraphicsContext = this.graphicsContext2D

    init {
        super.setHeight(height.toDouble()*scale)
        super.setWidth(width.toDouble()*scale)
        clear()
    }

    fun clear() {
        for (i in pixels.indices) {
            for (j in pixels[i].indices) {
                pixels[i][j] = 0
            }
        }
        gc.fill = Color.BLACK
        gc.fillRect(0.0, 0.0, width.toDouble()*scale, height.toDouble()*scale)
    }

    fun setPixel(x: Int, y: Int, value: Boolean) {
        gc.fill = if (value) Color.WHITE else Color.BLACK
        gc.fillRect(x.toDouble()*scale, y.toDouble()*scale, scale, scale)
    }

    fun getPixel(x: Int, y: Int): Boolean {
        require(x in 0 until width && y in 0 until height)
        return pixels[x][y] == 1
    }

    fun getCanvasWidth(): Int = width

    fun getCanvasHeight(): Int = height
}
