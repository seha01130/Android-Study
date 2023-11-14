package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class LockerVPAdapter(fragment:Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3 //저장한 곡 , 음악파일

    override fun createFragment(position: Int): Fragment {
        return when (position) {  //저장한 곡 , 음악파일 각각을 눌렀을 때 position에 따라 다른 fragment를 보여줄거예요
            0 -> SavedSongFragment()
            1 -> MusicFileFragment()
            else -> SavedAlbumFragment()
        }
    }
}