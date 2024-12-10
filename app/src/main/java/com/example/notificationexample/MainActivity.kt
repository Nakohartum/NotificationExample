package com.example.notificationexample

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notificationexample.ui.theme.NotificationExampleTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Calendar

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotificationExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DateTimePicker(
                        modifier = Modifier.padding(innerPadding)
                    ) { }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(modifier: Modifier, onTimePicked: (Long) -> Unit){
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val permission = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()){

    }
    LaunchedEffect(null) {
        permission.launch(
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.SCHEDULE_EXACT_ALARM,
                Manifest.permission.SET_ALARM
            )
        )
    }
    LazyColumn(
        modifier = modifier
    ) {
        item {
            DatePicker(
                state = datePickerState
            )
            TimePicker(
                state = timePickerState
            )

            Button(
                onClick = {
                    createNotificationChannel(context)
                    val pickedTime = datePickerState.selectedDateMillis!! + (timePickerState.hour * 60 + timePickerState.minute) * 60 * 1000
                    val date = Instant.ofEpochMilli(pickedTime).atZone(ZoneId.systemDefault()).toLocalDate()
                    val dateTime = date.atTime(timePickerState.hour, timePickerState.minute)

                    scheduleNotification(context, dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                }
            ) {
                Text("Set")
            }
        }
    }
}

private fun createNotificationChannel(context: Context){
    val channel = NotificationChannel("MainChannel", "Notifications", NotificationManager.IMPORTANCE_HIGH).apply {
        description = "Test"
    }
    val manager = context.getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(channel)
}

@RequiresApi(Build.VERSION_CODES.S)
private fun scheduleNotification(context: Context, triggerTime: Long){
    val intent = Intent(context, NotificationReciever::class.java).apply {
        putExtra("test", "test")
    }
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    val alarmManager = context.getSystemService(AlarmManager::class.java)
    if (alarmManager.canScheduleExactAlarms()){
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }

}