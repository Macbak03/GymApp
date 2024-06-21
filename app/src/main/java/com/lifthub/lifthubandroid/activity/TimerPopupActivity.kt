package com.lifthub.lifthubandroid.activity

import android.os.Bundle
import android.os.PersistableBundle
import com.lifthub.lifthubandroid.R
import com.lifthub.lifthubandroid.databinding.ActivityTimerPopupActivityBinding
import com.lifthub.lifthubandroid.timer.util.AudioPlayer

class TimerPopupActivity: BaseActivity() {
    private lateinit var binding: ActivityTimerPopupActivityBinding
    private lateinit var audioPlayer: AudioPlayer

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityTimerPopupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        audioPlayer = AudioPlayer()
        audioPlayer.playSound(this, R.raw.timer_alarm)
        binding.btnStopAlarm.setOnClickListener{
            audioPlayer.stopSound()
            finish()
        }
    }
}