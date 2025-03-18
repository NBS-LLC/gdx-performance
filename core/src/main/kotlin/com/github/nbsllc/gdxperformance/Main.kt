package com.github.nbsllc.gdxperformance

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.utils.ScreenUtils

class Main : ApplicationAdapter() {

    private lateinit var camera: OrthographicCamera
    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var polygon1: Polygon
    private lateinit var polygon2: Polygon
    private lateinit var font: BitmapFont
    private lateinit var batch: SpriteBatch

    override fun create() {
        camera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.position.set(Gdx.graphics.width / 2f, Gdx.graphics.height / 2f, 0f)
        camera.update()

        shapeRenderer = ShapeRenderer()

        polygon1 = Polygon(
            floatArrayOf(
                100f, 100f,
                200f, 150f,
                150f, 250f,
                50f, 200f
            )
        )
        polygon1.setPosition(100f, 100f)

        polygon2 = Polygon(
            floatArrayOf(
                300f, 300f,
                400f, 350f,
                350f, 450f,
                250f, 400f
            )
        )
        polygon2.setPosition(200f, 200f)

        font = BitmapFont()
        batch = SpriteBatch()
    }

    override fun render() {
        // Gradient background
        val width = Gdx.graphics.width.toFloat()
        val height = Gdx.graphics.height.toFloat()

        ScreenUtils.clear(0f, 0f, 0f, 1f, true)

        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        for (y in 0 until height.toInt()) {
            val ratio = y / height
            val color = Color(0.1f * ratio, 0.3f * ratio, 0.5f * ratio, 1f)
            shapeRenderer.color = color
            shapeRenderer.rect(0f, y.toFloat(), width, 1f)
        }

        shapeRenderer.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

        // Render polygons
        shapeRenderer.color = Color.GRAY
        shapeRenderer.polygon(polygon1.transformedVertices)

        shapeRenderer.color = Color.GRAY
        shapeRenderer.polygon(polygon2.transformedVertices)

        shapeRenderer.end()

        // Display FPS
        batch.projectionMatrix = camera.combined
        batch.begin()
        font.draw(batch, "FPS: ${Gdx.graphics.framesPerSecond}", 10f, height - 10f)
        batch.end()
    }

    override fun dispose() {
        shapeRenderer.dispose()
        batch.dispose()
        font.dispose()
    }
}
