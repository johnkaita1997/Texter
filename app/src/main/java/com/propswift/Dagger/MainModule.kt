package com.propswift.Shared

import android.app.Activity
import android.content.Context
import com.propswift.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(ActivityComponent::class)
object MainModule {

    //For databases, retrofit etc. Avoids recreating an object and re uses it.
    @ActivityScoped
    @Provides  // Tells dagger we want to provide the dependeny for the function provideBookName() which is "Guiness Record Holders Book"
    @Named("carname")
    fun providecarname(@ApplicationContext context : Context, @Named("bookname") bookname: String) = "${context.getString(R.string.carname)} - ${bookname}"

}