package com.example.junckcleaner.views.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.junckcleaner.R
import com.example.junckcleaner.annotations.MyAnnotations
import com.example.junckcleaner.prefrences.AppPreferences
import com.example.junckcleaner.utils.Internet
import com.example.junckcleaner.viewmodel.ViewModelApps
import com.google.firebase.database.*
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ActivitySplash : AppCompatActivity() {
    var viewModelApps: ViewModelApps? = null
    var executor: Executor = Executors.newSingleThreadExecutor()
    var rootNode: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null
    var internet: Internet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val preferences = AppPreferences(this)
        val internet = Internet(this)

        preferences.addBoolean(MyAnnotations.PLAY_ANIMATION, true)
        val set: MutableSet<String> = mutableSetOf()
        viewModelApps = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )
            .get(ViewModelApps::class.java)
        viewModelApps!!.allApps.observe(this,
            { strings ->
                executor.execute {
                    strings.forEach {
                        set.add(it)
                    }
                    preferences.adStringSet(MyAnnotations.ALL_APPS, set)
                }

            })
        viewModelApps!!.userApps.observe(this,
            { strings ->
                executor.execute {
                    strings.forEach {
                        set.add(it)
                    }
                    preferences.adStringSet(MyAnnotations.USER_APPS, set)
                }

            })
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        Handler(Looper.getMainLooper()).postDelayed({
            window.navigationBarColor = ContextCompat.getColor(this, R.color.color_third)
            window.statusBarColor = ContextCompat.getColor(this, R.color.color_main)
        }, 2500)
        val i = Intent(this, ActivityMain::class.java)
        Handler(Looper.getMainLooper()).postDelayed({

            startActivity(i)
            finish()
        }, 6000)

        val executor: Executor = Executors.newSingleThreadExecutor()
        executor.execute(Runnable {
            if (internet.isConnected) {
                loadLoadFireBase("for_you")
                loadLoadFireBase("apps")
            }
        })

    }

    fun saveMoreAppToDevice(parent: String, folder: String, file: String) {
        val firebaseStorage = FirebaseStorage.getInstance()
        val storageReference = firebaseStorage.reference.child("$folder/$file")
        var localFile: File? = null
        try {
            localFile = File(getExternalFilesDir(parent).toString() + "$folder$file")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        storageReference.getFile(localFile!!)
            .addOnSuccessListener { taskSnapshot: FileDownloadTask.TaskSnapshot ->
                // Local temp file has been created
                val snapshot = taskSnapshot
            }.addOnFailureListener { exception: Exception ->
                // Handle any errors
                exception.printStackTrace()
            }
    }

    fun loadLoadFireBase(dir: String) {
        rootNode = FirebaseDatabase.getInstance()
        databaseReference = rootNode!!.reference.child(dir)
        val list: MutableList<String> = ArrayList()
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    val child = snap.key
                    list.add(child!!)

                }
                if (list.isNotEmpty()) {
                    var i = 1;
                    for (app in list) {
                        if (dir == "for_you") {
                            saveMoreAppToDevice("cache/*", "for_you_$i", "logo.png");
                        } else {
                            saveMoreAppToDevice("more_apps/*", "app_$i", "logo.png");
                            saveMoreAppToDevice("more_apps/*", "app_$i", "picture_1.png");
                            saveMoreAppToDevice("more_apps/*", "app_$i", "picture_2.png");
                            saveMoreAppToDevice("more_apps/*", "app_$i", "picture_3.png");

                        }
                        i++
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ActivitySplash, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

}