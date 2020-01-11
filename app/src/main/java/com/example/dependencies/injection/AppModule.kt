package com.example.dependencies.injection

import com.example.dependencies.api.ApiFactory
import com.example.dependencies.api.CodeApi
import com.example.dependencies.provider.CodeProvider
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    private val BASE_URL: String = "https://i3xszi9pil.execute-api.us-west-2.amazonaws.com/dev/code/"

    @Provides
    fun providesCodeApi() = ApiFactory.create<CodeApi>(BASE_URL)

    @Provides
    fun providesCodeProvider(codeApi: CodeApi) = CodeProvider(codeApi)
}