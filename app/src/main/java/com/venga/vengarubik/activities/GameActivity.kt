package com.venga.vengarubik.activities

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
<<<<<<< HEAD
import android.view.View
=======
import android.util.Log
>>>>>>> 71e88a4490327b05e3f7285dbca20733d1c7288c
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.venga.vengarubik.R
<<<<<<< HEAD
import com.venga.vengarubik.models.*
=======
import com.venga.vengarubik.models.Obstacle
import com.venga.vengarubik.models.ScoreManager
>>>>>>> 71e88a4490327b05e3f7285dbca20733d1c7288c
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

    private val shakeTreshold = 1000
    private var jumpHigh: Boolean = false
    private var jumpLow: Boolean = false
    private var evilFairies: Boolean = false
    private var mAccelero = floatArrayOf(0f, 0f, 0f)
    private var mLastAccelero = floatArrayOf(0f, 0f, 0f)
    private var lastAcceleroUpdate = 0L

    private val obstacles = mutableListOf<Obstacle>()

    private val isDead = false
    //endregion

    private var estAccroupi = false

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

        obstacles.add(FloorSpike(R.id.floorSpike1, 1f))
        obstacles.add(FloorSpike(R.id.floorSpike2, 1.2f))
        obstacles.add(RoofSpike(R.id.roofSpike1, 1.4f))
        obstacles.add(Fairy(R.id.fairy1, 1.6f))
        obstacles.add(FloorSpike(R.id.floorSpike3, 1.8f))
        obstacles.add(Breakable(R.id.breakable1, 1.9f))
        obstacles.add(Breakable(R.id.breakable2, 2f))
        obstacles.add(Fairy(R.id.fairy2, 2.3f))
        obstacles.add(Fairy(R.id.fairy3, 2.4f))
        obstacles.add(RoofSpike(R.id.roofSpike2, 2.6f))
        obstacles.add(FloorSpike(R.id.floorSpike4, 2.8f))
        obstacles.add(RoofSpike(R.id.roofSpike3, 3f))
        obstacles.add(FloorSpike(R.id.floorSpike5, 3.2f))
        obstacles.add(FloorSpike(R.id.floorSpike6, 3.4f))
        obstacles.add(Breakable(R.id.breakable3, 3.5f))
        obstacles.add(Breakable(R.id.breakable4, 3.6f))

        mChronometer.start()
    }

    override fun onStart() {
        super.onStart()
        musicPlayer.start()

        loop()
    }

    private val refreshDelay = 100
    private fun loop() {
        if (isDead) return
        updateJumpBools()
<<<<<<< HEAD

        //Boucle de jeu
        updateObstacle()

=======
        updateAccroupi()
        Log.d("info", estAccroupi.toString())
>>>>>>> 71e88a4490327b05e3f7285dbca20733d1c7288c
        Handler(Looper.getMainLooper()).postDelayed({
            loop()
        }, refreshDelay.toLong())
    }

    private var crochFrames = 0
    private fun updateAccroupi(){
       crochFrames += 1
        if (crochFrames == 15){
            estAccroupi = false
            crochFrames = 0
        }
    }

    private var jumpFrames = 0
    private fun updateJumpBools() {  // 20 frames
        jumpFrames += 1
        if (jumpFrames == 20) {
            jumpLow = false
            jumpHigh = false
            jumpFrames = 0
        }
    }

    private fun updateObstacle(){
        for(obs in obstacles){
            obs.pos -= 0.01f

            if(obs is Breakable){
                if (obs.pos < 0.25f) win()
            }

            val cl = findViewById<View>(R.id.runner) as ConstraintLayout
            val cs = ConstraintSet()

            cs.clone(cl)
            cs.setHorizontalBias(obs.imgId, obs.pos)
            cs.applyTo(cl)
        }
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
            mAccelero = event.values
            if (curTime - lastAcceleroUpdate > 100) {
                val diffTime: Long = curTime - lastAcceleroUpdate
                lastAcceleroUpdate = curTime

                val speed: Float =
                    abs(mAccelero[0] + mAccelero[1] + mAccelero[2] - mLastAccelero[0] - mLastAccelero[1] - mLastAccelero[2]) / diffTime * 10000
                if (speed > shakeTreshold) {
                    //TODO
                    if (speed < 2000) {
                        jumpLow = true
                    }
                    else {
                        jumpHigh = true
                    }
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

            //Log.d("info", "0 : " + orientationAngles[0] + " 1 : " + orientationAngles[1] + " 2 : " + orientationAngles[2])

            var angle = orientationAngles[2] + 0.45f
            if (angle < 0) angle = 0f
            if (angle > 0.45) angle = 0.45f
            angle *= (1f / 0.45f)

            Log.d("info","Angle" + angle)

            if (angle > 0.03){
                estAccroupi = true
            }


            return
        }


        if (event.sensor.type == Sensor.TYPE_LIGHT) {
            evilFairies = event.values[0] >= 100f
            return
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
    //endregion

    // region Utilitaries
    private fun win(){
        mChronometer.stop()
        val timeLong = (SystemClock.elapsedRealtime() - mChronometer.base)
        val time = ((timeLong) / 1000).toInt()
        ScoreManager.addScore(time)
        val intent = Intent(this, ScoreActivity::class.java)
        intent.putExtra("actualScore", time)
        startActivity(intent)
    }
    // endregion

}