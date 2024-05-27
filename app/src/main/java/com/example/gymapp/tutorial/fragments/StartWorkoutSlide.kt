package com.example.gymapp.tutorial.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.gymapp.R
import com.example.gymapp.activity.MainActivity
import com.example.gymapp.databinding.FragmentStartWorkoutSlideBinding
import com.example.gymapp.fragment.SplashFragment

class StartWorkoutSlide : SplashFragment() {
    private var _binding: FragmentStartWorkoutSlideBinding? = null
    private val binding get() = _binding!!

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartWorkoutSlideBinding.inflate(layoutInflater, container, false)

        binding.finish.setOnClickListener {
            setTutorialFinished()
            openMainActivity()
        }

        exoPlayer = ExoPlayer.Builder(requireActivity()).build()
        playerView = binding.playerView

        playerView.player = exoPlayer
        val videoPath =
            "android.resource://${TutorialViewPagerFragment.PACKAGE_NAME}/${R.raw.create_workout_recording}"
        val uri = Uri.parse(videoPath)
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)

        exoPlayer.prepare()
        exoPlayer.playWhenReady = true


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}