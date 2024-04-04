package com.example.accel


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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





var arbitrary = 1



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

    private lateinit var obstacles: MutableList<Array<Any>>

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
        var set_but = findViewById<Button>(R.id.setting_but)
        deets = findViewById(R.id.debug)

        set_but.setOnClickListener {
            val intent: Intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        ball.layoutParams.height = diameter
        ball.layoutParams.width = diameter








        button1.setOnClickListener{
            ballReset()
            timeLeft = 5.00
            timer.start()
            button1.visibility = View.GONE
        }
        button1.visibility = View.GONE


        obstacles = mutableListOf(
            createObs(this,mv,200F, 500F, 250, 250, 1.0, "circle"),
            createObs(this,mv,700F, 500F, 250, 250, 1.0, "circle")
        )

        if (arbitrary > 0) {
            created_obs.removeAt(0)
            arbitrary --
        }

        if (created_obs.size > 0) {
            for (obs in created_obs) {
                obstacles.add(createObs(this,mv,0F,0F,obs[0] as Int,obs[1] as Int,obs[2] as Double,obs[3] as String))
            }
        }



        for (obs in obstacles) {
            (obs[0] as ImageView).setOnTouchListener { _, event ->
                // Get the X and Y coordinates of the touch event
                val x = event.rawX - ((obs[0] as ImageView).width / 2)
                val y = event.rawY - ((obs[0] as ImageView).width)

                // Perform actions based on the touch event
                when (event.action) {

                    MotionEvent.ACTION_MOVE -> {
                        // Finger is moving across the screen
                        // Perform any desired actions here
                        (obs[0] as ImageView).x = x
                        (obs[0] as ImageView).y = y
                    }
                }
                true
            }
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
                    xVelo = (-xVelo * obsDamp * COR).toFloat()
                }
                if (deltaY.toDouble() != 0.0) {
                    yVelo = (-yVelo * obsDamp * COR).toFloat()
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

                xVelo = ((speed * cos(newAng))* obsDamp * COR).toFloat()
                yVelo = ((speed * sin(newAng))* obsDamp * COR).toFloat()

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
        val newVelocity = if (newCoord != coord) -velocity * COR.toFloat() else velocity
        return Pair(newCoord, newVelocity)
    }

    fun Fdrag(velo: Float, Vmax: Float = 20F): Double {
        val Cd = (1.96)/(90.945*(terminal_velo.toDouble().pow(2)))
        val dir: Double
        if (velo > 0) {
            dir = 1.0
        }
        else {
            dir = -1.0
        }
        return (dir*((0.5) * (medium_density) * (velo.pow(2)) * 0.00001 * ((3.14* (diameter/2)/100).pow(2))).toFloat())
    }

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onSensorChanged(event: SensorEvent?) {

            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                val sides = event.values[0]
                val upDown = event.values[1]

                deets.text = obstacles.size.toString()

                var grav_real = medium_gravity/9.81

                xAccel = (sides * grav_real).toFloat()
                yAccel = (upDown * grav_real).toFloat()

                val rightBounds = (meas.right - ball.width).toFloat()
                val bottomBounds = (meas.bottom - ball.height).toFloat()

                var FdragX = Fdrag(xVelo)
                var FdragY = Fdrag(yVelo)

                deets.text = FdragY.toString()

                xVelo += xAccel - FdragX.toFloat()
                yVelo += yAccel - FdragY.toFloat()


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


