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
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.text.DecimalFormat
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
    private lateinit var meas: View
    private lateinit var mv: RelativeLayout
    private lateinit var set_but: Button
    private lateinit var cam_but: Button

    private lateinit var deets_1: LinearLayout
    private lateinit var deets_2: LinearLayout

    private lateinit var deets_xVelo: TextView
    private lateinit var deets_yVelo: TextView
    private lateinit var deets_tVelo: TextView
    private lateinit var deets_xAccel: TextView
    private lateinit var deets_yAccel: TextView
    private lateinit var deets_tAccel: TextView
    private lateinit var deets_Fdrag: TextView

    private lateinit var scrollView: ScrollView

    private lateinit var timer: CountDownTimer

    private lateinit var obstacles: MutableList<Array<Any>>

    private var xAccel = 0F
    private var yAccel = 0F
    var xVelo = 1F
    var yVelo = 1F
    val terminal_velo = 40


    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        deets_1 = findViewById(R.id.deets_1)
        deets_2 = findViewById(R.id.deets_2)

        deets_xVelo = findViewById(R.id.deets_xVelo)
        deets_yVelo= findViewById(R.id.deets_yVelo)
        deets_tVelo= findViewById(R.id.deets_tVelo)
        deets_xAccel= findViewById(R.id.deets_xAccel)
        deets_yAccel= findViewById(R.id.deets_yAccel)
        deets_tAccel= findViewById(R.id.deets_tAccel)
        deets_Fdrag = findViewById(R.id.deets_fdrag)



        ball = findViewById(R.id.pro_ball)
        meas = findViewById(R.id.measur)
        mv = findViewById(R.id.main_view)
        set_but = findViewById(R.id.setting_but)
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
            createObs(this,mv,400F, 500F, 250, 250, 1.0, "square"),

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
            (obs[0] as ImageView).setOnTouchListener { view, event ->
                // Get the X and Y coordinates of the touch event
                val x = event.rawX - (view.width / 2)
                val y = event.rawY - (view.width)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Finger touches the obstacle
                        // Disable scrolling of the scroll view
                        scrollView.requestDisallowInterceptTouchEvent(true)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // Finger is moving across the screen, and obstacles position follows the finger
                        view.x = x
                        view.y = y + scrollView.scrollY
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        // Re-enable scrolling of the scroll view when finger is lifted
                        scrollView.requestDisallowInterceptTouchEvent(false)
                    }
                }
                true
            }
        }



        setUpSensorStuff()
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
        val x2 = obsIV.x + obsIV.width
        val y2 = obsIV.y + obsIV.height

        val deltaX = ballCenterX - max(x1, min(ballCenterX, x2))
        val deltaY = ballCenterY - max(y1, min(ballCenterY, y2))


        if (obsShape == "square" && ((deltaX != 0F) xor (deltaY != 0F))){
            if (deltaX.pow(2) + deltaY.pow(2) < (ball.width / 2).toDouble().pow(2) ) {
                // Collision detected
                if (deltaX.toDouble() != 0.0) {
                    xVelo = -xVelo
                    val (x, y) = collisionDampener(xVelo,yVelo,COR*obsDamp)
                    xVelo = x
                    yVelo = y
                }
                if (deltaY.toDouble() != 0.0) {
                    yVelo = -yVelo
                    val (x, y) = collisionDampener(xVelo,yVelo,COR*obsDamp)
                    xVelo = x
                    yVelo = y
                }
            }
        }
        else {
            val xCi = if (obsShape == "circle") obsIV.x + obsIV.width/2 else (if ((ballCenterX - x1).absoluteValue < (ballCenterX - x2).absoluteValue) x1 else x2)
            val yCi = if (obsShape == "circle") obsIV.y + obsIV.height/2 else (if ((ballCenterY - y1).absoluteValue < (ballCenterY - y2).absoluteValue) y1 else y2)
            val deltaX = if (obsShape == "circle") ballCenterX - xCi else (if ((ballCenterX - x1).absoluteValue < (ballCenterX - x2).absoluteValue) ballCenterX - x1 else ballCenterX - x2)
            val deltaY = if (obsShape == "circle") ballCenterY - yCi else (if ((ballCenterY - y1).absoluteValue < (ballCenterY - y2).absoluteValue) ballCenterY - y1 else ballCenterY - y2)

                val obsWidth = if (obsShape == "circle") obsIV.width else 0

            if (deltaX.pow(2) + deltaY.pow(2) < ((ball.width / 2)+(obsWidth / 2)).toDouble().pow(2)) {
                val dx = xVelo.toDouble()
                val dy = yVelo.toDouble()
                val speed: Double = sqrt(dx.pow(2) + dy.pow(2))
                val currentAngle: Double = atan2(dy, dx)

                val reflecAngle: Double = atan2(xCi.toDouble() - ballCenterX, yCi.toDouble() - ballCenterY)
                val newAng: Double = 2*reflecAngle - currentAngle

                xVelo = (speed * cos(newAng)).toFloat()
                yVelo = (speed * sin(newAng)).toFloat()
                val (x, y) = collisionDampener(xVelo,yVelo,COR*obsDamp)
                xVelo = x
                yVelo = y
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


    fun collisionDampener(x: Float, y:Float, dampeningFactor: Double): Pair<Float,Float> {
        val newSpeed = sqrt(x.pow(2) + y.pow(2))*dampeningFactor
        val angle = atan(y.absoluteValue/x.absoluteValue)
        val xnew = if (x > 0) ((newSpeed * cos(angle)).toFloat()) else (-(newSpeed * cos(angle))).toFloat()
        val ynew = if (y > 0) (newSpeed * sin(angle)).toFloat() else (-(newSpeed * sin(angle))).toFloat()
        return Pair(xnew,ynew)
    }

    fun FdragAccel(): Pair<Double, Double> {
        val totalSpeed = sqrt(xVelo.pow(2) + yVelo.pow(2))
        val ballMass = ((4/3)*(3.14)*((diameter/2).toFloat().pow(3)))* ball_density
        val force =  (((0.5) * (medium_density) * (totalSpeed.pow(2))  * 0.1 *  ((3.14* ((diameter/2)/10).toFloat().pow(2)).pow(2))))
        val accel = force/ballMass
        val velAng = atan(-yVelo/xVelo)
        val xDrag = if (xVelo < 0)  (cos(velAng) * accel) else (-1*(cos(velAng) * accel))
        val yDrag = if (yVelo < 0) (sin(velAng) * accel) else (-1*(sin(velAng) * accel))
            return Pair(xDrag,yDrag)
    }

    var count:Int = 0

    fun formatFloat(floatValue: Float): String {
        val decimalFormat = DecimalFormat("00.00")
        return decimalFormat.format(floatValue)
    }

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onSensorChanged(event: SensorEvent?) {



            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                var sides = event.values[0]
                var upDown = event.values[1]






                


                val grav_real = medium_gravity/9.81

                val (x,y) = collisionDampener(sides,upDown,grav_real)
                sides = x
                upDown = y

                xAccel = sides
                yAccel = upDown


                val (xDrag,yDrag) = FdragAccel()
                xVelo += xAccel
                yVelo += yAccel

                if (count == 0) {
                    deets_xVelo.text = formatFloat(xVelo)
                    deets_yVelo.text = formatFloat(yVelo)
                    deets_tVelo.text = formatFloat((sqrt(xVelo.pow(2) + yVelo.pow(2))))

                    deets_xAccel.text = formatFloat(xAccel)
                    deets_yAccel.text = formatFloat(yAccel)
                    deets_tAccel.text = formatFloat((sqrt(xAccel.pow(2) + yAccel.pow(2))))
                    deets_Fdrag.text = formatFloat(yDrag.toFloat())

                    count = 10
                }
                else{
                    count -= 1
                }



                for (obs in obstacles){
                    colDetec(obs)
                }

                if (ball.y > 400 && cameraLock > 0){
                    scrollView.scrollY = ball.y.toInt()-800
                }

                set_but.y = (scrollView.scrollY + 20 ).toFloat()
                cam_but.y = (scrollView.scrollY + 20 ).toFloat()


                ball.apply {

                    // Screen Boundary Collision Logic
                    val rightBounds = (meas.right - ball.width).toFloat()
                    val bottomBounds = (meas.bottom - ball.height).toFloat()

                    if (ball.x > rightBounds && -xVelo > 0 || ball.x < 0 && -xVelo < 0){
                        xVelo = -xVelo
                        val (x, y) = collisionDampener(xVelo,yVelo,COR)
                        xVelo = x
                        yVelo = y
                    }

                    if (ball.y > bottomBounds && yVelo > 0 || ball.y < 0 && yVelo < 0){
                        yVelo = -yVelo
                        val (x, y) = collisionDampener(xVelo,yVelo,COR)
                        xVelo = x
                        yVelo = y
                    }


                    ball.x -= xVelo
                    ball.y += yVelo

                    deets_1.y = (scrollView.scrollY + 20 ).toFloat()
                    deets_2.y = (deets_1.height + deets_1.y ).toFloat()





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


