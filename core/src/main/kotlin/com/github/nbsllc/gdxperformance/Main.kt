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
        player.setPosition(x, y)
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

        createPlayer(width / 6f, height / 1.5f)

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

        updatePlayer(deltaTime)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

        // Render polygons
        shapeRenderer.color = Color.GRAY
        shapeRenderer.polygon(polygon1.transformedVertices)

        shapeRenderer.color = Color.GRAY
        shapeRenderer.polygon(player.transformedVertices)

        shapeRenderer.end()

        // Display FPS
        batch.projectionMatrix = camera.combined
        batch.begin()
        font.draw(batch, "FPS: ${Gdx.graphics.framesPerSecond}", 10f, height - 10f)
        batch.end()
    }

    override fun resize(width: Int, height: Int) {
        // Update your camera and other rendering-related variables here
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.position.set(width / 2f, height / 2f, 0f)
        camera.update()
    }

    override fun dispose() {
        shapeRenderer.dispose()
        batch.dispose()
        font.dispose()
    }
}
