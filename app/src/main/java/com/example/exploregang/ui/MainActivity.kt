package com.example.exploregang.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.exploregang.R
import com.example.exploregang.databinding.ActivityMainBinding
import com.example.exploregang.data.model.Actividad
import com.example.exploregang.util.Constants.ACTION_SHOW_ACTIVITY_DETAIL
import com.example.exploregang.util.Constants.collectionActivity

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navigationView.setOnNavigationItemSelectedListener { menuItem ->
            val navController = findNavController(R.id.nav_host_fragment_content_main)

            if (navController.currentDestination?.id != menuItem.itemId) {
                navController.navigate(menuItem.itemId)
            }

            true
        }
        if (intent?.action == ACTION_SHOW_ACTIVITY_DETAIL) {
            val activity = intent.getParcelableExtra<Actividad>(collectionActivity)
            if (activity != null) {
                // Aquí puedes abrir el fragmento ActivityDetailFragment y pasarle la actividad
                val bundle=Bundle()
                bundle.putParcelable(collectionActivity,activity)
                navController.navigate(R.id.activityDetailFragment,bundle)
            }
        }
    }

    /**
     * Rediríge al fragmento de detalles de actividad al pulsar sobre una notificación
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Verificar si se inició la actividad desde la notificación cuando llega un nuevo intent
        if (intent?.action == ACTION_SHOW_ACTIVITY_DETAIL) {
            val activity = intent.getParcelableExtra<Actividad>(collectionActivity)
            if (activity != null) {
                // Aquí puedes abrir el fragmento ActivityDetailFragment y pasarle la actividad
                val bundle=Bundle()
                bundle.putParcelable(collectionActivity,activity)
                navController.navigate(R.id.activityDetailFragment,bundle)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}