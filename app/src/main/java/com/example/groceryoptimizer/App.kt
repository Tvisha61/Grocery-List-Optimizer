package com.example.groceryoptimizer

import android.app.Application
import com.example.groceryoptimizer.db.InMemoryDb

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        InMemoryDb.init(this)
    }
}
