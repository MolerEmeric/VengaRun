package com.venga.vengarubik.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.venga.vengarubik.R
import kotlin.properties.Delegates

class IntroActivity : AppCompatActivity() {

    private lateinit var soundPool: SoundPool
    private lateinit var musicPlayer: MediaPlayer
    private var introSound by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        soundPool = SoundPool.Builder().setMaxStreams(6)
            .setAudioAttributes(AudioAttributes.Builder().build()).build()
        val btnForward = soundPool.load(this, R.raw.btn_forward, 1)

        musicPlayer = MediaPlayer.create(this, R.raw.start_app)
        musicPlayer.setVolume(0.7f, 0.7f)

        Handler(Looper.getMainLooper()).postDelayed({
            findViewById<ImageView>(R.id.clickableBg).setOnClickListener {
                soundPool.play(btnForward, 1f, 1f, 0, 0, 1f)
                val intent = Intent(this, DifficultyActivity::class.java)
                startActivity(intent);
            }
        }, 7000)


        Handler(Looper.getMainLooper()).postDelayed({
            val appNameFadeInAnimator =
                ObjectAnimator.ofFloat(findViewById<TextView>(R.id.AppNameText), "alpha", 0f, 1f)
            appNameFadeInAnimator.duration = 1000
            appNameFadeInAnimator.start()
        }, 6000)

        Handler(Looper.getMainLooper()).postDelayed({
            val presentingFadeInAnimator =
                ObjectAnimator.ofFloat(findViewById<TextView>(R.id.presentingText), "alpha", 0f, 1f)
            presentingFadeInAnimator.duration = 1000
            presentingFadeInAnimator.start()
        }, 2000)



        Handler(Looper.getMainLooper()).postDelayed({
            val hintFadeInOutAnimator =
                ObjectAnimator.ofFloat(findViewById<TextView>(R.id.hintText), "alpha", 0f, 0.6f)
            hintFadeInOutAnimator.duration = 1000

            hintFadeInOutAnimator.repeatCount = ObjectAnimator.INFINITE
            hintFadeInOutAnimator.repeatMode = ObjectAnimator.REVERSE

            hintFadeInOutAnimator.start()
        }, 7000)

    }

    override fun onStart() {
        super.onStart()
        musicPlayer.start()
    }
}