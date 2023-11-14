package com.example.flo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment : Fragment() {
    lateinit var binding : FragmentAlbumBinding
    private var gson: Gson = Gson()

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    private var isLiked: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAlbumBinding.inflate(inflater,container,false)

        //Home에서 넘어온 데이터 받아오기
        //album에서 argument꺼내서 json형태로 받아서 앨범객체로 변환해서 받아옴
        val albumJson = arguments?.getString("album")
        val album = gson.fromJson(albumJson, Album::class.java)

        //Home에서 넘어온 데이터를 반영
        isLiked = isLikedAlbum(album.id)
        setInit(album) //setInit통해서 binding해줌
        setClickListeners(album) //album은 현재 저장된 앨범에 대한 데이터

//        // HomeFragment.kt에서 받은 정보로 text바꿔주기
//        val title = arguments?.getString("title")
//        val singer = arguments?.getString("singer")
//        binding.albumMusicTitleTv.text = "IU 5th Album '$title'"
//        binding.albumSingerNameTv.text = singer

        //뒤로가기 누르면 fragment 교체
        //클릭하면 main_frm이 HomeFragment()로 대체되는 코드
        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction().
            replace(R.id.main_frm,HomeFragment()).commitAllowingStateLoss()
        }

        //View와 연결해주는 작업
        val albumAdapter = AlbumVPAdapter(this) //초기화
        binding.albumContentVp.adapter = albumAdapter
        //TabLayout을 ViewPager2와 연결하는 중재자 Mediator  탭이 선택될때 ViewPager2의 위치를 선택된 탭과 동기화하고
        // 사용자가 ViewPager2를 스크롤 할 때 TapLayout에 스크롤 위치를 동기화함
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) { //인자값은 연결할 TapLayout, ViewPager
            //TabLayout에 어떠한 텍스트가 들어갈지 적어주면 됨
            tab, position ->
            tab.text = information[position]
        }.attach() //attach로 TabLayout과 ViewPager를 붙여줌

        return binding.root
    }

    private fun setInit(album: Album){
        binding.albumAlbumIv.setImageResource(album.coverImg!!)
        binding.albumMusicTitleTv.text = album.title.toString()
        binding.albumSingerNameTv.text = album.singer.toString()

        if(isLiked) {
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val jwt = spf!!.getInt("jwt", 0)
        Log.d("MAIN_ACT/GET_JWT", "jwt_token: $jwt")

        return jwt
    }

    //LikeTable에 정보를 저장 //앨범을 좋아요 했을 때 이 앨범이 좋아요 했다는 것을 DB에 저장
    private fun likeAlbum(userId: Int, albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val like = Like(userId, albumId)

        songDB.albumDao().likeAlbum(like)
    }

    private fun disLikeAlbum(userId: Int, albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        songDB.albumDao().disLikeAlbum(userId, albumId)
    }

    private fun isLikedAlbum(albumId: Int): Boolean {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()

        //어떤 유저가 해당 앨범을 좋아요 했는지 확인해주는 변수
        val likeId: Int? = songDB.albumDao().isLikedAlbum(userId, albumId)

        //만약 유저가 앨범을 좋아요 했으면 likeId가 null이 아님 그래서 이 return값은 true가 됨
        //좋아요 안하면 null임. return값은 false가 됨
        return likeId != null
    }

    private fun setClickListeners(album: Album) {
        val userId: Int = getJwt()

        binding.albumLikeIv.setOnClickListener {
            if(isLiked) {
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
                disLikeAlbum(userId, album.id)
            } else {
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
                likeAlbum(userId, album.id)
            }

            isLiked = !isLiked
        }

        //set click listener
        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }
    }
}