package com.example.groceryoptimizer.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.groceryoptimizer.databinding.ActivityMainBinding
import com.example.groceryoptimizer.R

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        setSupportActionBar(b.toolbar)
        b.toolbar.setNavigationOnClickListener {
            b.drawerLayout.openDrawer(GravityCompat.START)
        }

        b.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_add -> startActivity(Intent(this, AddItemActivity::class.java))
                R.id.nav_list -> startActivity(Intent(this, ViewListActivity::class.java))
                R.id.nav_optimize -> startActivity(Intent(this, OptimizeActivity::class.java))
            }
            b.drawerLayout.closeDrawers()
            true
        }

        b.btnAdd.setOnClickListener { startActivity(Intent(this, AddItemActivity::class.java)) }
        b.btnList.setOnClickListener { startActivity(Intent(this, ViewListActivity::class.java)) }
        b.btnOptimize.setOnClickListener { startActivity(Intent(this, OptimizeActivity::class.java)) }
    }
}
