package com.example.geoq.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.MvpFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.geoq.OtherClasses.QuestItemList
import com.example.geoq.R
import com.example.geoq.models.InnerModels.Quest
import com.example.geoq.mvps.Presenters.QuestListPresenter
import com.example.geoq.mvps.views.QuestListView
import com.example.geoq.ui.activities.QuestInformationActivity
import com.example.geoq.ui.decoration.SpaceItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_quest_list.*

class QuestListFragment : MvpFragment(), QuestListView {

    @InjectPresenter
    lateinit var questListPresenter: QuestListPresenter

    private var listener: OnFragmentInteractionListener? = null
    private val MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 3154
    private var locationManager: LocationManager? = null
    private var currentItem: QuestItemList? = null
    private val myLocation: Location = Location("")
    private val questLocation = Location("")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        return inflater.inflate(R.layout.fragment_quest_list, container, false)
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        btn_star_loading.setOnClickListener {
            questListPresenter.onLoadButtonClicked()
        }
    }

    fun showLoading() {
        btn_star_loading.visibility = Button.INVISIBLE
        iv_borders.visibility = ImageView.INVISIBLE
        rellay_quest_list.visibility = ConstraintLayout.VISIBLE
    }

    fun hideLoading() {
        rellay_quest_list.visibility = ConstraintLayout.INVISIBLE
        rv_quest_list.visibility = RecyclerView.VISIBLE
    }

    fun beginState() {
        btn_star_loading.visibility = Button.VISIBLE
        rv_quest_list.visibility = RecyclerView.INVISIBLE
    }

    override fun perfromLoading() {
        showLoading()
        var ref = FirebaseDatabase.getInstance().getReference("quests")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    rv_quest_list.adapter = null
                    var adapter = GroupAdapter<GroupieViewHolder>()
                    p0.children.forEach {
                        val quest = it.getValue(Quest::class.java)
                        performGettingLocation()
                        questLocation.longitude = quest?.startPoint?.longtitude!!
                        questLocation.latitude = quest?.startPoint?.latitude!!
                        val distanceInMeters = myLocation.distanceTo(questLocation)
                        if (quest != null && quest.authorUid != FirebaseAuth.getInstance().currentUser?.uid) {
                            adapter.add(QuestItemList(quest, distanceInMeters))
                        }
                    }
                    adapter.setOnItemClickListener { item, view ->
                        currentItem = item as QuestItemList
                        questListPresenter.onAdapterClicked()

                    }
                    rv_quest_list.adapter = adapter
                    hideLoading()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context, "$p0", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun showExtra() {
        val intent = Intent(context, QuestInformationActivity::class.java)
        intent.putExtra("name", currentItem?.quest?.name)
        intent.putExtra("desc", currentItem?.quest?.description)
        intent.putExtra("point", currentItem?.quest?.startPointAddress)
        intent.putExtra("id", currentItem?.quest?.id)
        intent.putExtra("author", currentItem?.quest?.authorName)
        intent.putExtra("quest", currentItem?.quest)
        startActivity(intent)
        activity?.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }


    fun performGettingLocation() {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                MY_PERMISSIONS_REQUEST_COARSE_LOCATION
            )
        } else handleRequest()
    }

    @SuppressLint("MissingPermission")
    fun handleRequest() {
        locationManager?.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )
    }

    override fun setUp() {
        performGettingLocation()
        rv_quest_list.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(SpaceItemDecoration(16))
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_COARSE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    handleRequest()
                } else {
                    Toast.makeText(
                        context,
                        "Пожалуйста разрешите использование геолокации",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
            else -> {
            }
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            myLocation.longitude = location.longitude
            myLocation.latitude = location.latitude
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

}
