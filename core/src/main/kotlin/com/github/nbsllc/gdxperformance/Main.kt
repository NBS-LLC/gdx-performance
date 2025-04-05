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
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport

class Main : ApplicationAdapter() {

    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: Viewport
    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var anomaly: Polygon
    private lateinit var player: Polygon
    private lateinit var font: BitmapFont
    private lateinit var batch: SpriteBatch

    private fun createPlayer(x: Float, y: Float) {
        player = Polygon(
            floatArrayOf(
                0f, 0f,
                30f, 10f,
                0f, 20f
            )
        )
        player.setOrigin(15f, 10f)
        player.setPosition(x - 15f, y - 10f)
    }

    private fun createAnomaly(x: Float, y: Float) {
        anomaly = Polygon(
            floatArrayOf(
                0f, 0f,
                100f, 0f,
                100f, 100f,
                0f, 100f
            )
        )
        anomaly.setOrigin(50f, 50f)
        anomaly.setPosition(x - 50f, y - 50f)
    }

    private fun updatePlayer(deltaTime: Float) {
        val speed = 250f
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

        var x = player.x
        var y = player.y
        x += velocity.x * speed * deltaTime
        y += velocity.y * speed * deltaTime
        player.setPosition(x, y)
    }

    private fun updateAnomaly(deltaTime: Float) {
        anomaly.rotation += 90f * deltaTime
    }

    override fun create() {
        val width = 1000f
        val height = 1000f

        camera = OrthographicCamera()
        camera.setToOrtho(false, width, height)
        camera.position.set(0f, 0f, 0f)
        camera.update()

        viewport = ExtendViewport(width, height, camera)

        shapeRenderer = ShapeRenderer()

        createAnomaly(width / 2f, height / 2f)
        createPlayer(width / 6f, height / 1.5f)

        font = BitmapFont()
        batch = SpriteBatch()
    }

    override fun render() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit()
        }

        val deltaTime = Gdx.graphics.deltaTime

        ScreenUtils.clear(0f, 0f, 0f, 1f, true)

        camera.update()
        viewport.apply()

        // Gradient background
        val width = viewport.worldWidth
        val height = viewport.worldHeight

        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        for (y in 0 until height.toInt()) {
            val ratio = y / height
            val color = Color(0.1f * ratio, 0.3f * ratio, 0.5f * ratio, 1f)
            shapeRenderer.color = color
            shapeRenderer.rect(0f, y.toFloat(), width, 1f)
        }

        shapeRenderer.end()

        updateAnomaly(deltaTime)
        updatePlayer(deltaTime)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

        shapeRenderer.color = Color.GRAY
        shapeRenderer.polygon(anomaly.transformedVertices)

        shapeRenderer.color = Color.GRAY
        shapeRenderer.polygon(player.transformedVertices)

        shapeRenderer.end()

        // Display FPS
        batch.projectionMatrix = camera.combined
        batch.begin()
        font.draw(batch, "FPS: ${Gdx.graphics.framesPerSecond}", 10f, height - 10f)
        font.draw(batch, "Resolution: ${Gdx.graphics.width}x${Gdx.graphics.height}", 10f, height - 30f)
        batch.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        shapeRenderer.dispose()
        batch.dispose()
        font.dispose()
    }
}
