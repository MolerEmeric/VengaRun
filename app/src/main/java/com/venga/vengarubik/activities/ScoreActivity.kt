package com.venga.vengarubik.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.venga.vengarubik.R
import com.venga.vengarubik.models.FirebaseDB
import kotlin.math.min

class ScoreActivity : AppCompatActivity() {
    private lateinit var soundPool: SoundPool

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        soundPool = SoundPool.Builder().setMaxStreams(6)
            .setAudioAttributes(AudioAttributes.Builder().build()).build()

        val btnBackward = soundPool.load(this, R.raw.btn_backward, 1)
        val startGame = soundPool.load(this, R.raw.start_game, 1)

        val time = intent.getLongExtra("time", 2)
        val difficulty = intent.getIntExtra("difficulty", 2)
        val nickname = intent.getStringExtra("nickname")
        findViewById<TextView>(R.id.titleText).text =  "${difficulty}x${difficulty} ${getString(R.string.your_score)} : $time"
        findViewById<TextView>(R.id.scoresText).text =  "${getString(R.string.top_scores)}"
        FirebaseDB.getTopScores(nickname!!, difficulty) { sortedScores ->
            val timeScoreIdMap = mapOf(
                1 to R.id.timeScore1,
                2 to R.id.timeScore2,
                3 to R.id.timeScore3,
                4 to R.id.timeScore4,
                5 to R.id.timeScore5,
                6 to R.id.timeScore6,
                7 to R.id.timeScore7,
                8 to R.id.timeScore8,
                9 to R.id.timeScore9,
                10 to R.id.timeScore10
            )

            for (i in 0 until min(10, sortedScores.size)) {
                findViewById<TextView>(timeScoreIdMap[i + 1]!!).text = "${sortedScores[i]}"
                findViewById<TextView>(timeScoreIdMap[i + 1]!!).text = "${sortedScores[i]}"
            }
            if (sortedScores.size > 0 && time > sortedScores[0]) {
                findViewById<TextView>(R.id.feedbackText).text =  "${getString(R.string.worse_feedback)}"
            }
            else if (sortedScores.size > 0 && time < sortedScores[0]) {
                findViewById<TextView>(R.id.feedbackText).text =  "${getString(R.string.better_feedback)}"
            }
        }

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork

        if (network == null) {
            Toast.makeText(this, getString(R.string.inaccessible_database), Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.menuButton).setOnClickListener{
            soundPool.play(btnBackward, 1f, 1f, 0, 0, 1f)
            startActivity(Intent(this, DifficultyActivity::class.java))
        }

        findViewById<Button>(R.id.restartButton).setOnClickListener{
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("difficulty", difficulty)
            intent.putExtra("nickname", nickname)
            startActivity(intent)
            soundPool.play(startGame, 1f, 1f, 0, 0, 1f)
        }
   }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}