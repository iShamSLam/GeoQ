package com.example.geoq.OtherClasses

import com.example.geoq.R
import com.example.geoq.models.InnerModels.Step
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.recycler_view_item_1.view.*

class StepItem(val step: Step) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tv_info_step_riddle.text = step.riddle
    }

    override fun getLayout(): Int {
        return R.layout.recycler_view_item_1
    }

}