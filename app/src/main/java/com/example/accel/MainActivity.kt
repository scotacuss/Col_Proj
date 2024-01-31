package com.example.accel

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate




class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var ball: ImageView
    private lateinit var details_1: TextView
    private lateinit var details_2: TextView
    private lateinit var meas: View
    private lateinit var obs: ImageView
    private lateinit var ll_lay: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ball = findViewById(R.id.pro_ball)
        details_1 = findViewById(R.id.deets_1)
        details_2 = findViewById(R.id.deets_2)
        meas = findViewById(R.id.measur)
        obs = findViewById(R.id.obs_1)
        ll_lay = findViewById(R.id.ll_main_layout)

        fun createObs(x: Float, y:Float, ht:Int, wd:Int, shape:String = "Rectangle", angle:Int = 0, elas:Double = 1.0): ImageView {
            val obs = ImageView(this)
            obs.x = 50F
            obs.y = 50F
            obs.layoutParams = LinearLayout.LayoutParams(100,100)
            obs.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
            ll_lay.addView(obs)
            return obs
        }

//        val obs1 = createObs(150F,50F,50,500)


        setUpSensorStuff()
    }

    private fun setUpSensorStuff(){
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this,it,SensorManager.SENSOR_DELAY_FASTEST, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    private var xAccel = 0F
    private var yAccel = 0F

    private var xVelo = 0F
    private var yVelo = 0F

    private val terminal_velo = 10
    private val dampner = 0.8F // coefficient of restitution

    fun handleBoundaryCollision(coord: Float, minBound: Float, maxBound: Float, velocity: Float): Pair<Float, Float> {
        val newCoord = when {
            coord > maxBound -> maxBound
            coord < minBound -> minBound
            else -> coord
        }
        val newVelocity = if (newCoord != coord) -velocity * dampner else velocity
        return Pair(newCoord, newVelocity)
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val sides = event.values[0]
            val upDown = event.values[1]

            val rightBounds = (meas.right - ball.width).toFloat()
            val bottomBounds = (meas.bottom - ball.height).toFloat()

            xAccel = sides/100
            yAccel = upDown/100

            xVelo += xAccel
            yVelo += yAccel

            if (xVelo > terminal_velo || xVelo < -terminal_velo){
                xVelo = (terminal_velo-0.5).toFloat()
            }

            if (yVelo > terminal_velo || yVelo < -terminal_velo){
                yVelo = (terminal_velo-0.5).toFloat()
            }


            ball.apply {

                ball.x -= xVelo.toFloat()
                ball.y += yVelo.toFloat()

                if (obs.x < ball.x && ball.x < obs.x+obs.width && obs.y < ball.y && ball.y > obs.y+obs.height){
                    ball.x = 250F
                    ball.y = 250F

                }


                val (newX, newXVelo) = handleBoundaryCollision(ball.x,0F, rightBounds, xVelo)
                ball.x = newX
                xVelo = newXVelo

                val (newY, newYVelo) = handleBoundaryCollision(ball.y,0F, bottomBounds, yVelo)
                ball.y = newY
                yVelo = newYVelo
            }


            details_1.text = "x Velocity ${xVelo}\ny Velocity ${yVelo}\n"
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