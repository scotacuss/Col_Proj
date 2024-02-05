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
import android.view.WindowManager
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var ball: ImageView
    private lateinit var details_1: TextView
    private lateinit var details_2: TextView
    private lateinit var meas: View
    private lateinit var mv: RelativeLayout

    lateinit var obs1: Array<Any>

    private lateinit var ll_lay: LinearLayout


    fun createObs(x: Float, y: Float, ht: Int, wd: Int, dampning:Double = 0.8, shape: String = "square"): Array<Any> {
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
        return arrayOf(obs,dampning)
    }




//    fun colDetec(obs: ImageView, obsDamp: Double) {
//        val x = ball.x + (ball.width / 2)
//        val y = ball.y + (ball.height / 2)
//        val x1 = obs.x
//        val y1 = obs.y
//        val x2 = obs.x + obs.width
//        val y2 = obs.y + obs.height
//
//        if (x1 < x && x < x2){
//            if (y < y1){
//                if (y < y1 && y1-y < ball.height/2){
//                    ball.y -= 1
//                    yVelo = (-yVelo * obsDamp).toFloat()
//                }
//            }
//            else if(y-y2 < ball.height/2){
//                ball.y += 1
//                yVelo = (-yVelo*obsDamp).toFloat()
//            }
//        }
//        else if (y1 < y && y < y2) {
//            if (x < x1){
//                if(x1-x < ball.width/2){
//                    ball.x -= 1
//                    xVelo = (-xVelo * obsDamp).toFloat()
//                }
//            }
//            else if (x-x2 < ball.width/2){
//                ball.x += 1
//                xVelo = (-xVelo*obsDamp).toFloat()
//            }
//        }
//        else if(x < x1 && y < y1){
//            if (ball.width/2 > (sqrt((x1-x).pow(2) + (y1-y).pow(2))).absoluteValue){
//                ball.x -= 1
//                ball.y -= 1
//                xVelo = (-xVelo*obsDamp).toFloat()
//                yVelo = (-xVelo * obsDamp).toFloat()
//            }
//        }
//        else if(x > x2 && y < y1){
//            if (ball.width/2 > (sqrt((x-x2).pow(2) + (y1-y).pow(2))).absoluteValue){
//                ball.x += 1
//                ball.y -= 1
//                xVelo = (-xVelo*obsDamp).toFloat()
//                yVelo = (-xVelo * obsDamp).toFloat()
//            }
//        }
//        else if(x < x1 && y > y2){
//            if (ball.width/2 > (sqrt((x1 - x).pow(2) + (y-y2).pow(2))).absoluteValue){
//                ball.x -= 1
//                ball.y += 1
//                xVelo = (-xVelo*obsDamp).toFloat()
//                yVelo = (-xVelo * obsDamp).toFloat()
//            }
//        }
//        else if(x > x2 && y > y2){
//            if (ball.width/2 > (sqrt((x-x2).pow(2) + (y-y2).pow(2))).absoluteValue){
//                ball.x += 1
//                ball.y += 1
//                xVelo = (-xVelo*obsDamp).toFloat()
//                yVelo = (-xVelo * obsDamp).toFloat()
//            }
//        }
//    }

    fun colDetec(obs: ImageView, obsDamp: Double) {
        // ChatGPT
        val ballCenterX = ball.x + (ball.width / 2)
        val ballCenterY = ball.y + (ball.height / 2)

        val x1 = obs.x
        val y1 = obs.y
        val x2 = obs.x + obs.width
        val y2 = obs.y + obs.height

        val deltaX = ballCenterX - max(x1, min(ballCenterX, x2))
        val deltaY = ballCenterY - max(y1, min(ballCenterY, y2))

        if (deltaX.pow(2) + deltaY.pow(2) < (ball.width / 2).toDouble().pow(2)) {
            // Collision detected
            if (deltaX.toDouble() != 0.0) {
                ball.x += if (deltaX < 0) -1 else 1
                xVelo = (-xVelo * obsDamp).toFloat()
            }
            if (deltaY.toDouble() != 0.0) {
                ball.y += if (deltaY < 0) -1 else 1
                yVelo = (-yVelo * obsDamp).toFloat()
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ball = findViewById(R.id.pro_ball)
        details_1 = findViewById(R.id.deets_1)
        details_2 = findViewById(R.id.deets_2)
        meas = findViewById(R.id.measur)
        mv = findViewById(R.id.main_view)

        setUpSensorStuff()


        // Obstacles
        obs1 = createObs(300F, 900F, 500, 500, 0.8)
    }





    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    private var xAccel = 0F
    private var yAccel = 0F

    private var xVelo = 0F
    private var yVelo = 0F

    private val terminal_velo = 40
    private val dampner = 0.8F // coefficient of restitution

    fun screenBoundaryCollision(coord: Float, minBound: Float, maxBound: Float, velocity: Float): Pair<Float, Float> {
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


            xAccel = sides / 5
            yAccel = upDown / 5

            xVelo += xAccel
            yVelo += yAccel

            colDetec(obs1[0] as ImageView, obs1[1] as Double)


            if (xVelo > terminal_velo || xVelo < -terminal_velo) {
                if (xVelo < 0){
                    xVelo = -(terminal_velo + 0.5).toFloat()
                }
                else{
                xVelo = (terminal_velo - 0.5).toFloat()
                }
            }

            if (yVelo > terminal_velo || yVelo < -terminal_velo) {
                if (yVelo < 0){
                    yVelo = -(terminal_velo + 0.5).toFloat()
                }
                else {
                    yVelo = (terminal_velo - 0.5).toFloat()
                }
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

            }

//            (obs1[0] as ImageView)


            details_1.text = "x Velocity ${2}\n"
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