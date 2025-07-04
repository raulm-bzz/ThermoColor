package com.example.thermocolor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var editColdThreshold: EditText
    private lateinit var editHotThreshold: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    companion object {
        const val PREFS_NAME = "thermoPrefs"
        const val KEY_COLD = "coldLimit"
        const val KEY_HOT = "hotLimit"
        const val KEY_TEMP = "temperature"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_settings)

        editColdThreshold = findViewById(R.id.editColdThreshold)
        editHotThreshold = findViewById(R.id.editHotThreshold)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

        // Lade vorhandene Werte
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cold = prefs.getFloat(KEY_COLD, 15f)
        val hot = prefs.getFloat(KEY_HOT, 25f)

        editColdThreshold.setText(cold.toString())
        editHotThreshold.setText(hot.toString())

        saveButton.setOnClickListener {
            val coldValue = editColdThreshold.text.toString().toFloatOrNull()
            val hotValue = editHotThreshold.text.toString().toFloatOrNull()

            if (coldValue != null && hotValue != null) {
                // Speichern in SharedPreferences
                prefs.edit()
                    .putFloat(KEY_COLD, coldValue)
                    .putFloat(KEY_HOT, hotValue)
                    .apply()

                // Ergebnis an MainActivity zurückgeben (optional)
                val resultIntent = Intent()
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                editColdThreshold.error = "Bitte gültige Werte eingeben"
                editHotThreshold.error = "Bitte gültige Werte eingeben"
            }
        }

        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
