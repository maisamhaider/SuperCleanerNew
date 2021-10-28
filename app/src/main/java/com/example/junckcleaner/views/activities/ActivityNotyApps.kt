package com.example.junckcleaner.views.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.junckcleaner.R
import com.example.junckcleaner.adapters.AdapterNotyApps
import com.example.junckcleaner.annotations.MyAnnotations
import com.example.junckcleaner.interfaces.SelectAll
import com.example.junckcleaner.prefrences.AppPreferences
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ActivityNotyApps : AppCompatActivity(), SelectAll {

    var adapterApps: AdapterNotyApps? = null
    private var checkBoxSelectAllNotyApp: CheckBox? = null
    var preferences: AppPreferences? = null
    private var textViewSelectedApps: TextView? = null
    var hashSet: MutableSet<String> = mutableSetOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noty_apps)
        val preferences = AppPreferences(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView);
        val imageView_back = findViewById<ImageView>(R.id.imageView_back);
        checkBoxSelectAllNotyApp = findViewById(R.id.checkBoxSelectAllNotyApp);
        textViewSelectedApps = findViewById(R.id.textViewSelectedApps);

        val allApp = mutableSetOf<String>()

        val executor: Executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            allApp.addAll(preferences.getStringSet(MyAnnotations.ALL_APPS))
            //set check bos
            checkBoxSelectAllNotyApp!!.isChecked =
                preferences.getStringSet(MyAnnotations.NOTY_APPS) != null &&
                        preferences.getStringSet(MyAnnotations.NOTY_APPS).size == allApp.size

            //all apps

            handler.post {
                if (allApp.isEmpty()) {
                    Toast.makeText(
                        this@ActivityNotyApps,
                        "No App found", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val list: MutableList<String> = ArrayList(allApp)
                    adapterApps!!.submitList(list)
                }
            }
        }

        imageView_back.setOnClickListener {
            finish()
        }


        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        adapterApps = AdapterNotyApps(this, preferences, allApp, this)
        recyclerView.layoutManager = llm
        recyclerView.adapter = adapterApps

        if (preferences.getStringSet(MyAnnotations.NOTY_APPS) != null &&
            preferences.getStringSet(MyAnnotations.NOTY_APPS).isNotEmpty()
        ) {
            textViewSelectedApps!!.text =
                "Apps selected: " + preferences!!.getStringSet(MyAnnotations.NOTY_APPS).size

        } else {
            textViewSelectedApps!!.text = "Apps selected: 0"
        }

        checkBoxSelectAllNotyApp!!.setOnClickListener {
            if (preferences.getStringSet(MyAnnotations.NOTY_APPS) != null &&
                preferences.getStringSet(MyAnnotations.NOTY_APPS).size == allApp.size
            ) {
                //un checked all
                adapterApps!!.unSelectAll()
            } else {
                //checked all
                adapterApps!!.selectAll()
            }
        }

    }

    @SuppressLint("SetTextI18n")
    override fun selectAll(isSelectAll: Boolean, size: String?) {
        textViewSelectedApps!!.text = "Apps selected: $size"

        checkBoxSelectAllNotyApp!!.isChecked = isSelectAll
    }


}