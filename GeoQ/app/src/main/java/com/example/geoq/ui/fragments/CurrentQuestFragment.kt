package com.example.geoq.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.MvpFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.geoq.R
import com.example.geoq.models.InnerModels.GqUser
import com.example.geoq.models.InnerModels.Quest
import com.example.geoq.mvps.Presenters.CurrentQuestPresenter
import com.example.geoq.mvps.views.CurrentQuestView
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_current_quest.*
import kotlin.random.Random


class CurrentQuestFragment : MvpFragment(), CurrentQuestView {

    private val myLocation: Location = Location("")
    private val MY_PERMISSIONS_REQUEST_COARSE_LOCATION: Int = 321
    @InjectPresenter
    lateinit var curQpresenter: CurrentQuestPresenter
    private var listener: OnFragmentInteractionListener? = null
    private var locationManager: LocationManager? = null
    private var quest: Quest? = null
    private var user: GqUser? = null
    private var mAttacher: PhotoViewAttacher? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_current_quest, container, false)
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        btn_get_help.setOnClickListener {
            curQpresenter.onHelpButtonClicked()
        }
        btn_chech_answer.setOnClickListener {
            curQpresenter.onCheckButtonClicked()
        }
    }

    override fun showHelp() {
        val step = quest?.steps!![user?.currentStepNum!!]
        if (step.help.isEmpty()) {
            tv_curr_help.visibility = TextView.INVISIBLE
            Picasso.get().load(step.helpPicture).into(cim_helps)
            cim_helps.setOnClickListener {
                mAttacher = PhotoViewAttacher(cim_helps)
            }
        } else {
            cim_helps.visibility = CircleImageView.INVISIBLE
            tv_curr_help.visibility = TextView.VISIBLE
            tv_curr_help.text = step.help
        }
    }

    override fun checkAnswer() {
        performGettingLocation()
        val step = quest?.steps!![user?.currentStepNum!!]
        val stepLocation = Location("")
        stepLocation.longitude = step.finishPoint.longtitude
        stepLocation.latitude = step.finishPoint.latitude
        if (myLocation.distanceTo(stepLocation) > 60) {
            performUpdateStep()
        }
    }

    private fun performUpdateStep() {
        if (quest?.steps!![user?.currentStepNum!!] == quest!!.steps!!.last())
            Congratilation()
        else {
            FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("currentStepNum")
                .setValue(user?.currentStepNum?.plus(1))
            curQpresenter.onStepPassed()
        }
    }

    private fun Congratilation() {
        wheelprogress.setStepCountText("100%")
        wheelprogress.setPercentage(360)
        val passedDistance = CalculateDistance()
        val bonus = Random(quest?.steps?.size!!).nextInt()
        val ref = FirebaseDatabase.getInstance().getReference(("users"))
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
        ref.child("helpsEnable")
            .setValue(user?.helpsEnable?.plus(bonus))
        ref.child("passedQuestCount")
            .setValue(user?.passedQuestCount?.plus(1))
        ref.child("passedDistance")
            .setValue(user?.passedDistance?.plus(passedDistance))
        ref.child("currentQuest")
            .setValue(null)
        Toast.makeText(context, "Поздравляю вы прошли квест", Toast.LENGTH_LONG).show()
        fragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, NoCurrentQuestFragment() as android.app.Fragment)
            .commit()
    }

    private fun CalculateDistance(): Float {
        val location1 = Location("")
        val location2 = Location("")
        var result = 0.0
        if (quest?.steps?.size!! > 1) {
            for (i in 1..quest?.steps?.size!!) {
                location1.longitude = quest!!.steps!![i - 1].finishPoint.longtitude
                location1.latitude = quest!!.steps!![i - 1].finishPoint.latitude
                location2.longitude = quest!!.steps!![i].finishPoint.longtitude
                location2.latitude = quest!!.steps!![i].finishPoint.latitude
                result += location1.distanceTo(location2)
            }
            return ((result / 1000).toFloat())
        } else
            return ((250 / 1000).toFloat())
    }

    fun UpdateFields() {
        if (user?.currentQuest == null) {
            fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, NoCurrentQuestFragment() as android.app.Fragment)
                .commit()
        } else {
            wheelprogress.setStepCountText("${user?.currentStepNum} из ${quest?.steps?.size}")
            wheelprogress.setPercentage((360 / quest?.steps?.size!!) * user?.currentStepNum!!)
            tv_curr_name.text = quest?.name
            tv_curr_riddle.text = quest?.steps!![user?.currentStepNum!!]?.riddle.toString()
        }
    }

    override fun showFields() {
        val ref = FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user1 = p0.getValue(GqUser::class.java)
                user1.let {
                    user = user1
                    quest = user1?.currentQuest
                    UpdateFields()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context, "$p0", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
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

    @SuppressLint("MissingPermission")
    fun handleRequest() {
        locationManager?.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }
}
