package com.example.junckcleaner.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.junckcleaner.R
import com.example.junckcleaner.adapters.AdapterNoty
import com.example.junckcleaner.annotations.MyAnnotations
import com.example.junckcleaner.interfaces.SelectAll
import com.example.junckcleaner.models.ModelNoty
import com.example.junckcleaner.prefrences.AppPreferences


class FragmentNotificationManager : Fragment(), SelectAll {

    var recyclerView: RecyclerView? = null
    var textViewMessages: TextView? = null
    var textViewNotFound: TextView? = null
    var textViewClean: TextView? = null
    var textViewCleanText: TextView? = null
    var checkBoxSelectAllNoty: CheckBox? = null

    private var lm: LinearLayoutManager? = null
    var preferences: AppPreferences? = null
    var adapter: AdapterNoty? = null
    var notifications: MutableList<ModelNoty>? = null


    companion object {
        @JvmStatic
        fun newInstance() = FragmentNotificationManager()


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notifications = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notification_manager, container, false)
        textViewNotFound = root.findViewById(R.id.textViewNotFound);
        textViewClean = root.findViewById(R.id.textView_install);
        textViewCleanText = root.findViewById(R.id.textView_clean_text);
        textViewMessages = root.findViewById(R.id.textViewMessages);
        checkBoxSelectAllNoty = root.findViewById(R.id.checkBoxSelectAllNoty);
        recyclerView = root.findViewById(R.id.recyclerView)
        preferences = AppPreferences(activity)



        return root
    }

    private fun conditionalCode(notifications: List<ModelNoty>) {
        adapter = AdapterNoty(activity, this, notifications)

        lm = LinearLayoutManager(activity)
        lm!!.orientation = LinearLayoutManager.VERTICAL

        recyclerView!!.layoutManager = lm
        recyclerView!!.adapter = adapter
        if (notifications.isNotEmpty()
        ) {
            adapter!!.submitList(notifications)
        } else {
            textViewNotFound!!.visibility = View.VISIBLE
            textViewClean!!.visibility = View.GONE
            textViewCleanText!!.visibility = View.GONE
            Toast.makeText(context, "Notification not found", Toast.LENGTH_SHORT).show()
        }


        if (notifications.isNotEmpty()
        ) {
            textViewMessages!!.text = "Total notifications: " + notifications!!.size
            textViewMessages!!.visibility = View.VISIBLE
            checkBoxSelectAllNoty!!.visibility = View.VISIBLE

        } else {
            textViewMessages!!.visibility = View.GONE
            checkBoxSelectAllNoty!!.visibility = View.GONE
        }

        checkBoxSelectAllNoty!!.setOnClickListener {
            if (notifications.size == adapter!!.modelNotyList.size) {
                //un checked all
                adapter!!.unSelectAll()
            } else {
                //checked all
                adapter!!.selectAll()
            }
        }

    }


    override fun selectAll(isSelectAll: Boolean, size: String?) {

        checkBoxSelectAllNoty!!.isChecked = isSelectAll
    }

    override fun onResume() {
        super.onResume()
        preferences!!.addBoolean(MyAnnotations.NOTIFICATION_FRAGMENT, true)
        if (notifications.isNullOrEmpty()) {
        } else {
            notifications!!.clear()
        }
        if (preferences!!.getNoty(MyAnnotations.NOTIFICATIONS) != null &&
            preferences!!.getNoty(MyAnnotations.NOTIFICATIONS).isNotEmpty()
        ) {
            notifications!!.addAll(preferences!!.getNoty(MyAnnotations.NOTIFICATIONS))
        }

        conditionalCode(notifications!!)

        textViewClean!!.setOnClickListener {
            if (adapter!!.modelNotyList.isNullOrEmpty()) {
                Toast.makeText(activity, "Notification is not selected", Toast.LENGTH_SHORT).show()
            } else {

                adapter!!.modelNotyList.forEach {
                    notifications!!.remove(it)
                }

                preferences!!.setNoty(MyAnnotations.NOTIFICATIONS, notifications)
                adapter!!.modelNotyList.clear()
                if (notifications.isNullOrEmpty()) {
                } else {
                    notifications!!.clear()
                    notifications!!.addAll(preferences!!.getNoty(MyAnnotations.NOTIFICATIONS))
                }
                conditionalCode(notifications!!)

            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        preferences!!.addBoolean(MyAnnotations.NOTIFICATION_FRAGMENT, false)
    }

    override fun onPause() {
        super.onPause()
        preferences!!.addBoolean(MyAnnotations.NOTIFICATION_FRAGMENT, false)
    }

    override fun onStop() {
        super.onStop()
        preferences!!.addBoolean(MyAnnotations.NOTIFICATION_FRAGMENT, false)
    }
}