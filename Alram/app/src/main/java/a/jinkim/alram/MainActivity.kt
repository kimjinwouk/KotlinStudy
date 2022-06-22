package a.jinkim.alram

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        bindViews()

        initOnOffButton()
        initChangeAlramTimeButton()

        val model = fetchDataFromSharedPreference()
        renderView(model)

    }

    private fun renderView(model: AlramDisplayModel) {
        findViewById<TextView>(R.id.ampmTextView).apply {
            text = model.ampmText
        }

        findViewById<TextView>(R.id.timeTextView).apply {
            text = model.timeText
        }

        findViewById<Button>(R.id.onOffButton).apply {
            text = model.onOffText
            tag = model
        }

    }

    private fun fetchDataFromSharedPreference(): AlramDisplayModel {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENECE_NAME, Context.MODE_PRIVATE)
        val timeDBValue = sharedPreferences.getString(ALRAM_KEY, "9:30") ?: "9:30"
        val onOffDBValue = sharedPreferences.getBoolean(ONOFF_KEY, false)
        val alramData = timeDBValue.split(":")

        val alramModel = AlramDisplayModel(
            hour = alramData[0].toInt(),
            minute = alramData[1].toInt(),
            onOff = onOffDBValue
        )

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ALRAM_REQUEST_CODE,
            Intent(this, AlramReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        if ((pendingIntent == null) and alramModel.onOff) {
            alramModel.onOff = false
        } else if ((pendingIntent != null) and alramModel.onOff.not()) {
            pendingIntent.cancel()
        }

        return alramModel
    }


    companion object {
        private const val SHARED_PREFERENECE_NAME = "time"
        private const val ALRAM_KEY = "alram"
        private const val ONOFF_KEY = "onOff"
        private const val ALRAM_REQUEST_CODE = 1000
    }

    private fun initChangeAlramTimeButton() {
        val changeAlaramTimeButton = findViewById<Button>(R.id.changeAlramTimeButton)
        changeAlaramTimeButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                this, { picker, hour, minute ->

                    val model = saveAlramModel(hour, minute, false)
                    renderView(model)
                    cancelAlram()


                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
            ).show()
        }
    }

    private fun cancelAlram() {
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ALRAM_REQUEST_CODE,
            Intent(this, AlramReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.cancel()
    }

    private fun saveAlramModel(hour: Int, minute: Int, onOff: Boolean): AlramDisplayModel {
        val Model = AlramDisplayModel(
            hour = hour,
            minute = minute,
            onOff = onOff
        )

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENECE_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(ALRAM_KEY, Model.makeDataForDB())
            putBoolean(ONOFF_KEY, Model.onOff)
            commit()
        }


        return Model
    }


    private fun initOnOffButton() {
        val onOffButton = findViewById<Button>(R.id.onOffButton)
        onOffButton.setOnClickListener {

            val model = it.tag as? AlramDisplayModel ?: return@setOnClickListener
            val newModel = saveAlramModel(model.hour, model.minute, model.onOff.not())
            renderView(newModel)

            if (newModel.onOff) {
                // 켜진 경우 -> 알람을 등록
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, newModel.hour)
                    set(Calendar.MINUTE, newModel.minute)

                    if (before(Calendar.getInstance())) {
                        add(Calendar.DATE, 1)
                    }
                }

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, AlramReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(this, ALRAM_REQUEST_CODE,
                    intent, PendingIntent.FLAG_IMMUTABLE)

                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )

            } else {
                cancelAlram()
            }

        }
    }

    private fun initViews() {

    }

    private fun bindViews() {

    }
}