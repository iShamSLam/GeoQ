package com.example.geoq.OtherClasses

import com.example.geoq.R
import com.example.geoq.models.InnerModels.Quest
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.recycler_view_item.view.*

class QuestItemList(val quest: Quest, val distance: Float) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tv_name_item_name.text = quest.name
        viewHolder.itemView.tv_start_item_point.text = quest.startPointAddress
        viewHolder.itemView.tv_distance_to.text = distance.toString() + "m"
    }

    override fun getLayout(): Int {
        return R.layout.recycler_view_item
    }

}