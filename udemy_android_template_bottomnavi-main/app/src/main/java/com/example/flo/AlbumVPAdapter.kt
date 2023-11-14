package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AlbumVPAdapter(fragment:Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int  = 3 //수록곡, 상세정보, 영상 을 ViewPager로 구현할거임 3개의 fragment를 만들거니까

    //HomeFragment에서 배너로 구성할떄는 addFragment라는 함수를 만들어서 각각에 똑같은 화면의 이미지만 바꿔서 fragment를 만들어줬어요
    //근데 수록곡, 상세정보, 영상이 반복된 화면이 아니라 각각의 다른 view를 가지고 있기때문에 fragment를 각각 만들어줘서 연결해주도록 할게요
    override fun createFragment(position: Int): Fragment {
        return when(position) { //수록곡, 상세정보, 영상을 눌렀을 때 position에 따라 다른 fragment를 보여줄거예요
            0 -> SongFragment()
            1 -> DetailFragment()
            else -> VideoFragment()
        }
    }
}