package com.example.junckcleaner.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.junckcleaner.R
import com.example.junckcleaner.annotations.MyAnnotations
import com.example.junckcleaner.broadcasts.BroadcastServiceStarter
import com.example.junckcleaner.permissions.MyPermissions
import com.example.junckcleaner.prefrences.AppPreferences
import com.example.junckcleaner.utils.Utils
import java.util.*


class ServiceSmartCharge : Service() {

    private var context: Context? = null
    private var timer: Timer? = null

    var preferences: AppPreferences? = null
    var utils: Utils? = null


    private var myPermissions: MyPermissions? = null


    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        startServiceOnOreo()
        preferences = AppPreferences(this)
        utils = Utils(this)
        myPermissions = MyPermissions(this)
        smartBroadcast();

        timer = Timer("Smart Charge")
        timer!!.schedule(updateTask, 0, 1000)
    }


    private val updateTask: TimerTask = object : TimerTask() {
        override fun run() {
            //check new Alert

        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager): String {
        val channelId = "channel_smart_charge" + this.getString(R.string.app_name)
        val channelName = "Foreground Service Smart Charge"
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN)
        channel.importance = NotificationManager.IMPORTANCE_NONE
        channel.lockscreenVisibility = Notification.VISIBILITY_SECRET
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    private fun startServiceOnOreo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channelId = createNotificationChannel(notificationManager)
            val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
                this, channelId
            ).setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("Smart Charge")
                .setContentText("We care about your battery")
                .setStyle(NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            val notification: Notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_logo)
//                .setContentInfo("We care about your battery")
                .setCategory(NotificationCompat.CATEGORY_SERVICE).build()
            startForeground(101, notification)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        timer!!.cancel()
        timer = null
        val intent = Intent()
        intent.action = MyAnnotations.START_SMART_SERVICE
        intent.setClass(this, BroadcastServiceStarter::class.java)
        this.sendBroadcast(intent)
    }

    private fun smartBroadcast() {
        val smartChargeChargerReceiver: BroadcastReceiver = SmartChargeChargerReceiver()
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val intentChargingFilter = IntentFilter(Intent.ACTION_POWER_CONNECTED)
        val intentChargingDisFilter = IntentFilter(Intent.ACTION_POWER_DISCONNECTED)

        try {
//            unregisterReceiver(smartChargeChargerReceiver)
            registerReceiver(smartChargeChargerReceiver, intentFilter)
            registerReceiver(smartChargeChargerReceiver, intentChargingFilter)
            registerReceiver(smartChargeChargerReceiver, intentChargingDisFilter)
        } catch (e: Exception) {

        }

    }


    class SmartChargeChargerReceiver : BroadcastReceiver() {
        var notified = false
        var chargerConnected = false
        var wifiManager: WifiManager? = null
        var bluetoothManager: BluetoothManager? = null
        var bluetoothAdapter: BluetoothAdapter? = null
        var wifi = 0
        var bluetooth = 0
        var brightness = 0

        override fun onReceive(context: Context, intent: Intent) {
            val preferences = AppPreferences(context)
            wifiManager = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            bluetoothManager =
                context.applicationContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager!!.adapter

            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val charge = intent.action

            val smartCharge = preferences.getBoolean(
                MyAnnotations.CHARGING_FINISHED_SWITCH,
                false
            )
            val finishedEnabled = preferences.getBoolean(
                MyAnnotations.CHARGING_FINISHED_SWITCH,
                false
            )
            when (charge) {
                Intent.ACTION_BATTERY_CHANGED -> {
                    Log.e("ServiceSmartCharge", "ACTION_BATTERY_CHANGED 1")


                    if (chargerConnected && finishedEnabled && !notified && level >= 100) {
                        batteryFull(context)
                        notified = true
                        Log.e("ServiceSmartCharge", "notified")

                    }
                }
                Intent.ACTION_POWER_CONNECTED -> {
                    chargerConnected = true
                    notified = false
                    Log.e("ServiceSmartCharge", "ACTION_POWER_CONNECTED")

                    if (chargerConnected && finishedEnabled && !notified && level >= 100) {
                        notified = true
                        batteryFull(context)

//                        notificationHelper.notify(
//                            1, notificationHelper.notifyBuilder(
//                                "SMART_CHANNEL_ID",
//                                "Smart Charge",
//                                "Battery is full",
//                                R.drawable.ic_smart_charge,
//                                url,
//                                AudioManager.STREAM_NOTIFICATION
//                            )
//                        )
                        Log.e("ServiceSmartCharge", "notified")

                    }


                    if (smartCharge) {
                        if (preferences.getBoolean(MyAnnotations.WIFI_SWITCH, false)) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                isWifiEnable(context, false);

                            } else {
                                wifi = if (wifiManager!!.isWifiEnabled) {
                                    1
                                } else {
                                    0
                                }
                                Log.e("ServiceSmartCharge", "wifi $wifi");
                                if (wifiManager!!.isWifiEnabled) {
                                    isWifiEnable(context, false);
                                    Log.e("ServiceSmartCharge", "wifi below M");
                                }
                            }
                        }
                        //brightness
                        if (preferences.getBoolean(MyAnnotations.BRIGHTNESS_SWITCH, false)) {
                            brightness = if (isAutoBightEnable(context)) 1 else 0
                            Log.e("brightness", brightness.toString())
                            if (isAutoBightEnable(context)) {
                                setAutoBightEnable(context, false);
                            }
                            Log.e("brightness", brightness.toString())

                        }
                        //bluetooth
                        if (preferences.getBoolean(MyAnnotations.BLUETOOTH_SWITCH, false)) {
                            try {

                                if (bluetoothAdapter != null) {
                                    bluetooth = if (bluetoothAdapter!!.isEnabled) 1 else 0
                                    if (bluetoothAdapter!!.isEnabled) {
                                        //on it
                                        Log.e("ServiceSmartCharge", "bluetooth off")
                                        setBluetoothOnOff(context, false)
                                    }
                                }
                            } catch (e: Exception) {

                            }
                        }
                        //sync
                        if (preferences.getBoolean(MyAnnotations.SYNCHRONIZED_SWITCH, false)) {
                            setSync(context)
                        }
                    }
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    try {

                        if (notified) {
                            val notificationManager =
                                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.cancel(1)

                        }
                    } catch (e: Exception) {
                        e.stackTrace
                    }

                    chargerConnected = false
                    notified = false
                    if (smartCharge) {
                        try {
                            if (preferences.getBoolean(MyAnnotations.WIFI_SWITCH, false)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    isWifiEnable(context, true) //
                                } else {
                                    if (wifi == 1) {
                                        if (!wifiManager!!.isWifiEnabled) {
                                            isWifiEnable(context, true) //
                                        }
                                    }
                                }
                                wifi = -1

                            }
                        } catch (e: Exception) {

                        }

                        if (preferences.getBoolean(MyAnnotations.BRIGHTNESS_SWITCH, false)) {
                            try {
                                //brightness was enabled
                                if (brightness == 1) {
                                    if (!isAutoBightEnable(context)) {
                                        setAutoBightEnable(context, true);
                                    }
                                }
                            } catch (e: Exception) {

                            }
                        }

                        if (preferences.getBoolean(MyAnnotations.BLUETOOTH_SWITCH, false)) {
                            try {

                                if (bluetoothAdapter != null) {
                                    //bluetooth = was enabled
                                    if (bluetooth == 1) {
                                        if (!bluetoothAdapter!!.isEnabled) {
                                            setBluetoothOnOff(context, true)
                                            bluetooth = -1
                                        }
                                    }
                                }

                            } catch (e: Exception) {
                            }
                        }
                        if (preferences.getBoolean(MyAnnotations.SYNCHRONIZED_SWITCH, false)) {
                            try {

                                setSync(context)
                            } catch (e: Exception) {

                            }
                        }
//                    }
                    }
                }
            }
        }

        private fun batteryFull(context: Context) {
            val url =
                Uri.parse("android.resource://" + context.packageName + "/" + R.raw.notification_sound)

            var channelId = ""
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                channelId = "channel_battery_full" + context.getString(R.string.app_name)
                val channelName = "full battery Service Smart Charge"
                val channel =
                    NotificationChannel(
                        channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                notificationManager.createNotificationChannel(channel)
            }

            val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
                context, channelId

            ).setSmallIcon(R.drawable.ic_smart_charge)
                .setContentTitle("Smart Charge")
                .setContentText("Your mobile battery is full")
                .setSound(url)
                .setStyle(NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            val notification: Notification = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.drawable.ic_smart_charge)
//                .setCategory(NotificationCompat.CATEGORY_REMINDER).build()
            notificationManager.notify(1, notificationBuilder.build())

        }


        private fun setBluetoothOnOff(context: Context?, onIt: Boolean) {
            when {
                bluetoothAdapter == null -> {
                    Toast.makeText(context, "Device does not supported ", Toast.LENGTH_SHORT).show()
                }
                onIt -> {
                    bluetoothAdapter!!.enable()
                }
                else -> {
                    bluetoothAdapter!!.disable()
                }
            }
        }

        private fun isAutoBightEnable(context: Context): Boolean {
            return try {
                Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE
                ) == 1
            } catch (e: SettingNotFoundException) {
                e.printStackTrace()
                false
            }
        }

        private fun setAutoBightEnable(context: Context, auto: Boolean) {
            try {
                Settings.System.putInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    if (auto) Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC else Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
                )
                Log.e("brightness", "set")

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("brightness", e.message.toString())

            }
        }


        private fun setSync(context: Context) {
            val intent = Intent(Settings.ACTION_SYNC_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //        intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent)
        }


        private fun isWifiEnable(context: Context, onOff: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.startActivity(
                    Intent(Settings.Panel.ACTION_WIFI)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } else {
                wifiManager!!.isWifiEnabled = onOff
            }
        }
    }

}



