package com.example.junckcleaner.views.activities

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.os.Build.VERSION
import android.os.StrictMode.VmPolicy
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.junckcleaner.R
import com.example.junckcleaner.adapters.AdapterForYouApps
import com.example.junckcleaner.models.ModelForYouApps
import com.example.junckcleaner.permissions.MyPermissions
import com.example.junckcleaner.prefrences.AppPreferences
import com.example.junckcleaner.utils.Internet
import com.example.junckcleaner.views.fragments.FragmentHome
import com.example.junckcleaner.views.fragments.MeFragment
import com.example.junckcleaner.views.fragments.ToolbarFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import java.util.*


class ActivityMain : BaseActivity() {
    var rootNode: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null

    var bottomNavigationView: BottomNavigationView? = null
    var doubleBackToExitPressedOnce = false
    var permissions: MyPermissions? = null
    var preferences: AppPreferences? = null
    var clBottomSheet: ConstraintLayout? = null
    var flAdplaceholder: FrameLayout? = null
    var recyclerView: RecyclerView? = null
    var internet: Internet? = null
    val i = 0;
    var layout_exit_ad: View? = null;
    var layout_exit_button: View? = null;
    var layout_exit_for_you: View? = null;

    @SuppressLint("NonConstantResourceId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        clBottomSheet = findViewById(R.id.clBottomSheet)
        permissions = MyPermissions(this)
        preferences = AppPreferences(this)
        internet = Internet(this)

        if (VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            StrictMode.setVmPolicy(
                VmPolicy.Builder() // Other StrictMode checks that you've previously added.
                    // ...
                    .detectUnsafeIntentLaunch()
                    .penaltyLog() // Consider also adding penaltyDeath()
                    .build()
            )
        }

        flAdplaceholder = findViewById(R.id.fl_adplaceholder)


        layout_exit_ad = findViewById(R.id.layout_exit_ad)
        layout_exit_button = findViewById(R.id.layout_exit_button)
        layout_exit_for_you = findViewById(R.id.layout_exit_for_you)

        val textViewNotNow = findViewById<TextView>(R.id.textViewNotNow)
        val textViewExit = findViewById<TextView>(R.id.textViewExit)
        recyclerView = findViewById(R.id.recyclerView)

        val native: View = LayoutInflater.from(this).inflate(R.layout.ad_unified_exit, null)
        val appCompatImageView20 = native.findViewById<ImageView>(R.id.appCompatImageView62)

        bottomNavigationView!!.selectedItemId = R.id.item_home
        loadFragment(FragmentHome())
        bottomNavigationView!!.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.item_home -> loadFragment(FragmentHome())
                R.id.item_toolbox -> loadFragment(ToolbarFragment())
                R.id.item_me -> loadFragment(MeFragment())
            }
            true
        }

        appCompatImageView20.setOnClickListener {
            clBottomSheet!!.visibility = View.GONE
            bottomNavigationView!!.visibility = View.VISIBLE
        }
        textViewNotNow.setOnClickListener {
            clBottomSheet!!.visibility = View.GONE
            bottomNavigationView!!.visibility = View.VISIBLE

        }
        textViewExit.setOnClickListener {
            clBottomSheet!!.visibility = View.GONE
            finish()
        }

        if (internet!!.isConnected) {
            loadLoadFireBase()
            refreshAd2(flAdplaceholder)
            layout_exit_ad!!.visibility = View.VISIBLE
            layout_exit_for_you!!.visibility = View.VISIBLE
        } else {
            layout_exit_ad!!.visibility = View.GONE
            layout_exit_for_you!!.visibility = View.GONE
        }

    }


    fun checkPermission(): Boolean {
        return if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val write = ContextCompat.checkSelfPermission(
                applicationContext,
                permission.WRITE_EXTERNAL_STORAGE
            )
            val read = ContextCompat.checkSelfPermission(
                applicationContext,
                permission.READ_EXTERNAL_STORAGE
            )
            write == PackageManager.PERMISSION_GRANTED &&
                    read == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onResume() {
        super.onResume()
        if (!permissions!!.checkPermission()) {
            permissions!!.requestPermissions()
        } else if (!checkPermission()) {
            showPermissionDialog()
        }

    }

    fun showPermissionDialog() {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                openSomeActivityForResult(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                openSomeActivityForResult(intent)
            }
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE),
                333
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE),
                333
            )
        }
    }

    var someActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) {

    }
    var someActivityResultLauncher1 = registerForActivityResult(
        StartActivityForResult()
    ) {
        if (it.data != null && it.data!!.component != null && !TextUtils.isEmpty(
                it.data!!.component!!.flattenToShortString()
            )
        ) {
            val appName: String = it.data!!.component!!.packageName
            // Now you know the app being picked.
            // data is a copy of your launchIntent with this important extra info added.

            // Start the selected activity
            if (appName.contains("chrome")) {
                val intent = Intent("org.chromium.chrome.browser.incognito.OPEN_PRIVATE_TAB")
                startActivity(intent)
            } else {
                try {
                    val launchIntent =
                        packageManager.getLaunchIntentForPackage(appName)
                    launchIntent?.let { startActivity(it) }
                } catch (e: PackageManager.NameNotFoundException) {
                }
            }
        }
    }

    fun openSomeActivityForResult(intent: Intent) {
        someActivityResultLauncher.launch(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        val current = bottomNavigationView!!.selectedItemId
        if (current == R.id.item_home) {
            if (clBottomSheet!!.isVisible) {
                bottomNavigationView!!.visibility = View.VISIBLE
                clBottomSheet!!.visibility = View.GONE
            } else {
                (flAdplaceholder)
                bottomNavigationView!!.visibility = View.GONE
                clBottomSheet!!.visibility = View.VISIBLE
            }
        } else {
            if (doubleBackToExitPressedOnce) {
                doubleBackToExitPressedOnce = false
                bottomNavigationView!!.visibility = View.GONE
                clBottomSheet!!.visibility = View.VISIBLE

                return
            }
            if (clBottomSheet!!.isVisible) {
                bottomNavigationView!!.visibility = View.VISIBLE
                clBottomSheet!!.visibility = View.GONE
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please double tap to exit", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed({
                doubleBackToExitPressedOnce = false
            }, 800)
        }
    }


    fun loadRecycler(list: ArrayList<ModelForYouApps>) {
        val layoutManager = LinearLayoutManager(this)
        val moreApps = AdapterForYouApps(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerView!!.adapter = moreApps
        recyclerView!!.layoutManager = layoutManager
        moreApps.submitList(list)
    }

    fun loadLoadFireBase() {
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
                    getData(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ActivityMain, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    fun getData(list: MutableList<String>): ArrayList<ModelForYouApps> {
        val appsArrayList: ArrayList<ModelForYouApps> = ArrayList()
        if (list.isNotEmpty()) {
            for (app in list) {
                databaseReference = rootNode!!.reference.child("for_you/$app")
                databaseReference!!.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        appsArrayList.add(snapshot.getValue(ModelForYouApps::class.java)!!)
                        if (appsArrayList.isNotEmpty()) {
                            loadRecycler(appsArrayList)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@ActivityMain,
                            "Something went wrong",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
        return appsArrayList
    }


}