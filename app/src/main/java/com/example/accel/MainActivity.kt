package com.example.accel


import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.math.RoundingMode
import kotlin.math.absoluteValue
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var ball: ImageView
    private lateinit var details_1: TextView
    private lateinit var meas: View
    private lateinit var mv: RelativeLayout
    private lateinit var finLine: View
    private lateinit var button1: Button
    private lateinit var strtbut: Button

    private lateinit var deets: TextView

    private lateinit var timer: CountDownTimer


    private lateinit var obstacles: Array<Array<Any>>



    var timeLeft: Double = 5.00

    private var xAccel = 0F
    private var yAccel = 0F
    var xVelo = 0F
    var yVelo = 0F
    val terminal_velo = 40
    val dampner = 0.8F // coefficient of restitution




    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        details_1 = findViewById(R.id.deets_1)
        ball = findViewById(R.id.pro_ball)
        meas = findViewById(R.id.measur)
        mv = findViewById(R.id.main_view)
        finLine = findViewById(R.id.finish_line)
        button1 = findViewById(R.id.fail_win)
        deets = findViewById(R.id.debug)




        button1.setOnClickListener{
            ballReset()
            timeLeft = 5.00
            timer.start()
            button1.visibility = View.GONE
        }
        button1.visibility = View.GONE


        obstacles = arrayOf(
            createObs(this,mv,200F, 500F, 250, 250, 0.8, "circle"),
            createObs(this,mv,700F, 500F, 250, 250, 0.8, "circle")
        )

        (obstacles[0][0] as ImageView).setOnTouchListener { _, event ->
            // Get the X and Y coordinates of the touch event
            val x = event.x
            val y = event.y

            // Perform actions based on the touch event
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    // Finger is moving across the screen
                    // Perform any desired actions here
                    deets.text = "x = $x \n y = $y"
                    (obstacles[0][0] as ImageView).x = x
                    (obstacles[0][0] as ImageView).y = y
                }
            }
            true
        }

        button1.bringToFront()


        timer = object  : CountDownTimer(5400,100){
            override fun onTick(millisUntilFinished: Long) {
                details_1.text = timeLeft.toBigDecimal().setScale(1, RoundingMode.UP).toDouble().toString()
                timeLeft -= 0.1
                if (timeLeft < -0.1){
                    timer.cancel()
                    details_1.visibility = View.GONE
                    ball.visibility = View.GONE
                    button1.visibility = View.VISIBLE
                }
                if (ball.visibility == View.GONE){
                    details_1.visibility = View.GONE
                    timer.cancel()
                }
                else{
                    details_1.visibility = View.VISIBLE
                }
            }
            override fun onFinish() {
                timeLeft = 5.00
                timer.start()
            }
        }
        setUpSensorStuff()
    }

    fun ballReset(){
        ball.y = meas.y + ball.width
        ball.x = (finLine.width/2).toFloat()
        xVelo = 0F
        yVelo = 0F
        ball.visibility = VISIBLE
    }

    override fun onStart() {
        super.onStart()
        timer.start()
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

    fun colDetec(obs: Array<Any>) {
        // ChatGPT
        val obsIV: ImageView = obs[0] as ImageView
        val obsDamp: Double = obs[1] as Double
        val obsShape: String = obs[2] as String

        var ballCenterX = ball.x + (ball.width / 2)
        var ballCenterY = ball.y + (ball.height / 2)

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
                    xVelo = (-xVelo * obsDamp).toFloat()
                }
                if (deltaY.toDouble() != 0.0) {
                    yVelo = (-yVelo * obsDamp).toFloat()
                }
            }
        }
        else {
            val deltaX = ballCenterX - xCi
            val deltaY = ballCenterY - yCi
            var offset = 0
            val ballR = ball.width/2
            val obsR = obsIV.width/2

            if (deltaX.pow(2) + deltaY.pow(2) < ((ball.width / 2)+(obsIV.width / 2)).toDouble().pow(2)) {
                val dx = xVelo.toDouble()
                val dy = yVelo.toDouble()
                val speed: Double = sqrt(dx.pow(2) + dy.pow(2))
                val currentAngle: Double = atan2(dy, dx)

                val reflecAngle: Double = atan2(xCi.toDouble() - ballCenterX, yCi.toDouble() - ballCenterY)
                val newAng: Double = 2*reflecAngle - currentAngle

                xVelo = (speed * cos(newAng)).toFloat()
                yVelo = (speed * sin(newAng)).toFloat()

            }
        }

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



    fun screenBoundaryCollision(coord: Float, minBound: Float, maxBound: Float, velocity: Float): Pair<Float, Float> {
        val newCoord = when {
            coord > maxBound -> maxBound
            coord < minBound -> minBound
            else -> coord
        }
        val newVelocity = if (newCoord != coord) -velocity * dampner else velocity
        return Pair(newCoord, newVelocity)
    }

    fun Fdrag(velo: Float, Vmax: Float = 20F): Float {
        val Cd = (1.96)/(90.945*(terminal_velo.toDouble().pow(2)))
        val dir: Double
        if (velo > 0) {
            dir = 1.0
        }
        else {
            dir = -1.0
        }
        return (dir*((0.5) * (1.29) * (velo.pow(2)) * Cd * (ball.width))).toFloat()
    }





    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onSensorChanged(event: SensorEvent?) {

            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                val sides = event.values[0]
                val upDown = event.values[1]

                xAccel = sides / 5
                yAccel = upDown / 5

                val rightBounds = (meas.right - ball.width).toFloat()
                val bottomBounds = (meas.bottom - ball.height).toFloat()

                var FdragX = Fdrag(xVelo)
                var FdragY = Fdrag(yVelo)



                xVelo += xAccel - FdragX
                yVelo += yAccel - FdragY


//                colDetec(obs1)
                for (obs in obstacles){
                    colDetec(obs)
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

//                    if (ball.y + ball.width / 2 > finLine.y) {
//                        ball.visibility = View.GONE
//                        button1.visibility = View.VISIBLE
//                    }

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


