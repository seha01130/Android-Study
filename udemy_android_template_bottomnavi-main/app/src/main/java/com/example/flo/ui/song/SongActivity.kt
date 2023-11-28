package com.example.flo.ui.song

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.CustomToast
import com.example.flo.R
import com.example.flo.data.entities.Song
import com.example.flo.data.local.SongDatabase
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    lateinit var binding : ActivitySongBinding
    lateinit var timer : Timer
    //Timer를 만들어주면 전역변수로 Timer를 하나 생성하고 아래에 initSong()함수에 처음들어오는 Song객체에 데이터를 초기화해줌과 동시에
    //Timer 객체를 만들고 시작해주겠습니다.
    private var mediaPlayer: MediaPlayer? = null //액티비티가 소멸될때 미디어플레이어를 해제시켜줘야해서 nullable로.
    private var gson: Gson = Gson()

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //함수를 하나 만들고 전역변수로 Song을 두어서(12번 라인) Song data class를 초기화해주는 함수
        initPlayList()
        initSong()
        initClickListener()

        //위의 initSong()이랑 setPlayer() 함수 있으니까 이제 이거 없어도 되지
//        if (intent.hasExtra("title") && intent.hasExtra("singer")){
//            binding.songMusicTitleTv.text = intent.getStringExtra("title")!!
//            binding.songSingerNameTv.text = intent.getStringExtra("singer")!!
//        }

        //[Song]화면 한곡재생 버튼 클릭 시 스레드 재시작 구현해보기
        binding.songRepeatIv.setOnClickListener {
            timer.interrupt()
            timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)

            mediaPlayer?.stop() //stop() 후에 다시 music이랑 mediaPlayer 만들어줘야함.

            val music = resources.getIdentifier(songs[nowPos].music, "raw", this.packageName)
            mediaPlayer = MediaPlayer.create(this, music) //MediaPlayer에게 이 음악을 재생할거야 라고 알려주면 됨

            if (songs[nowPos].isPlaying == true) {  //플레이중에 버튼누르면 다시 1초로 시작되면서 음악도 처음부터 나오고
                mediaPlayer?.start()
                try {
                    timer.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            //그 외에는 if문 바로 윗부분까지만 실행이돼서 원래 타이머 멈추고 타이머 다시 만들고
            //미디어 멈추고 다시 음악이랑 미디어흘레이어 새로 만들어서
            //다시 시작하기 전 준비까지!!!!만 해놓는거임.
            //타이머랑 음악은 언제 시작하냐면 
            /*
                binding.songMiniplayerIv.setOnClickListener {
                    setPlayerStatus(true)
                }
                여기서 플레이버튼 누르면 setPlayerStatus함수를 호출하고 그러면 isPlaying이 true가 될 테니까
                setPlayerStatus 함수에서 if (isPlaying) 안에서 
                    mediaPlayer?.start()
                    timer.start()
                이렇게 두 개를 시작해주는거임.
                
                그럼 맨처음에 songActivity오면 initSong()을 호출하잖아.
                initSon()이 startTimer()를 호출하거든?
                
                private fun startTimer() {
                    timer = Timer(song.playTime, song.isPlaying)
                      //binding.songRepeatIv.setOnClickListener{} 부분때문에 아래 코드 주석처리함
                      //timer.start()
                }
                
                startTimer에서 원래 Timer.start()를 해줬는데 그럼 songActivity 처음 진입하자마자
                1. Timer 스레드 실행하고
                2. 플레이버튼 누르면 또 Timer 스레드 실행
                이렇게 돼서 스레드가 시작이 2번되는데 이러면 오류가 나서 앱이 종료돼
                따라서 startTimer() 함수 내에서는 timer.start()를 해주지 말고
                플레이버튼을 누르면 timer.start()를 해주자 그래야 Timer 스레드가 하나만 실행되니까.
            * */
        }
    }

    //DB에 저장되어있는 song객체를 뽑아와서 songs에 저장
    private fun initPlayList() {
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun initClickListener() {
        //내리기버튼 (누르면 MainActivity로 돌아감)
        binding.songDownIb.setOnClickListener{
            val resultIntent = Intent()
            resultIntent.putExtra("result_data", intent.getStringExtra("title"))
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        //플레이버튼의 clickListener
        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(true)
        }
        //정지버튼의 clickListener
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }
        binding.songNextIv.setOnClickListener{
            moveSong(+1)
        }
        binding.songPreviousIv.setOnClickListener {
            moveSong(-1)
        }
        binding.songLikeIv.setOnClickListener {
            if (!songs[nowPos].isLike) {
                CustomToast.showToast(this, "custom toast message")
            }
            setLike(songs[nowPos].isLike)
        }
    }

    private fun initSong() {
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId)
        Log.d("now Song ID", songs[nowPos].id.toString())

//        if (intent.hasExtra("title") && intent.hasExtra("singer")){
//            song = Song(
//                intent.getStringExtra("title")!!,
//                intent.getStringExtra("singer")!!,
//                intent.getIntExtra("second", 0),
//                intent.getIntExtra("playTime", 0),
//                intent.getBooleanExtra("isPlaying", false),
//                intent.getStringExtra("music")!!
//            )
//        }
        //initSong()함수에 처음들어오는 Song객체에 데이터를 초기화해줌과 동시에
        //Timer 객체를 만들고 시작해주기
        startTimer()
        setPlayer(songs[nowPos])
    }

    private fun setLike(isLike: Boolean){
        songs[nowPos].isLike = !isLike
        //DB의 값도 업데이트 해줘야함
        songDB.songDao().updateIsLikeById(!isLike, songs[nowPos].id)

        //새롭게 렌더링
        if (!isLike) {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun moveSong(direct: Int) {
        if (nowPos + direct <0){
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct >= songs.size){
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct

        timer.interrupt()
        startTimer()

        mediaPlayer?.release()
        mediaPlayer = null

        setPlayer(songs[nowPos])
    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for (i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    //저희가 보는 songActivity화면에 받아와서 초기화된 Song에 대한 데이터의 정보를 렌더링 해주기
    private fun setPlayer(song: Song) {
        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songAlbumIv.setImageResource(song.coverImg!!)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)  //seekBar에 progress라는 속성이 있음
        //음악은 String값. 실제로 실행시키려면 리소스파일에서 해당 String값으로 찾아서 리소스를 반환해주는 무언가가 필요함
                                         // 찾고자하는 리소스의 이름, 폴더(폴더에 있는지), 패키지이름 에 넣어주면 됩니다
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        //리소스를 반환을 받았으니 리소스를 미디어플레이어에게 올려줘야겠죠
        mediaPlayer = MediaPlayer.create(this, music) //MediaPlayer에게 이 음악을 재생할거야 라고 알려주면 됨

        //song 데이터에 따라 좋아요버튼 표시해주기
        if (song.isLike) {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

        setPlayerStatus(song.isPlaying)
    }

    fun setPlayerStatus(isPlaying : Boolean) {
        //노래 재생버튼과 정지버튼을 눌렀을 때 이 Thread가 멈추고 시작되어야하잖아요 isPlaying값을 true false로 바꿔줌으로써 처리해줬죠
        //setPlayerStatus함수의 값이 변할 때 song의 isPlaying이라는 값과 timer의 isPlaying이라는 값을 초기화해주면 됨
        songs[nowPos].isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if (isPlaying) { //플레이중이 true일때는 정지버튼이 보이게
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start()
            //binding.songRepeatIv.setOnClickListener{} 부분때문에 아래 코드 추가함
            timer.start()
        }
        else { //플레이중이 false일때는 즉, 멈춤일때는 플레이버튼이 보이게
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            if(mediaPlayer?.isPlaying == true){ //미디어플레이어는 재생중이 아닐때 pause를 하게되면 오류가 생길수있어서 추가.
                mediaPlayer?.pause()
            }
        }
    }

    //startTimer라는 함수를 만들어서 타이머를 객체생성과 시작을 동시에 할 수 있도록 만들어주겠습니다
    //initSong() 에서 startTimer() 함수를 호출함
    private fun startTimer() {
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        //binding.songRepeatIv.setOnClickListener{} 부분때문에 아래 코드 주석처리함
//        timer.start()
    }

    //inner클래스로 timer클래스를 만들어주고 Thread를 상속받아주겠습니다 //Inner키워드 없이 내부에 클래스를 만들면 정적인 클래스가 되어버려서
    //외부의 변수에 접근할 수 없게됨.
    //우리는 Thread에서 시간이 지남에 따라 seekBar와 TimerTextView의 값을 바꿔줘야하기때문에 binding변수를 사용해야함 -> Inner클래스로 만들어줌
    //생성자로 보여주고자 하는 노래가 총 몇초인지, 지금 진행중인지
    inner class Timer(private val playTime: Int,var isPlaying: Boolean = true ) : Thread() {

        private var second : Int = 0
        private var mills: Float = 0f  //mills가 1000단위가 될떄마다 1초가 되는 것임

        override fun run() {
            super.run()
            //쓰레드를 멈추는 방법에는 interrupt를 내는거. 오류를 내서 쓰레드를 강제로 멈추는 것. 오류가 나도 프로그램이 종료되는것이 아니라
            //catch문 내의 코드가 실행되는 try catch문을 사용
            try {
                //이 Timer는 계속 진행되어야 하니까 while (true)로
                while (true) {

                    if (second >= playTime) { //노래가 끝나면 반복이 종료되도록
                        //노래 다 끝나면 Timer 다 끝나면 플레이버튼이 멈춤버튼으로 바뀌어야하는데 이부분 오류 남. 나중에 수정 필요
//                        isPlaying = false
//                        setPlayerStatus(isPlaying)
                        break
                    }

                    if (isPlaying) {
                        sleep(50) //진행되는 시간 관리
                        mills += 50 //시간이 50mills만큼 증가했으니 seekBar에 progress값도 증가해야됨 -> View를 렌더링해주는 작업이죠
                        // -> Handler 혹은 runOnUiThread사용

                        //seekBar 렌더링
                        runOnUiThread {
                            //전체 mills값을 playTIme으로 나누고 퍼센테이지율로 환산하기 위하여 *100을 해주면 progress값이 나옴
                            binding.songProgressSb.progress = ((mills / playTime) * 100).toInt()
                        }

                        //노래 재생된 시간 표시 텍스트(SongStartTimeTv) 렌더링
                        if (mills % 1000 == 0f) {
                            runOnUiThread {
                                //뷰 렌더링 작업
                                binding.songStartTimeTv.text = String.format("%02d:%02d", second / 60, second % 60)
                            }
                            second++
                        }
                        //노래 남은 시간 표시 텍스트(SongStartTimeTv) 렌더링
                        if (mills % 1000 == 0f) {
                            runOnUiThread {
                                //뷰 렌더링 작업
                                binding.songEndTimeTv.text = String.format("%02d:%02d",
                                    playTime / 60, playTime % 60)
                            }
//                            second++
                        }
                    }
                }
            }catch(e: InterruptedException) {
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
    //[사용자가 포커스를 잃었을 때 음악이 중지]
    override fun onPause() {
        super.onPause()
        setPlayerStatus(false)
        //몇초까지 재생이 되었었는지를 반영
        //현재 밀리세컨드로 계산되고있는데 Song.kt는 초단위로 계산하기때문에 1000으로 나눠줌
        songs[nowPos].second = ((binding.songProgressSb.progress * songs[nowPos].playTime)/100)/1000

        //어플이 종료되어도 계속 플레이되려면 어딘가에 저장되어야겠죠
        //sharedPreferences -> 내부저장소에 데이터를 저장할 수 있게 해주는 것. 앱이 종료되었다가 다시 실행해도 데이터를 꺼내서 사용할 수 있게 해줌
        //간단한 설정값과 같은 데이터를 저장할 때 유용함 / 무거운 데이터나 중요한건 DB나 서버, 파일의 형태로 저정하겠지만
        //로그인 할 때 비밀번호같은 간단한 값은 sharedPreferences가 매우 유용함
                             //(sharedPreferences의 이름, private하게 자기 앱에서만 사용 가능할 수 있게 한다는 뜻)
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        //sharedPreferences는 데이터를 조작할 때 editor를 사용해야만 가능함
        val editor = sharedPreferences.edit() //에디터
//        editor.putString("title", song.title) //이것 song에 있는 객체의 갯수만큼 반복해주면 되겠죠
//        editor.putString("title", song.singer) ... 이렇게 -> 번거로워! JSON포맷으로 객체를 한번에 넣어줄 것임.
        //Song객체를 JSON으로 변환시켜줘야.. -> GSON 사용 -> 자바객체를 JSON으로, JSON을 자바 객체로 변환
        //GSON 사용하려면 라이브러리 추가. -> build.gradle의 dependencies
//        val songJson = gson.toJson(songs[nowPos]) //song객체를 JSON포맷으로 변환시켜줌
                //(sharedPreferences안에 저장된 데이터의 이름, 넣을 JSON)
        editor.putInt("songId", songs[nowPos].id)

        editor.apply()
    }

    //앱이 꺼질떄 자동으로 호출되는 onDestroy함수를 사용해서 로그가 잘 나오는지 확인
    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt() //쓰레드를 인터럽트
        //불필요한 리소스 방지 위해 미디어 플레이어가 갖고있던 리소스를 해제시켜주기
        mediaPlayer?.release()
        mediaPlayer = null //미디어 플레이어도 해제
    }
}