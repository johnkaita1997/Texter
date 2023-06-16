package com.tafatalkstudent.Shared

import android.content.Context
import com.tafatalkstudent.Retrofit.MyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //For databases, retrofit etc. Avoids recreating an object and re uses it.
    @Singleton
    @Provides  // Tells dagger we want to provide the dependeny for the function provideBookName() which is "Guiness Record Holders Book"
    @Named("bookname")
    fun providebookname() = "Guiness Record Holders Book"

    @Singleton
    @Provides
    @Named("classname")
    fun provideclassname() = "Class Four"


    @Provides
    @Singleton
    @Named("myapi")
    fun providemyapi() : MyApi {
        return MyApi()
    }

    @Provides
    @Singleton
    @Named("activitycontext")
    fun provideActivity(@ActivityContext context: Context) : Context = context

}