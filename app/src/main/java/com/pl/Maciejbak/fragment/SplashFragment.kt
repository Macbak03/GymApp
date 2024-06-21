package com.pl.Maciejbak.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.fragment.findNavController
import com.pl.Maciejbak.R
import com.pl.Maciejbak.activity.MainActivity
import com.pl.Maciejbak.databinding.FragmentSplashBinding

open class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var text: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val colors = listOf(Color.White, Color(145, 35, 35), Color(213, 171, 61))
    private var colorIndex = 0

    private val changeColorRunnable = object : Runnable{
        override fun run() {
            text.setTextColor(colors[colorIndex].toArgb())
            colorIndex = (colorIndex + 1) % colors.size
            handler.postDelayed(this, 500)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(layoutInflater, container, false)

        text = binding.logoText
        handler.post(changeColorRunnable)

        Handler().postDelayed({
            if(onTutorialFinished()){
                openMainActivity()
            }else{
                findNavController().navigate(R.id.action_splashFragment_to_tutorialViewPagerFragment)
            }
        }, 2000)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(changeColorRunnable)
        _binding = null
    }

    private fun onTutorialFinished(): Boolean{
        val sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(TUTORIAL_FINISHED_ID, false)
    }

    protected fun openMainActivity(){
        val intent = Intent(activity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    protected fun setTutorialFinished(){
        val sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(TUTORIAL_FINISHED_ID, true)
        editor.apply()
    }

    companion object{
        const val TUTORIAL_FINISHED_ID = "com.pl.Maciejbak.tutorial.finished"
        const val PREF_NAME = "tutorial"
    }
}