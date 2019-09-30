package com.example.geoq.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.MvpActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.geoq.Api.YandexApiService
import com.example.geoq.R
import com.example.geoq.models.InnerModels.Quest
import com.example.geoq.models.InnerModels.Step
import com.example.geoq.models.OuterModels.Yandex
import com.example.geoq.mvps.Presenters.AddStepToQuestPresenter
import com.example.geoq.mvps.views.AddStepToQuestView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.android.AndroidInjection
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_step_to_quest.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class AddStepToQuestActivity : MvpActivity(), AddStepToQuestView {

    @InjectPresenter
    lateinit var addStepPresenter: AddStepToQuestPresenter
    @Inject
    lateinit var api: YandexApiService
    private var selectedPhotoUri: Uri? = null
    private val MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 3154
    private var locationManager: LocationManager? = null
    private val mCompositeDisposable = CompositeDisposable()
    private var longt: Double? = null
    private var lat: Double? = null
    private var currentStep: Step = Step()
    private var questSteps: ArrayList<Step>? = ArrayList()
    private var createdQuest: Quest? = Quest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_step_to_quest)
        AndroidInjection.inject(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        btn_select_step_photo.setOnClickListener {
            addStepPresenter.onSelectPhotoButtonClicked()
        }
        btn_getGeoCode_step.setOnClickListener {
            addStepPresenter.onGetLocationButtonClicked()
        }
        btn_add_next_step.setOnClickListener {
            addStepPresenter.onNextStepButtonClicked()
        }
        btn_finish_creating.setOnClickListener {
            addStepPresenter.onFinishButtonClicked()
        }
    }

    override fun startPhotoSelector() {
        Log.d("Main", "Try to show selector")
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun performNextStep() {
        if (ed_step_point.text.isEmpty()
            or ed_step_help.text.isEmpty()
            or ed_step_riddle.text.isEmpty()
        ) {
            Toast.makeText(this, "Заполните все поля или завершите создание", Toast.LENGTH_SHORT)
                .show()
            return
        }
        currentStep.riddle = ed_step_riddle.text.toString()
        val loc = com.example.geoq.models.InnerModels.Location()
        loc.longtitude = longt!!
        loc.latitude = lat!!
        currentStep.finishPoint = loc
        if (ed_step_help.isEnabled) {
            currentStep.help = ed_step_help.text.toString()
            questSteps?.add(currentStep)
            prepareViewToAnotherStep()
        } else {
            upLoadImageToFirestore()
            showLoading()
        }
        if (btn_finish_creating.text == "отменить")
            btn_finish_creating.text = "завершить"
    }

    private fun showLoading() {
        cl_main.visibility = ConstraintLayout.INVISIBLE
        step_proggress_bar.visibility = ProgressBar.VISIBLE
    }

    private fun prepareViewToAnotherStep() {
        Toast.makeText(this, "Шаг добавлен", Toast.LENGTH_LONG).show()
        ed_step_help.isEnabled = true
        ed_step_riddle.text.clear()
        ed_step_help.text.clear()
        ed_step_point.text.clear()
        currentStep = Step()
    }

    override fun performFinish() {
        if (questSteps.isNullOrEmpty()) {
            Toast.makeText(this, "Cоздание квеста отменено", Toast.LENGTH_SHORT)
                .show()
        } else {
            var bungle = intent.extras
            createdQuest?.id = UUID.randomUUID().toString()
            createdQuest?.authorUid = FirebaseAuth.getInstance().currentUser!!.uid
            createdQuest?.authorName = bungle.get("username").toString()
            createdQuest?.description = bungle.get("QuestDescription").toString()
            createdQuest?.name = bungle.get("QuestName").toString()
            val loc = com.example.geoq.models.InnerModels.Location()
            loc.longtitude = bungle.get("longtitude").toString().toDouble()
            loc.latitude = bungle.get("latitude").toString().toDouble()
            createdQuest?.startPoint = loc
            createdQuest?.startPointAddress = bungle.get("QuestStartPointAddress").toString()
            createdQuest?.steps = questSteps
            saveQuestToDatabase()
        }
    }

    /* fun updateUserInformation() {
         val ref = FirebaseDatabase.getInstance().getReference()
         val childUpdate = HashMap<String, Any>()
         val uid = FirebaseAuth.getInstance().currentUser!!.uid
         childUpdate["/users/$uid/createdQuestCount"] = (currUserQuestCount?.plus(1)).toString()
         ref.updateChildren(childUpdate)
     }*/

    private fun saveQuestToDatabase() {
        val ref = FirebaseDatabase.getInstance().getReference("/quests/${createdQuest?.id}")
        ref.setValue(createdQuest)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Квест успешно сохранён",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Не удалось сохранить по причине: $it", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun upLoadImageToFirestore() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { it ->
                Log.d("Main", "Photo uploaded : ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("Main", "File location $it")
                    currentStep.helpPicture = it.toString()
                    questSteps?.add(currentStep)
                    prepareViewToAnotherStep()
                    hideLoading()

                }
                    .addOnFailureListener {
                        Toast.makeText(
                            this, "Не удалось загрузить фотографию", Toast.LENGTH_SHORT
                        ).show()
                    }
            }
    }

    private fun hideLoading() {
        cl_main.visibility = ConstraintLayout.VISIBLE
        step_proggress_bar.visibility = ProgressBar.INVISIBLE
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

    @SuppressLint("MissingPermission")
    fun handleRequest() {
        if (ed_step_point.text.isEmpty()) {
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                locationListener
            )
            getLocation("$longt,$lat")
        } else {
            getLocation(ed_step_point.text.toString())
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            selectedPhotoUri = data?.data
            ed_step_help.isEnabled = false
            ed_step_help.setText("В качестве подсказки выбрана фотография")
        }
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

    private fun getLocation(geoParam: String) {
        api.getGeoCodeInformation(geoParam)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Yandex> {
                override fun onSuccess(t: Yandex) {
                    val text =
                        t.response.geoObjectCollection.featureMember.first()
                            .geoObject.metaDataProperty.geocoderMetaData.text
                    ed_step_point.setText(text)
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
}
