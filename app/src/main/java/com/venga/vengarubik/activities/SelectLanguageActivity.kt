package com.venga.vengarubik.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.LocaleList
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.venga.vengarubik.R
import java.util.*


class SelectLanguageActivity() : AppCompatActivity() {
    var currentLanguage: String = Locale.getDefault().language
    private lateinit var soundPool: SoundPool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        loadLocale()
        setContentView(R.layout.activity_select_language)

        soundPool = SoundPool.Builder().setMaxStreams(6)
            .setAudioAttributes(AudioAttributes.Builder().build()).build()
        val btnForward = soundPool.load(this, R.raw.btn_forward, 1)

        val validateButton = findViewById<Button>(R.id.validateButton)
        val changeLangButton = findViewById<Button>(R.id.changeLang)
        val flagView = findViewById<ImageView>(R.id.imageView3)


        when (currentLanguage) {
            "en" -> flagView.setImageResource(R.drawable.us_flag)
            "fr" -> flagView.setImageResource(R.drawable.fr_flag)
            "de" -> flagView.setImageResource(R.drawable.de_flag)
            "it" -> flagView.setImageResource(R.drawable.it_flag)
        }

        validateButton.setOnClickListener {
            soundPool.play(btnForward, 1f,1f,0, 0,1f)
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent);
        }

        changeLangButton.setOnClickListener {
            soundPool.play(btnForward, 1f,1f,0, 0,1f)
            val languages = arrayOf("English", "Français", "Deutsch", "Italiano")
            val langSelectorBuilder = AlertDialog.Builder(this)
            langSelectorBuilder.setTitle(R.string.choose_lang)

            val selected = when(currentLanguage){
                "en" -> 0
                "fr" -> 1
                "de" -> 2
                else -> 3
            }
            langSelectorBuilder.setSingleChoiceItems(languages, selected) { dialog, selection ->
                soundPool.play(btnForward, 1f,1f,0, 0,1f)
                when (selection) {
                    0 -> setLocale("en")
                    1 -> setLocale("fr")
                    2 -> setLocale("de")
                    3 -> setLocale("it")
                }
                recreate()
                dialog.dismiss()
            }
            langSelectorBuilder.create().show()
        }
    }

    private fun setLocale(localeToSet: String) {
        currentLanguage = localeToSet
        val localeListToSet = LocaleList(Locale(localeToSet))
        LocaleList.setDefault(localeListToSet)
        resources.configuration.setLocales(localeListToSet)
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)
        val sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        sharedPref.putString("locale_to_set", localeToSet)
        sharedPref.apply()
    }

    private fun loadLocale() {
        val sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val localeToSet: String = sharedPref.getString("locale_to_set", "")!!
        setLocale(localeToSet)
    }
}