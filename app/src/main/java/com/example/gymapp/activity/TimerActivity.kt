package com.example.gymapp.activity

import android.os.Bundle
import com.example.gymapp.databinding.ActivityTimerBinding

class TimerActivity: BaseActivity() {
    private lateinit var binding: ActivityTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        loadTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}