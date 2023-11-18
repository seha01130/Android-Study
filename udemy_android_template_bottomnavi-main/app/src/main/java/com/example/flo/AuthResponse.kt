package com.example.flo

import com.google.gson.annotations.SerializedName


data class AuthResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Result?
    //로그인 API와 회원가입 API를 같은 데이터클래스로 응답값을 받고있어요. 그렇기때문에 Result에 null처리를 해야
    //회원가입 API를 사용했을 때 알아서 null처리가 되기때문에 같이 사용할 수 있어요
)

data class Result(
    @SerializedName("userIdx") val userIdx: Int,
    @SerializedName("jwt") val jwt: String
)
