package com.example.flo

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast

object CustomToast {
    fun showToast(context: Context, message: String) {

        val inflater = LayoutInflater.from(context)
        var customToast = inflater.inflate(R.layout.custom_toast, null) // 팝업 시킬 뷰 생성

//        customToast.setBackgroundResource(android.R.drawable.toast_frame)

        var image: ImageView = customToast.findViewById(R.id.customToastImage)
        image.setImageResource(R.drawable.ic_my_like_on)

        var t = Toast(context)
        t.setGravity(Gravity.CENTER, 0, -150) //toast 위치 설정
        t.duration = Toast.LENGTH_SHORT
        t.view = customToast
        t.show()
    }
}