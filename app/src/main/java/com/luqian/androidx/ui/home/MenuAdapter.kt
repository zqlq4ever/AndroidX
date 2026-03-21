package com.luqian.androidx.ui.home

import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.luqian.androidx.R
import android.view.LayoutInflater
import android.view.ViewGroup

class MenuAdapter : BaseQuickAdapter<String, QuickViewHolder>() {

    override fun onCreateViewHolder(
        context: android.content.Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false))
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: String?) {
        item?.let {
            holder.itemView.findViewById<android.widget.TextView>(R.id.tv_menu).text = it
        }
    }
}
