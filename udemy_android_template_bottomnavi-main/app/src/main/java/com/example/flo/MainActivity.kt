package com.example.flo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var song: Song = Song()
    private var gson: Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //우리가 앱이 실행될 때 테마를 Splash 테마로 지정해주었으니, 앱이 로드된 이후, 즉 onCreate가 실행됐을때는
        //다시 테마를 원래 테마로 돌려줘야죠
        setTheme(R.style.Theme_FLO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputDummySongs()
        inputDummyAlbums()

        //sharedPreferences에 저장된 값을 가져올거니까 아래 코드는 필요없죠
//        val song = Song(binding.mainMiniplayerTitleTv.text.toString(), binding.mainMiniplayerSingerTv.text.toString(),
//            0, 60, false, "music_lilac")

        binding.mainPlayerCl.setOnClickListener{
            //startActivity(Intent(this, SongActivity::class.java))
//            val intent = Intent(this, SongActivity::class.java)
//            intent.putExtra("title", song.title)
//            intent.putExtra("singer", song.singer)
//            intent.putExtra("second", song.second)
//            intent.putExtra("playTime", song.playTime)
//            intent.putExtra("isPlaying", song.isPlaying)
//            intent.putExtra("music", song.music)
//            startActivityForResult(intent, 100)
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", song.id)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }

        initBottomNavigation()

        Log.d("song", song.title + song.singer)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            100 -> {
                if (resultCode == RESULT_OK) {
                    val result = data?.getStringExtra("result_data")
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initBottomNavigation(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
    
    private fun setMiniPlayer(song: Song) {
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer  //만든 seekbar의 max가 10만이어서 100000으로 쓴거임
        binding.mainMiniplayerProgressSb.progress = (song.second*100000)/song.playTime
    }

    // [SongActivity에서 저장되었던 Song데이터를 가져와서 MainActivity 미니플레이어에 반영]
    //액티비티 전환이 될 때 onStart()부터 시작되기 때문임.
    //MainActivity에서 SongActivity로 갔다가 다시 MainActivity로 돌아오게되면 onStart()부터 시작됨
    override fun onStart() {
        super.onStart()
//        //sharedPreferences에 저장되어있던 값을 가져와야겠죠
//        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
//        val songJson = sharedPreferences.getString("songData", null)
//
//        //가져온 값을 song데이터 클래스 객체에 담아줘야겠죠? 저장해줘야겠죠?
//        song = if(songJson == null) { //맨처음에는 sharedPreferences에 저장된 값이 아무것도 없을 것이기 때문에 오류처리
//            Song("라일락", "아이유(IU)", 0, 60, false, "music_lilac")
//        } else {  //저장된 값이 있을 때엔 그 값을 가져오기
//            gson.fromJson(songJson, Song::class.java)  //fromJson 사용해서 songJson을 Song클래스 자바객체로 변경
//        }

        //Song을 초기화해줘야함. sharedPreference를 통해서 id를 받아온다고 했죠
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        //DB에서 해당 id에 해당하는 Song을 가져오기 -> DAO에 메소드 구현해서 가져오기
        val songDB = SongDatabase.getInstance(this)!!
        song = if(songId == 0){
            songDB.songDao().getSong(1)
        } else {
            songDB.songDao().getSong(songId)
        }

        //log로 받아온 songId를 확인하고 데이터렌더링 해주기
        Log.d("song ID", song.id.toString())
        
        //miniplayer에 반영
        setMiniPlayer(song)
    }

    private fun inputDummySongs(){
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        if (songs.isNotEmpty()) return

        songDB.songDao().insert(
            Song(
                "Lilac",
                "아이유 (IU)",
                0,
                200,
                false,
                "music_lilac",
                R.drawable.img_album_exp2,
                false,
                0
            )
        )

        songDB.songDao().insert(
            Song(
                "Flu",
                "아이유 (IU)",
                0,
                200,
                false,
                "music_flu",
                R.drawable.img_album_exp2,
                false,
                0
            )
        )

        songDB.songDao().insert(
            Song(
                "Butter",
                "방탄소년단 (BTS)",
                0,
                190,
                false,
                "music_butter",
                R.drawable.img_album_exp,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Next Level",
                "에스파 (AESPA)",
                0,
                210,
                false,
                "music_next",
                R.drawable.img_album_exp3,
                false,
                2
            )
        )


        songDB.songDao().insert(
            Song(
                "Boy with Luv",
                "방탄소년단 (BTS)",
                0,
                230,
                false,
                "music_lilac",
                R.drawable.img_album_exp4,
                false,
                3
            )
        )


        songDB.songDao().insert(
            Song(
                "BBoom BBoom",
                "모모랜드 (MOMOLAND)",
                0,
                240,
                false,
                "music_bboom",
                R.drawable.img_album_exp5,
                false,
                4
            )
        )

        songDB.songDao().insert(
            Song(
                "작은 것들을 위한 시",
                "방탄소년단 (BTS)",
                0,
                240,
                false,
                "music_bboom",
                R.drawable.img_album_exp4,
                false,
                3
            )
        )

        songDB.songDao().insert(
            Song(
                "SPICY",
                "에스파 (AESPA)",
                0,
                240,
                false,
                "music_bboom",
                R.drawable.img_album_exp3,
                false,
                2
            )
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", _songs.toString())
    }

    //ROOM_DB
    private fun inputDummyAlbums() {
        val songDB = SongDatabase.getInstance(this)!!
        val albums = songDB.albumDao().getAlbums()

        if (albums.isNotEmpty()) return

        songDB.albumDao().insert(
            Album(
                0,
                "IU 5th Album 'LILAC'", "아이유 (IU)", R.drawable.img_album_exp2
            )
        )

        songDB.albumDao().insert(
            Album(
                1,
                "Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp
            )
        )

        songDB.albumDao().insert(
            Album(
                2,
                "iScreaM Vol.10 : Next Level Remixes", "에스파 (AESPA)", R.drawable.img_album_exp3
            )
        )

        songDB.albumDao().insert(
            Album(
                3,
                "MAP OF THE SOUL : PERSONA", "방탄소년단 (BTS)", R.drawable.img_album_exp4
            )
        )

        songDB.albumDao().insert(
            Album(
                4,
                "GREAT!", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5
            )
        )
        val _albums = songDB.albumDao().getAlbums()
        Log.d("SELECT ALBUM", _albums.toString())

    }
}