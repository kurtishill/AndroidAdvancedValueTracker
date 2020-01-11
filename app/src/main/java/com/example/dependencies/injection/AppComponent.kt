package com.example.dependencies.injection

import com.example.dependencies.provider.CodeProvider
import com.example.dependencies.viewmodel.MainActivityViewModel
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(mainActivityViewModel: MainActivityViewModel)
    fun inject(codeProvider: CodeProvider)
}