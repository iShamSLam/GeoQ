package com.example.geoq.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.MvpActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.geoq.Api.YandexApiService
import com.example.geoq.R
import com.example.geoq.models.OuterModels.Yandex
import com.example.geoq.mvps.Presenters.CreateQuestPresenter
import com.example.geoq.mvps.views.CreateQuestView
import dagger.android.AndroidInjection
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create_quest.*
import javax.inject.Inject

class CreateQuestActivity : MvpActivity(), CreateQuestView {

    @InjectPresenter
    lateinit var createQuestPresenter: CreateQuestPresenter

    @Inject
    lateinit var api: YandexApiService

    private var locationManager: LocationManager? = null
    private val mCompositeDisposable = CompositeDisposable()
    //lateinit var api: YandexApiService
    private var longt: Double? = null
    private var lat: Double? = null
    private var username: String? = null
    private val MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 3154


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_quest)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        AndroidInjection.inject(this)
        username = intent.extras.get("username").toString()
        btn_getGeoCode.setOnClickListener {
            createQuestPresenter.onGetLocationButtonClicked()
        }
        btn_createQ_next.setOnClickListener {
            createQuestPresenter.onNextButtonClicked()
        }
    }

    override fun performAddSteps() {
        if (ed_quest_name.text.isEmpty()
            or ed_quest_description.text.isEmpty()
            or ed_start_point.text.isEmpty()
        ) {
            Toast.makeText(
                this, "Пожалуйста заполните все поля", Toast.LENGTH_SHORT
            )
                .show()
            return
        }
        val intent = Intent(this, AddStepToQuestActivity::class.java)
        intent.putExtra("QuestName", ed_quest_name.text)
        intent.putExtra("QuestDescription", ed_quest_description.text)
        intent.putExtra("QuestStartPointAddress", ed_start_point.text)
        intent.putExtra("longtitude", longt)
        intent.putExtra("username", username)
        intent.putExtra("latitude", lat)
        startActivity(intent)
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        finish()
    }

    @SuppressLint("MissingPermission")
    fun handleRequest() {
        if (ed_start_point.text.isEmpty()) {
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                locationListener
            )
            getLocation("$longt,$lat")
        } else {
            getLocation(ed_start_point.text.toString())
        }
    }

    override fun performGettingLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
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
                        this,
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

    private fun getLocation(geoParam: String) {
        api.getGeoCodeInformation(geoParam)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Yandex> {
                override fun onSuccess(t: Yandex) {
                    val text =
                        t.response.geoObjectCollection.featureMember.first()
                            .geoObject.metaDataProperty.geocoderMetaData.text
                    ed_start_point.setText(text)
                    val loc = t.response.geoObjectCollection.featureMember.first()
                        .geoObject.point.pos.split(" ")
                    longt = loc[0].toDouble()
                    lat = loc[1].toDouble()
                }

                override fun onError(e: Throwable) {
                    Log.d("Abracadabra", "something went wrong")
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }
            })
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            lat = location.latitude
            longt = location.longitude
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onDestroy() {
        if (!mCompositeDisposable.isDisposed)
            mCompositeDisposable.dispose()
        super.onDestroy()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
    }
}
