package com.footballbattle.game.app

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.footballbattle.game.app.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater,container,false)
        val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
        binding.imageView12.setOnClickListener {
            navController.popBackStack()
        }
        var music = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("music",false)
        var sound = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("sound",false)
        setMusic(music)
        setSound(sound)
        binding.imageView10.setOnClickListener {
            sound = ! sound
            requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("sound",sound).apply()
            setSound(sound)
        }
        binding.imageView9.setOnClickListener {
            music = ! music
            requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("music",music).apply()
            setMusic(music)
        }
        return binding.root
    }
    fun setMusic(s: Boolean) {
        binding.imageView8.setImageResource(if(s) R.drawable.enabled else R.drawable.disabled)
    }


    fun setSound(s: Boolean) {
        binding.imageView11.setImageResource(if(s) R.drawable.enabled else R.drawable.disabled)
    }

}