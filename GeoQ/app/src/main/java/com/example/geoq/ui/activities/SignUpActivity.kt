package com.example.geoq.ui.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.arellomobile.mvp.MvpActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.geoq.R
import com.example.geoq.models.InnerModels.GqUser
import com.example.geoq.mvps.Presenters.SignUpPresenter
import com.example.geoq.mvps.views.SignUpView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUpActivity : MvpActivity(), SignUpView {

    @InjectPresenter
    lateinit var signUpPresenter: SignUpPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btn_sg.setOnClickListener {
            signUpPresenter.onSingUpButtomClicked()
        }

        btn_select_photo.setOnClickListener {
            signUpPresenter.onSelectPhotoBtmClicked()
        }
    }

    private var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            selectedPhotoUri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            profile_image.setImageBitmap(bitmap)
            btn_select_photo.alpha = 0f
        }
    }

    override fun startPhotoSelector() {
        Log.d("Main", "Try to show selector")
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun performRegister() {
        val email = ed_email_sg.text.toString()
        val password = ed_pass_sg.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this, "Пожалуйста заполните все поля", Toast.LENGTH_SHORT
            ).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                Log.d("Main", "Аккаунт :${it.result?.user?.uid}" + "зарегистрирован")
                Toast.makeText(
                    this, "Регистрация прошла успешно", Toast.LENGTH_SHORT
                ).show()
                uploadImageToFirestore()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this, "Не удалссь создать пользователя: ${it.message}", Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun uploadImageToFirestore() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { it ->
                Log.d("Main", "Photo uploaded : ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("Main", "File location $it")
                    saveUserToDatabase(it.toString())
                }
                    .addOnFailureListener {
                        Toast.makeText(
                            this, "Не удалось загрузить фотографию", Toast.LENGTH_SHORT
                        ).show()
                    }
            }

    }

    private fun saveUserToDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = GqUser(
            username = ed_username_sg.text.toString(),
            profileImageUrl = profileImageUrl,
            uid = uid
        )
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Main", "GqUser saved")
            }.addOnFailureListener {
                Log.d("Main", "$it")
            }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
    }
}
