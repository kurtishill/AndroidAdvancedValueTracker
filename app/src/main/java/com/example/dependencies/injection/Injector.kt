package com.example.dependencies.injection

object Injector {
    lateinit var appComponent: AppComponent

    fun init() {
        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule())
            .build()
    }
}