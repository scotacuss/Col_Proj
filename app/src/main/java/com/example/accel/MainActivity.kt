package com.example.accel


import android.annotation.SuppressLint
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
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlin.math.absoluteValue
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


var arbitrary = 1
var cameraLock: Int = 2



class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var ball: ImageView
    private lateinit var details_1: TextView
    private lateinit var meas: View
    private lateinit var mv: RelativeLayout
    private lateinit var set_but: Button
    private lateinit var cam_but: Button

    private lateinit var deets: TextView
    private lateinit var scrollView: ScrollView

    private lateinit var timer: CountDownTimer

    private lateinit var obstacles: MutableList<Array<Any>>

    private var xAccel = 0F
    private var yAccel = 0F
    var xVelo = 0F
    var yVelo = 0F
    val terminal_velo = 40


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
        set_but = findViewById(R.id.setting_but)
        deets = findViewById(R.id.debug)
        scrollView = findViewById(R.id.main_scroll_view)
        cam_but = findViewById(R.id.camera_but)

        cam_but.setOnClickListener {
            cameraLock *= (-1)
        }


















        set_but.setOnClickListener {
            val intent: Intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        ball.layoutParams.height = diameter
        ball.layoutParams.width = diameter











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



        setUpSensorStuff()
    }

    fun ballReset(){
        ball.y = meas.y + ball.width
        ball.x = (meas.width/2).toFloat()
        xVelo = 0F
        yVelo = 0F
        ball.visibility = VISIBLE
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

    fun FdragAccel(Xvelo: Float, Yvelo: Float): Double {
        val totalSpeed = sqrt(Xvelo.pow(2) + Yvelo.pow(2))
        val ballMass = ((4/3)*(3.14)*((diameter/2).toFloat().pow(3)))* ball_density
        val force =  (((0.5) * (medium_density) * (totalSpeed.pow(2))  * ((3.14* (diameter/2)/100).pow(2))).toDouble())
        val accel = force/ballMass
        return accel
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
                
                val speed = sqrt(xVelo.pow(2) + yVelo.pow(2))


                var dragAccel = FdragAccel(yVelo,xVelo)
                var velAng = atan(-yVelo/xVelo)
                val xDrag = if (xVelo < 0)  (cos(velAng) * dragAccel) else (-1*(cos(velAng) * dragAccel))

                val yDrag = if (yVelo < 0) (sin(velAng) * dragAccel) else (-1*(sin(velAng) * dragAccel))


                details_1.text = (sin(velAng) * dragAccel).toString()

                xVelo += xAccel
                yVelo += yAccel


//                colDetec(obs1)
                for (obs in obstacles){
                    colDetec(obs)
                }

                if (ball.y > 400 && cameraLock > 0){
                    scrollView.scrollY = ball.y.toInt()-800
                }

                set_but.y = (scrollView.scrollY + 20 ).toFloat()
                cam_but.y = (scrollView.scrollY + 20 ).toFloat()
                details_1.y = (scrollView.scrollY + 20 ).toFloat()







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


