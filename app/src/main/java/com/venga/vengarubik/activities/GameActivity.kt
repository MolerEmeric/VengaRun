package com.venga.vengarubik.activities

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
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
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.constraintlayout.widget.ConstraintLayout
import com.venga.vengarubik.Color
import com.venga.vengarubik.Direction
import com.venga.vengarubik.Face
import com.venga.vengarubik.R
import com.venga.vengarubik.models.FirebaseDB
import com.venga.vengarubik.models.User
import com.venga.vengarubik.models.Cube
import com.venga.vengarubik.models.ExtremeCube
import com.venga.vengarubik.models.HardCube
import com.venga.vengarubik.models.NormalCube
import java.util.*
import kotlin.math.abs
import kotlin.properties.Delegates
import androidx.constraintlayout.widget.ConstraintSet

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

    private val rawFlipSoundIds = listOf(
        R.raw.flip1,
        R.raw.flip2,
        R.raw.flip3,
        R.raw.flip4,
        R.raw.flip5,
        R.raw.flip6,
        R.raw.flip7,
        R.raw.flip8,
        R.raw.flip9,
        R.raw.flip10,
        R.raw.flip11,
        R.raw.flip12,
        R.raw.flip13,
        R.raw.flip14,
        R.raw.flip15
    )
    private val flipSoundIds = mutableListOf<Int>()
    private var winSound by Delegates.notNull<Int>()
    private var selectCaseSound by Delegates.notNull<Int>()
    private lateinit var soundPool: SoundPool

    private var globalSaturation = 1f

    private var cube: Cube = NormalCube()

    private var cubeCaseImgs = mutableListOf<MutableList<ImageFilterView>>()
    private var selectedCaseImg: ImageFilterView? = null
    private var selectedCaseImgAnimator: ObjectAnimator? = null
    private var selectedFace: Face? = null
    private var selectedCaseValue: Int? = null

    private lateinit var selectTileHint: TextView
    private lateinit var topController: LinearLayout
    private lateinit var frontRightController: LinearLayout
    private lateinit var mChronometer: Chronometer

    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor
    private lateinit var acceleroMeter: Sensor
    private lateinit var rotatoMeter: Sensor

    private var enableLight by Delegates.notNull<Boolean>()
    private var enableRotato by Delegates.notNull<Boolean>()
    private var enableAccelero by Delegates.notNull<Boolean>()
    private var difficulty by Delegates.notNull<Int>()
    private lateinit var nickname: String

    private val shakeTreshold = 1500
    private var mAccelero = floatArrayOf(0f, 0f, 0f)
    private var mLastAccelero = floatArrayOf(0f, 0f, 0f)
    private var lastAcceleroUpdate = 0L

    private var isWin = false
    //endregion

    // region Life Cycles
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        nickname = intent.getStringExtra("nickname").toString()
        enableLight = intent.getBooleanExtra("enable_light", false)
        enableRotato = intent.getBooleanExtra("enable_rotato", false)
        enableAccelero = intent.getBooleanExtra("enable_accelero", false)

        mChronometer = findViewById<Chronometer>(R.id.chronometer)
        val solveButtonView = findViewById<Button>(R.id.solveButton)

        if (nickname.contains("ADMIN")) solveButtonView.visibility = View.VISIBLE

        topController = findViewById(R.id.TopController)
        frontRightController = findViewById(R.id.FrontRightController)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        acceleroMeter = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        rotatoMeter = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        selectTileHint = findViewById<TextView>(R.id.selectTileHint)
        val hintFadeInOutAnimator = ObjectAnimator.ofFloat(selectTileHint, "alpha", 0f, 0.6f)
        hintFadeInOutAnimator.duration = 1000

        hintFadeInOutAnimator.repeatCount = ObjectAnimator.INFINITE
        hintFadeInOutAnimator.repeatMode = ObjectAnimator.REVERSE

        hintFadeInOutAnimator.start()

        findViewById<ImageButton>(R.id.ctrlDown1).setOnClickListener { flip(Direction.DOWN) }
        findViewById<ImageButton>(R.id.ctrlDown2).setOnClickListener { flip(Direction.DOWN) }
        findViewById<ImageButton>(R.id.ctrlUp1).setOnClickListener { flip(Direction.UP) }
        findViewById<ImageButton>(R.id.ctrlUp2).setOnClickListener { flip(Direction.UP) }
        findViewById<ImageButton>(R.id.ctrlLeft1).setOnClickListener { flip(Direction.LEFT) }
        findViewById<ImageButton>(R.id.ctrlLeft2).setOnClickListener { flip(Direction.LEFT) }
        findViewById<ImageButton>(R.id.ctrlRight1).setOnClickListener { flip(Direction.RIGHT) }
        findViewById<ImageButton>(R.id.ctrlRight2).setOnClickListener { flip(Direction.RIGHT) }

        findViewById<ConstraintLayout>(R.id.NormalCubeLayout).visibility = View.GONE
        findViewById<ConstraintLayout>(R.id.HardCubeLayout).visibility = View.GONE
        findViewById<ConstraintLayout>(R.id.ExtremeCubeLayout).visibility = View.GONE
        difficulty= intent.getIntExtra("difficulty", 2)
        when (difficulty) {
            2 -> {
                findViewById<ConstraintLayout>(R.id.NormalCubeLayout).visibility = View.VISIBLE
                cube = NormalCube()
                cubeCaseImgs = mutableListOf(
                    mutableListOf(
                        findViewById(R.id.N_T0), findViewById(R.id.N_T1), findViewById(R.id.N_T2),
                        findViewById(R.id.N_T3)
                    ), mutableListOf(), mutableListOf(
                        findViewById(R.id.N_F0), findViewById(R.id.N_F1), findViewById(R.id.N_F2),
                        findViewById(R.id.N_F3)
                    ), mutableListOf(
                        findViewById(R.id.N_R0), findViewById(R.id.N_R1), findViewById(R.id.N_R2),
                        findViewById(R.id.N_R3)
                    ), mutableListOf(), mutableListOf()
                )
            }
            3 -> {
                findViewById<ConstraintLayout>(R.id.HardCubeLayout).visibility = View.VISIBLE
                cube = HardCube()
                cubeCaseImgs = mutableListOf(
                    mutableListOf(
                        findViewById(R.id.H_T0), findViewById(R.id.H_T1), findViewById(R.id.H_T2),
                        findViewById(R.id.H_T3), findViewById(R.id.H_T4), findViewById(R.id.H_T5),
                        findViewById(R.id.H_T6), findViewById(R.id.H_T7), findViewById(R.id.H_T8),
                    ), mutableListOf(), mutableListOf(
                        findViewById(R.id.H_F0), findViewById(R.id.H_F1), findViewById(R.id.H_F2),
                        findViewById(R.id.H_F3), findViewById(R.id.H_F4), findViewById(R.id.H_F5),
                        findViewById(R.id.H_F6), findViewById(R.id.H_F7), findViewById(R.id.H_F8),
                    ),
                    mutableListOf(
                        findViewById(R.id.H_R0), findViewById(R.id.H_R1), findViewById(R.id.H_R2),
                        findViewById(R.id.H_R3), findViewById(R.id.H_R4), findViewById(R.id.H_R5),
                        findViewById(R.id.H_R6), findViewById(R.id.H_R7), findViewById(R.id.H_R8),
                    ),
                    mutableListOf(),
                    mutableListOf()
                )
            }
            4 -> {
                findViewById<ConstraintLayout>(R.id.ExtremeCubeLayout).visibility = View.VISIBLE
                cube = ExtremeCube()
                cubeCaseImgs = mutableListOf(
                    mutableListOf(
                        findViewById(R.id.E_T0),
                        findViewById(R.id.E_T1),
                        findViewById(R.id.E_T2),
                        findViewById(R.id.E_T3),
                        findViewById(R.id.E_T4),
                        findViewById(R.id.E_T5),
                        findViewById(R.id.E_T6),
                        findViewById(R.id.E_T7),
                        findViewById(R.id.E_T8),
                        findViewById(R.id.E_T9),
                        findViewById(R.id.E_T10),
                        findViewById(R.id.E_T11),
                        findViewById(R.id.E_T12),
                        findViewById(R.id.E_T13),
                        findViewById(R.id.E_T14),
                        findViewById(R.id.E_T15)
                    ), mutableListOf(), mutableListOf(
                        findViewById(R.id.E_F0),
                        findViewById(R.id.E_F1),
                        findViewById(R.id.E_F2),
                        findViewById(R.id.E_F3),
                        findViewById(R.id.E_F4),
                        findViewById(R.id.E_F5),
                        findViewById(R.id.E_F6),
                        findViewById(R.id.E_F7),
                        findViewById(R.id.E_F8),
                        findViewById(R.id.E_F9),
                        findViewById(R.id.E_F10),
                        findViewById(R.id.E_F11),
                        findViewById(R.id.E_F12),
                        findViewById(R.id.E_F13),
                        findViewById(R.id.E_F14),
                        findViewById(R.id.E_F15)
                    ),
                    mutableListOf(
                        findViewById(R.id.E_R0),
                        findViewById(R.id.E_R1),
                        findViewById(R.id.E_R2),
                        findViewById(R.id.E_R3),
                        findViewById(R.id.E_R4),
                        findViewById(R.id.E_R5),
                        findViewById(R.id.E_R6),
                        findViewById(R.id.E_R7),
                        findViewById(R.id.E_R8),
                        findViewById(R.id.E_R9),
                        findViewById(R.id.E_R10),
                        findViewById(R.id.E_R11),
                        findViewById(R.id.E_R12),
                        findViewById(R.id.E_R13),
                        findViewById(R.id.E_R14),
                        findViewById(R.id.E_R15)
                    ),
                    mutableListOf(),
                    mutableListOf()
                )
            }
        }

        updateCubeColors()

        musicPlayer = MediaPlayer.create(this, musicLoopIds.random())
        musicPlayer.setVolume(0.5f, 0.5f)
        musicPlayer.isLooping = true

        soundPool = SoundPool.Builder().setMaxStreams(6)
            .setAudioAttributes(AudioAttributes.Builder().build()).build()
        winSound = soundPool.load(this, R.raw.win, 1)
        selectCaseSound = soundPool.load(this, R.raw.select_case, 1)
        for (flipId in rawFlipSoundIds) flipSoundIds.add(soundPool.load(this, flipId, 1))

        mChronometer.start()

        solveButtonView.setOnClickListener {
            cube.solve()
            win()
        }
    }

    override fun onStart() {
        super.onStart()
        musicPlayer.start()
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
        if (isWin) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis()

            mAccelero = event.values
            if (enableAccelero && curTime - lastAcceleroUpdate > 100) {
                val diffTime: Long = curTime - lastAcceleroUpdate
                lastAcceleroUpdate = curTime

                val speed: Float =
                    abs(mAccelero[0] + mAccelero[1] + mAccelero[2] - mLastAccelero[0] - mLastAccelero[1] - mLastAccelero[2]) / diffTime * 10000
                if (speed > shakeTreshold && !cube.isReset) {
                    cube.reset()
                    playResetSoundEffect()
                    updateCubeColors()
                }
                mLastAccelero = mAccelero.clone()
            }

            return
        }

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR && enableRotato) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            val orientationAngles = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            var angle = orientationAngles[2] + 0.22f
            if (angle < 0f) angle = 0f
            if (angle > 0.44f) angle = 0.44f
            angle *= (1f / 0.44f)

            val cl = findViewById<View>(R.id.gameLayout) as ConstraintLayout
            val cs = ConstraintSet()
            cs.clone(cl)
            cs.setHorizontalBias(R.id.TopController, angle)
            cs.setHorizontalBias(R.id.FrontRightController, angle)
            cs.applyTo(cl)
            return
        }


        if (event.sensor.type == Sensor.TYPE_LIGHT && enableLight) {
            val light_value =
                if (event.values[0] < 100f) 0f else if (event.values[0] > 500f) 500f else event.values[0]

            globalSaturation = light_value / 500f
            updateCubeColors()
            return
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
    //endregion

    // region Utilitaries
    private fun updateCubeColors() {
        cube.actualCube[Face.FRONT.value].forEachIndexed { caseNum, caseVal ->
            setCaseColor(Face.FRONT, caseNum, Color from caseVal, globalSaturation)
        }
        cube.actualCube[Face.TOP.value].forEachIndexed { caseNum, caseVal ->
            setCaseColor(Face.TOP, caseNum, Color from caseVal, globalSaturation)
        }
        cube.actualCube[Face.RIGHT.value].forEachIndexed { caseNum, caseVal ->
            setCaseColor(Face.RIGHT, caseNum, Color from caseVal, globalSaturation)
        }
    }

    private fun setCaseColor(face: Face, case: Int, color: Color, saturation: Float) {
        setImageColor(cubeCaseImgs[face.value][case], color, saturation)
    }

    private fun setImageColor(img: ImageFilterView, color: Color, saturation: Float) {
        val isColorWhite = (Color from color.value == Color.WHITE)
        val colorARGB =
            if (isColorWhite) android.graphics.Color.HSVToColor(255, floatArrayOf(0f, 0f, 1f))
            else android.graphics.Color.HSVToColor(
                255,
                floatArrayOf(color.hue, globalSaturation, color.brightness)
            )

        img.setColorFilter(colorARGB)
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun imgSelect(view: View) {
        if (isWin) return

        soundPool.play(selectCaseSound, 1f, 1f, 0, 0, 1f)
        val clickViewFullname = resources.getResourceName(view.id)
        val isTwoDigitId = (clickViewFullname.subSequence(
            clickViewFullname.length - 3,
            clickViewFullname.length - 2
        )).toString() != "_"

        val imgFaceStr = clickViewFullname.subSequence(
            clickViewFullname.length - (if (isTwoDigitId) 3 else 2),
            clickViewFullname.length - if (isTwoDigitId) 2 else 1
        ).toString()

        selectedFace = Face.FRONT
        when (imgFaceStr) {
            "R" -> selectedFace = Face.RIGHT
            "T" -> selectedFace = Face.TOP
        }

        selectedCaseValue = (clickViewFullname.subSequence(
            clickViewFullname.length - (if (isTwoDigitId) 2 else 1),
            clickViewFullname.length
        )).toString().toInt()

        topController.visibility = View.INVISIBLE
        frontRightController.visibility = View.INVISIBLE
        selectTileHint.visibility = View.GONE
        when (selectedFace) {
            Face.FRONT, Face.RIGHT -> frontRightController.visibility = View.VISIBLE
            else -> topController.visibility = View.VISIBLE
        }

        if (selectedCaseImgAnimator != null && selectedCaseImg != null) {
            ObjectAnimator.ofPropertyValuesHolder(
                selectedCaseImg,
                PropertyValuesHolder.ofFloat("scaleX", 1f),
                PropertyValuesHolder.ofFloat("scaleY", 1f)
            )
            selectedCaseImgAnimator!!.duration = 150
            selectedCaseImgAnimator!!.repeatCount = 1
            selectedCaseImgAnimator!!.start()
        }

        selectedCaseImg = cubeCaseImgs[selectedFace!!.value][selectedCaseValue!!]

        selectedCaseImgAnimator = ObjectAnimator.ofPropertyValuesHolder(
            selectedCaseImg,
            PropertyValuesHolder.ofFloat("scaleX", 0.8f),
            PropertyValuesHolder.ofFloat("scaleY", 0.8f)
        )
        selectedCaseImgAnimator!!.duration = 300

        selectedCaseImgAnimator!!.repeatCount = ObjectAnimator.INFINITE
        selectedCaseImgAnimator!!.repeatMode = ObjectAnimator.REVERSE

        selectedCaseImgAnimator!!.start()
    }

    fun flip(dir: Direction) {
        playRdmFlickSound()
        cube.flip(selectedFace!!, selectedCaseValue!!, dir)
        updateCubeColors()
        if (cube.isSolved) win()
    }

    private fun playRdmFlickSound() {
        soundPool.play(flipSoundIds.random(), 1f, 1f, 0, 0, 1f)
    }

    private fun playResetSoundEffect() {
        Handler(Looper.getMainLooper()).postDelayed({
            soundPool.play(flipSoundIds.random(), 1f, 1f, 0, 0, 1f)
        }, 0)
        Handler(Looper.getMainLooper()).postDelayed({
            soundPool.play(flipSoundIds.random(), 1f, 1f, 0, 0, 1f)
        }, 25)
        Handler(Looper.getMainLooper()).postDelayed({
            soundPool.play(flipSoundIds.random(), 1f, 1f, 0, 0, 1f)
        }, 50)
        Handler(Looper.getMainLooper()).postDelayed({
            soundPool.play(flipSoundIds.random(), 1f, 1f, 0, 0, 1f)
        }, 75)
        Handler(Looper.getMainLooper()).postDelayed({
            soundPool.play(flipSoundIds.random(), 1f, 1f, 0, 0, 1f)
        }, 100)
    }

    private fun win(){
        mChronometer.stop()
        isWin = true
        soundPool.play(winSound, 1f, 1f, 0, 0, 1f)
        globalSaturation = 1f
        updateCubeColors()
        findViewById<TextView>(R.id.winText).visibility = View.VISIBLE
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        frontRightController.visibility = View.INVISIBLE
        topController.visibility = View.INVISIBLE

        soundPool.play(winSound, 1f, 1f, 0, 0, 1f)

        val timeLong = (SystemClock.elapsedRealtime() - mChronometer.base)
        val time = (timeLong) / 1000
        val intent = Intent(this, ScoreActivity::class.java)
        intent.putExtra("difficulty", difficulty)
        intent.putExtra("nickname", nickname)
        intent.putExtra("time", time)
        intent.putExtra("enable_rotato", enableRotato)
        intent.putExtra("enable_light", enableLight)
        intent.putExtra("enable_accelero", enableAccelero)
        val user = User(nickname, difficulty, time)
        FirebaseDB.addUserWithScore(user)
        startActivity(intent)
    }
    // endregion

}