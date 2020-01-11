package com.example.dependencies.provider

import android.util.Log
import com.example.dependencies.api.CodeApi
import com.example.dependencies.api.SendCodeResponse
import com.example.dependencies.extensions.addTo
import com.example.dependencies.injection.Injector
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CodeProvider @Inject constructor(
    private val codeApi: CodeApi
) {
    val valueSource: PublishRelay<SendCodeResponse> = PublishRelay.create()
    private val compositeDisposable = CompositeDisposable()

    init {
        Injector.appComponent.inject(this)
    }

    fun postNumber(code: Int) {
        codeApi.postNumber(code)
            .subscribeOn(Schedulers.io())
            .subscribe({
                valueSource.accept(it)
            }, { throwable ->
                Log.d("CodeProvider", throwable.localizedMessage ?: "Error")
            }).addTo(compositeDisposable)
    }

    fun updateNumber(code: Int, value: Int) {
        codeApi.updateNumber(code, value)
            .subscribeOn(Schedulers.io())
            .subscribe({
                valueSource.accept(it)
            }, { throwable ->
                Log.d("CodeProvider", throwable.localizedMessage ?: "Error")
            }).addTo(compositeDisposable)
    }
}