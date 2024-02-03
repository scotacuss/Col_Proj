package com.example.accel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.Image
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlin.math.absoluteValue


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var ball: ImageView
    private lateinit var details_1: TextView
    private lateinit var details_2: TextView
    private lateinit var meas: View
    private lateinit var mv: RelativeLayout

    lateinit var obs1: ImageView

    private lateinit var ll_lay: LinearLayout


    fun createObs(x: Float, y: Float, ht: Int, wd: Int, shape: String = "square"): ImageView {
        var obs = ImageView(this)
        if (shape == "circle") {
            obs.setImageResource(R.drawable.circ_obs)
        } else {
            obs.setBackgroundColor(Color.parseColor("#000000"))
        }
        obs.layoutParams = LinearLayout.LayoutParams(wd, ht)
        obs.x = x
        obs.y = y

        mv.addView(obs)
        return obs
    }


    fun colDetec(projectile: ImageView, obs: ImageView, obs_shape: String = "square"){
        val pro_x = projectile.x + (projectile.width / 2)
        val pro_y = projectile.y + (projectile.height / 2)
        val obs_R = obs.x + obs.width
        val obs_B = obs.y + obs.height


            if (pro_y > obs.y && pro_y < obs_B && (obs.x - pro_x) < projectile.width/2){
                projectile.x -= 1
                 }
            else if (pro_y > obs.y && pro_y < obs_B && (pro_x - obs_R) < projectile.width/2){
                projectile.x += 1
            }
            else if (pro_x > obs.x && pro_x < obs_R && (obs.y - pro_y) < projectile.height/2){
                projectile.y -= 1
            }
            else if (pro_x > obs.x && pro_x < obs_R && (pro_y - obs_B) < projectile.width/2){
                projectile.y += 1
            }
        }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ball = findViewById(R.id.pro_ball)
        details_1 = findViewById(R.id.deets_1)
        details_2 = findViewById(R.id.deets_2)
        meas = findViewById(R.id.measur)
        mv = findViewById(R.id.main_view)

        setUpSensorStuff()


        // Obstacles
        obs1 = createObs(500F, 900F, 100, 700)
    }


    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    private var xAccel = 0F
    private var yAccel = 0F

    private var xVelo = 0F
    private var yVelo = 0F

    private val terminal_velo = 10
    private val dampner = 0.8F // coefficient of restitution

    fun screenBoundaryCollision(
        coord: Float,
        minBound: Float,
        maxBound: Float,
        velocity: Float

    ): Pair<Float, Float> {
        val newCoord = when {
            coord > maxBound -> maxBound
            coord < minBound -> minBound
            else -> coord
        }
        val newVelocity = if (newCoord != coord) -velocity * dampner else velocity
        return Pair(newCoord, newVelocity)
    }


    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val sides = event.values[0]
            val upDown = event.values[1]

            val rightBounds = (meas.right - ball.width).toFloat()
            val bottomBounds = (meas.bottom - ball.height).toFloat()


            xAccel = sides / 100
            yAccel = upDown / 100

            xVelo += xAccel
            yVelo += yAccel


            if (xVelo > terminal_velo || xVelo < -terminal_velo) {
                xVelo = (terminal_velo - 0.5).toFloat()
            }

            if (yVelo > terminal_velo || yVelo < -terminal_velo) {
                yVelo = (terminal_velo - 0.5).toFloat()
            }


            ball.apply {

                ball.x -= xVelo.toFloat()
                ball.y += yVelo.toFloat()


                // Screen Boundary Collision Logic
                val (newX, newXVelo) = screenBoundaryCollision(ball.x, 0F, rightBounds, xVelo)
                ball.x = newX
                xVelo = newXVelo

                val (newY, newYVelo) = screenBoundaryCollision(ball.y, 0F, bottomBounds, yVelo)
                ball.y = newY
                yVelo = newYVelo

                colDetec(ball,obs1)
            }


            details_1.text = "x Velocity ${xAccel}\n"
            details_2.text = "ball x ${ball.x}\nball y ${ball.y}\n"
        }
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
}