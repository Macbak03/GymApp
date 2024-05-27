package com.example.gymapp.tutorial.fragments

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.viewpager2.widget.ViewPager2
import com.example.gymapp.R
import com.example.gymapp.databinding.FragmentCreatePlanSlideBinding
import com.example.gymapp.fragment.SplashFragment

class CreatePlanSlide : SplashFragment() {
    private var _binding: FragmentCreatePlanSlideBinding? = null
    private val binding get() = _binding!!

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlanSlideBinding.inflate(layoutInflater, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.tutorialViewPager)

        binding.nextPlanTutorial.setOnClickListener {
            viewPager?.currentItem = 1
        }

        binding.skipPlanTutorial.setOnClickListener {
            setTutorialFinished()
            openMainActivity()
        }

        exoPlayer = ExoPlayer.Builder(requireActivity()).build()
        playerView = binding.playerView

        playerView.player = exoPlayer
        val videoPath =
            "android.resource://${TutorialViewPagerFragment.PACKAGE_NAME}/${R.raw.create_plan_recording}"
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