package com.cems.devtask.ui.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.cems.devtask.repository.Repository
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import java.util.*

class RefreshDataService : Service() {

    private val repository: Repository by inject()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val timer = Timer()
        val hourlyTask: TimerTask = object : TimerTask() {
            override fun run() {
                repository.refreshRepositories(CompositeDisposable()) {}
            }
        }
        timer.schedule(hourlyTask, 0L, 1000 * 60 * 60)
        return START_STICKY
    }
}