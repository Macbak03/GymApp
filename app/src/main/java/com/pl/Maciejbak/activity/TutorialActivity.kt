package com.pl.Maciejbak.activity

import android.os.Bundle
import com.pl.Maciejbak.databinding.ActivityTutorialBinding

class TutorialActivity : BaseActivity() {
    private lateinit var binding: ActivityTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        loadTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}