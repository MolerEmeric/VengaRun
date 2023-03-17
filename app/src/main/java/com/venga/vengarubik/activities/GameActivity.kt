package com.venga.vengarubik.activities

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.venga.vengarubik.R
import com.venga.vengarubik.models.Obstacle
import kotlin.math.abs
import kotlin.properties.Delegates

class GameActivity : AppCompatActivity(), SensorEventListener {


    // region Class Fields
    private val musicLoopIds = listOf(
        R.raw.music_loop1,
        R.raw.music_loop2,
        R.raw.music_loop3,
        R.raw.music_loop4,
        R.raw.music_loop5,
        R.raw.music_loop6,
        R.raw.music_loop7,
        R.raw.music_loop8,
        R.raw.music_loop9,
        R.raw.music_loop10,
        R.raw.music_loop11,
        R.raw.music_loop12,
        R.raw.music_loop13
    )

    private lateinit var musicPlayer: MediaPlayer
    private var winSound by Delegates.notNull<Int>()
    private var selectCaseSound by Delegates.notNull<Int>()

    private lateinit var soundPool: SoundPool
    private lateinit var mChronometer: Chronometer

    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor
    private lateinit var acceleroMeter: Sensor
    private lateinit var rotatoMeter: Sensor

    private val shakeTreshold = 1500
    private var mAccelero = floatArrayOf(0f, 0f, 0f)
    private var mLastAccelero = floatArrayOf(0f, 0f, 0f)
    private var lastAcceleroUpdate = 0L

    private val obstacles = mutableListOf<Obstacle>()
    //endregion

    // region Life Cycles
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        mChronometer = findViewById<Chronometer>(R.id.chronometer)

        findViewById<Button>(R.id.score).setOnClickListener{
            win()
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        acceleroMeter = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        rotatoMeter = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        musicPlayer = MediaPlayer.create(this, musicLoopIds.random())
        musicPlayer.setVolume(0.5f, 0.5f)
        musicPlayer.isLooping = true

        soundPool = SoundPool.Builder().setMaxStreams(6)
            .setAudioAttributes(AudioAttributes.Builder().build()).build()

        selectCaseSound = soundPool.load(this, R.raw.select_case, 1)

        mChronometer.start()
    }

    override fun onStart() {
        super.onStart()
        musicPlayer.start()

        loop()
    }

    private val refreshDelay = 100
    private fun loop() {
        //x² de jeu

        Handler(Looper.getMainLooper()).postDelayed({
            loop()
        }, refreshDelay.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayer.release()
        soundPool.release()
    }

    override fun onPause() {
        super.onPause()
        musicPlayer.pause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        musicPlayer.start()
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, acceleroMeter, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, rotatoMeter, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis() //SAUTER PLUS HAUT OU PLUS BAS
            println("TESTTTT")
            mAccelero = event.values
            if (curTime - lastAcceleroUpdate > 100) {
                val diffTime: Long = curTime - lastAcceleroUpdate
                lastAcceleroUpdate = curTime

                val speed: Float =
                    abs(mAccelero[0] + mAccelero[1] + mAccelero[2] - mLastAccelero[0] - mLastAccelero[1] - mLastAccelero[2]) / diffTime * 10000
                if (speed > shakeTreshold) {
                    //TODO
                }
                mLastAccelero = mAccelero.clone()
            }

            return
        }

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            val orientationAngles = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientationAngles) //REGARDER QUEL ANGLE UTIISER

            var angle = orientationAngles[2] + 0.22f
            if (angle < 0f) angle = 0f
            if (angle > 0.44f) angle = 0.44f
            angle *= (1f / 0.44f)

            return
        }


        if (event.sensor.type == Sensor.TYPE_LIGHT) {
            val light_value =
                if (event.values[0] < 100f) 0f else if (event.values[0] > 500f) 500f else event.values[0]

            return
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
    //endregion

    // region Utilitaries
    private fun win(){
        mChronometer.stop()

        val timeLong = (SystemClock.elapsedRealtime() - mChronometer.base)
        val time = (timeLong) / 1000
        val intent = Intent(this, ScoreActivity::class.java)
        startActivity(intent)
    }
    // endregion

}