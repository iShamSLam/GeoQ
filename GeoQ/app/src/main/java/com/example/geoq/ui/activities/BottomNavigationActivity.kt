package com.example.geoq.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import com.arellomobile.mvp.MvpActivity
import com.arellomobile.mvp.MvpFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.geoq.R
import com.example.geoq.mvps.Presenters.BottomNavigationPresenter
import com.example.geoq.mvps.views.BottomNavigationView
import com.example.geoq.ui.fragments.CurrentQuestFragment
import com.example.geoq.ui.fragments.NoCurrentQuestFragment
import com.example.geoq.ui.fragments.ProfileFragment
import com.example.geoq.ui.fragments.QuestListFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_bottom__navigation_.*

class BottomNavigationActivity : MvpActivity(), BottomNavigationView,
    com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemReselectedListener,
    ProfileFragment.OnFragmentInteractionListener, QuestListFragment.OnFragmentInteractionListener,
    CurrentQuestFragment.OnFragmentInteractionListener,
    NoCurrentQuestFragment.OnFragmentInteractionListener {

    @InjectPresenter
    lateinit var bottomNavPresenter: BottomNavigationPresenter
    private var selectedFragment: MvpFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom__navigation_)
        verifyUserIsLoggedIn()
        if (savedInstanceState == null) {
            switchFragment()
        }
        bottomNavigationView.setOnNavigationItemReselectedListener(this)
    }

    override fun onNavigationItemReselected(p0: MenuItem) {
        when (p0.itemId) {
            R.id.nav_list -> {
                if (selectedFragment is QuestListFragment) {
                    return
                }
                selectedFragment = QuestListFragment()
                switchFragment()
            }
            R.id.nav_current_quest -> {
                if (selectedFragment is CurrentQuestFragment)
                    return
                selectedFragment = CurrentQuestFragment()
                switchFragment()
            }
            R.id.nav_profile -> {
                if (selectedFragment is ProfileFragment) {
                }
                selectedFragment = ProfileFragment()
                switchFragment()
            }
        }
    }

    private fun switchFragment() {
        fragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, selectedFragment)
            .commit()
    }

    override fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            finish()
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
    }
}
