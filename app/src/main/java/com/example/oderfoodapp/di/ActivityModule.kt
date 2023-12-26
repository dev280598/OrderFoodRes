package com.example.oderfoodapp.di

import com.example.oderfoodapp.view.FoodDetailActivity
import com.example.oderfoodapp.view.FoodListActivity
import com.example.oderfoodapp.view.HomeActivity
import com.example.oderfoodapp.view.MainActivity
import com.example.oderfoodapp.view.OrderStatusActivity
import com.example.oderfoodapp.view.ShowComment_Activity
import com.example.oderfoodapp.view.SignInActivity
import com.example.oderfoodapp.view.SignUpActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector
    @ActivityScoped
    abstract fun mainAct(): MainActivity

    @ContributesAndroidInjector
    @ActivityScoped
    abstract fun homeAct(): HomeActivity

    @ContributesAndroidInjector
    @ActivityScoped
    abstract fun foodDetailAct(): FoodDetailActivity

    @ContributesAndroidInjector
    @ActivityScoped
    abstract fun foodListAct(): FoodListActivity

    @ContributesAndroidInjector
    @ActivityScoped
    abstract fun orderSttAct(): OrderStatusActivity

    @ContributesAndroidInjector
    @ActivityScoped
    abstract fun commentAct(): ShowComment_Activity

    @ContributesAndroidInjector
    @ActivityScoped
    abstract fun signInAct(): SignInActivity

    @ContributesAndroidInjector
    @ActivityScoped
    abstract fun signUpAct(): SignUpActivity
}