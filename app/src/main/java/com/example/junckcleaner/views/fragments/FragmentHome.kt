package com.example.junckcleaner.views.fragments

import android.Manifest
import android.animation.ValueAnimator
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.junckcleaner.BuildConfig
import com.example.junckcleaner.R
import com.example.junckcleaner.adapters.AdapterForYouApps
import com.example.junckcleaner.annotations.MyAnnotations
import com.example.junckcleaner.models.ModelForYouApps
import com.example.junckcleaner.permissions.MyPermissions
import com.example.junckcleaner.prefrences.AppPreferences
import com.example.junckcleaner.utils.Internet
import com.example.junckcleaner.utils.Utils
import com.example.junckcleaner.views.activities.*
import com.google.firebase.database.*


class FragmentHome : Fragment() {
    var rootNode: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null

    var utils: Utils? = null

    var textView_memoryUsedPercentage: TextView? = null
    var textView_storageusedPer: TextView? = null
    var progressBar: ProgressBar? = null
    var progressBar2: ProgressBar? = null
    var appPreferences: AppPreferences? = null


    fun FragmentHomeNew() {
        // Required empty public constructor
    }


    fun newInstance() {
        return FragmentHomeNew()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_home, container, false)
        appPreferences = AppPreferences(context)
        utils = Utils(context)
        val internet = Internet(context)

        textView_memoryUsedPercentage = root.findViewById(R.id.textView_memoryUsedPercentage)
        textView_storageusedPer = root.findViewById(R.id.textView_storageusedPer)
        progressBar = root.findViewById(R.id.progressBar)
        progressBar2 = root.findViewById(R.id.progressBar2)
        val textView_cleanMain: TextView = root.findViewById(R.id.textView_cleanMain)
        val cl_cpu_clean: ConstraintLayout = root.findViewById(R.id.cl_cpu_clean)
        val cl_power_save: ConstraintLayout = root.findViewById(R.id.cl_power_save)
        val cl_game_boost: ConstraintLayout = root.findViewById(R.id.cl_game_boost)
        val cl_clean_up: ConstraintLayout = root.findViewById(R.id.cl_clean_up)
        val cl_antivirus: ConstraintLayout = root.findViewById(R.id.cl_antivirus)
        val cl_phone_booster: ConstraintLayout = root.findViewById(R.id.cl_phone_booster)
        val cl_private_browsing: ConstraintLayout = root.findViewById(R.id.cl_private_browsing)
        val cl_app_lock: ConstraintLayout = root.findViewById(R.id.cl_app_lock)
        val lottie: LottieAnimationView = root.findViewById(R.id.lottie)
        lottie.setOnClickListener {
            if (internet.isConnected) {
                forYouDialog()
            } else {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show()
            }
        }

        textView_cleanMain.setOnClickListener {
            val activityMain = activity as ActivityMain

            if (!activityMain.checkPermission()) {
                activityMain.showPermissionDialog()
            } else if (!hasUsageStatsPermission()) {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            } else {
                requireContext().startActivity(Intent(activity, JunkActivity::class.java))
            }
        }
        cl_cpu_clean.setOnClickListener {
            if (!hasUsageStatsPermission()) {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            } else {
                requireContext().startActivity(
                    Intent(
                        activity,
                        CpuCoolerActivity::class.java
                    )
                )
            }

        }
        cl_power_save.setOnClickListener {
            if (!hasUsageStatsPermission()) {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            } else {
                requireContext().startActivity(
                    Intent(
                        activity,
                        BatterySaverActivity::class.java
                    )
                )
            }
        }
        cl_game_boost.setOnClickListener {
            if (!hasUsageStatsPermission()) {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            } else {
                requireContext().startActivity(
                    Intent(
                        activity,
                        GameBoosterActivity::class.java
                    )
                )
            }

        }
        cl_clean_up.setOnClickListener {
            val activityMain = activity as ActivityMain
            if (!activityMain.checkPermission()) {
                activityMain.showPermissionDialog()
            } else if (!hasUsageStatsPermission()) {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            } else {
                requireContext().startActivity(Intent(activity, JunkActivity::class.java))
            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                if (!activityMain.checkIfGotAccess()) {
//                    activityMain.openDirectory()
//                } else {
//                    requireContext().startActivity(Intent(activity, JunkActivity::class.java))
////                    activityMain.onGotAccessWhatsApp()
//                }
//            } else {
//                if (activityMain.checkPermission()) {
//                    requireContext().startActivity(Intent(activity, JunkActivity::class.java))
//                } else {
//                    activityMain.showPermissionDialog()
//                }
//            }
        }
        cl_antivirus.setOnClickListener {
            if (!hasUsageStatsPermission()) {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            } else if (!checkPermission()) {
                showPermissionDialog()
            } else {
                requireContext().startActivity(
                    Intent(
                        activity,
                        AntivirusActivity::class.java
                    )
                )
            }

        }
        cl_phone_booster.setOnClickListener {
            if (!hasUsageStatsPermission()) {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            } else {
                requireContext().startActivity(
                    Intent(
                        activity,
                        BoostActivity::class.java
                    )
                )

            }
        }
        cl_private_browsing.setOnClickListener {
            doSocialShare("Private Browsing", "Select browser please")
        }

        cl_app_lock.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(context)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    )
                    startActivity(intent)
                } else if (!hasUsageStatsPermission()) {
                    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                } else {
                    goToAppLocker()
                }
            } else {
                goToAppLocker()
            }
        }
        if (appPreferences!!.getBoolean(MyAnnotations.PLAY_ANIMATION, false)) {
            memoryUsage(true)
            storageUsage(true)
            appPreferences!!.addBoolean(MyAnnotations.PLAY_ANIMATION, false)
        } else {
            memoryUsage(false)
            storageUsage(false)
        }

        return root
    }

    private fun doSocialShare(title: String?, text: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("http://www.example.com")
        var browserList: List<ResolveInfo?>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // gets all
            browserList = requireContext().packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_ALL
            )
            // only the defaults
            browserList = requireContext().packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        } else {
            browserList = requireContext().packageManager.queryIntentActivities(intent, 0)
        }
        val activityMain = activity as ActivityMain
        // First search for compatible apps with sharing (Intent.ACTION_SEND)
        val targetedShareIntents: MutableList<Intent> = ArrayList()
        val shareIntent = Intent(Intent.ACTION_VIEW)
        shareIntent.data = Uri.parse("http://www.example.com")
        // Set title and text to share when the user selects an option.
        val list = ArrayList<String>()
        if (browserList.isNotEmpty()) {
            for (info in browserList) {
                if (!list.contains(info.activityInfo.packageName.lowercase())) {
                    val targetedShare = Intent(Intent.ACTION_VIEW)
                    targetedShare.data = Uri.parse("http://www.example.com")
//                targetedShare.type = "text/plain" // put here your mime type
                    targetedShare.setPackage(info.activityInfo.packageName.lowercase())
                    targetedShareIntents.add(targetedShare)
                    list.add(info.activityInfo.packageName.lowercase())
                }

            }
            // Then show the ACTION_PICK_ACTIVITY to let the user select it
            val intentPick = Intent()
            intentPick.action = Intent.ACTION_PICK_ACTIVITY
            // Set the title of the dialog
            intentPick.putExtra(Intent.EXTRA_TITLE, title)
            intentPick.putExtra(Intent.EXTRA_INTENT, shareIntent)
            // Call StartActivityForResult so we can get the app name selected by the user
            intentPick.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toTypedArray())

            activityMain.someActivityResultLauncher1.launch(intentPick)
        }

    }

    private fun goToAppLocker() {
        if (!appPreferences!!.getBoolean(MyAnnotations.IS_LOCKED, false)) {
            startActivity(
                Intent(activity, ActivityCreatePattern::class.java)
                    .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CREATE_PATTERN)
            )
        } else {
            if (appPreferences!!.getBoolean(MyAnnotations.PATTERN_ENABLED, true)) {
                startActivity(
                    Intent(activity, ActivityCreatePattern::class.java)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.DRAW_PATTERN)
                )
            } else {
                startActivity(
                    Intent(activity, ActivityCreatePin::class.java)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.ENTER_PIN)
                )
            }
        }
    }


    private fun hasUsageStatsPermission(): Boolean {
        val permission = MyPermissions(context)
        return permission.hasUsageStatsPermission()
    }


    private fun memoryUsage(play: Boolean) {
        val activityManager =
            requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        progressBar!!.max = 100
        val mem = utils!!.getPercentage(
            memoryInfo.totalMem.toFloat(),
            (memoryInfo.totalMem - memoryInfo.availMem).toFloat()
        )
        val prog = utils!!.getPercentage(
            memoryInfo.totalMem.toFloat(),
            (memoryInfo.totalMem - memoryInfo.availMem).toFloat()
        ).toInt()

        if (play) {
            setMemoryAnimation(0f, mem)
            setMemoryProgAnimation(0, prog)
        } else {
            textView_memoryUsedPercentage!!.text = String.format("%.1f", mem) + " %"
            progressBar!!.progress = prog

        }


    }

    private fun storageUsage(play: Boolean) {
        progressBar2!!.max = 100
        val total = utils!!.getDataSizeFloat(utils!!.totalStorage).toInt()
        val used =
            utils!!.getDataSizeFloat(utils!!.totalStorage - utils!!.availableStorage).toInt()

        val storePercentage = utils!!.getPercentage(
            utils!!.totalStorage,
            utils!!.totalStorage - utils!!.availableStorage
        )

        if (play) {

            setStorageAnimation(0f, storePercentage)

            setStorageProgAnimation(
                0, utils!!.getPercentage(
                    utils!!.totalStorage, utils!!.totalStorage - utils!!.availableStorage
                ).toInt()
            )

        } else {
            textView_storageusedPer!!.text = String.format("%.1f", storePercentage) + " %"
            progressBar2!!.progress = utils!!.getPercentage(
                utils!!.totalStorage, utils!!.totalStorage - utils!!.availableStorage
            ).toInt()

        }

    }

    private fun setMemoryAnimation(from: Float, to: Float) {
        val animator = ValueAnimator.ofFloat(from, to)
        animator.duration = 3500
        animator.addUpdateListener { valueAnimator: ValueAnimator ->
            val value = valueAnimator.animatedValue as Float
            textView_memoryUsedPercentage!!.text = String.format("%.1f", value) + " %"
        }
        animator.start()
    }

    private fun setMemoryProgAnimation(from: Int, to: Int) {
        val animator = ValueAnimator.ofInt(from, to)
        animator.duration = 3500
        animator.addUpdateListener { valueAnimator: ValueAnimator ->
            val value = valueAnimator.animatedValue as Int
            progressBar!!.progress = value
        }
        animator.start()
    }

    private fun setStorageAnimation(from: Float, to: Float) {
        val animator = ValueAnimator.ofFloat(from, to)
        animator.duration = 3500
        animator.addUpdateListener { valueAnimator: ValueAnimator ->
            val value = valueAnimator.animatedValue as Float
            textView_storageusedPer!!.text = String.format("%.1f", value) + " %"
        }
        animator.start()
    }

    private fun setStorageProgAnimation(from: Int, to: Int) {
        val animator = ValueAnimator.ofInt(from, to)
        animator.duration = 3500
        animator.addUpdateListener { valueAnimator: ValueAnimator ->
            val value = valueAnimator.animatedValue as Int
            progressBar2!!.progress = value
        }
        animator.start()
    }


    fun forYouDialog() {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_for_apps_dialog, null)
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setView(view)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val imageViewCross: ImageView = view.findViewById(R.id.imageViewCross)
        val lottiePaper: LottieAnimationView = view.findViewById(R.id.lottiePaper)

        lottiePaper.playAnimation()

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        imageViewCross.setOnClickListener {
            dialog.cancel()
        }
        loadLoadFireBase(recyclerView)
    }

    fun loadRecycler(recyclerView: RecyclerView, list: ArrayList<ModelForYouApps>) {
        val layoutManager = LinearLayoutManager(context)
        val moreApps =
            AdapterForYouApps(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerView.adapter = moreApps
        recyclerView.layoutManager = layoutManager
        moreApps.submitList(list)
    }

    fun loadLoadFireBase(recyclerView: RecyclerView) {
        rootNode = FirebaseDatabase.getInstance()

        databaseReference = rootNode!!.reference.child("for_you")
        val list: MutableList<String> = ArrayList()
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    val child = snap.key
                    list.add(child!!)
                }
                if (list.isNotEmpty()) {
                    getData(recyclerView, list)
                } else {
                    Toast.makeText(context, "No app found", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    fun getData(recyclerView: RecyclerView, list: MutableList<String>): ArrayList<ModelForYouApps> {
        val appsArrayList: ArrayList<ModelForYouApps> = ArrayList()
        if (list.isNotEmpty()) {
            for (app in list) {
                databaseReference = rootNode!!.reference.child("for_you/$app")
                databaseReference!!.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        appsArrayList.add(snapshot.getValue(ModelForYouApps::class.java)!!)
                        if (appsArrayList.isNotEmpty()) {
                            loadRecycler(recyclerView, appsArrayList)
                        } else {
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            context,
                            "Something went wrong",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
        return appsArrayList
    }

    fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val write = ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val read = ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            write == PackageManager.PERMISSION_GRANTED &&
                    read == PackageManager.PERMISSION_GRANTED
        }
    }

    fun showPermissionDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                openSomeActivityForResult(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                openSomeActivityForResult(intent)
            }
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                333
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                333
            )
        }
    }

    var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {

    }

    fun openSomeActivityForResult(intent: Intent) {
        someActivityResultLauncher.launch(intent)
    }

}