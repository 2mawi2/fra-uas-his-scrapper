package com.scrapper.his_scrapper.application

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.scrapper.his_scrapper.LoginActivity
import com.scrapper.his_scrapper.MainActivity
import com.scrapper.his_scrapper.data.local.*
import com.scrapper.his_scrapper.data.remote.HisService
import com.scrapper.his_scrapper.data.remote.IHisService
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainModule {

    private val context: Context
    private val db: ScrapperDatabase

    constructor(context: Context) {
        this.context = context
        this.db = Room.databaseBuilder(
            context.applicationContext,
            ScrapperDatabase::class.java,
            "Scrapper_database"
        ).build()
    }

    @Provides
    fun provideContext(): Context = context

    @Provides
    fun providePreferencesRepo(encryptor: IEncryptor, decryptor: IDecryptor): IPreferencesRepo {
        return PreferencesRepo(context, decryptor, encryptor)
    }

    @Provides
    fun providesHisService(): IHisService = HisService()

    @Provides
    fun provideEncryptor(): IEncryptor = Encryptor()

    @Provides
    fun provideDecryptor(): IDecryptor = Decryptor()

    @Singleton
    @Provides
    fun providesScrapperDatabase(): ScrapperDatabase = db

    @Singleton
    @Provides
    fun provideGradeDao(db: ScrapperDatabase): GradeDao = db.gradeDao()

    @Singleton
    @Provides
    fun provideGradeRepo(gradeDao: GradeDao): IGradeRepo = GradeRepo(gradeDao)
}

@Singleton
@Component(modules = [MainModule::class])
interface MainComponent {
    fun context(): Context

    fun inject(app: MainActivity)

    fun inject(app: LoginActivity)

    fun productDao(): GradeDao

    fun demoDatabase(): ScrapperDatabase

    fun productRepository(): GradeRepo
}