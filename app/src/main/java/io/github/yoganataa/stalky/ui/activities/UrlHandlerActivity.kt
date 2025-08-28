package io.github.yoganataa.stalky.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity // Changed from android.app.Activity
import androidx.core.app.TaskStackBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.yoganataa.stalky.MainActivity

@AndroidEntryPoint
class UrlHandlerActivity : ComponentActivity() { // Changed from Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle the incoming intent
        intent?.data?.let { uri ->
            // Process the URL and pass it to the main activity
            val mainIntent = Intent(this, MainActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                data = uri
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            
            // Create a task stack to ensure proper navigation
            TaskStackBuilder.create(this).run {
                addNextIntentWithParentStack(mainIntent)
                startActivities()
            }
        }
        
        // Finish this activity
        finish()
    }
}