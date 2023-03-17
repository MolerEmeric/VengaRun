package com.venga.vengarubik.activities

import android.content.Intent
import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.venga.vengarubik.R
import kotlin.properties.Delegates


class DifficultyActivity : AppCompatActivity() {
    private lateinit var soundPool: SoundPool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_difficulty)

        soundPool = SoundPool.Builder().setMaxStreams(6)
            .setAudioAttributes(AudioAttributes.Builder().build()).build()
        val btnForward = soundPool.load(this, R.raw.btn_forward, 1)
        val btnBackward = soundPool.load(this, R.raw.btn_backward, 1)
        val startGame = soundPool.load(this, R.raw.start_game, 1)
        val txtEdit = soundPool.load(this, R.raw.text_edit, 1)


        var enteredNickname = ""
        val validateButton = findViewById<Button>(R.id.startButton)
        val nicknameEdit = findViewById<EditText>(R.id.PseudoEditText)

        nicknameEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                enteredNickname = s.toString()
                soundPool.play(txtEdit, 1f, 1f, 0, 0, 1f)

                if (enteredNickname == "") nicknameEdit.setTypeface(null, Typeface.ITALIC)
                else nicknameEdit.setTypeface(null, Typeface.NORMAL)
                validateButton.isEnabled = enteredNickname != ""
                validateButton.alpha = if (enteredNickname != "") 1f else 0.5f
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        nicknameEdit.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }

        nicknameEdit.setText(intent.getStringExtra("difficulty"))

        val radioGroup = findViewById<RadioGroup>(R.id.difficulties)
        when(intent.getIntExtra("difficulty", 2)){
            3 -> radioGroup.check(R.id.hardDifficulty)
            4 -> radioGroup.check(R.id.extremeDifficulty)
            else -> radioGroup.check(R.id.normalDifficulty)
        }

        findViewById<RadioButton>(R.id.normalDifficulty).setOnClickListener {
            soundPool.play(btnForward, 1f, 1f, 0, 0, 1f)
            nicknameEdit.clearFocus()
        }
        findViewById<RadioButton>(R.id.hardDifficulty).setOnClickListener {
            soundPool.play(btnForward, 1f, 1f, 0, 0, 1f)
            nicknameEdit.clearFocus()
        }
        findViewById<RadioButton>(R.id.extremeDifficulty).setOnClickListener {
            soundPool.play(btnForward, 1f, 1f, 0, 0, 1f)
            nicknameEdit.clearFocus()
        }

        findViewById<Switch>(R.id.rotatoSwitch).isChecked = intent.getBooleanExtra("enable_rotato", false)
        findViewById<Switch>(R.id.lightSwitch).isChecked = intent.getBooleanExtra("enable_light", false)
        findViewById<Switch>(R.id.acceleroSwitch).isChecked = intent.getBooleanExtra("enable_accelero", false)

        findViewById<Switch>(R.id.rotatoSwitch).setOnClickListener{
            soundPool.play(btnForward, 1f, 1f, 0, 0, 1f)
            nicknameEdit.clearFocus()
        }

        findViewById<Switch>(R.id.lightSwitch).setOnClickListener{
            soundPool.play(btnForward, 1f, 1f, 0, 0, 1f)
            nicknameEdit.clearFocus()
        }

        findViewById<Switch>(R.id.acceleroSwitch).setOnClickListener{
            soundPool.play(btnForward, 1f, 1f, 0, 0, 1f)
            nicknameEdit.clearFocus()
        }

        findViewById<ImageView>(R.id.clickableBg2).setOnClickListener{
            nicknameEdit.clearFocus()
        }

        validateButton.setOnClickListener {
            soundPool.play(startGame, 1f, 1f, 0, 0, 1f)
            val enteredDifficulty =
                when (findViewById<RadioGroup>(R.id.difficulties).checkedRadioButtonId) {
                    R.id.hardDifficulty -> 3
                    R.id.extremeDifficulty -> 4
                    else -> 2
                }

            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("difficulty", enteredDifficulty)
            intent.putExtra("nickname", enteredNickname)
            intent.putExtra("enable_rotato", findViewById<Switch>(R.id.rotatoSwitch).isChecked)
            intent.putExtra("enable_light", findViewById<Switch>(R.id.lightSwitch).isChecked)
            intent.putExtra("enable_accelero", findViewById<Switch>(R.id.acceleroSwitch).isChecked)
            startActivity(intent)
        }

        findViewById<Button>(R.id.previousButton).setOnClickListener {
            soundPool.play(btnBackward, 1f, 1f, 0, 0, 1f)
            startActivity(Intent(this, SelectLanguageActivity::class.java))
        }
    }
}