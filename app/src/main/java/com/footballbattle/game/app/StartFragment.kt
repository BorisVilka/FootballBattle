package com.footballbattle.game.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.footballbattle.game.app.databinding.FragmentStartBinding

class StartFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStartBinding.inflate(inflater,container,false)
        val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
        binding.imageView3.setOnClickListener {
            navController.navigate(R.id.gameFragment,Bundle().apply { putInt("mode",0) })
        }
        binding.imageView4.setOnClickListener {
            navController.navigate(R.id.gameFragment,Bundle().apply { putInt("mode",1) })
        }
        binding.imageView5.setOnClickListener {
            navController.navigate(R.id.settingsFragment)
        }

        binding.imageView6.setOnClickListener {
            navController.navigate(R.id.topFragment)
        }
        return binding.root
    }


}