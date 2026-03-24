package com.luqian.androidx.ui.home

import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.luqian.androidx.R
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView

class MenuAdapter : BaseQuickAdapter<MenuItem, QuickViewHolder>() {

    private val accentColors = listOf(
        "#F48FB1",
        "#A5D6A7",
        "#90CAF9",
        "#FFE082",
        "#CE93D8"
    )

    private val iconBgColors = listOf(
        "#FCE4EC",
        "#E8F5E9",
        "#E3F2FD",
        "#FFF8E1",
        "#F3E5F5"
    )

    private val iconTintColors = listOf(
        "#EC407A",
        "#66BB6A",
        "#42A5F5",
        "#FFA726",
        "#AB47BC"
    )

    override fun onCreateViewHolder(
        context: android.content.Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false))
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: MenuItem?) {
        item?.let {
            holder.itemView.findViewById<android.widget.TextView>(R.id.tv_menu).text = it.title
            holder.itemView.findViewById<android.widget.ImageView>(R.id.iv_icon).setImageResource(it.iconRes)

            val colorIndex = position % accentColors.size

            val viewAccent = holder.itemView.findViewById<android.view.View>(R.id.view_accent)
            val accentDrawable = GradientDrawable()
            accentDrawable.setColor(android.graphics.Color.parseColor(accentColors[colorIndex]))
            viewAccent.background = accentDrawable

            val cardIcon = holder.itemView.findViewById<CardView>(R.id.card_icon)
            cardIcon.setCardBackgroundColor(android.graphics.Color.parseColor(iconBgColors[colorIndex]))

            val ivIcon = holder.itemView.findViewById<android.widget.ImageView>(R.id.iv_icon)
            ivIcon.setColorFilter(android.graphics.Color.parseColor(iconTintColors[colorIndex]))
        }
    }
}

data class MenuItem(
    val title: String,
    val iconRes: Int
)