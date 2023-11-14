package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

//viewPager에 이미지를 넣어줄건데 fragment라는 조각을 만들어서 그 안에 이미지 하나를 넣어줘서 여러 fragment가 좌우로 슬라이드 되도록 해야함
class BannerVPAdapter(fragment:Fragment) : FragmentStateAdapter(fragment) {
    //여러 fragment들을 viewPager에서 보여줘야하잖아요 그러기위해서는 여러개의 fragment들을 담아둘 공간이 필요 -> list사용
    //private로 선언한 이유 -> 이 클래스 내에서만 사용할 변수. 외부에서 데이터 변경하는걸 방지하기 위함
    private val fragmentlist : ArrayList<Fragment> = ArrayList() //ArrayList() 써서 초기화
    
    //이 클래스에서 연결된 viewPager에게 데이터를 전달할 때 데이터를 몇개를 전달할것이냐를 써주는 함수
    //override fun getItemCount(): Int = fragmentlist.size //아래와 같은거임. 더 간단하게 사용
    override fun getItemCount(): Int {
        return fragmentlist.size
    }

    //fragmentlist 안에 있는 item들 즉, fragment들을 생성해주는 함수
    override fun createFragment(position: Int): Fragment = fragmentlist[position]
    //즉, 0부터 시작해서 getItemCount된 만큼까지 반환해주는거임
    //getItemCount의 값이 4라면 0,1,2,3까지 실행이 됨
    
    //함수가 처음 실행될때 fragmentlist에는 아무것도 없겠죠 따라서 homeFragment에서 추가해줄 fragment를 써주기 위해서 사용
    fun addFragment(fragment:Fragment) {
        fragmentlist.add(fragment)
        notifyItemInserted(fragmentlist.size-1) //리스트안에 새로운 값이 추가가 됐을떄 viewPager에게 새로운 값이 추가됐다고 알려줘야해서
        //fragmentlist.size-1이라고 인자값 써주는거. 새로운값이 list에 추가되는값을 말해주는거
        //처음에 list에 들어가게되면 list[0]에 들어가게되고 list의 size는 1이 되겠죠. 따라서 fragmentlist.size-1을 해준거임
        //즉, viewPager에게 리스트에 새로운 값이 추가가 되었으니 이것도 추가해서 보여줘 라는 의미의 코드임
        
    }
}