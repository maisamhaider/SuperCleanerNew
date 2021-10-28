package com.example.junckcleaner.views.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.*
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.cottacush.android.hiddencam.CameraType
import com.cottacush.android.hiddencam.HiddenCam
import com.cottacush.android.hiddencam.OnImageCapturedListener
import com.example.junckcleaner.R
import com.example.junckcleaner.annotations.MyAnnotations
import com.example.junckcleaner.models.ModelIntruder
import com.example.junckcleaner.permissions.MyPermissions
import com.example.junckcleaner.prefrences.AppPreferences
import com.example.junckcleaner.services.AppLockService
import com.example.junckcleaner.utils.Utils
import com.example.junckcleaner.viewmodel.ViewModelIntruder
import com.takwolf.android.lock9.Lock9View
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

class ActivityServiceLocker : AppCompatActivity(), View.OnClickListener, OnImageCapturedListener {


    private lateinit var imagePath: String
    var view1: View? = null
    var view2: View? = null
    var view3: View? = null
    var view4: View? = null
    var viewDisable: View? = null
    var textview1: TextView? = null
    var textview2: TextView? = null
    var textview3: TextView? = null
    var textview4: TextView? = null
    var textview5: TextView? = null
    var textview6: TextView? = null
    var textview7: TextView? = null
    var textview8: TextView? = null
    var textview9: TextView? = null
    var textview0: TextView? = null
    var textviewPinNotMatched: TextView? = null
    private var textView_forget_pin: TextView? = null
    var textviewTimer: TextView? = null
    var container: FrameLayout? = null
    var password = ""
    var savedPassword = ""
    var app = ""

    var i = 0

    private lateinit var hiddenCam: HiddenCam
    private lateinit var baseStorageFolder: File

    var preferences: AppPreferences? = null
    var utils: Utils? = null
    var context: Context? = null
    //

    var isUnlocked = false;

    private var wrongAttempts = 0
    private var myPermissions: MyPermissions? = null
    private var viewModelIntruder: ViewModelIntruder? = null
    var lastId: Int? = null
    var currentApp = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation.and(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        context = this@ActivityServiceLocker
    }


    private fun views(length: Int) {
        when (length) {
            0 -> {
                view1!!.background = ContextCompat.getDrawable(this, R.drawable.bg_empty_circle)
                view2!!.background = ContextCompat.getDrawable(this, R.drawable.bg_empty_circle)
                view3!!.background = ContextCompat.getDrawable(this, R.drawable.bg_empty_circle)
                view4!!.background = ContextCompat.getDrawable(this, R.drawable.bg_empty_circle)
            }
            1 -> view1!!.background =
                ContextCompat.getDrawable(this, R.drawable.bg_filled_circle)
            2 -> {
                view1!!.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_filled_circle)
                view2!!.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_filled_circle)
            }
            3 -> {
                view1!!.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_filled_circle)
                view2!!.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_filled_circle)
                view3!!.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_filled_circle)
            }
            4 -> {
                view1!!.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_filled_circle)
                view2!!.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_filled_circle)
                view3!!.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_filled_circle)
                view4!!.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_filled_circle)
            }
        }
    }

    private fun goToNextActivity(password: String) {
        views(password.length)
        if (password.length == 4) {
            savedPassword = preferences!!.getString(MyAnnotations.PIN, "")
            //if new password and saved password matched then go to main actissvity
            if (savedPassword == password) {
                finishAffinity()
                this.password = ""
                isUnlocked = true
            } else {
                this.password = ""
                i = i.plus(1)
                views(0)

                if (preferences!!.getBoolean(MyAnnotations.VIB_FEEDBACK, false)) {
                    utils!!.setViberate()
                }
                textviewPinNotMatched!!.text = "Wrong PIN is entered"
                textviewPinNotMatched!!.setTextColor(Color.RED)
                Handler(Looper.getMainLooper()).postDelayed({
                    if (i < 5) {
                        textviewPinNotMatched!!.text = "Try again"
                    } else {
                        textviewPinNotMatched!!.text = "Please wait until time finished"
                    }
                    textviewPinNotMatched!!.setTextColor(Color.WHITE)

                }, 1500)
                if (i >= 5) {
                    disableButtons()
                    object : CountDownTimer(30000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            textviewTimer!!.text = (millisUntilFinished / 1000).toString()
                            //here you can have your logic to set text to edittext
                        }

                        override fun onFinish() {
                            textviewTimer!!.text = ""
                            textviewPinNotMatched!!.text = "Try again"
                            i = 0
                            enableButtons()

                        }
                    }.start()
                }

                startCamera()


            }
        }
    }


    private fun disableButtons() {
        viewDisable!!.visibility = View.VISIBLE
    }

    private fun enableButtons() {
        viewDisable!!.visibility = View.GONE
    }


    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.textView_0 -> {
                password += textview0!!.text.toString()
                goToNextActivity(password)
            }
            R.id.textView_1 -> {
                password += textview1!!.text.toString()
                goToNextActivity(password)
            }
            R.id.textView_2 -> {
                password += textview2!!.text.toString()
                goToNextActivity(password)
            }
            R.id.textView_3 -> {
                password += textview3!!.text.toString()
                goToNextActivity(password)
            }
            R.id.textView_4 -> {
                password += textview4!!.text.toString()
                goToNextActivity(password)
            }
            R.id.textView_5 -> {
                password += textview5!!.text.toString()
                goToNextActivity(password)
            }
            R.id.textView_6 -> {
                password += textview6!!.text.toString()
                goToNextActivity(password)
            }
            R.id.textView_7 -> {
                password += textview7!!.text.toString()
                goToNextActivity(password)
            }
            R.id.textView_8 -> {
                password += textview8!!.text.toString()
                goToNextActivity(password)
            }
            R.id.textView_9 -> {
                password += textview9!!.text.toString()
                goToNextActivity(password)
            }
            R.id.textView_forget_pin ->
                startActivity(
                    Intent(this, ActivitySetSecurityQuestion::class.java)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.FORGOT_PIN)
                        .putExtra(MyAnnotations.IN_APP, false)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )

        }
    }

    private fun startCamera() {
        wrongAttempts++
        if (app != currentApp) {
            //insert
            if (preferences!!.getBoolean(MyAnnotations.TAKE_SELFIE, false)) {
                if (myPermissions!!.checkPermission()) {
                    if (wrongAttempts >= getAttemptsToTakeSelfie()) {
                        //take selfie
                        hiddenCam.captureImage()

                        Handler(Looper.getMainLooper()).postDelayed({
                            if (imagePath.isNotEmpty()) {
                                app = currentApp
                                viewModelIntruder!!.insertIntruder(
                                    ModelIntruder(
                                        app, imagePath,
                                        wrongAttempts.toString(),
                                        getDate(System.currentTimeMillis()),
                                        getTime(System.currentTimeMillis()),
                                    )
                                )
                            }
                        }, 1000)

                    }
                }
            }
        } else {
            //update

            val modelIntruder = ModelIntruder(
                app, imagePath, wrongAttempts.toString(),
                getDate(System.currentTimeMillis()),
                getTime(System.currentTimeMillis()),
            )

            try {
                modelIntruder.id = lastId!!
            } catch (e: Exception) {
                Log.e("viewModelIntruder", e.message!!)
            }
            viewModelIntruder!!.updateIntruder(modelIntruder)
        }


    }

    private fun getAttemptsToTakeSelfie(): Int {

        //CHECK WRONG ATTEMPTS
        when (preferences!!.getString(MyAnnotations.WRONG_ATTEMPTS, MyAnnotations.TIMES_3)) {
            MyAnnotations.TIMES_3 -> return 3
            MyAnnotations.TIMES_4 -> return 4
            MyAnnotations.TIMES_5 -> return 5
            MyAnnotations.TIMES_10 -> return 10
            MyAnnotations.TIMES_15 -> return 15
        }
        return 3
    }


    fun getDate(milli: Long): String {
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = milli
        return SimpleDateFormat("dd/MM/yyyy").format(cal.timeInMillis)
    }

    fun getTime(milli: Long): String {
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = milli
        return SimpleDateFormat("hh:mm a").format(cal.timeInMillis)
    }


    override fun onImageCaptured(image: File) {
        val message = "Image captured, saved to:${image.absolutePath}"
        imagePath = image.absolutePath

    }

    override fun onImageCaptureError(e: Throwable?) {
        e?.run {
            val message = "Image captured failed:${e.message}"
            printStackTrace()
        }
    }


    private fun checkFingerPrintsAvailable(): Boolean {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {

                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN,
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> return false
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                return false
            }
        }
        return false
    }


    private fun createPrompt(): PromptInfo {
        return PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("unlock by fingerprint")
            .setNegativeButtonText("Cancel")
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        Handler(Looper.getMainLooper()).postDelayed({ AppLockService.isLock = false }, 400)
        finish()

    }

    override fun onStop() {
        super.onStop()
        Handler(Looper.getMainLooper()).postDelayed({ AppLockService.isLock = false }, 400)
        finish()
    }

    override fun onPause() {
        super.onPause()
        if (!isUnlocked) {
            preferences!!.addString(MyAnnotations.PREVIOUS_APP, packageName)
        }
        finish()
    }

    override fun onResume() {
        super.onResume()
        context = this@ActivityServiceLocker
        preferences = AppPreferences(this)
        utils = Utils(this)
        myPermissions = MyPermissions(this)

        currentApp = preferences!!.getString(MyAnnotations.CURRENT_APP, packageName);
        baseStorageFolder = File(
            getExternalFilesDir(null)!!
                .absolutePath, "intruders" + System.currentTimeMillis()
        ).apply {
            if (exists()) deleteRecursively()
            mkdir()
        }
        if (preferences!!.getBoolean(MyAnnotations.TAKE_SELFIE, false)) {
            hiddenCam = HiddenCam(
                this, baseStorageFolder, this,
                targetResolution = Size(1080, 1920),
                cameraType = CameraType.FRONT_CAMERA
            )
            hiddenCam.start()

        }


        //viewModel
        viewModelIntruder = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(ViewModelIntruder::class.java)

        viewModelIntruder!!.lastInserted.observe(this, { item ->
            if (item != null) {
                lastId = item.id
            }
        })

        if (preferences!!.getBoolean(MyAnnotations.PATTERN_ENABLED, false)) {
            setContentView(R.layout.layout_draw_pattern)
            pattern()

        } else {
            setContentView(R.layout.activity_enter_pin)
            pin()
        }

        viewDisable!!.setOnClickListener { }

        textviewTimer = findViewById(R.id.textView_timer)


        window!!.statusBarColor = ContextCompat.getColor(this, R.color.color_main)
        window!!.navigationBarColor = ContextCompat.getColor(this, R.color.color_third)

        if (preferences!!.getBoolean(MyAnnotations.FINGER_PRINT, false)) {
            if (checkFingerPrintsAvailable()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    fingerprints()
                }
            }
        }

    }

    private fun fingerprints() {
        val biometricPrompt: BiometricPrompt
        val executor: Executor = ContextCompat.getMainExecutor(this)
        biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    // use login pass
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        // show pattern or pin pad
                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    isUnlocked = true
                    finishAffinity()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            })
        biometricPrompt.authenticate(createPrompt())
    }

    private fun pattern() {
        val lock9View: Lock9View = findViewById(R.id.lock_9_view)
        container = findViewById(R.id.container)
        viewDisable = findViewById(R.id.view_disable)

        val textviewForgetPattern: TextView = findViewById(R.id.textView_forget_pattern)
        val textviewPatterNotMatched: TextView = findViewById(R.id.textView_patter_not_matched)

        val imageviewLockedPatternApp: ImageView =
            findViewById(R.id.imageView_locked_pattern_app)

        enableButtons()
        imageviewLockedPatternApp.setImageDrawable(
            utils!!.appInfo<Any>(
                currentApp, MyAnnotations.APP_ICON
            ) as Drawable
        )
        savedPassword = preferences!!.getString(MyAnnotations.PATTERN, "")

        lock9View.setCallBack { password: String ->
            if (savedPassword == password) {
                isUnlocked = true
                finishAffinity()
            } else {
                i = i.plus(1)
                if (preferences!!.getBoolean(MyAnnotations.VIB_FEEDBACK, false)) {
                    utils!!.setViberate()
                }
                textviewPatterNotMatched.text = "Wrong Pattern"
                textviewPatterNotMatched.setTextColor(Color.RED)
                Handler(Looper.getMainLooper()).postDelayed({
                    if (i < 5) {
                        textviewPatterNotMatched.text = "Try again"
                    } else {
                        textviewPatterNotMatched.text = "Please wait until time finished"
                    }
                    textviewPatterNotMatched.setTextColor(Color.WHITE)

                }, 1500)
                if (i >= 5) {
                    disableButtons()
                    object : CountDownTimer(30000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            textviewTimer!!.text = (millisUntilFinished / 1000).toString()
                        }

                        override fun onFinish() {
                            textviewTimer!!.text = ""
                            textviewPatterNotMatched.text = "Try again"
                            i = 0
                            enableButtons()
                            textviewPatterNotMatched.setTextColor(Color.WHITE)
                        }
                    }.start()
                }
                //take selfie
                startCamera()


            }
        }
        textviewForgetPattern.setOnClickListener {
            val intent: Intent =
                Intent(context, ActivitySetSecurityQuestion::class.java)
            intent.putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.FORGOT_PATTERN)
            intent.putExtra(MyAnnotations.IN_APP, false)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            this.applicationContext.startActivity(intent)
        }
    }

    private fun pin() {
        viewDisable = findViewById(R.id.view_disable)
        val imageviewLockedPatternApp: ImageView =
            findViewById(R.id.image_pin_locked_app)
        textviewPinNotMatched = findViewById(R.id.textView_pin_not_matched)
        imageviewLockedPatternApp.setImageDrawable(
            utils!!.appInfo(currentApp, MyAnnotations.APP_ICON) as Drawable
        )
        enableButtons()
        container = findViewById(R.id.container)
        view1 = findViewById(R.id.view_1)
        view2 = findViewById(R.id.view_2)
        view3 = findViewById(R.id.view_3)
        view4 = findViewById(R.id.view_4)
        textview1 = findViewById(R.id.textView_1)
        textview2 = findViewById(R.id.textView_2)
        textview3 = findViewById(R.id.textView_3)
        textview4 = findViewById(R.id.textView_4)
        textview5 = findViewById(R.id.textView_5)
        textview6 = findViewById(R.id.textView_6)
        textview7 = findViewById(R.id.textView_7)
        textview8 = findViewById(R.id.textView_8)
        textview9 = findViewById(R.id.textView_9)
        textview0 = findViewById(R.id.textView_0)
        textView_forget_pin = findViewById(R.id.textView_forget_pin)

        textview1!!.setOnClickListener(this)
        textview2!!.setOnClickListener(this)
        textview3!!.setOnClickListener(this)
        textview4!!.setOnClickListener(this)
        textview5!!.setOnClickListener(this)
        textview6!!.setOnClickListener(this)
        textview7!!.setOnClickListener(this)
        textview8!!.setOnClickListener(this)
        textview9!!.setOnClickListener(this)
        textview0!!.setOnClickListener(this)
        textView_forget_pin!!.setOnClickListener(this)
    }

    override fun onBackPressed() {
    }
}