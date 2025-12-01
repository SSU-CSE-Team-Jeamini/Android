package com.example.jaemini_app

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController

class MainActivity : AppCompatActivity() {

    // username 변수 추가! (companion object 사용)
    companion object {
        var username: String? = null
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Intent로 전달받은 username 저장
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
            if (navController.currentDestination?.id != R.id.profileFragment) {
                navController.navigate(R.id.profileFragment)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}