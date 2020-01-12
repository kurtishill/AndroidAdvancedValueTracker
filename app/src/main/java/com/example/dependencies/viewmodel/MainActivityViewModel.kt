package com.example.dependencies.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dependencies.extensions.addTo
import com.example.dependencies.injection.Injector
import com.example.dependencies.provider.CodeProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainActivityViewModel : ViewModel() {

    @Inject lateinit var codeProvider: CodeProvider

    private val compositeDisposable = CompositeDisposable()

    val number = MutableLiveData<Int>()
    val error = MutableLiveData<String?>()
    val loading = MutableLiveData<Boolean>()
    val success = MutableLiveData<Boolean?>()

    init {
        Injector.appComponent.inject(this)
    }

    fun postNumber(code: Int) {
        error.value = null
        loading.value = true
        success.value = null
        codeProvider.postNumber(code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                loading.value = false
                if (it.success)
                    number.value = it.value
                else
                    error.value = it.message
            }, {
                loading.value = false
                error.value = it.localizedMessage ?: "Error"
                number.value = null
            }).addTo(compositeDisposable)
    }

    fun updateNumber(code: Int, value: Int) {
        error.value = null
        loading.value = true
        success.value = null
        codeProvider.updateNumber(code, value)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                loading.value = false
                if (it.success) {
                    number.value = it.value
                    success.value = true
                } else
                    error.value = it.message
            }, {
                loading.value = false
                error.value = it.localizedMessage ?: "Error"
                number.value = null
            }).addTo(compositeDisposable)
    }
}