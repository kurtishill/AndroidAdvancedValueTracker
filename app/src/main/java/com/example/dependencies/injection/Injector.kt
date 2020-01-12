package com.example.dependencies.injection

object Injector {
    var appComponent: AppComponent = DaggerAppComponent
        .builder()
        .appModule(AppModule())
        .build()
}