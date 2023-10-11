package com.example.gymapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gymapp.databinding.ActivityCreateRoutineBinding

class CreateRoutineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateRoutineBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}