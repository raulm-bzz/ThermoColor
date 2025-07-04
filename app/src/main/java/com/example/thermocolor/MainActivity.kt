package com.example.thermocolor

import android.graphics.Color
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity(), SensorEventListener  {

    //Sensor variables
    private lateinit var sensorManager: SensorManager
    private var temperatureSensor: Sensor? = null

    //Modules from the site
    private lateinit var temperatureText: TextView
    private lateinit var settingsButton: Button
    private lateinit var rootLayout: LinearLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        //val prefs = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)



        //initialising the modules from the site, now editable etc.
        temperatureText = findViewById(R.id.temperatureText)
        settingsButton = findViewById(R.id.settingsButton)

        //initialising sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //temperature sensor initialising
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        if (temperatureSensor == null) {
            temperatureText.text = "Temperatursensor nicht verfügbar"
        }

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

    }


    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        //changeBackground()
    }

    fun changeBackground(){
        val prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val temperature = prefs.getFloat(SettingsActivity.KEY_TEMP, 1f)
        val hotLimit = prefs.getFloat(SettingsActivity.KEY_HOT, 25f)
        val coldLimit = prefs.getFloat(SettingsActivity.KEY_COLD, 15f)
        val normalized = (temperature - coldLimit) / (hotLimit - coldLimit)

        val temperatureColors = listOf(
            "#440e09", "#440e09", "#4a1c00", "#4b2c00", "#473b00", "#3d4a12", "#2f5830", "#1e634c", "#156c68",
            "#267281", "#427796", "#5f7aa5"
        )
        rootLayout = findViewById(R.id.rootLayout)

        if (temperature <= coldLimit) {
            rootLayout.setBackgroundColor(Color.parseColor(temperatureColors.last()))
            return
        }
        if (temperature >= hotLimit) {
            rootLayout.setBackgroundColor(Color.parseColor(temperatureColors.first()))
            return
        }

        val index = ((1 - normalized) * (12)).toInt()  // 1-normalized inverts to match color order
        rootLayout.setBackgroundColor(Color.parseColor(temperatureColors[index]))

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                val temperature = it.values[0]
                temperatureText.text = "${"%.1f".format(temperature)} °C"
                //save temperature in local
                val sharedPref = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putFloat(SettingsActivity.KEY_TEMP, temperature)
                    apply()
                }
                //onResume()
                changeBackground()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Completion
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //outState.putString("weight_input", weightInput.text.toString())
        //outState.putString("height_input", heightInput.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        //weightInput.setText(savedInstanceState.getString("weight_input", ""))
        //heightInput.setText(savedInstanceState.getString("height_input", ""))
    }
    }
