package com.example.comp3717_project.game

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.media.SoundPool
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.comp3717_project.R
import com.example.comp3717_project.game.Cloud.Companion.CLOUD_DISTANCE
import com.example.comp3717_project.game.Cloud.Companion.CLOUD_WIDTH
import com.example.comp3717_project.game.Obstacle.Companion.OBSTACLE_DISTANCE
import com.example.comp3717_project.game.Obstacle.Companion.OBSTACLE_WIDTH
import com.example.comp3717_project.game.Surfer.Companion.GRAVITY
import com.example.comp3717_project.game.Surfer.Companion.SURFER_HEIGHT
import com.example.comp3717_project.game.Surfer.Companion.SURFER_START_X
import com.example.comp3717_project.game.Surfer.Companion.SURFER_START_Y
import com.example.comp3717_project.game.Surfer.Companion.SURFER_WIDTH
import kotlinx.coroutines.*

class GameView(
    context: Context,
    attrs: AttributeSet?,
) : View(context, attrs) {

    companion object {
        const val GAME_UPDATE_RATE = 30L
        val OBSTACLE_COLOR = Color.rgb(0, 128, 0)
        const val SCORE_COLOR = Color.BLACK
        const val SCORE_SIZE = 150f
        const val SCORE_FONT = R.font.vt323
        const val OBSTACLE_SHADER_WIDTH = OBSTACLE_WIDTH * 3
    }

    // Game variables
    var score = 0
    private var isPaused = false
    private var isGameOver = false

    // Sound variables
    private var soundPool: SoundPool? = SoundPool.Builder().setMaxStreams(10).build()
    private val jumpSoundId = soundPool?.load(context, R.raw.jump, 1)
    private val collisionSoundId = soundPool?.load(context, R.raw.collison, 1)
    private val gainPointSoundId = soundPool?.load(context, R.raw.gain_point, 1)

    // Game objects
    private val surfer = Surfer(SURFER_START_X, SURFER_START_Y, 0, GRAVITY)
    private val obstacles = mutableListOf<Obstacle>()
    private val clouds = mutableListOf<Cloud>()

    // Bitmaps and paints
    private var surferBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.skysurfer)
    private val cloudBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.cloud)
    private val cobblestoneBitmap = BitmapFactory.decodeResource(resources, R.drawable.cobblestone)
    private val scaledCobblestoneBitmap = Bitmap.createScaledBitmap(cobblestoneBitmap, OBSTACLE_SHADER_WIDTH, OBSTACLE_SHADER_WIDTH, true)
    private val cobblestoneShader = BitmapShader(scaledCobblestoneBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
    private val obstaclePaint = Paint().apply {
        color = OBSTACLE_COLOR
        shader = cobblestoneShader
    }
    private val scorePaint = Paint().apply {
        val typeface = ResourcesCompat.getFont(context, SCORE_FONT)
        color = SCORE_COLOR
        textSize = SCORE_SIZE
        textAlign = Paint.Align.LEFT
        setTypeface(typeface)
    }
    private val borderPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }


    @SuppressLint("ResourceAsColor", "DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Draw the black border
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), borderPaint)
        canvas?.drawColor(Color.rgb(125, 209, 236))

        // Draw the clouds
        clouds.forEach { cloud ->
            canvas?.drawBitmap(
                cloudBitmap,
                null,
                Rect(cloud.x, cloud.y, cloud.x + cloud.width, cloud.y + cloud.height),
                null
            )
        }

        // Draw the surfer
        canvas?.drawBitmap(
            surferBitmap,
            null,
            Rect(surfer.x, surfer.y, surfer.x + SURFER_WIDTH, surfer.y + SURFER_HEIGHT),
            null
        )



        // Draw the obstacles
        obstacles.forEach { obstacle ->
            // translate the shader so that it moves with the obstacle
            obstaclePaint.shader = Matrix().apply {
                setTranslate(obstacle.x.toFloat(), 0f)
            }.let { matrix ->
                cobblestoneShader.apply {
                    setLocalMatrix(matrix)
                }
            }

            // draw the top obstacle
            canvas?.drawRect(
                obstacle.x.toFloat(),
                0f,
                (obstacle.x + OBSTACLE_WIDTH).toFloat(),
                obstacle.topHeight.toFloat(),
                obstaclePaint
            )

            // draw the bottom obstacle
            canvas?.drawRect(
                obstacle.x.toFloat(),
                obstacle.bottomY.toFloat(),
                (obstacle.x + OBSTACLE_WIDTH).toFloat(),
                height.toFloat(),
                obstaclePaint
            )
        }

        // Draw the score
        canvas?.drawText("$score", (width / 2).toFloat(), 200f, scorePaint)
    }

    fun update() {
        if (!isPaused) {
            // Update the surfer
            surfer.update()

            // Check if the surfer is out of the screen
            if (surfer.y < 0 || surfer.y > height) {
                println("OUT OF SCREEN")
                gameOver()
            }

            // Update the obstacles
            val iterator = obstacles.iterator()
            while (iterator.hasNext()) {
                val obstacle = iterator.next()
                obstacle.update()

                if (surfer.collidesWith(obstacle, this)) {
                    println("COLLISION")
                    gameOver()
                }

                if (obstacle.x < -OBSTACLE_WIDTH)
                    iterator.remove()

                if (obstacle.x <= surfer.x && !obstacle.hasBeenPassed) {
                    obstacle.pass()
                    score++
                    (context as GameActivity).highScore = score
                    soundPool?.play(gainPointSoundId!!, 1f, 1f, 1, 0, 1f)
                }

            }

            // Add a new obstacle when necessary
            if (obstacles.isEmpty() || obstacles.last().x < width - OBSTACLE_DISTANCE - OBSTACLE_WIDTH) {
                obstacles.add(Obstacle(width, this))
            }

            // Update the clouds
            val cloudIterator = clouds.iterator()
            while (cloudIterator.hasNext()) {
                val cloud = cloudIterator.next()
                cloud.update()
            }

            // Add a new cloud when necessary
            if (clouds.isEmpty() || clouds.last().x < width - CLOUD_DISTANCE - CLOUD_WIDTH) {
                clouds.add(Cloud(width, this))
            }


            // Redraw the UI
            invalidate()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN && !isPaused) {
            soundPool?.play(jumpSoundId!!, 1f, 1f, 1, 0, 1f)
            surfer.jump()
        }
        return true
    }

    fun togglePause(isPaused: Boolean) {
        if (isGameOver) return
        this.isPaused = isPaused
    }

    fun toggleSound(isSoundOn: Boolean) {
        if (isSoundOn) {
            soundPool?.setVolume(jumpSoundId!!, 1f, 1f)
            soundPool?.setVolume(collisionSoundId!!, 1f, 1f)
            soundPool?.setVolume(gainPointSoundId!!, 1f, 1f)
        } else {
            soundPool?.setVolume(jumpSoundId!!, 0f, 0f)
            soundPool?.setVolume(collisionSoundId!!, 0f, 0f)
            soundPool?.setVolume(gainPointSoundId!!, 0f, 0f)
        }
    }

    private fun gameOver() {
        if (!isGameOver) {
            isGameOver = true
            soundPool?.play(collisionSoundId!!, 1f, 1f, 1, 0, 1f)
            isPaused = true

            (context as GameActivity).toggleGameOverFragmentVisibility(true)
        }
    }

    fun restartGame() {
        // reset state variables
        isPaused = false
        isGameOver = false

        // Reset the score
        score = 0

        // Reset the surfer position and velocity
        surfer.x = SURFER_START_X
        surfer.y = SURFER_START_Y
        surfer.speed = 0

        // Clear the obstacles list
        obstacles.clear()

        // Add the initial obstacle
        obstacles.add(Obstacle(width, this))

        surfer.update()

        // Redraw the UI
        invalidate()
    }

}
