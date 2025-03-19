package com.github.nbsllc.gdxperformance

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils

class Main : ApplicationAdapter() {

    private lateinit var camera: OrthographicCamera
    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var polygon1: Polygon
    private lateinit var polygon2: Polygon
    private lateinit var font: BitmapFont
    private lateinit var batch: SpriteBatch

    override fun create() {
        val width = Gdx.graphics.width
        val height = Gdx.graphics.height

        camera = OrthographicCamera(width.toFloat(), height.toFloat())
        camera.position.set(width / 2f, height / 2f, 0f)
        camera.update()

        shapeRenderer = ShapeRenderer()

        polygon1 = Polygon(
            floatArrayOf(
                0f, 0f,
                100f, 0f,
                100f, 100f,
                0f, 100f
            )
        )
        polygon1.setOrigin(50f, 50f)
        polygon1.setPosition(width / 2f - 50f, height / 2f - 50f)

        polygon2 = Polygon(
            floatArrayOf(
                0f, 0f,
                100f, 0f,
                100f, 100f,
                0f, 100f
            )
        )
        polygon2.setOrigin(50f, 50f)
        polygon2.setPosition(width / 2f + 200f, height / 2f + 200f)

        font = BitmapFont()
        batch = SpriteBatch()
    }

    override fun render() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit()
        }

        val deltaTime = Gdx.graphics.deltaTime

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

        // Rotate polygon1
        polygon1.rotation += 90f * deltaTime

        // Handle movement of polygon2
        var polygon2x = polygon2.x
        var polygon2y = polygon2.y
        val velocity = Vector2()

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x = -1f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x = 1f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            velocity.y = 1f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            velocity.y = -1f
        }

        // Should really use len2 for perf, but len looks smoother to me
        if (velocity.len() > 0) {
            velocity.nor()
        }

        polygon2x += velocity.x * 100f * deltaTime
        polygon2y += velocity.y * 100f * deltaTime
        polygon2.setPosition(polygon2x, polygon2y)

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
