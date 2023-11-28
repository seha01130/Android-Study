package com.example.flo.ui.main.home

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.ui.main.album.AlbumFragment
import com.example.flo.ui.main.album.AlbumRVAdapter
import com.example.flo.ui.main.banner.BannerFragment
import com.example.flo.ui.main.banner.BannerVPAdapter
import com.example.flo.ui.panel.PanelFragment
import com.example.flo.ui.panel.PanelVPAdapter
import com.example.flo.R
import com.example.flo.data.entities.Album
import com.example.flo.data.local.SongDatabase
import com.example.flo.databinding.FragmentHomeBinding
import com.example.flo.ui.main.MainActivity
import com.google.gson.Gson

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private var albumDatas = ArrayList<Album>()

    private lateinit var songDB: SongDatabase

    /* viewPagerHorizontal 에 사용돠는 handler, runnable */
    //자동슬라이드 구현
    private val hHandler = Handler()
    private val hRunnable =
        Runnable {
            binding.frHomeVP.currentItem = binding.frHomeVP.currentItem + 1
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

//        //클릭하면 main_frm이 fragment2로 바뀌는 코드
//        binding.homeAlbumIv1.setOnClickListener{
//            //HomeFragment에서 앨범의 데이터를 AlbumFragment로 전달하여 클릭한 앨범과 동일한 앨범이 나오도록 구현
//            var fragment2 = AlbumFragment()
//            var bundle = Bundle()
//            bundle.putString("title",binding.releaseAlbumTitleTv1.text.toString())
//            bundle.putString("singer",binding.releaseAlbumSingerTv1.text.toString())
//            fragment2.arguments = bundle //fragment의 arguments에게 데이터를 담은 bundle을 넘겨줌
//
//            //HomeFragment는 MainActivity안에 있는 하나의 조각
//            //그래서 그 조각을 어디서 변경하는지에 대해서 써준다고 생각하면 됨
//            (context as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_frm,fragment2).commitAllowingStateLoss()
//            //replace -> 원래 Fragment조각을 다른 Fragment조각으로 대체한다고 생각해라
//            //R.id.main_frm를 AlbumFragment()로 변경
//        }

        //Album데이터에 데이터 추가
        songDB = SongDatabase.getInstance(requireContext())!!
        albumDatas.addAll(songDB.albumDao().getAlbums())
        //데이터 리스트 생성 더미 데이터
//        albumDatas.apply {
//            add(Album(1, "Butter", "방탄소년단(BTS)", R.drawable.img_album_exp))
//            add(Album(0, "Lilac", "아이유(IU)", R.drawable.img_album_exp2))
//            add(Album(2, "Next Level", "에스파(AESPA)", R.drawable.img_album_exp3))
//            add(Album(3, "Boy with Luv", "방탄소년단(BTS)", R.drawable.img_album_exp4))
//            add(Album(4, "BBoom BBoom", "모모랜드(MOMOLAND)", R.drawable.img_album_exp5))
//            add(Album(0,"Weekend", "태연(Tae Yeon)", R.drawable.img_album_exp6))
//        }

        //어댑터와 데이터리스트를 연결
        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        //리사이클러뷰에 어댑터를 연결해서 너가 사용해야할 어댑터는 이것이다라고 알려줌
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        //AlbumRVAdapter 외부에서 listener객체를 던져주기
        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener {

            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
            }
        })

        //ViewPager라는 전자제품을 전원선 Adapter를 연결해서 전기 data를 가져와서 사용
        //homeFragment에서 fragment데이터를 추가해주고 viewPager연결하기
        BannerViewPager()
        panelViewPager()

        return binding.root
    }

    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }

    //배너 ViewPager 작업
    private fun BannerViewPager() {
        val bannerAdapter = BannerVPAdapter(this) //초기화해주기
        // list안에 fragment를 추가해주자
        //viewPager에 fragment들을 추가해서 viewPager에 잘 적용이 되어있는지 확인
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))  //()안에 추가할 fragment를 써주기
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2)) //fragment 추가 //list에 fragment가 총 2개 들어가있는 것
        //viewPager와 Adapter연결
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL //viewPager가 좌우로 스크롤되게
    }

    //판넬 ViewPager 작업
    private fun panelViewPager() {
        val panelAdapter = PanelVPAdapter(this) //초기화해주기
        panelAdapter.addFragment(PanelFragment(R.drawable.discovery_banner_aos))  //()안에 추가할 fragment를 써주기
        panelAdapter.addFragment(PanelFragment(R.drawable.img_default_4_x_1)) //fragment 추가
        panelAdapter.addFragment(PanelFragment(R.drawable.img_album_exp4))
        panelAdapter.addFragment(PanelFragment(R.drawable.img_album_exp6))
        panelAdapter.addFragment(PanelFragment(R.drawable.img_album_exp5))
        //viewPager와 Adapter연결
        binding.frHomeVP.adapter = panelAdapter
        binding.frHomeVP.orientation = ViewPager2.ORIENTATION_HORIZONTAL //viewPager가 좌우로 스크롤되게
        //dotsIndicator와 연동
        binding.frHomeVP.offscreenPageLimit = 5
        binding.dotsIndicator.setViewPager2(binding.frHomeVP)

        //자동슬라이드 구현
        binding.frHomeVP.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                hHandler.removeCallbacks(hRunnable)
                hHandler.postDelayed(hRunnable, 2000) // Slide duration 2 seconds
            }
        })
    }

    //자동슬라이드 구현
    override fun onResume() {
        super.onResume()

        hHandler.postDelayed(hRunnable, 2000)  // viewPagerHorizontal 2초마다 Slide
    }
    //자동슬라이드 구현
    override fun onPause() {
        super.onPause()

        hHandler.removeCallbacks(hRunnable)  // viewPagerHorizontal 2초마다 Slide
    }
    
}