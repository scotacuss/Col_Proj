package com.example.accel

import android.widget.ImageView
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

class ObseleteStuff
{


//    if (ballCenterY > yCi && ballCenterX > xCi) { // bottom right
//                    offset = 180
//                }
//                else if (ballCenterY > yCi){ //bottom left
//                    offset = 270
//                }
//                else if (ballCenterX > xCi){ // top right
//                    offset = 90
//                }
//
//                val hyp = ballR+obsR
//                val thetaPos = asin((yCi-ballCenterY)/(hyp))
//
//                var oppXvelo = -xVelo
//                var oppYvelo = -yVelo
//
//                val thetaVel = atan(yVelo.absoluteValue/xVelo.absoluteValue)
//
//                val thetaTrue = (thetaPos+thetaVel)/2
//                deets.text = thetaTrue.toString()
//
//                val TotalVelo = (xVelo.absoluteValue + yVelo.absoluteValue)*0.9
//
//
//
//                if (ballCenterX < xCi) {
//                    xVelo = (((TotalVelo)/(tan(thetaTrue)+1))).toFloat()
//                }
//                else if (ballCenterX > xCi) {
//                    xVelo = (-((TotalVelo)/(tan(thetaTrue)+1))).toFloat()
//                }
//                if (ballCenterY < yCi) {
//                    yVelo = (-(TotalVelo - xVelo.absoluteValue)*0.7).toFloat()
//                }
//                else if (ballCenterY > yCi) {
//                    yVelo = ((TotalVelo - xVelo.absoluteValue)*0.7).toFloat()
//                }



//    fun ObeseleteColDetec(obs: ImageView, obsDamp: Double) {
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

}