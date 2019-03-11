package com.scrapper.his_scrapper

import android.content.ContextWrapper
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

interface IPreferencesRepo {
    fun isUserLoggedIn(): Boolean
}

class PreferencesRepo @Inject constructor() : IPreferencesRepo {
    override fun isUserLoggedIn(): Boolean {
        return true
    }
}

@Module
class MainModule(private val context: ContextWrapper) {

    @Provides
    fun provideContext(): ContextWrapper {
        return context
    }

    @Provides
    fun providePreferencesRepo(): IPreferencesRepo {
        return PreferencesRepo()
    }
}

@Component(modules = [MainModule::class])
interface MainComponent {
    fun context(): ContextWrapper

    fun inject(app: MainActivity)
}

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesRepo: IPreferencesRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DaggerMainComponent.builder().mainModule(MainModule(this)).build()

        val isUserLoggedIn = preferencesRepo.isUserLoggedIn()
        var textView = findViewById<TextView>(text.id)
        textView.text = isUserLoggedIn.toString()
    }
}
