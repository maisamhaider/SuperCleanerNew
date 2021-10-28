package com.example.junckcleaner.services

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.*
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.ViewModelProvider
import com.example.junckcleaner.R
import com.example.junckcleaner.annotations.MyAnnotations
import com.example.junckcleaner.broadcasts.BroadcastServiceStarter
import com.example.junckcleaner.permissions.MyPermissions
import com.example.junckcleaner.prefrences.AppPreferences
import com.example.junckcleaner.utils.Utils
import com.example.junckcleaner.viewmodel.ViewModelApps
import com.example.junckcleaner.views.activities.ActivityServiceLocker
import java.util.*
import kotlin.collections.ArrayList


class AppLockService : Service(), LifecycleOwner, View.OnClickListener {
    private val dispatcher = ServiceLifecycleDispatcher(this)

    companion object {

        @JvmStatic
        var screenOff: Boolean = false

        @JvmStatic
        var screenOn: Boolean = false

        @JvmStatic
        var isLock: Boolean = false

        @JvmStatic
        var setLockAfterThisTime: Long = 0

        @JvmStatic
        var lastTimeScreenON: Long = 0
    }

    var appPack = ""

    var password = ""
    var app = ""

    var i = 0

    private var dialog: Dialog? = null
    private var context: Context? = null
    private var timer: Timer? = null
    var imageView: ImageView? = null
    var preferences: AppPreferences? = null
    var utils: Utils? = null


    var set: MutableSet<String>? = null

    private var windowManager: WindowManager? = null
    private var myPermissions: MyPermissions? = null

    var viewOverly: View? = null
    private var viewModelApps: ViewModelApps? = null
    var oldList: MutableList<String>? = null
    var allApps: MutableList<String>? = null
    var stopGettingApp = false
    var launch = ""

    //dialog
    var textViewAppName: TextView? = null
    var textViewYes: TextView? = null
    var textViewNo: TextView? = null
    var imageViewAppIcon: ImageView? = null


    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        startServiceOnOreo()
        preferences = AppPreferences(this)
        utils = Utils(this)
        myPermissions = MyPermissions(this)

        oldList = ArrayList()
        allApps = ArrayList()

        val mReceiver: BroadcastReceiver = ScreenReceiver()
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(mReceiver, filter)


        viewModelApps = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(ViewModelApps::class.java)
        viewModelApps.let { lifecycle::addObserver }


        //viewModel
        viewModelApps = ViewModelApps(this.application)


        timer = Timer("AppLockService")
        timer!!.schedule(updateTask, 0, 100)
        imageView = ImageView(this)
        imageView!!.visibility = View.GONE
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        imageView = ImageView(this)
        imageView!!.visibility = View.GONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
            layoutParams.gravity = Gravity.TOP or Gravity.CENTER
            layoutParams.x = applicationContext.resources.displayMetrics.widthPixels / 2
            layoutParams.y = applicationContext.resources.displayMetrics.heightPixels / 2
            windowManager!!.addView(imageView, layoutParams)
        } else {
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
            params.gravity = Gravity.TOP or Gravity.CENTER
            params.x = applicationContext.resources.displayMetrics.widthPixels / 2
            params.y = applicationContext.resources.displayMetrics.heightPixels / 2
            windowManager!!.addView(imageView, params)
        }
    }


    private val updateTask: TimerTask = object : TimerTask() {
        override fun run() {
            //check new Alert

            Handler(Looper.getMainLooper()).post {
                newAlert()
            }
            // check app is locked
            if (!isLock) {
                if (isConcernedAppIsInForeground()) {
                    Handler(Looper.getMainLooper()).post {
                        val c = preferences!!.getString(MyAnnotations.CURRENT_APP, "")
                        val p = preferences!!.getString(MyAnnotations.PREVIOUS_APP, "")

                        if (preferences!!.getString(MyAnnotations.CURRENT_APP, "") !=
                            preferences!!.getString(MyAnnotations.PREVIOUS_APP, "")
                        ) {
                            preferences!!.addString(
                                MyAnnotations.PREVIOUS_APP,
                                preferences!!.getString(MyAnnotations.CURRENT_APP, "")
                            )
                            showDialog()


                        } else {
                            if (screenOff) {
                                val calendar: Calendar = Calendar.getInstance()
                                if (preferences!!.getString(
                                        MyAnnotations.AUTOMATICALLY_LOCK,
                                        MyAnnotations.AFTER_SCREEN_OFF
                                    ).equals(MyAnnotations.AFTER_SCREEN_OFF)
                                ) {
                                    showDialog()
                                    screenOff = false
                                } else if (setLockAfterThisTime < calendar.timeInMillis) {

                                    showDialog()
                                    screenOff = false

                                } else {
                                    if (screenOn) {
                                        screenOff = false
                                    }
                                }

                            }
                        }
                    }
                } else {
                    preferences!!.addString(MyAnnotations.PREVIOUS_APP, packageName)
                }
            } else {
                if (imageView != null) {
                }
            }
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        dispatcher.onServicePreSuperOnStart()
        set = preferences!!.getStringSet(MyAnnotations.APPS_SET)


        return START_STICKY
    }

    fun newAlert() {
        if (preferences!!.getBoolean(MyAnnotations.NEW_ALERT, false)) {

            viewModelApps!!.getAllApps2(false).observe(this@AppLockService, { it ->
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
                launchAlertDialog()

            } else if (launch == "no") {
                removeFromAlert()
            }
        }


    }

    private fun launchAlertDialog() {
        launch = ""

        val layoutInflater = LayoutInflater.from(this)
        viewOverly = layoutInflater.inflate(
            R.layout.activity_alert_dialog,
            null,
            false
        )


        textViewAppName = viewOverly!!.findViewById(R.id.textView_app_name)
        textViewYes = viewOverly!!.findViewById(R.id.textView_yes)
        textViewNo = viewOverly!!.findViewById(R.id.textView_no)
        imageViewAppIcon = viewOverly!!.findViewById(R.id.imageView_app_icon)

        val utils = Utils(this)
        textViewYes!!.setOnClickListener(this)
        textViewNo!!.setOnClickListener(this)

        textViewAppName!!.text = utils.appInfo(appPack, MyAnnotations.APP_NAME)
        imageViewAppIcon!!.setImageDrawable(
            utils.appInfo<Any>(appPack, MyAnnotations.APP_ICON) as Drawable
        )

        dialog = Dialog(context!!, R.style.Theme_Design_NoActionBar)
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.setCancelable(true)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog!!.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        } else {
            dialog!!.window!!.setType(WindowManager.LayoutParams.TYPE_PHONE)
        }
        dialog!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            320
        )
        dialog!!.setContentView(viewOverly!!)

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.setGravity(Gravity.CENTER)
        dialog!!.show()
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

    fun isConcernedAppIsInForeground(): Boolean {
        try {
            set = mutableSetOf()
            set = preferences!!.getStringSet(MyAnnotations.APPS_SET)


            var packageName = ""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val usage =
                    context!!.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

                val time = System.currentTimeMillis()
                val stats = usage.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    0, time
                )
                if (stats != null) {
                    val runningTask: SortedMap<Long, UsageStats> = TreeMap()
                    for (usageStats in stats) {
                        runningTask[usageStats.lastTimeUsed] = usageStats
                    }
                    packageName = if (runningTask.isEmpty()) {
                        ""
                    } else {
                        runningTask[runningTask.lastKey()]!!.packageName
                    }
                }
            } else {
                val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
                manager.getRunningTasks(4)
                packageName = manager.runningAppProcesses[0].processName
            }
            if (set != null) {
                if (set!!.contains(packageName)) {
                    preferences!!.addString(MyAnnotations.CURRENT_APP, packageName)
                    return true
                }
            }
        } catch (e: Exception) {
            return false
        }


        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager): String {
        val channelId = "channel" + this.getString(R.string.app_name)
        val channelName = "Foreground Service"
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
                .setContentTitle(this.getString(R.string.app_name))
                .setStyle(NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            val notification: Notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentInfo("")
                .setCategory(NotificationCompat.CATEGORY_SERVICE).build()
            startForeground(100, notification)
        }
    }


    private fun showDialog() {
        isLock = true
        val i = Intent(this@AppLockService, ActivityServiceLocker::class.java)
        i.putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, "")
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }


    override fun onDestroy() {
        super.onDestroy()
        timer!!.cancel()
        timer = null
        if (imageView != null) {
            windowManager!!.removeView(imageView)
        }

        password = ""
        dispatcher.onServicePreSuperOnDestroy()

        val intent = Intent()
        intent.action = MyAnnotations.START_LOCKER_SERVICE
        intent.setClass(this, BroadcastServiceStarter::class.java)
        this.sendBroadcast(intent)

    }


    class ScreenReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val preferences = AppPreferences(context)
            val calendar: Calendar = Calendar.getInstance()

            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                screenOff = true
                screenOn = false

                when (preferences.getString(
                    MyAnnotations.AUTOMATICALLY_LOCK,
                    ""
                )) {
//                    MyAnnotations.AFTER_SCREEN_OFF -> {
//                        calendar.add(Calendar.SECOND, -1)
//                        setLockAfterThisTime = calendar.timeInMillis
//                    }
                    MyAnnotations.IMMEDIATELY -> {
                        calendar.add(Calendar.SECOND, 15)
                        setLockAfterThisTime = calendar.timeInMillis

                    }
                    MyAnnotations.SECONDS_30 -> {
                        calendar.add(Calendar.SECOND, 30)
                        setLockAfterThisTime = calendar.timeInMillis

                    }
                    MyAnnotations.MINUTE_1 -> {
                        calendar.add(Calendar.MINUTE, 1)
                        setLockAfterThisTime = calendar.timeInMillis

                    }
                    MyAnnotations.MINUTES_5 -> {
                        calendar.add(Calendar.MINUTE, 5)
                        setLockAfterThisTime = calendar.timeInMillis

                    }
                    MyAnnotations.MINUTES_10 -> {
                        calendar.add(Calendar.MINUTE, 10)
                        setLockAfterThisTime = calendar.timeInMillis

                    }
                    else -> {
                        setLockAfterThisTime = calendar.timeInMillis
                    }
                }
            } else if (intent.action == Intent.ACTION_SCREEN_ON) {
                lastTimeScreenON = calendar.timeInMillis
                screenOn = true

            }

        }


    }


    override fun getLifecycle() = dispatcher.lifecycle
    override fun onClick(p0: View?) {
        when {
            p0!!.id == R.id.textView_yes -> {
                Handler(Looper.getMainLooper()).post {
                    if (dialog != null) {
                        var set: MutableSet<String>? =
                            preferences!!.getStringSet(MyAnnotations.APPS_SET)
                        if (set != null) {
                            set.add(appPack)
                            preferences!!.adStringSet(MyAnnotations.APPS_SET, set)
                            if (dialog!!.isShowing) {
                                dialog!!.dismiss()
                            }
                        } else {
                            set = mutableSetOf()
                            set.add(appPack)
                            preferences!!.adStringSet(MyAnnotations.APPS_SET, set)
                            if (dialog!!.isShowing) {
                                dialog!!.dismiss()
                            }
                        }
                    }

                }
            }
            p0.id == R.id.textView_no -> {
                Handler(Looper.getMainLooper()).post {
                    if (dialog != null) {
                        if (dialog!!.isShowing) {
                            dialog!!.dismiss()

                        }
                    }

                }
            }

        }
    }


}



