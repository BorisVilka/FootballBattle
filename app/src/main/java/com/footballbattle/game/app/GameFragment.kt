package com.footballbattle.game.app

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.footballbattle.game.app.databinding.FragmentGameBinding

class GameFragment : Fragment() {


    private lateinit var binding: FragmentGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGameBinding.inflate(inflater,container,false)
        binding.game.mode = requireArguments().getInt("mode")
        binding.game.setEndListener(object : GameView.Companion.EndListener {
            override fun end() {
                requireActivity().runOnUiThread {
                    if(binding.game.mode==0) {
                        val f = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("classic","0/0")
                        val a = f!!.split("/")[0].toInt()
                        if(a<binding.game.scoreM) requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putString("classic","${binding.game.scoreM}/${binding.game.scoreE}").apply()
                    } else {
                    }
                    if(binding.game.scoreE>binding.game.scoreM) binding.lose.visibility = View.VISIBLE
                    else binding.win.visibility = View.VISIBLE
                }
            }

            override fun score(score: Int) {

            }

        })
        binding.lose.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
            navController.popBackStack()
        }
        binding.win.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
            navController.popBackStack()
        }
        return binding.root
    }


}