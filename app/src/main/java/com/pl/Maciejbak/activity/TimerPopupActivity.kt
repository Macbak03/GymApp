package com.pl.Maciejbak.activity

import android.os.Bundle
import android.os.PersistableBundle
import com.pl.Maciejbak.R
import com.pl.Maciejbak.databinding.ActivityTimerPopupActivityBinding
import com.pl.Maciejbak.timer.util.AudioPlayer

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