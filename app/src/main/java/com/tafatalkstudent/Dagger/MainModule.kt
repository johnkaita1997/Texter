package com.tafatalkstudent.Shared

import android.content.Context
import com.tafatalkstudent.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Named


@Module
@InstallIn(ActivityComponent::class)
object MainModule {

    //For databases, retrofit etc. Avoids recreating an object and re uses it.
    @ActivityScoped
    @Provides  // Tells dagger we want to provide the dependeny for the function provideBookName() which is "Guiness Record Holders Book"
    @Named("carname")
    fun providecarname(@ApplicationContext context : Context, @Named("bookname") bookname: String) = "${context.getString(R.string.carname)} - ${bookname}"

}