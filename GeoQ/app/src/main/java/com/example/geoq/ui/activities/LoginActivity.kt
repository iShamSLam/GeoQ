package com.example.geoq.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.arellomobile.mvp.MvpActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.geoq.R
import com.example.geoq.mvps.Presenters.LoginPresenter
import com.example.geoq.mvps.views.LoginView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : MvpActivity(), LoginView {

    @InjectPresenter
    lateinit var loginPresenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_sng_up.setOnClickListener {
            loginPresenter.onRegisterButtonClicked()
        }

        bt_sgn_in.setOnClickListener {
            loginPresenter.onLoginButtomClicked()
        }

    }

    override fun redirectToRegister() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }


    override fun performLogin() {
        val email = ed_user_lg.text.toString()
        val password = ed_pass_lg.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this, "Пожалуйста заполните все поля", Toast.LENGTH_SHORT
            ).show()
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                Toast.makeText(
                    this, FirebaseAuth.getInstance().currentUser?.email, Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this, BottomNavigationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this, "Не удалось войти по причине : ${it.message}", Toast.LENGTH_SHORT
                ).show()
            }

    }
}
