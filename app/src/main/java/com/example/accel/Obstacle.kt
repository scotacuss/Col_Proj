package com.example.accel
import android.content.Context
import  com.example.accel.MainActivity

import android.graphics.Color
import android.text.Layout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout



fun createObs(th:Context ,rl: RelativeLayout, x: Float, y: Float, ht: Int, wd: Int, dampning:Double = 0.8, shape: String = "square"): Array<Any> {
    val obs = ImageView(th)
    if (shape == "circle") {
        obs.setImageResource(R.drawable.circ_obs)
    } else {
        obs.setBackgroundColor(Color.parseColor("#000000"))
    }
    obs.layoutParams = LinearLayout.LayoutParams(wd, ht)
    obs.x = x
    obs.y = y


    rl.addView(obs)
    return arrayOf(obs,dampning,shape)
}



