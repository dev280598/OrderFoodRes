package com.example.oderfoodapp.di

import android.app.Application
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides

@Module
object AppModule {
    @Provides
    fun buildFirebaseDatabase() : FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    fun buildRequestManager(context: Application) : RequestManager {
        return Glide.with(context)
    }
}
