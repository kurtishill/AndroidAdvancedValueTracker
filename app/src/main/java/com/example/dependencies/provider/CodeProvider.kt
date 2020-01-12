package com.example.dependencies.provider

import com.example.dependencies.api.CodeApi
import com.example.dependencies.api.SendCodeResponse
import com.example.dependencies.injection.Injector
import io.reactivex.Single
import javax.inject.Inject

class CodeProvider @Inject constructor(
    private val codeApi: CodeApi
) {
    init {
        Injector.appComponent.inject(this)
    }

    fun postNumber(code: Int): Single<SendCodeResponse> = codeApi.postNumber(code)

    fun updateNumber(code: Int, value: Int): Single<SendCodeResponse> =
        codeApi.updateNumber(code, value)
}