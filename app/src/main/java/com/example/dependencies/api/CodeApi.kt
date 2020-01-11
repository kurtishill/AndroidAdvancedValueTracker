package com.example.dependencies.api

import io.reactivex.Single
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CodeApi {
    @POST("{code}")
    fun postNumber(@Path("code") code: Int): Single<SendCodeResponse>

    @PUT("{code}")
    fun updateNumber(@Path("code") code: Int,
                     @Query("value") value: Int): Single<SendCodeResponse>
}