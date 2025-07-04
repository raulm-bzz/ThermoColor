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
        val prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)

        //Initialising the modules from the site, now editable etc.
        temperatureText = findViewById(R.id.temperatureText)
        settingsButton = findViewById(R.id.settingsButton)

        //Initialising Sensor Manager and Temperature Sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        if (temperatureSensor == null) {
            temperatureText.text = "Temperatursensor nicht verfügbar"
        }

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("givenTemp", prefs.getFloat(SettingsActivity.KEY_TEMP, 20f))            //Send Current Temperature with intent
            startActivity(intent)
        }

    }


    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        //changeBackground()
    }

    fun changeBackground(){
        //Background element
        rootLayout = findViewById(R.id.rootLayout)

        //Get prefs from local
        val prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val temperature = prefs.getFloat(SettingsActivity.KEY_TEMP, 1f)
        val hotLimit = prefs.getFloat(SettingsActivity.KEY_HOT, 25f)
        val coldLimit = prefs.getFloat(SettingsActivity.KEY_COLD, 15f)

        //Logic to determine which of the 12 Colors is appropriate
        val normalized = (temperature - coldLimit) / (hotLimit - coldLimit)

        //12 Different Backgroundcolors resembling temperature
        val temperatureColors = listOf(
            "#440e09", "#440e09", "#4a1c00", "#4b2c00", "#473b00", "#3d4a12", "#2f5830", "#1e634c",
            "#156c68", "#267281", "#427796", "#5f7aa5"
        )


        if (temperature <= coldLimit) {
            rootLayout.setBackgroundColor(Color.parseColor(temperatureColors.last()))
            return
        }
        if (temperature >= hotLimit) {
            rootLayout.setBackgroundColor(Color.parseColor(temperatureColors.first()))
            return
        }
        else {
            val index = ((1 - normalized) * (12)).toInt()  //1 minus normalised inverts to match color order
            rootLayout.setBackgroundColor(Color.parseColor(temperatureColors[index]))
        }


    }

    //While on SettingsActivity or exited
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            //Select the correct Sensor
            if (it.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                val temperature = it.values[0]
                temperatureText.text = "${"%.1f".format(temperature)} °C"

                //save temperature in local
                val sharedPref = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putFloat(SettingsActivity.KEY_TEMP, temperature)
                    apply()
                }

                //Execute Background change
                changeBackground()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Completion
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }
    }
