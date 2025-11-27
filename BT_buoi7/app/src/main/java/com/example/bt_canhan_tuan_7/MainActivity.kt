package com.example.bt_canhan_tuan_7

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.bt_canhan_tuan_7.databinding.ActivityMainBinding
import com.example.bt_canhan_tuan_7.ui.fragment.AppointmentListFragment
import com.example.bt_canhan_tuan_7.ui.notification.NotificationWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appointmentFragment: AppointmentListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // View Binding
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            // Setup AppBar
            setSupportActionBar(binding.topAppBar as androidx.appcompat.widget.Toolbar)

            // Initialize Fragment
            if (savedInstanceState == null) {
                appointmentFragment = AppointmentListFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, appointmentFragment)
                    .commit()
            } else {
                appointmentFragment = supportFragmentManager
                    .findFragmentById(R.id.fragmentContainer) as? AppointmentListFragment
                    ?: AppointmentListFragment()
            }

            // Setup FAB
            binding.fabAddAppointment.setOnClickListener {
                try {
                    appointmentFragment.showAddAppointmentDialog()
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error showing add dialog", e)
                }
            }

            // Schedule notification worker
            try {
                scheduleNotificationWorker()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error scheduling notification worker", e)
            }

        } catch (e: Exception) {
            Log.e("MainActivity", "Fatal error in MainActivity.onCreate", e)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_filter -> {
                try {
                    appointmentFragment.showFilterDialog()
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error showing filter dialog", e)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun scheduleNotificationWorker() {
        val notificationWork = PeriodicWorkRequestBuilder<NotificationWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "appointment_notification",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            notificationWork
        )
    }
}