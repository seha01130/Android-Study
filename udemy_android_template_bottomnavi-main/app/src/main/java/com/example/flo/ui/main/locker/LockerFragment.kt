package com.example.flo.ui.main.locker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentLockerBinding
import com.example.flo.ui.main.MainActivity
import com.example.flo.ui.signin.LoginActivity
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {

    lateinit var binding: FragmentLockerBinding
    private val information = arrayListOf("저장한 곡", "음악파일", "저장앨범")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        //View와 연결해주는 작업
        val lockerAdapter = LockerVPAdapter(this) //초기화
        binding.frLocLockerVP.adapter = lockerAdapter
        TabLayoutMediator(binding.frLocTabLayout, binding.frLocLockerVP) { //인자값은 연결할 TapLayout, ViewPager
            //TabLayout에 어떠한 텍스트가 들어갈지 적어주면 됨
                tab, position ->
            tab.text = information[position]
        }.attach() //attach로 TabLayout과 ViewPager를 붙여줌

        binding.frLocLogin.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        return binding.root
    }

    //여기 아래 부분 -> 로그인상태면 로그아웃 text보여주게 표시, 로그아웃상대면 로그인 text보여주게 표시

    override fun onStart() {
        super.onStart()

        initViews()
    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth" , AppCompatActivity.MODE_PRIVATE)
        //auth -> jwt를 저장했던 auth라는 이름으로 가져오고 (LoginActivity.kt의 saveJwt함수 참고)
        //activity? 이거는 fragment에서 사용할때 적는 방법이라고 생각하면 됨

        return spf!!.getInt("jwt", 0)
        //sharedPreference에서 가져온 값(LoginActivity.kt의 saveJwt함수 참고)이 없다면 0을 반환
        //jwt의 값을 가져올 수 있다면 jwt의 값에 따라 view의 text를 로그인으로 할지 로그아웃으로 할지
        //결정해서 view를 초기화시켜 -> initViews()
    }

    private fun initViews() {
        val jwt: Int = getJwt()

        if (jwt == 0){
            binding.frLocLogin.text = "로그인"

            binding.frLocLogin.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
        else{
            binding.frLocLogin.text = "로그아웃"

            binding.frLocLogin.setOnClickListener {
                logout()
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }

    private fun logout() { //jwt를 0인 상태, 즉 로그인이 안된 상태로 만들어주겠습니다
        val spf = activity?.getSharedPreferences("auth" , AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()

        editor.remove("jwt") //jwt라는 키값에 저장된 값을 없애준다
        editor.apply()
    }
}