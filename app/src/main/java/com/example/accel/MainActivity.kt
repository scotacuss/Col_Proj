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
import android.os.CountDownTimer
import android.view.VelocityTracker
import android.view.View
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Button
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
    private lateinit var meas: View
    private lateinit var mv: RelativeLayout
    private lateinit var finLine: View
    private lateinit var button1: Button




    private lateinit var timer: CountDownTimer
    lateinit var obs1: Array<Any>

    var timeLeft: Int = 5



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
        return arrayOf(obs,dampning,shape)
    }





    fun colDetec(obs: Array<Any>, shape: String = "square") {
        // ChatGPT
        val obsIV: ImageView = obs[0] as ImageView
        val obsDamp: Double = obs[1] as Double
        val obsShape: String = obs[2] as String

        val ballCenterX = ball.x + (ball.width / 2)
        val ballCenterY = ball.y + (ball.height / 2)

        val x1 = obsIV.x
        val y1 = obsIV.y
        val xSq = obsIV.x + obsIV.width
        val ySq = obsIV.y + obsIV.height
        val xCi = obsIV.x + obsIV.width/2
        val yCi = obsIV.y + obsIV.height/2


        if (obsShape == "square"){
            val deltaX = ballCenterX - max(x1, min(ballCenterX, xSq))
            val deltaY = ballCenterY - max(y1, min(ballCenterY, ySq))

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
        else {
            val deltaX = ballCenterX - xCi
            val deltaY = ballCenterY - yCi

            if (deltaX.pow(2) + deltaY.pow(2) < ((ball.width / 2)+(obsIV.width / 2)).toDouble().pow(2)) {
            //TODO
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
        meas = findViewById(R.id.measur)
        mv = findViewById(R.id.main_view)
        finLine = findViewById(R.id.finish_line)
        button1 = findViewById<Button>(R.id.fail_win)




        button1.setOnClickListener{
            ball.y = meas.y + ball.width
            ball.x = (finLine.width/2).toFloat()
            xVelo = 0F
            yVelo = 0F
            ball.visibility = VISIBLE
            button1.visibility = View.GONE

        }
        button1.visibility = View.GONE

        setUpSensorStuff()

        // Obstacles
        obs1 = createObs(400F, 900F, 250, 250, 0.8)


        button1.bringToFront()

        timer = object  : CountDownTimer(6000,1000){
            override fun onTick(millisUntilFinished: Long) {
                details_1.text = timeLeft.toString()
                timeLeft -= 1

            }

            override fun onFinish() {
                timeLeft = 5
                timer.start()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        timer.start()
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
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

            colDetec(obs1)


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

                if(ball.y + ball.width/2 > finLine.y){
                    ball.visibility = View.GONE
                    button1.visibility = View.VISIBLE
                }

            }





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


