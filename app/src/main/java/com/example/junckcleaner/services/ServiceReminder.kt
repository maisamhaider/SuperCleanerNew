package com.example.junckcleaner.services

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.ViewModelProvider
import com.example.junckcleaner.R
import com.example.junckcleaner.annotations.MyAnnotations
import com.example.junckcleaner.permissions.MyPermissions
import com.example.junckcleaner.prefrences.AppPreferences
import com.example.junckcleaner.utils.Utils
import com.example.junckcleaner.viewmodel.ViewModelApps
import com.example.junckcleaner.views.activities.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


class ServiceReminder : Service(), LifecycleOwner {
    private val dispatcher = ServiceLifecycleDispatcher(this)
//    var fo: FileObserver? = null


    private var context: Context? = null
    private var timer: Timer? = null

    var preferences: AppPreferences? = null
    var utils: Utils? = null


    private var myPermissions: MyPermissions? = null

    var oldList: MutableList<String>? = null
    var allApps: MutableList<String>? = null

    private var viewModelApps: ViewModelApps? = null

    var stopGettingApp = false
    var launch = ""
    private var appPack = ""

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        startServiceOnOreo()
        preferences = AppPreferences(this)
        utils = Utils(this)
        myPermissions = MyPermissions(this)

        oldList = ArrayList()
        allApps = ArrayList()



        viewModelApps = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(ViewModelApps::class.java)
        viewModelApps.let { lifecycle::addObserver }

        //viewModel
        viewModelApps = ViewModelApps(this.application)

        timer = Timer("Smart Charge")
        timer!!.schedule(updateTask, 0, 4000)
    }


    private val updateTask: TimerTask = object : TimerTask() {
        override fun run() {
            //check new Alert
            if (preferences!!.getBoolean(MyAnnotations.DND_SWITCH, false)) {
                val simpleDateFormat = SimpleDateFormat("h:mm a")

                val startMilliTime = getTime(
                    simpleDateFormat.format(
                        preferences!!.getLong(
                            MyAnnotations.DND_START_TIME,
                            0
                        )
                    )
                )

                val endMilliTime = getTime(
                    simpleDateFormat.format(
                        preferences!!.getLong(
                            MyAnnotations.DND_END_TIME,
                            0
                        )
                    )
                )

                val current = getTime(simpleDateFormat.format(System.currentTimeMillis()))

                if (current in (startMilliTime + 1) until endMilliTime) {
                    //break time
                    Log.d("ServiceReminder", "Break")
                } else {
                    Log.d("ServiceReminder", "No break")
                    startAction();
                }
            } else {
                Log.d("ServiceReminder", "DND OFF")
                //dnd is off
                startAction();
            }
        }
    }

    fun getTime(string: String): Long {
        val sdf = SimpleDateFormat("h:mm a")
        try {
            val mDate = sdf.parse(string)
            return mDate.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0.toLong()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        dispatcher.onServicePreSuperOnStart()
        return START_STICKY
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager): String {
        val channelId = "channel_reminder";
        val channelName = "Foreground Service Reminder"
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
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
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Super Cleaner Reminder is enable")
                .setStyle(NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            val notification: Notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_logo)
                .setCategory(NotificationCompat.CATEGORY_SERVICE).build()
            startForeground(102, notification)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        timer!!.cancel()
        timer = null
        dispatcher.onServicePreSuperOnDestroy()


    }

    fun startAction() {
        Executors.newSingleThreadExecutor().execute {

            if (preferences!!.getString(
                    MyAnnotations.JUNK_REMINDER_FREQUENCY,
                    MyAnnotations.NEVER_REMIND
                ) != null &&
                preferences!!.getString(
                    MyAnnotations.JUNK_REMINDER_FREQUENCY,
                    MyAnnotations.NEVER_REMIND
                ).isNotEmpty()
                &&
                !preferences!!.getString(
                    MyAnnotations.JUNK_REMINDER_FREQUENCY,
                    MyAnnotations.NEVER_REMIND
                ).equals(MyAnnotations.NEVER_REMIND)
            ) {
//
                junkReminder()
            }

        }

        Executors.newSingleThreadExecutor().execute {
            if (preferences!!.getBoolean(MyAnnotations.PHONE_BOOST_REMINDER, false)) {
                phoneBoostReminder()
            }
        }

        Executors.newSingleThreadExecutor().execute {
            if (preferences!!.getBoolean(MyAnnotations.CPU_COOLER_REMINDER, false)) {
                cpuCoolerReminder()
            }
        }

        Executors.newSingleThreadExecutor().execute {
            if (preferences!!.getBoolean(MyAnnotations.BATTERY_SAVER, false)) {
                batterySaverReminder()
            }
        }

        Handler(Looper.getMainLooper()).post {
            if (preferences!!.getBoolean(MyAnnotations.REAL_TIME_PROTECTION, false)) {
                realTimeProtection()
            }
        }
    }

    private fun junkReminder() {
        val calendar: Calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis


        when (preferences!!.getString(
            MyAnnotations.JUNK_REMINDER_FREQUENCY,
            MyAnnotations.NEVER_REMIND
        )) {

            MyAnnotations.EVERY_DAY -> {
                if (preferences!!.getLong(
                        MyAnnotations.NOTIFICATIONS_JUNK_NEXT_TIME,
                        0
                    ) != 0.toLong()
                ) {

                    val nextNotification = preferences!!.getLong(
                        MyAnnotations.NOTIFICATIONS_JUNK_NEXT_TIME, 0
                    )

                    if (currentTime > nextNotification) {
                        Handler(Looper.getMainLooper()).post(Runnable {

                            notify(
                                context!!,
                                200,
                                "Junk Cleaner",
                                "Clean junk files",
                                R.drawable.ic_clean_up_icon,
                                activityPendingIntent(JunkActivity(), 200)
                            )

                            calendar.add(Calendar.HOUR, 24)
                            preferences!!.addLong(
                                MyAnnotations.NOTIFICATIONS_JUNK_NEXT_TIME,
                                calendar.timeInMillis
                            )
                        })
                    }

                } else {
                    // first notification
                    Handler(Looper.getMainLooper()).post(Runnable {
                        notify(
                            context!!,
                            200,
                            "Junk Cleaner",
                            "Clean junk files",
                            R.drawable.ic_clean_up_icon,
                            activityPendingIntent(JunkActivity(), 200)
                        )
                        calendar.add(Calendar.HOUR, 24)
                        preferences!!.addLong(
                            MyAnnotations.NOTIFICATIONS_JUNK_NEXT_TIME,
                            calendar.timeInMillis
                        )
                    })

                }
            }
            MyAnnotations.EVERY_3_DAY -> {
                if (preferences!!.getLong(
                        MyAnnotations.NOTIFICATIONS_JUNK_NEXT_TIME,
                        0
                    ) != 0.toLong()
                ) {

                    val nextNotification = preferences!!.getLong(
                        MyAnnotations.NOTIFICATIONS_JUNK_NEXT_TIME, 0
                    )

                    if (currentTime > nextNotification) {
                        Handler(Looper.getMainLooper()).post {
                            notify(
                                context!!,
                                200,
                                "Junk Cleaner",
                                "Clean junk files",
                                R.drawable.ic_clean_up_icon,
                                activityPendingIntent(JunkActivity(), 200)
                            )

                            calendar.add(Calendar.HOUR, 24 * 3)
                            preferences!!.addLong(
                                MyAnnotations.NOTIFICATIONS_JUNK_NEXT_TIME,
                                calendar.timeInMillis
                            )
                        }
                    }

                } else {
                    // first notification
                    Handler(Looper.getMainLooper()).post(Runnable {
                        notify(
                            context!!,
                            200,
                            "Junk Cleaner",
                            "Clean junk files",
                            R.drawable.ic_clean_up_icon,
                            activityPendingIntent(JunkActivity(), 200)
                        )
                        calendar.add(Calendar.HOUR, 24 * 3)
                        preferences!!.addLong(
                            MyAnnotations.NOTIFICATIONS_JUNK_NEXT_TIME,
                            calendar.timeInMillis
                        )
                    })

                }
            }
            MyAnnotations.EVERY_7_DAY -> {
                if (preferences!!.getLong(
                        MyAnnotations.NOTIFICATIONS_JUNK_NEXT_TIME,
                        0
                    ) != 0.toLong()
                ) {

                    val nextNotification = preferences!!.getLong(
                        MyAnnotations.NOTIFICATIONS_JUNK_NEXT_TIME, 0
                    )

                    if (currentTime > nextNotification) {
                        Handler(Looper.getMainLooper()).post(Runnable {

                            notify(
                                context!!,
                                200,
                                "Junk Cleaner",
                                "Clean junk files",
                                R.drawable.ic_clean_up_icon,
                                activityPendingIntent(JunkActivity(), 200)
                            )

                            calendar.add(Calendar.HOUR, 24 * 7)
                            preferences!!.addLong(
                                MyAnnotations.NOTIFICATIONS_JUNK_NEXT_TIME,
                                calendar.timeInMillis
                            )
                        })
                    }

                } else {
                    // first notification
                    Handler(Looper.getMainLooper()).post {
                        notify(
                            context!!,
                            200,
                            "Junk Cleaner",
                            "Clean junk files",
                            R.drawable.ic_clean_up_icon,
                            activityPendingIntent(JunkActivity(), 200)
                        )
                        calendar.add(Calendar.HOUR, 24 * 7)
                        preferences!!.addLong(
                            MyAnnotations.NOTIFICATIONS_JUNK_NEXT_TIME,
                            calendar.timeInMillis
                        )
                    }

                }
            }
        }
    }

    private fun phoneBoostReminder() {
        if (ramAbove70Percent("")) {
            Handler(Looper.getMainLooper()).post {

                val c: Calendar = Calendar.getInstance()

                if (preferences!!.getLong(MyAnnotations.NOTIFICATIONS_PHONE_BOOST_NEXT_TIME, 0)
                    != 0.toLong()
                ) {

                    if (c.timeInMillis > preferences!!.getLong(
                            MyAnnotations.NOTIFICATIONS_PHONE_BOOST_NEXT_TIME,
                            0
                        )
                    ) {
                        notify(
                            context!!,
                            201,
                            "Phone Booster",
                            "Boost your phone now",
                            R.drawable.ic_phone_boosted_rocket,
                            activityPendingIntent(BoostActivity(), 201)
                        )
                        c.add(Calendar.MINUTE, 30)
                        preferences!!.addLong(
                            MyAnnotations.NOTIFICATIONS_PHONE_BOOST_NEXT_TIME,
                            c.timeInMillis
                        )
                    }

                } else {
                    //first notification
                    notify(
                        context!!,
                        201,
                        "Phone Booster",
                        "Boost your phone now",
                        R.drawable.ic_phone_boosted_rocket,
                        activityPendingIntent(BoostActivity(), 201)
                    )

                    c.add(Calendar.MINUTE, 30)
                    preferences!!.addLong(
                        MyAnnotations.NOTIFICATIONS_PHONE_BOOST_NEXT_TIME,
                        c.timeInMillis
                    )

                }

            }
        }


    }

    private fun cpuCoolerReminder() {
        if (ramAbove70Percent("")) {
            Handler(Looper.getMainLooper()).post {

                val c: Calendar = Calendar.getInstance()

                if (preferences!!.getLong(MyAnnotations.NOTIFICATIONS_CPU_COOLER_NEXT_TIME, 0)
                    != 0.toLong()
                ) {

                    if (c.timeInMillis > preferences!!.getLong(
                            MyAnnotations.NOTIFICATIONS_CPU_COOLER_NEXT_TIME,
                            0
                        )
                    ) {
                        notify(
                            context!!,
                            202,
                            "Cpu Cooler",
                            "Cool your CPU now",
                            R.drawable.ic_cpu_cooler_icon_small,
                            activityPendingIntent(CpuCoolerActivity(), 202)
                        )
                        c.add(Calendar.MINUTE, 30)
                        preferences!!.addLong(
                            MyAnnotations.NOTIFICATIONS_CPU_COOLER_NEXT_TIME,
                            c.timeInMillis
                        )
                    }

                } else {
                    //first notification
                    notify(
                        context!!,
                        202,
                        "Cpu Cooler",
                        "Cool your CPU now",
                        R.drawable.ic_cpu_cooler_icon_small,
                        activityPendingIntent(CpuCoolerActivity(), 202)
                    )

                    c.add(Calendar.MINUTE, 30)
                    preferences!!.addLong(
                        MyAnnotations.NOTIFICATIONS_CPU_COOLER_NEXT_TIME,
                        c.timeInMillis
                    )

                }

            }
        }


    }

    private fun batterySaverReminder() {
        if (ramAbove70Percent("")) {
            Handler(Looper.getMainLooper()).post {

                val c: Calendar = Calendar.getInstance()

                if (preferences!!.getLong(MyAnnotations.NOTIFICATIONS_BATTERY_SAVER_NEXT_TIME, 0)
                    != 0.toLong()
                ) {

                    if (c.timeInMillis > preferences!!.getLong(
                            MyAnnotations.NOTIFICATIONS_BATTERY_SAVER_NEXT_TIME,
                            0
                        )
                    ) {
                        notify(
                            context!!,
                            203,
                            "Battery Saver",
                            "Save battery by killing unnecessary tasks",
                            R.drawable.ic_power_saving_icon,
                            activityPendingIntent(BatterySaverActivity(), 203)
                        )
                        c.add(Calendar.MINUTE, 30)
                        preferences!!.addLong(
                            MyAnnotations.NOTIFICATIONS_BATTERY_SAVER_NEXT_TIME,
                            c.timeInMillis
                        )
                    }

                } else {
                    //first notification
                    notify(
                        context!!,
                        203,
                        "Battery Saver",
                        "Save battery by killing unnecessary tasks",
                        R.drawable.ic_power_saving_icon,
                        activityPendingIntent(BatterySaverActivity(), 203)
                    )
                    c.add(Calendar.MINUTE, 30)
                    preferences!!.addLong(
                        MyAnnotations.NOTIFICATIONS_BATTERY_SAVER_NEXT_TIME,
                        c.timeInMillis
                    )

                }

            }
        }


    }

    private fun ramAbove70Percent(cleanerType: String): Boolean {
        val activityManager = context!!.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val percentage = utils!!.getPercentage(
            memoryInfo.totalMem.toFloat(),
            (memoryInfo.totalMem - memoryInfo.availMem).toFloat()
        )
        if (percentage > 75) {
            return true
        }

        return false
    }

    private fun realTimeProtection() {
        viewModelApps!!.getAllApps2(false).observe(this@ServiceReminder, { it ->
            if (it != null) {
                if (!stopGettingApp) {
                    allApps = it
                    if (oldList!!.isEmpty()) {
                        oldList = it
                    }

                    if (oldList!!.size > allApps!!.size) {
                        launch = "no"

                    } else if (oldList!!.size < allApps!!.size) {
                        stopGettingApp = true

                        if (allApps != null && allApps!!.isNotEmpty()) {
                            allApps!!.removeAll(oldList!!)

                            if (allApps!!.isNotEmpty()) {
                                allApps!!.forEach {

                                    oldList!!.add(it)
                                    appPack = it
                                    launch = "yes"
                                    stopGettingApp = false

                                }
                            }
                        }

                    } else {
                        // if equal don do anything
                    }


                }

            }
        })
        if (launch == "yes") {
            if (appIsDanger(appPack)) {
                dangerApp()
            }
        } else if (launch == "no") {
            removeFromAlert()
        }


    }

    private fun channel(): String {
        var channelId = ""
        val notificationManager =
            context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            channelId = "Channel Reminder"
            val channelName =
                "channel is created to remind preferences set by user in this application"
            val channel = NotificationChannel(
                channelId, channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channel)
        }
        return channelId
    }

    private fun notify(
        context: Context,
        id: Int,
        title: String,
        body: String,
        icon: Int,
        pendingIntent: PendingIntent
    ) {

        val url =
            Uri.parse("android.resource://" + context.packageName + "/" + R.raw.notification_sound)

        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            context, channel()
        ).setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(body)
            .setSound(url)
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(id, notificationBuilder.build())

    }

    private fun removeFromAlert() {
        if (allApps != null && allApps!!.isNotEmpty()) {
            oldList!!.removeAll(allApps!!)
            //user uninstalled an app
            //remove from database
            if (oldList!!.isNotEmpty()) {
                oldList!!.forEach {
                    launch = ""
                    oldList!!.remove(it)
                    val set: MutableSet<String>? =
                        preferences!!.getStringSet(MyAnnotations.APPS_SET)
                    if (set != null) {
                        if (set.isNotEmpty()) {
                            if (set.contains(it)) {
                                set.remove(it)
                                preferences!!.adStringSet(MyAnnotations.APPS_SET, set)
                            }
                        }
                    }
                }
            }
        }
    }


    private fun dangerApp() {
        launch = ""
        notify(
            context!!,
            205,
            "Real Time Protection",
            "App is taking dangerous permission",
            R.drawable.ic_antivirus_icon,
            activityPendingIntent(AntivirusActivity(), 205)
        )
        stopGettingApp = false

    }


    private fun activityPendingIntent(cls: Activity, id: Int): PendingIntent {
        val notifyIntent = Intent(this, cls::class.java)
        notifyIntent.putExtra(MyAnnotations.ID, id)

        notifyIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this,
                0,
                notifyIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getActivity(
                    this,
                    0,
                    notifyIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getActivity(
                    this,
                    0,
                    notifyIntent,
                    0
                )
            }
        }

        return pendingIntent

    }

    override fun getLifecycle() = dispatcher.lifecycle
    fun dangerousPermissions(): ArrayList<String> {
        val permissions = ArrayList<String>()
        permissions.add(Manifest.permission.CAMERA)
        permissions.add(Manifest.permission.RECORD_AUDIO)
        permissions.add(Manifest.permission.GET_ACCOUNTS)
        permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.SEND_SMS)
        permissions.add(Manifest.permission.READ_SMS)
        permissions.add(Manifest.permission.RECEIVE_SMS)
        permissions.add(Manifest.permission.PROCESS_OUTGOING_CALLS)
        permissions.add(Manifest.permission.READ_PHONE_NUMBERS)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        return permissions
    }

    private fun appIsDanger(namePackage: String): Boolean {
        val permissions: Array<String>
        try {
            permissions = packageManager.getPackageInfo(
                namePackage, PackageManager.GET_PERMISSIONS
            ).requestedPermissions

            if (permissions.isNotEmpty()) {
                for (permission in permissions) {
                    if (dangerousPermissions()!!.contains(permission)) {
                        return true
                    }
                }
            }

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false
        }
        return false;
    }

}



