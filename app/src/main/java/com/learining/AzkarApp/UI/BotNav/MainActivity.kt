package com.learining.AzkarApp.UI.BotNav

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.learining.AzkarApp.R
import com.learining.AzkarApp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SETUP TOOLBAR
        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.mainToolbar.setTitleTextAppearance(
            this,
            com.google.android.material.R.style.TextAppearance_Material3_BodySmall
        )

        // SETUP NAVIGATION
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as? NavHostFragment

        navHostFragment?.let {
            navController = it.navController

            // CONNECT UI WITH NAVIGATION
            NavigationUI.setupActionBarWithNavController(
                this,
                navController,
                binding.mainDrawerLayout
            )
            binding.navView.setupWithNavController(navController)
            binding.bottomNav.setupWithNavController(navController)

            // 4. Handle Bottom Nav logic with animation
            binding.bottomNav.setOnItemSelectedListener { item ->
                val itemView = binding.bottomNav.findViewById<android.view.View>(item.itemId)
                itemView?.let { view ->
                    view.animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(100)
                        .withEndAction {
                            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                        }.start()
                }

                // Allow the standard navigation to happen
                NavigationUI.onNavDestinationSelected(item, navController)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (::navController.isInitialized) {
            NavigationUI.navigateUp(navController, binding.mainDrawerLayout)
        } else {
            super.onSupportNavigateUp()
        }
    }
}
