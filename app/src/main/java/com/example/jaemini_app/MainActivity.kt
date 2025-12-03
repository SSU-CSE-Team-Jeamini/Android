package com.example.jaemini_app

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController

class MainActivity : AppCompatActivity() {

    companion object {
        var username: String? = null

        // ✅ 캐시 추가
        var cachedProfile: ProfileData? = null
        var cacheTimestamp: Long = 0

        // ✅ 캐시 유효성 확인 (1분)
        fun isCacheValid(): Boolean {
            val now = System.currentTimeMillis()
            return cachedProfile != null && (now - cacheTimestamp) < 60000
        }
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = intent.getStringExtra("username")

        setupNavigation()
        setupBottomNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController
    }

    private fun setupBottomNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.nav_home)
        val navRanking = findViewById<LinearLayout>(R.id.nav_ranking)
        val navMypage = findViewById<LinearLayout>(R.id.nav_mypage)

        navHome.setOnClickListener {
            if (navController.currentDestination?.id != R.id.homeFragment) {
                navController.navigate(R.id.homeFragment)
            }
        }

        navRanking.setOnClickListener {
            if (navController.currentDestination?.id != R.id.rankingFragment) {
                navController.navigate(R.id.rankingFragment)
            }
        }

        navMypage.setOnClickListener {
            if (navController.currentDestination?.id != R.id.nav_mypage) {
                navController.navigate(R.id.profileFragment)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}