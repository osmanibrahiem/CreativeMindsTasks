package com.cems.devtask.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.cems.devtask.helper.LocaleManager
import com.cems.devtask.repository.Repository
import com.cems.devtask.repository.local.ReposDatabase
import com.cems.devtask.ui.viewmodel.MainViewModel

val mainModule = module {

    single { Room.databaseBuilder(get(), ReposDatabase::class.java, "repositories_db").build() }
    single { get<ReposDatabase>().reposDao() }
    single { get<ReposDatabase>().remoteKeysDao() }


    single<Repository> { Repository(get(), get()) }

    viewModel { MainViewModel(LocaleManager.setLocale(androidContext()), get()) }

}