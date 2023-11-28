package com.example.flo.ui.song

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentDetailBinding
import com.example.flo.databinding.FragmentSongBinding

class SongFragment : Fragment() {

    lateinit var binding : FragmentSongBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(inflater, container, false)

        //Mixoff를 클릭
        this.binding.songMixoffTg.setOnClickListener {
            setMixoffStatus(true)
        }
        //Mixon를 클릭
        this.binding.songMixonTg.setOnClickListener {
            setMixoffStatus(false)
        }
        return binding.root
    }

    fun setMixoffStatus(isOff : Boolean) {
        if (isOff) { //off이미지일떄는 on이미지로 바꿔
            binding.songMixonTg.visibility = View.VISIBLE
            binding.songMixoffTg.visibility = View.GONE
        }
        else { //on이미지일떄는 off이미지로 바꿔
            binding.songMixonTg.visibility = View.GONE
            binding.songMixoffTg.visibility = View.VISIBLE
        }
    }

}