package com.scrapper.his_scrapper

import android.content.Context
import android.content.ContextWrapper
import dagger.Component
import dagger.Module
import dagger.Provides

@Module
class MainModule(private val context: Context) {

    @Provides
    fun provideContext(): Context {
        return context
    }

    @Provides
    fun providePreferencesRepo(): IPreferencesRepo {
        return PreferencesRepo(context)
    }

    @Provides
    fun providesHisService(): IHisService {
        return HisService()
    }
}

@Component(modules = [MainModule::class])
interface MainComponent {
    fun context(): Context

    fun inject(app: MainActivity)

    fun inject(app: LoginActivity)
}