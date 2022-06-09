package com.kimjinwouk.weather

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView

class SplashActivity : BaseActivity() {

    private val DURATION : Long = 500
    private val imageview_icon : ImageView by lazy {
        findViewById<ImageView>(R.id.imageview_icon)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)




        val anim1 = ObjectAnimator.ofFloat(imageview_icon, View.ROTATION,0f,-45f)
        val anim2 = ObjectAnimator.ofFloat(imageview_icon, View.ROTATION,-45f,45f)
        val anim3 = ObjectAnimator.ofFloat(imageview_icon, View.ROTATION,45f,0f)

        anim1.duration = 500
        anim1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {

            }
            override fun onAnimationEnd(animation: Animator?) {
                AnimatorSet().apply{
                    play(anim2)
                    duration = 500
                    start()
                }
            }
        })
        anim1.start()

        anim2.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {

            }
            override fun onAnimationEnd(animation: Animator?) {
                AnimatorSet().apply{
                    play(anim3)
                    duration = 500
                    start()
                }
            }
        })


        anim3.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {

            }
            override fun onAnimationEnd(animation: Animator?) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }
        })
    }
}