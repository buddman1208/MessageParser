package com.example.messageparser

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface SendService {
    @POST
    public fun postToServer(@Url url : String, @Body model : PostModel) : Call<ResponseBody>
}