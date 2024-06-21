package com.lifthub.lifthubandroid.tutorial.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.viewpager2.widget.ViewPager2
import com.lifthub.lifthubandroid.R
import com.lifthub.lifthubandroid.databinding.FragmentCreateRoutineSlideBinding
import com.lifthub.lifthubandroid.fragment.SplashFragment

class CreateRoutineSlide : SplashFragment() {
    private var _binding: FragmentCreateRoutineSlideBinding? = null
    private val binding get() = _binding!!

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateRoutineSlideBinding.inflate(layoutInflater, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.tutorialViewPager)

        binding.nextRoutineTutorial.setOnClickListener {
            viewPager?.currentItem = 2
        }

        binding.skipRoutineTutorial.setOnClickListener {
            setTutorialFinished()
            openMainActivity()
        }

        exoPlayer = ExoPlayer.Builder(requireActivity()).build()
        playerView = binding.playerView

        playerView.player = exoPlayer
        val videoPath =
            "android.resource://${TutorialViewPagerFragment.PACKAGE_NAME}/${R.raw.create_routine_recording}"
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