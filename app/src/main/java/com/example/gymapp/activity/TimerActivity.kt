package com.example.gymapp.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.example.gymapp.R
import com.example.gymapp.databinding.ActivityTimerBinding
import com.example.gymapp.timer.TimerExpiredReceiver
import com.example.gymapp.timer.util.NotificationUtil
import com.example.gymapp.timer.util.PrefUtil
import java.util.Calendar

class TimerActivity : BaseActivity() {
    private lateinit var binding: ActivityTimerBinding

    private lateinit var timer: CountDownTimer

    private var timerLengthSeconds: Long = 0
    private var timerState = TimerState.Stopped

    private var timerSetterMinutes: Int = 0
    private var timerSetterSeconds: Int = 0

    private var secondsRemaining: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        loadTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonTimerPlayPause.setOnClickListener {
            timerState = if (timerState == TimerState.Paused || timerState == TimerState.Stopped) {
                startTimer()
                TimerState.Running
            } else {
                timer.cancel()
                TimerState.Paused
            }
            updateButtons()
            handleTimePickerStatus()
        }

        binding.buttonTimerClear.setOnClickListener {
            onTimerFinished()
            handleTimePickerStatus()
        }
        binding.buttonTimerStop.setOnClickListener {
            NotificationUtil.audioPlayer.stopSound()
            it.visibility = View.GONE
        }

        binding.numberPickerMinutes.minValue = 0
        binding.numberPickerMinutes.maxValue = 59
        binding.numberPickerSeconds.minValue = 0
        binding.numberPickerSeconds.maxValue = 59

        if (binding.numberPickerMinutes.value == 0) {
            binding.numberPickerSeconds.value = 1
        }

        handleTimerPicker()
    }

    override fun onResume() {
        super.onResume()
        initTimer()
        NotificationUtil.hideTimerNotification(this)
        removeAlarm(this)
    }

    override fun onPause() {
        super.onPause()
        showNotification()
        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this)
        PrefUtil.setSecondsRemaining(secondsRemaining, this)
        PrefUtil.setTimerState(timerState, this)

        PrefUtil.setPickerMinutes(timerSetterMinutes, this)
        PrefUtil.setPickerSeconds(timerSetterSeconds, this)
    }

    private fun handleTimerPicker() {
        val numberPickerSeconds = binding.numberPickerSeconds
        val numberPickerMinutes = binding.numberPickerMinutes
        numberPickerMinutes.setOnValueChangedListener { picker, oldVal, newVal ->
            if (newVal == 0 && numberPickerSeconds.value == 0) {
                val minValue = 1
                numberPickerSeconds.value = minValue
                timerSetterSeconds = minValue
                val formattedSeconds = getString(R.string.timeLessThan10, minValue.toString())
                binding.textViewSeconds.text = formattedSeconds
            }
            if (timerState == TimerState.Stopped) {
                binding.textViewMinutes.text = picker.value.toString()
                timerSetterMinutes = picker.value
                setTimerOnPickerChange()
            }
        }
        numberPickerSeconds.setOnValueChangedListener { picker, oldVal, newVal ->
            if (newVal == 0 && numberPickerMinutes.value == 0) {
                picker.value = oldVal
            }
            if (timerState == TimerState.Stopped) {
                if (picker.value < 10) {
                    val formattedSeconds = getString(R.string.timeLessThan10, picker.value.toString())
                    binding.textViewSeconds.text = formattedSeconds
                } else {
                    binding.textViewSeconds.text = picker.value.toString()
                }
            }
            timerSetterSeconds = picker.value
            setTimerOnPickerChange()

        }
    }

    private fun setTimerOnPickerChange() {
        timerLengthSeconds = (timerSetterMinutes * 60).toLong() + timerSetterSeconds.toLong()
        PrefUtil.setTimerLength(timerLengthSeconds, this)
        setNewTimerLength()
        secondsRemaining = timerLengthSeconds
    }


    private fun handleTimePickerStatus() {
        if (timerState == TimerState.Running || timerState == TimerState.Paused) {
            binding.numberPickerSeconds.isEnabled = false
            binding.numberPickerMinutes.isEnabled = false
        } else {
            binding.numberPickerSeconds.isEnabled = true
            binding.numberPickerMinutes.isEnabled = true
        }
    }

    private fun setTimePicker() {
        binding.numberPickerMinutes.value = PrefUtil.getPickerMinutes(this)
        binding.numberPickerSeconds.value = PrefUtil.getPickerSeconds(this)
        timerSetterMinutes = binding.numberPickerMinutes.value
        timerSetterSeconds = binding.numberPickerSeconds.value
        val seconds = (timerSetterMinutes * 60 + timerSetterSeconds).toLong()
        PrefUtil.setTimerLength(seconds, this)
    }


    private fun initTimer() {
        timerState = PrefUtil.getTimerState(this)

        setTimePicker()

        handleTimePickerStatus()

        setNewTimerLength()

        secondsRemaining =
            if (timerState == TimerState.Running || timerState == TimerState.Paused) {
                PrefUtil.getSecondsRemaining(this)
            } else
                timerLengthSeconds

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)
        if (alarmSetTime > 0) {
            secondsRemaining -= nowSeconds - alarmSetTime
        }

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }

        if (secondsRemaining <= 0) {
            onTimerFinished()
            binding.buttonTimerStop.visibility = View.VISIBLE
        } else if (timerState == TimerState.Running) {
            startTimer()
        }

        updateButtons()
        updateCountdownUI()
    }

    private fun onTimerFinished() {
        timerState = TimerState.Stopped
        timer.cancel()

        setNewTimerLength()

        binding.progressCountdown.progress = 0f

        PrefUtil.setSecondsRemaining(timerLengthSeconds, this)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        handleTimePickerStatus()
        updateCountdownUI()
    }

    private fun playAudio() {
        NotificationUtil.audioPlayer.playSound(this, R.raw.timer_alarm)
        binding.buttonTimerStop.visibility = View.VISIBLE
    }

    private fun startTimer() {
        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
                if (secondsRemaining <= 0) {
                    playAudio()
                }
            }
        }
        timerState = TimerState.Running
        timer.start()
    }

    private fun setNewTimerLength() {
        val timerLength = PrefUtil.getTimerLength(this)
        timerLengthSeconds = timerLength
        binding.progressCountdown.progressMax = timerLengthSeconds.toFloat()
    }

    private fun setPreviousTimerLength() {
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
        binding.progressCountdown.progressMax = timerLengthSeconds.toFloat()
    }

    private fun updateCountdownUI() {
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        binding.textViewMinutes.text = ("$minutesUntilFinished")
        binding.textViewSeconds.text = (if (secondsStr.length == 2) secondsStr else "0$secondsStr")
        binding.progressCountdown.progress = (timerLengthSeconds - secondsRemaining).toFloat()
    }

    private fun updateButtons() {
        when (timerState) {
            TimerState.Running -> {
                binding.buttonTimerClear.isEnabled = true
                binding.imagePlayPause.setBackgroundResource(R.drawable.baseline_pause_24)
            }

            TimerState.Stopped -> {
                binding.buttonTimerClear.isEnabled = false
                binding.imagePlayPause.setBackgroundResource(R.drawable.baseline_play_arrow_24)
            }

            TimerState.Paused -> {
                binding.buttonTimerClear.isEnabled = true
                binding.imagePlayPause.setBackgroundResource(R.drawable.baseline_play_arrow_24)
            }
        }
    }


    private fun showNotification() {
        if (timerState == TimerState.Running) {
            timer.cancel()
            val wakeUpTime = setAlarm(this, nowSeconds, secondsRemaining)
            NotificationUtil.showTimerRunning(this, wakeUpTime)
        } else if (timerState == TimerState.Paused) {
            NotificationUtil.showTimerPaused(this)
        }
    }

    companion object {
        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
                    val intent =
                        Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    context.startActivity(intent)
                    return -1
                }
            }
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context) {
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }

    enum class TimerState {
        Stopped, Paused, Running
    }
}