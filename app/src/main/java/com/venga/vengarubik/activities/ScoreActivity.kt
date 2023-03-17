package com.venga.vengarubik.activities

import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.venga.vengarubik.R
import com.venga.vengarubik.models.ScoreManager
import kotlin.math.min

class ScoreActivity : AppCompatActivity() {
    private lateinit var soundPool: SoundPool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        soundPool = SoundPool.Builder().setMaxStreams(6)
            .setAudioAttributes(AudioAttributes.Builder().build()).build()

        val btnBackward = soundPool.load(this, R.raw.btn_backward, 1)
        val startGame = soundPool.load(this, R.raw.start_game, 1)


        val scores = ScoreManager().getInstance()?.getScore();
        val timeScoreIdMap = mapOf(
            1 to R.id.timeScore1,
            2 to R.id.timeScore2,
            3 to R.id.timeScore3,
            4 to R.id.timeScore4,
            5 to R.id.timeScore5
        )

        /*for (i in 0 until 5) {
            findViewById<TextView>(timeScoreIdMap[i + 1]!!).text = "${scores?.get(i)}"
            findViewById<TextView>(timeScoreIdMap[i + 1]!!).text = "${scores?.get(i)}"
        }*/

        findViewById<Button>(R.id.menuButton).setOnClickListener{
            soundPool.play(btnBackward, 1f, 1f, 0, 0, 1f)
            startActivity(Intent(this, IntroActivity::class.java))
        }

        findViewById<Button>(R.id.restartButton).setOnClickListener{
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            soundPool.play(startGame, 1f, 1f, 0, 0, 1f)
        }

   }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}