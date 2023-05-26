package com.footballbattle.game.app

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.footballbattle.game.app.databinding.FragmentTopBinding

class TopFragment : Fragment() {

    private lateinit var binding: FragmentTopBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTopBinding.inflate(inflater,container,false)
        val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
        binding.imageView13.setOnClickListener {
            navController.popBackStack()
        }
        binding.textView44.text = "Your best rating:\n"+ requireContext().getSharedPreferences("prefs",Context.MODE_PRIVATE).getString("classic","None")
        binding.textView4.text = "Your best rating:\n"+requireContext().getSharedPreferences("prefs",Context.MODE_PRIVATE).getString("endless","None")
        return binding.root
    }


}