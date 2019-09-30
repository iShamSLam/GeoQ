package com.example.geoq.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.MvpActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.geoq.OtherClasses.StepItem
import com.example.geoq.R
import com.example.geoq.models.InnerModels.Quest
import com.example.geoq.models.InnerModels.Step
import com.example.geoq.mvps.Presenters.QuestInformationPresenter
import com.example.geoq.mvps.views.QuestInformationView
import com.example.geoq.ui.decoration.SpaceItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_quest_information.*

class QuestInformationActivity : MvpActivity(), QuestInformationView {

    @InjectPresenter
    lateinit var questInformationPresenter: QuestInformationPresenter
    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest_information)
        btn_delete_quest.setOnClickListener {
            questInformationPresenter.onDeleteButtonClicked()
        }
        btn_begin_quest.setOnClickListener {
            questInformationPresenter.onBeginQuestButtonClicked()
        }
    }

    override fun deleteQuest() {
        val ref = FirebaseDatabase.getInstance().getReference("quests")
            .child("$id")
        ref.setValue(null)
        finish()
    }

    override fun subcribeToQuest() {
        var quest = intent.extras.getSerializable("quest") as Quest
        val ref = FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
        ref.child("currentQuest")
            .setValue(quest)
        ref.child("currentStep")
            .setValue(quest.steps?.get(0))
        ref.child("currentStepNum")
            .setValue(0)
        Toast.makeText(
            this,
            "Вы начали квест посмотреть прогресс вы можете во вкладке \"Текущий квест\"",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun showInformation() {
        var bundle = intent.extras
        tv_info_name.text = bundle.get("name").toString()
        tv_quest_description_info.text = bundle.get("desc").toString()
        tv_ahahah.text = bundle.get("point").toString()
        id = bundle.get("id").toString()
        if (bundle.get("author") == null) {
            val steps = bundle.getSerializable("steps") as ArrayList<Step>
            rv_steps_info.visibility = RecyclerView.VISIBLE
            tv_author.visibility = TextView.INVISIBLE
            btn_begin_quest.visibility = Button.INVISIBLE
            btn_delete_quest.visibility = Button.VISIBLE
            tv_variable.text = "Привязанные шаги:"
            var adapter = GroupAdapter<GroupieViewHolder>()
            steps.forEach {
                adapter.add(StepItem(it))
            }
            rv_steps_info.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                addItemDecoration(SpaceItemDecoration(4))
            }
            rv_steps_info.adapter = adapter
        } else
            rv_steps_info.visibility = RecyclerView.INVISIBLE
        tv_variable.text = "Автор"
        btn_delete_quest.visibility = Button.INVISIBLE
        btn_begin_quest.visibility = Button.VISIBLE
        tv_author.visibility = TextView.VISIBLE
        tv_author.text = bundle.get("author").toString()

    }
}
