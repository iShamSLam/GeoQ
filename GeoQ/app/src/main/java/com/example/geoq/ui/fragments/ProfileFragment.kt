package com.example.geoq.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.MvpFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.geoq.OtherClasses.QuestItemProfile
import com.example.geoq.R
import com.example.geoq.models.InnerModels.GqUser
import com.example.geoq.models.InnerModels.Quest
import com.example.geoq.mvps.Presenters.ProfilePresenter
import com.example.geoq.mvps.views.ProfileView
import com.example.geoq.ui.activities.CreateQuestActivity
import com.example.geoq.ui.activities.LoginActivity
import com.example.geoq.ui.activities.QuestInformationActivity
import com.example.geoq.ui.decoration.SpaceItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.profile_fragment.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : MvpFragment(), ProfileView {

    @InjectPresenter
    lateinit var profilePresenter: ProfilePresenter
    private var param1: String? = null
    private var param2: String? = null
    private var currUserQuestCount: Int? = null
    private var listener: OnFragmentInteractionListener? = null
    private var currentItem: QuestItemProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button_sign_out.setOnClickListener {
            profilePresenter.onSignOutButtonClicked()
        }
        button_new_quest.setOnClickListener {
            profilePresenter.onCreateQuestButtonClicked()
        }
    }

    fun hideContext() {
        profile_main_context.visibility = RelativeLayout.INVISIBLE
        profile_progress_bar.visibility = ConstraintLayout.VISIBLE
    }

    fun showContext() {
        profile_progress_bar.visibility = ConstraintLayout.INVISIBLE
        profile_main_context.visibility = RelativeLayout.VISIBLE
    }

    override fun showUserAttribute() {
        hideContext()
        rv_profile.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(SpaceItemDecoration(16))
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users")
            .child(FirebaseAuth.getInstance().currentUser?.uid!!)
        val listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context, "$p0", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(GqUser::class.java)
                user?.let {
                    Picasso.get().load(it.profileImageUrl).into(profile_photo)
                    currUserQuestCount = it.passedQuestCount
                    tv_username_profile.text = it.username
                    tv_distance_count.text = it.passedDistance.toString()
                    tv_passed_count.text = it.passedQuestCount.toString()
                    fetchMyQuests()
                    showContext()
                }
            }
        }
        ref.addListenerForSingleValueEvent(listener)
    }

    override fun onResume() {
        fetchMyQuests()
        super.onResume()
    }

    private fun fetchMyQuests() {
        val ref = FirebaseDatabase.getInstance().getReference("/quests")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var adapter = GroupAdapter<GroupieViewHolder>()
                p0.children.forEach {
                    val quest = it.getValue(Quest::class.java)
                    if (quest != null && quest.authorUid == FirebaseAuth.getInstance().currentUser?.uid) {
                        adapter.add(QuestItemProfile(quest))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    currentItem = item as QuestItemProfile
                    profilePresenter.onAdapterItemClicked()
                }
                rv_profile.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context, "$p0", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun showQuestInformation() {
        val intent = Intent(context, QuestInformationActivity::class.java)
        intent.putExtra("name", currentItem?.quest?.name)
        intent.putExtra("desc", currentItem?.quest?.description)
        intent.putExtra("point", currentItem?.quest?.startPointAddress)
        intent.putExtra("id", currentItem?.quest?.id)
        intent.putExtra("steps", currentItem?.quest?.steps)
        startActivity(intent)
        activity?.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun performSignOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        activity?.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        activity?.finish()
    }

    override fun performCreatingQuest() {
        val intent = Intent(context, CreateQuestActivity::class.java)
        intent.putExtra("username", tv_username_profile.text)
        startActivity(intent)
        activity?.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
